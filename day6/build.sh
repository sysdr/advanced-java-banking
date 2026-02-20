#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
if [[ ! -d "$SCRIPT_DIR/LedgerSystem" ]] || [[ ! -f "$SCRIPT_DIR/LedgerSystem/pom.xml" ]]; then
  echo "[ERROR] Project not found. Run setup.sh first." >&2
  exit 1
fi
cd "$SCRIPT_DIR/LedgerSystem"
mvn clean install
echo "[SUCCESS] Build complete."
