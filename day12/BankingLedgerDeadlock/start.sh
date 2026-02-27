#!/bin/bash
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BUILD_DIR="$SCRIPT_DIR/build"
DEMO_PACKAGE="com.banking.ledger.demo"

if [[ ! -d "$BUILD_DIR" ]]; then
  echo "[ERROR] Build directory not found: $BUILD_DIR. Run ../setup.sh first." >&2
  exit 1
fi

if ! find "$BUILD_DIR" -name "*.class" -print | grep -q .; then
  echo "[ERROR] No compiled classes in $BUILD_DIR. Run ../setup.sh first." >&2
  exit 1
fi

# Run demos with full path (timeout for deadlock demo so script completes)
echo "Running Deadlock Demo (timeout 8s)..."
timeout 8 "$(command -v java)" -cp "$BUILD_DIR" "$DEMO_PACKAGE.DeadlockDemo" 2>&1 || true

echo ""
echo "Running Deadlock Prevented Demo..."
"$(command -v java)" -cp "$BUILD_DIR" "$DEMO_PACKAGE.DeadlockPreventedDemo" 2>&1

echo ""
echo "Running TryLock Demo..."
"$(command -v java)" -cp "$BUILD_DIR" "$DEMO_PACKAGE.TryLockDemo" 2>&1

echo ""
echo "All demos completed."
