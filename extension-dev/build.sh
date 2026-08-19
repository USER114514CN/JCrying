#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT="$ROOT/target"
SDK="$ROOT/sdklib/build"
SRC_ROOT="$ROOT/src/main/java"

# 临时源文件列表
SDK_LIST="${ROOT}/sources-sdk.txt"
MAIN_LIST="${ROOT}/sources-main.txt"

# 清理旧产物 + 创建目录
rm -rf "$SDK" "$OUT/classes" "$SDK_LIST" "$MAIN_LIST"
mkdir -p "$SDK" "$OUT/classes"

# 生成 SDK 源码列表（每行一个绝对路径）
find "$ROOT/sdklib/src" -type f -name '*.java' > "$SDK_LIST"
if [[ -s "$SDK_LIST" ]]; then
  javac -d "$SDK" @"$SDK_LIST"
fi

# 生成主项目源码列表
find "$SRC_ROOT" -type f -name '*.java' > "$MAIN_LIST"
if [[ ! -s "$MAIN_LIST" ]]; then
  echo "No Java source files found under $SRC_ROOT"
  exit 1
fi

javac -cp "$SDK" -d "$OUT/classes" @"$MAIN_LIST"

# 打包为 packcore.jar
jar cf "$OUT/packcore.jar" -C "$OUT/classes" .

# 删除编译期SDK产物、临时列表
rm -rf "$SDK" "$SDK_LIST" "$MAIN_LIST"

echo "Built: $OUT/packcore.jar"
