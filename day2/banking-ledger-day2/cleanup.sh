#!/bin/bash
# Stop containers, remove unused Docker resources, and clean project artifacts.
# Run from banking-ledger-day2 or by full path. Use with care: removes containers and prunes Docker.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

echo "[INFO] Stopping all Docker containers..."
docker stop $(docker ps -aq) 2>/dev/null || true
echo "[INFO] Removing all Docker containers..."
docker rm -f $(docker ps -aq) 2>/dev/null || true

echo "[INFO] Removing banking-ledger-day2 Docker image (if present)..."
docker rmi -f banking-ledger-day2:latest banking-ledger-day2_image 2>/dev/null || true

echo "[INFO] Pruning unused Docker resources (images, containers, volumes, networks)..."
docker system prune -af --volumes 2>/dev/null || true

echo "[INFO] Removing project artifacts (node_modules, venv, .pytest_cache, .pyc, Istio)..."
for d in node_modules venv .venv .pytest_cache __pycache__ istio; do
  find "$SCRIPT_DIR" -type d -name "$d" 2>/dev/null | while read -r path; do rm -rf "$path"; done
done
find "$SCRIPT_DIR" -name "*.pyc" -delete 2>/dev/null || true
find "$SCRIPT_DIR" -name "*.pyo" -delete 2>/dev/null || true
find "$SCRIPT_DIR" -path "*istio*" -type f \( -name "*.yaml" -o -name "*.yml" \) -delete 2>/dev/null || true

echo "[SUCCESS] Cleanup complete."
