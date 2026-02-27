#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONTAINER_NAME="banking-ledger-fairlock-app"
if docker ps -a --format '{{.Names}}' 2>/dev/null | grep -qx "$CONTAINER_NAME"; then
  echo "Stopping and removing: $CONTAINER_NAME"
  docker rm -f "$CONTAINER_NAME" 2>/dev/null || true
fi
echo "Containers cleaned up."
