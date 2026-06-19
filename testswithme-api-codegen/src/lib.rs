pub mod model;

pub mod io;
mod serializer;
mod transpiler;

pub use io::{collect_kotlin_files, read_file, write_file};
pub use model::{
    KotlinDeclaration, KotlinProperty, KotlinType, RustDeclaration, RustField, RustType,
    TypeReference,
};
pub use serializer::{serialize_rust_types, serialize_lib_rs};
pub use transpiler::{parse_kotlin_file, transform};
