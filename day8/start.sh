#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_NAME="banking-ledger-validation"
JAR_NAME="banking-ledger-validation-1.0-SNAPSHOT-jar-with-dependencies.jar"
if [[ ! -f "$SCRIPT_DIR/$PROJECT_NAME/target/$JAR_NAME" ]]; then
  echo "[ERROR] $SCRIPT_DIR/$PROJECT_NAME/target/$JAR_NAME not found. Run setup.sh first." >&2
  exit 1
fi
cd "$SCRIPT_DIR/$PROJECT_NAME"
exec java -jar "target/$JAR_NAME" "$@"
