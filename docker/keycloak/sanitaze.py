import json
import sys
from pathlib import Path


def sanitize_realm(input_file: Path, output_file: Path):
    with input_file.open("r", encoding="utf-8") as f:
        data = json.load(f)

    # --- Sanitizar Key Providers ---
    components = data.get("components", {})
    key_providers = components.get("org.keycloak.keys.KeyProvider", [])

    for provider in key_providers:
        config = provider.get("config", {})

        if "privateKey" in config:
            config["privateKey"] = ["DEV_PRIVATE_KEY"]

        if "secret" in config:
            config["secret"] = ["DEV_SECRET"]

        if "certificate" in config:
            config["certificate"] = ["DEV_CERTIFICATE"]

    # --- Sanitizar secrets de clientes ---
    for client in data.get("clients", []):
        if "secret" in client:
            client_id = client.get("clientId", "client").upper().replace("-", "_")
            client["secret"] = f"{client_id}_DEV_SECRET"

    # --- Guardar ---
    with output_file.open("w", encoding="utf-8") as f:
        json.dump(data, f, indent=2, ensure_ascii=False)

    print(f"✔ Sanitized file written to: {output_file}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python sanitize_realm.py <realm-export.json>")
        sys.exit(1)

    input_path = Path(sys.argv[1])

    if not input_path.exists():
        print(f"❌ File not found: {input_path}")
        sys.exit(1)

    output_path = input_path.with_name(
        f"{input_path.stem}_sanitized{input_path.suffix}"
    )

    sanitize_realm(input_path, output_path)