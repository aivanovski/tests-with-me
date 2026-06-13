# Build Instructions

## Generate Rust API DTOs

Run the code generator from the repository root:

```shell
cargo run -p testswithme-api-codegen
```

The generator reads Kotlin DTOs, requests, and responses from
`testswithme-backend-api` and writes the generated Rust declarations to
`testswithme-api-rust/src/lib.rs`.

To use custom paths:

```shell
cargo run -p testswithme-api-codegen -- \
  --input <kotlin-source-directory> \
  --output <rust-output-file>
```

Check that the generated Rust crate compiles:

```shell
cargo check -p testswithme-api-rust
```
