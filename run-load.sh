#!/usr/bin/env bash

set -euo pipefail

# --- Args ---
QUERY_FILE="${1:-}"
VARIABLES_FILE="${2:-}"

if [ -z "$QUERY_FILE" ]; then
  echo "Usage: ./run-load.sh <query.graphql> [variables.json]"
  exit 1
fi

if [ ! -f "$QUERY_FILE" ]; then
  echo "Error: query file not found: $QUERY_FILE"
  exit 1
fi

if [ -n "$VARIABLES_FILE" ] && [ ! -f "$VARIABLES_FILE" ]; then
  echo "Error: variables file not found: $VARIABLES_FILE"
  exit 1
fi

# --- Defaults ---
BASE_URL="${BASE_URL:-http://localhost:8060/graphql}"
AUTH_TOKEN="${AUTH_TOKEN:-}"
OPERATION_NAME="${OPERATION_NAME:-}"
REQUEST_TIMEOUT="${REQUEST_TIMEOUT:-60s}"

echo "Running k6 load test..."
echo "Query file: $QUERY_FILE"

if [ -n "$VARIABLES_FILE" ]; then
  echo "Variables file: $VARIABLES_FILE"
fi

if [ -n "$OPERATION_NAME" ]; then
  echo "Operation name: $OPERATION_NAME"
else
  echo "Operation name: <null>"
fi

echo "Base URL: $BASE_URL"
echo "Request timeout: $REQUEST_TIMEOUT"

# --- Run ---
k6 run \
  -e QUERY_FILE="$QUERY_FILE" \
  -e VARIABLES_FILE="$VARIABLES_FILE" \
  -e BASE_URL="$BASE_URL" \
  -e AUTH_TOKEN="$AUTH_TOKEN" \
  -e OPERATION_NAME="$OPERATION_NAME" \
  -e REQUEST_TIMEOUT="$REQUEST_TIMEOUT" \
  ./graphql-load.js