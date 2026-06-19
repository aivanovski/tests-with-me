use std::collections::VecDeque;
use std::fs;
use std::path::{Path, PathBuf};

pub fn read_file(path: &Path) -> Result<String, String> {
    return fs::read_to_string(path)
        .map_err(|error| format!("Unable to read file {}: {}", path.display(), error));
}

pub fn write_file(path: &Path, content: &str) -> Result<(), String> {
    if let Some(parent) = path.parent() {
        fs::create_dir_all(parent)
            .map_err(|error| format!("Unable to create {}: {error}", parent.display()))?;
    }

    fs::write(path, content)
        .map_err(|error| format!("Unable to write {}: {error}", path.display()))
}

pub fn collect_kotlin_files(root: &Path) -> Result<Vec<PathBuf>, String> {
    let mut kotlin_files = vec![];

    let mut directories = VecDeque::new();
    directories.push_back(root.to_path_buf());

    while !directories.is_empty() {
        let path = directories
            .pop_front()
            .ok_or_else(|| "Unable to read directory".to_string())?;

        let files = fs::read_dir(&path)
            .map_err(|error| format!("Unable to read directory {}: {}", path.display(), error))?;

        for file in files {
            let file_path = file.map(|file| file.path()).map_err(|error| {
                format!("Unable to read directory {}: {}", path.display(), error)
            })?;

            if file_path.is_dir() {
                directories.push_back(file_path);
            } else {
                let extension = file_path
                    .extension()
                    .ok_or_else(|| "Unable to get extension")?;

                if extension == "kt" {
                    kotlin_files.push(file_path);
                }
            }
        }
    }

    Ok(kotlin_files)
}
