#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT="$ROOT/target"
SDK="$ROOT/sdklib/build"
SRC_ROOT="$ROOT/src/main/java"

mkdir -p "$OUT/classes"
rm -rf "$SDK" "$OUT/classes"
mkdir -p "$SDK" "$OUT/classes"

mapfile -d '' sdk_files < <(find "$ROOT/sdklib/src" -type f -name '*.java' -print0)
if (( ${#sdk_files[@]} > 0 )); then
  javac -d "$SDK" "${sdk_files[@]}"
fi

mapfile -d '' src_files < <(find "$SRC_ROOT" -type f -name '*.java' -print0)
if (( ${#src_files[@]} == 0 )); then
  echo "No Java source files found under $SRC_ROOT"
  exit 1
fi

javac -cp "$SDK" -d "$OUT/classes" "${src_files[@]}"

jar cf "$OUT/demo-reverse-encoder-1.0.0.jar" -C "$OUT/classes" .

rm -rf "$SDK"
echo "Built: $OUT/demo-reverse-encoder-1.0.0.jar"
