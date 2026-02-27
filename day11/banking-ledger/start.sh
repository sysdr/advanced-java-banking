#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/target"
MAIN_CLASS="com.bank.ledger.LedgerSimulator"

if [[ ! -d "$BUILD_DIR" ]]; then
  echo "[ERROR] Build directory not found: $BUILD_DIR. Run ../setup.sh or ./build.sh first." >&2
  exit 1
fi
if ! find "$BUILD_DIR" -name 'LedgerSimulator.class' -type f | grep -q .; then
  echo "[ERROR] LedgerSimulator.class not found in $BUILD_DIR. Run ../setup.sh or ./build.sh first." >&2
  exit 1
fi

cd "$SCRIPT_DIR"
exec java -cp "$BUILD_DIR" "$MAIN_CLASS" "$@"
