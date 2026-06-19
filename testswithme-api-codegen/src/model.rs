// Kotlin spec
#[derive(Debug)]
pub struct KotlinType {
    pub package: String,
    pub type_name: String,
    pub declaration: KotlinDeclaration,
}

#[derive(Debug)]
pub enum KotlinDeclaration {
    DataClass { properties: Vec<KotlinProperty> },
    Enum { cases: Vec<String> },
}

#[derive(Debug, Clone)]
pub struct KotlinProperty {
    pub name: String,
    pub kotlin_type: String,
}

// Rust spec
#[derive(Debug, Clone)]
pub struct RustType {
    pub namespace: String,
    pub name: String,
    pub declaration: RustDeclaration,
}

#[derive(Debug, Clone)]
pub enum RustDeclaration {
    Struct { fields: Vec<RustField> },
    Enum { cases: Vec<String> },
}

#[derive(Debug, Clone)]
pub struct RustField {
    pub name: String,
    pub type_reference: TypeReference,
}

#[derive(Debug, Clone)]
pub struct TypeReference {
    pub name: String,
    pub parameter_names: Vec<String>,
    pub nullable: bool,
}
