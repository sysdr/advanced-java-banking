#!/bin/bash
# Run from any location using full path: /path/to/banking-ledger-core/run.sh
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
exec java -cp out com.bank.ledger.MainApp "$@"
