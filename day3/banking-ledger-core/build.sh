#!/bin/bash
# Build banking-ledger-core. Run from project dir or by full path. Run setup.sh from day3 first to generate sources.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
SRC_DIR="src/main/java/com/bank/ledger"
if [[ ! -d "$SRC_DIR" ]]; then
  echo "[ERROR] $SRC_DIR not found. Run setup.sh from day3 first." >&2
  exit 1
fi
echo "[INFO] Building banking-ledger-core..."
mkdir -p out
javac -d out "$SRC_DIR"/*.java
echo "[SUCCESS] Build complete."
