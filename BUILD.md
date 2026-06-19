# Build Instructions

## Generate Rust API DTOs

Run the code generator from the repository root:

```shell
cargo run -p testswithme-api-codegen
```

To use custom paths:

```shell
cargo run -p testswithme-api-codegen -- \
  --input <kotlin-source-path> \
  --output <rust-output-path>
```

Check that the generated Rust crate compiles:

```shell
cargo check -p testswithme-api-rust
```
