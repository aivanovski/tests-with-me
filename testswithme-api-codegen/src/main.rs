use clap::Parser;
use std::collections::HashMap;
use std::path::{Path, PathBuf};
use std::process::ExitCode;
use testswithme_api_codegen::{
    RustType, collect_kotlin_files, parse_kotlin_file, read_file, serialize_lib_rs,
    serialize_rust_types, transform, write_file,
};

#[derive(Debug, Parser)]
#[command(name = "codegen")]
struct CliArguments {
    #[arg(long, default_value = "testswithme-backend-api/src")]
    input: String,
    #[arg(long, default_value = "testswithme-api-rust/src")]
    output: String,
}

fn main() -> ExitCode {
    let args = CliArguments::parse();

    match run(args) {
        Ok(_) => ExitCode::SUCCESS,
        Err(error) => {
            eprintln!("codegen: {error}");
            ExitCode::FAILURE
        }
    }
}

fn run(args: CliArguments) -> Result<(), String> {
    let workspace_dir = workspace_root()?;

    let input_dir = workspace_dir.join(args.input);
    let output_dir = workspace_dir.join(args.output);

    let kotlin_files = collect_kotlin_files(&input_dir)?;
    let rust_types = kotlin_files
        .into_iter()
        .filter(|kotlin_file| {
            let file_name = kotlin_file
                .file_stem()
                .map(|name| name.to_str().unwrap_or(""))
                .unwrap_or("");

            has_supported_suffix(file_name)
        })
        .map(|kotlin_file| -> Result<_, String> {
            let kotlin_type = parse_kotlin_file(read_file(&kotlin_file)?)?;
            transform(kotlin_type)
        })
        .collect::<Result<Vec<_>, _>>()?;

    let namespace_to_types_map = group_types_by_namespace(rust_types);

    // Write all DTO's
    for (namespace, types) in &namespace_to_types_map {
        let rust_code = serialize_rust_types(types);
        let path = output_dir.join(format!("{}.rs", namespace).to_string());
        write_file(&path, &rust_code)?;

        for t in types {
            println!("Serialize {}:{} into {}", namespace, t.name, path.display())
        }
    }

    // Write crate root (libs.rs0
    let lib_rs_path = output_dir.join("lib.rs");
    write_file(&lib_rs_path, &serialize_lib_rs(&namespace_to_types_map))?;
    println!("Serialize crate root into {}", lib_rs_path.display());

    Ok(())
}

fn group_types_by_namespace(types: Vec<RustType>) -> HashMap<String, Vec<RustType>> {
    let mut types_by_namespace = HashMap::new();

    for t in types {
        types_by_namespace
            .entry(t.namespace.to_string())
            .or_insert_with(Vec::new)
            .push(t.clone());
    }

    types_by_namespace
}

fn has_supported_suffix(class_name: &str) -> bool {
    ["Dto", "Request", "Response"]
        .iter()
        .any(|suffix| class_name.ends_with(suffix))
}

fn workspace_root() -> Result<PathBuf, String> {
    Path::new(env!("CARGO_MANIFEST_DIR"))
        .parent()
        .map(|parent| parent.to_path_buf())
        .ok_or_else(|| "Unable to determine parent directory".to_string())
}
