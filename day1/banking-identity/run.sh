#!/bin/bash
# Run from any location using full path: /path/to/banking-identity/run.sh
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"
exec mvn -q exec:java -Dexec.mainClass=com.bank.identity.Main "$@"
