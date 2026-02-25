#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/build"
MAIN_CLASS="com.ledger.app.LedgerApp"

if [[ ! -d "$BUILD_DIR" ]]; then
  echo "[ERROR] Build directory not found: $BUILD_DIR. Run setup.sh first." >&2
  exit 1
fi
if ! find "$BUILD_DIR" -name 'LedgerApp.class' -type f | grep -q .; then
  echo "[ERROR] LedgerApp.class not found in $BUILD_DIR. Run setup.sh first." >&2
  exit 1
fi

cd "$SCRIPT_DIR"
exec java -cp "$BUILD_DIR" "$MAIN_CLASS" "$@"
