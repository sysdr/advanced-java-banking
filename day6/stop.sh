#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
for c in mybank-ledger-app-instance; do
  if docker ps -a --format '{{.Names}}' 2>/dev/null | grep -qx "$c"; then
    echo "Stopping and removing: $c"
    docker rm -f "$c" 2>/dev/null || true
  fi
done
echo "Containers cleaned up."
