#!/usr/bin/env bash

set -e

# --- Args ---
QUERY_FILE=$1
VARIABLES_FILE=$2

if [ -z "$QUERY_FILE" ]; then
  echo "Usage: ./run-load.sh <query.graphql> [variables.json]"
  exit 1
fi

# --- Defaults ---
BASE_URL=${BASE_URL:-http://localhost:8060/graphql}
AUTH_TOKEN=${AUTH_TOKEN:-}

echo "Running k6 load test..."
echo "Query file: $QUERY_FILE"

if [ -n "$VARIABLES_FILE" ]; then
  echo "Variables file: $VARIABLES_FILE"
fi

# --- Export envs ---
export QUERY_FILE
export VARIABLES_FILE
export BASE_URL
export AUTH_TOKEN

# --- Run ---
k6 run ./graphql-load.js