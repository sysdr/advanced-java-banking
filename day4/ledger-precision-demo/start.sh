#!/bin/bash
# Start the ledger-precision-demo. Run from project dir or by full path. Run setup.sh first.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
if [[ ! -f target/ledger-precision-demo-1.0-SNAPSHOT-jar-with-dependencies.jar ]]; then
  echo "[ERROR] target/ledger-precision-demo-1.0-SNAPSHOT-jar-with-dependencies.jar not found. Run setup.sh from day4 first." >&2
  exit 1
fi
exec java -jar "target/ledger-precision-demo-1.0-SNAPSHOT-jar-with-dependencies.jar" --demo "$@"
