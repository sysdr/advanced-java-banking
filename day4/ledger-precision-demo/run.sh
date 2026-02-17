#!/bin/bash
# Run from any location using full path. Runs demo mode (non-interactive).
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
exec java -jar "target/ledger-precision-demo-1.0-SNAPSHOT-jar-with-dependencies.jar" --demo "$@"
