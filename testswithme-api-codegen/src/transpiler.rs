use regex::Regex;

use crate::model::{
    KotlinDeclaration, KotlinProperty, KotlinType, RustDeclaration, RustField, RustType,
    TypeReference,
};

pub fn transform(kotlin_type: KotlinType) -> Result<RustType, String> {
    let declaration = match kotlin_type.declaration {
        KotlinDeclaration::Enum { cases: variants } => RustDeclaration::Enum { cases: variants },
        KotlinDeclaration::DataClass { properties } => {
            let fields = properties
                .into_iter()
                .map(|property| {
                    let type_reference = kotlin_type_to_rust(property.kotlin_type)?;

                    Ok::<_, String>(RustField {
                        name: to_snake_case(&property.name),
                        type_reference,
                    })
                })
                .collect::<Result<Vec<_>, _>>()?;

            RustDeclaration::Struct { fields }
        }
    };

    let namespace = kotlin_type
        .package
        .rsplit_once('.')
        .map(|(_, suffix)| suffix)
        .unwrap_or(&kotlin_type.package)
        .to_string();

    Ok(RustType {
        namespace,
        name: kotlin_type.type_name,
        declaration,
    })
}

pub fn parse_kotlin_file(source: String) -> Result<KotlinType, String> {
    let package_regex =
        Regex::new(r"(?m)^\s*package\s+([A-Za-z_][A-Za-z0-9_.]*)\s*$").expect("valid regex");
    let package = package_regex
        .captures(&source)
        .and_then(|captures| captures.get(1))
        .map(|value| value.as_str().to_owned())
        .ok_or_else(|| "Missing package declaration".to_owned())?;

    let data_class_regex = Regex::new(r"(?s)\bdata\s+class\s+([A-Za-z_][A-Za-z0-9_]*)\s*\((.*?)\)")
        .expect("valid regex");
    if let Some(captures) = data_class_regex.captures(&source) {
        let name = captures[1].to_owned();
        let properties = parse_properties(&captures[2])?;
        return Ok(KotlinType {
            package,
            type_name: name,
            declaration: KotlinDeclaration::DataClass { properties },
        });
    }

    let enum_regex = Regex::new(r"(?s)\benum\s+class\s+([A-Za-z_][A-Za-z0-9_]*)\s*\{(.*?)}")
        .expect("valid regex");
    if let Some(captures) = enum_regex.captures(&source) {
        let name = captures[1].to_owned();
        let variants = captures[2]
            .split([',', ';'])
            .map(str::trim)
            .filter(|variant| !variant.is_empty())
            .map(|variant| {
                variant
                    .split_whitespace()
                    .next()
                    .unwrap_or(variant)
                    .to_owned()
            })
            .collect();
        return Ok(KotlinType {
            package,
            type_name: name,
            declaration: KotlinDeclaration::Enum { cases: variants },
        });
    }

    Err("Expected a data class or enum class".to_owned())
}

fn parse_properties(source: &str) -> Result<Vec<KotlinProperty>, String> {
    split_top_level(source, ',')
        .into_iter()
        .filter(|property| !property.trim().is_empty())
        .map(|property| {
            let property_regex = Regex::new(
                r"(?s)^\s*(?:@[A-Za-z0-9_.]+(?:\([^)]*\))?\s*)*(?:val|var)\s+([A-Za-z_][A-Za-z0-9_]*)\s*:\s*(.+?)\s*(?:=.+)?$",
            )
                .expect("valid regex");
            let captures = property_regex
                .captures(property)
                .ok_or_else(|| format!("Invalid property declaration: {}", property.trim()))?;
            Ok(KotlinProperty {
                name: captures[1].to_owned(),
                kotlin_type: captures[2].trim().to_owned(),
            })
        })
        .collect()
}

fn split_top_level(source: &str, separator: char) -> Vec<&str> {
    let mut parts = Vec::new();
    let mut start = 0;
    let mut depth = 0;

    for (index, character) in source.char_indices() {
        match character {
            '<' | '(' | '[' => depth += 1,
            '>' | ')' | ']' => depth -= 1,
            _ if character == separator && depth == 0 => {
                parts.push(&source[start..index]);
                start = index + character.len_utf8();
            }
            _ => {}
        }
    }
    parts.push(&source[start..]);
    parts
}

fn kotlin_type_to_rust(kotlin_type: String) -> Result<TypeReference, String> {
    let kotlin_type = kotlin_type.trim();
    let nullable = kotlin_type.ends_with("?");
    let non_nullable_type = kotlin_type.strip_suffix("?").unwrap_or(kotlin_type);

    if let Some((container, inner)) = split_generic_type(non_nullable_type) {
        let converted = match container {
            "List" | "MutableList" | "Set" | "MutableSet" => TypeReference {
                name: "Vec".to_string(),
                parameter_names: vec![format_rust_type_name(inner)],
                nullable,
            },
            "Map" | "MutableMap" => {
                let arguments = split_top_level(inner, ',');
                if arguments.len() != 2 {
                    return Err(format!(
                        "Map requires two type arguments: {non_nullable_type}"
                    ));
                }
                TypeReference {
                    name: "std::collections::HashMap".to_string(),
                    parameter_names: vec![arguments[0].to_string(), arguments[1].to_string()],
                    nullable,
                }
            }
            _ => {
                return Err(format!(
                    "Unsupported generic Kotlin type: {non_nullable_type}"
                ));
            }
        };

        return Ok(converted);
    }

    Ok(TypeReference {
        name: format_rust_type_name(non_nullable_type),
        parameter_names: vec![],
        nullable,
    })
}

fn format_rust_type_name(name: &str) -> String {
    match name {
        "String" | "Char" => "String".to_string(),
        "Boolean" => "bool".to_string(),
        "Byte" => "i8".to_string(),
        "Short" => "i16".to_string(),
        "Int" => "i32".to_string(),
        "Long" => "i64".to_string(),
        "Float" => "f32".to_string(),
        "Double" => "f64".to_string(),
        identifier => format!("crate::{identifier}"),
    }
}

fn split_generic_type(kotlin_type: &str) -> Option<(&str, &str)> {
    let open = kotlin_type.find('<')?;
    if !kotlin_type.ends_with('>') {
        return None;
    }
    Some((
        &kotlin_type[..open],
        &kotlin_type[open + 1..kotlin_type.len() - 1],
    ))
}

fn to_snake_case(value: &str) -> String {
    let mut result = String::new();
    for character in value.chars() {
        if character.is_ascii_uppercase() {
            if !result.is_empty() {
                result.push('_');
            }
            result.push(character.to_ascii_lowercase());
        } else {
            result.push(character);
        }
    }
    result
}
