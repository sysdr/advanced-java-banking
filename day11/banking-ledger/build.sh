#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SRC_DIR="$SCRIPT_DIR/src/main/java/com/bank/ledger"
BUILD_DIR="$SCRIPT_DIR/target"

cd "$SCRIPT_DIR"
mkdir -p "$BUILD_DIR"
echo "Compiling Java sources..."
javac -d "$BUILD_DIR" "$SRC_DIR"/*.java
echo "Build successful."
