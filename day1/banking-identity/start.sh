#!/bin/bash
# Start the banking-identity demo. Run from banking-identity or by full path. Run ./build.sh first if needed.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

if [[ ! -f pom.xml ]]; then
  echo "[ERROR] pom.xml not found. Run ../setup.sh from day1 first." >&2
  exit 1
fi

exec mvn -q exec:java -Dexec.mainClass=com.bank.identity.Main "$@"
