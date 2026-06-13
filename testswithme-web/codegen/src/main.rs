use std::{
    env,
    path::{Path, PathBuf},
    process,
};

const DEFAULT_INPUT_DIRECTORY: &str = "testswithme-backend-api";
const DEFAULT_OUTPUT_FILE: &str = "backend-api/src/lib.rs";

fn main() {
    if let Err(error) = run() {
        eprintln!("codegen: {error}");
        process::exit(1);
    }
}

fn run() -> Result<(), String> {
    let mut arguments = env::args().skip(1);
    let workspace = Path::new(env!("CARGO_MANIFEST_DIR"))
        .parent()
        .ok_or_else(|| "Unable to locate the Cargo workspace".to_owned())?;
    let repository = workspace
        .parent()
        .ok_or_else(|| "Unable to locate the repository root".to_owned())?;
    let default_input = repository.join(DEFAULT_INPUT_DIRECTORY);
    let default_output = workspace.join(DEFAULT_OUTPUT_FILE);
    let mut input = default_input.clone();
    let mut output = default_output.clone();

    while let Some(argument) = arguments.next() {
        match argument.as_str() {
            "--input" => {
                input = PathBuf::from(
                    arguments
                        .next()
                        .ok_or_else(|| "--input requires a path".to_owned())?,
                );
            }
            "--output" => {
                output = PathBuf::from(
                    arguments
                        .next()
                        .ok_or_else(|| "--output requires a path".to_owned())?,
                );
            }
            "--help" | "-h" => {
                println!(
                    "Usage: codegen [--input <directory>] [--output <file>]\n\
                     Defaults:\n  --input {}\n  --output {}",
                    default_input.display(),
                    default_output.display()
                );
                return Ok(());
            }
            _ => return Err(format!("Unknown argument: {argument}")),
        }
    }

    let generated_count = codegen::generate(&input, &output)?;
    println!(
        "Generated {generated_count} declarations in {}",
        output.display()
    );
    Ok(())
}
