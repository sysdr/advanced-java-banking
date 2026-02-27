#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src/main/java"
BUILD_DIR="$SCRIPT_DIR/build"

if [[ ! -d "$SRC_DIR" ]]; then
  echo "[ERROR] Source directory not found: $SRC_DIR. Run ../setup.sh first." >&2
  exit 1
fi

if ! find "$SRC_DIR" -name "*.java" -print | grep -q .; then
  echo "[ERROR] No Java sources in $SRC_DIR. Run ../setup.sh first." >&2
  exit 1
fi

mkdir -p "$BUILD_DIR"
cd "$SCRIPT_DIR"
find "$SRC_DIR" -name "*.java" > sources.txt
if ! javac -d "$BUILD_DIR" -cp "$BUILD_DIR" @sources.txt; then
  rm -f sources.txt
  echo "[ERROR] Compilation failed." >&2
  exit 1
fi
rm -f sources.txt
echo "[SUCCESS] Build complete. Classes in $BUILD_DIR"
