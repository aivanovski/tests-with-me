#!/usr/bin/env sh

cd api

output=$(mill main.assembly 2> /dev/null)

java -jar ./out/main/assembly.dest/out.jar "$@"