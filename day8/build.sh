#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="banking-ledger-validation"
if [[ ! -d "$SCRIPT_DIR/$PROJECT_NAME" ]] || [[ ! -f "$SCRIPT_DIR/$PROJECT_NAME/pom.xml" ]]; then
  echo "[ERROR] Project not found. Run setup.sh first." >&2
  exit 1
fi
cd "$SCRIPT_DIR/$PROJECT_NAME"
mvn clean install
echo "[SUCCESS] Build complete."
