#!/bin/bash
# Build ledger-precision-demo. Run from project dir or by full path. Run setup.sh from day4 first to generate sources.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
if [[ ! -f pom.xml ]]; then
  echo "[ERROR] pom.xml not found. Run setup.sh from day4 first." >&2
  exit 1
fi
echo "[INFO] Building ledger-precision-demo..."
mvn clean package -DskipTests -q
echo "[SUCCESS] Build complete."
