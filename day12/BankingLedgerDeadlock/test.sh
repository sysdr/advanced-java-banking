#!/bin/bash
# Test: run demos and verify expected output (safe demos complete successfully).
set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
START_SH="$SCRIPT_DIR/start.sh"
SETUP_SH="$SCRIPT_DIR/../setup.sh"

# Ensure build exists (setup.sh lives in parent day12 directory)
if [[ ! -d "$SCRIPT_DIR/build" ]] || ! find "$SCRIPT_DIR/build" -name "*.class" -print | grep -q .; then
  echo "Build not found. Running setup..."
  bash "$SETUP_SH" >/dev/null 2>&1
fi

if [[ ! -x "$START_SH" ]]; then
  echo "FAIL: start.sh not found or not executable."
  exit 1
fi

echo "Running demos via start.sh..."
OUTPUT="$(bash "$START_SH" 2>&1)"
EXIT=$?

if [[ $EXIT -ne 0 ]]; then
  echo "FAIL: start.sh exited with $EXIT"
  echo "$OUTPUT"
  exit 1
fi

# Demo 2 (Deadlock Prevented) must show success
if ! echo "$OUTPUT" | grep -q "DEMO 2: Safe transfers completed successfully"; then
  echo "FAIL: Expected output to contain 'DEMO 2: Safe transfers completed successfully'"
  echo "$OUTPUT"
  exit 1
fi

# Demo 3 must finish
if ! echo "$OUTPUT" | grep -q "DEMO 3: tryLock transfers finished"; then
  echo "FAIL: Expected output to contain 'DEMO 3: tryLock transfers finished'"
  echo "$OUTPUT"
  exit 1
fi

# Balances correct in Demo 2
if ! echo "$OUTPUT" | grep -q "Balances are correct"; then
  echo "FAIL: Expected 'Balances are correct' from Demo 2"
  echo "$OUTPUT"
  exit 1
fi

echo "PASS: All demos ran and produced expected output."
