#!/usr/bin/env bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PYTHON_SCRIPT="$SCRIPT_DIR/seed_catalogs.py"

function print_help() {
  cat <<EOF

Seed catalogs, products and reviews via GraphQL (wrapper script)

USAGE:
  ./seed_catalogs.sh [options]

OPTIONS:
  --url <url>                GraphQL endpoint (default: http://localhost:8060/graphql)
  --catalogs <n>             Number of catalogs to create (default: 100)
  --min-products <n>         Minimum products per catalog (default: 10)
  --max-products <n>         Maximum products per catalog (default: 30)
  --min-reviews <n>          Minimum reviews per product (default: 0)
  --max-reviews <n>          Maximum reviews per product (default: 5)
  --sleep-ms <ms>            Delay between catalogs in ms (default: 50)

EXAMPLES:

  # Default run
  ./seed_catalogs.sh

  # Heavy load
  ./seed_catalogs.sh --catalogs 1000 --min-products 50 --max-products 100

  # No reviews
  ./seed_catalogs.sh --min-reviews 0 --max-reviews 0

  # Custom endpoint
  ./seed_catalogs.sh --url http://localhost:8080/graphql

EOF
}

# Show help
if [[ "$1" == "--help" || "$1" == "-h" ]]; then
  print_help
  exit 0
fi

# Check python
if ! command -v python3 &> /dev/null; then
  echo "❌ python3 is not installed"
  exit 1
fi

# Check script exists
if [[ ! -f "$PYTHON_SCRIPT" ]]; then
  echo "❌ Python script not found: $PYTHON_SCRIPT"
  exit 1
fi

echo "🚀 Running catalog seeder..."
echo "📄 Script: $PYTHON_SCRIPT"
echo

# Execute python script passing all args
python3 "$PYTHON_SCRIPT" "$@"