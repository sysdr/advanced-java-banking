#!/bin/bash
# Start the banking-ledger-core demo. Run from project dir or by full path. Run setup.sh first.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
if [[ ! -d out ]]; then
  echo "[ERROR] out/ not found. Run setup.sh from day3 first." >&2
  exit 1
fi
exec java -cp out com.bank.ledger.MainApp "$@"
