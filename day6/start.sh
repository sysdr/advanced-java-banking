#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR/LedgerSystem"
if [[ ! -f target/ledger-system-1.0-SNAPSHOT.jar ]]; then
  echo "[ERROR] target/ledger-system-1.0-SNAPSHOT.jar not found. Run setup.sh first." >&2
  exit 1
fi
exec java -jar "target/ledger-system-1.0-SNAPSHOT.jar" "$@"
