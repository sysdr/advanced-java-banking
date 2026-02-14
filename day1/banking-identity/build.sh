#!/bin/bash
# Build banking-identity project. Run from banking-identity or by full path.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [[ ! -f pom.xml ]]; then
  echo "[ERROR] pom.xml not found. Run ../setup.sh from day1 first." >&2
  exit 1
fi

echo "[INFO] Building banking-identity..."
mvn clean install -q
echo "[SUCCESS] Build complete."
