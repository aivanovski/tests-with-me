#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
API_DIR="$SCRIPT_DIR/api"
JAR_PATH="$API_DIR/target/api-client.jar"

cd "$API_DIR"
if [ ! -f "$JAR_PATH" ]; then
  sbt --batch --error assembly
fi

exec java -jar "$JAR_PATH" "$@"
