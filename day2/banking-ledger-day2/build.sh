#!/bin/bash
# Build banking-ledger-day2. Run from project dir or by full path.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
if [[ ! -f pom.xml ]]; then
  echo "[ERROR] pom.xml not found. Run setup.sh from day2 first." >&2
  exit 1
fi
echo "[INFO] Building banking-ledger-day2..."
mvn clean install -q
echo "[SUCCESS] Build complete."
