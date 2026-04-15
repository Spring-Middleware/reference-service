import random
import requests
import time
from decimal import Decimal

GRAPHQL_URL = "http://localhost:8060/graphql"

CATALOG_NAMES = [
    "Summer Essentials 2026",
    "Home Office Collection",
    "Back to School Picks",
    "Digital Learning Hub",
    "Gaming Setup Favorites",
    "Creative Studio Toolkit",
    "Remote Work Must-Haves",
    "Smart Home Selection",
    "Productivity Boosters",
    "Weekend Tech Deals",
    "Business Essentials",
    "Design & Content Pack",
    "Premium Workspace",
    "Developer Gear Collection",
    "Modern Living Catalog",
    "Travel & Mobility Picks",
    "Education Resources Library",
    "Content Creator Bundle",
    "Audio & Media Selection",
    "Spring Refresh Collection",
]

CATALOG_DESCRIPTIONS = [
    "A curated selection of products designed for everyday use.",
    "Hand-picked items for professionals, creators, and modern teams.",
    "A balanced mix of physical and digital products for real-world workflows.",
    "A practical catalog focused on quality, usability, and value.",
    "Selected products for productivity, learning, and digital experiences.",
]

PHYSICAL_PRODUCTS = [
    ("Mechanical Keyboard", "High-performance mechanical keyboard designed for daily productivity and comfort.", "KEY"),
    ("4K Monitor", "Ultra HD monitor with crisp image quality and wide viewing angles.", "MON"),
    ("Wireless Mouse", "Ergonomic wireless mouse with precise tracking and long battery life.", "MOU"),
    ("USB-C Docking Station", "Docking station with multi-port connectivity for laptops and desktops.", "DOC"),
    ("Noise Cancelling Headphones", "Over-ear headphones with active noise cancellation and premium sound.", "AUD"),
    ("Office Chair", "Ergonomic office chair built for long working sessions.", "CHR"),
    ("Laptop Stand", "Aluminum stand for better posture and desk organization.", "STD"),
    ("Webcam Full HD", "Full HD webcam for video calls, streaming, and remote collaboration.", "CAM"),
    ("Portable SSD", "High-speed external SSD for backups and portable storage.", "SSD"),
    ("Wi-Fi Router", "High-performance router for stable home and office connectivity.", "NET"),
    ("Desk Lamp", "LED desk lamp with adjustable brightness and modern design.", "LMP"),
    ("Graphic Tablet", "Precision drawing tablet for designers and digital artists.", "TAB"),
]

DIGITAL_PRODUCTS = [
    ("Java Design Patterns Guide", "Comprehensive digital guide covering practical design patterns in Java applications.", "pdf", "JDG"),
    ("Spring Boot Architecture Handbook", "Technical handbook focused on scalable Spring Boot service design.", "pdf", "SBH"),
    ("Microservices Fundamentals Course", "On-demand course introducing microservice architecture and best practices.", "mp4", "MSC"),
    ("GraphQL API Workshop", "Workshop package covering GraphQL schema design and implementation.", "zip", "GQL"),
    ("System Design Cheatsheet", "Condensed reference for backend system design interviews and architecture reviews.", "pdf", "SYS"),
    ("Developer Productivity Toolkit", "Digital toolkit with templates, checklists, and workflow assets.", "zip", "DPT"),
    ("Clean Code Reference Pack", "Collection of practical coding guidelines and examples.", "epub", "CCR"),
    ("Cloud Deployment Playbook", "Operational playbook for deploying cloud-native backend services.", "pdf", "CDP"),
    ("Testing Strategies Masterclass", "Recorded training focused on unit, integration, and contract testing.", "mp4", "TSM"),
    ("Distributed Systems Notes", "Structured notes on resilience, messaging, consistency, and observability.", "pdf", "DSN"),
]

STATUSES = ["ACTIVE"]
CURRENCIES = ["EUR"]
sku_counter = 1

def gql(query: str):
    response = requests.post(
        GRAPHQL_URL,
        json={"query": query},
        headers={"Content-Type": "application/json"},
        timeout=60,
    )
    response.raise_for_status()
    data = response.json()
    if "errors" in data:
        raise RuntimeError(data["errors"])
    return data["data"]

def next_sku(prefix: str) -> str:
    global sku_counter
    sku = f"{prefix}-{sku_counter:06d}"
    sku_counter += 1
    return sku

def random_catalog_name(i: int) -> str:
    base = random.choice(CATALOG_NAMES)
    return f"{base} #{i:03d}"

def random_catalog_description() -> str:
    return random.choice(CATALOG_DESCRIPTIONS)

def random_price(min_value: float, max_value: float) -> float:
    return float(Decimal(str(random.uniform(min_value, max_value))).quantize(Decimal("0.01")))

def create_catalog(i: int) -> dict:
    name = random_catalog_name(i)
    description = random_catalog_description()
    mutation = f"""
    mutation {{
        createCatalog(input: {{
            name: "{name}"
            description: "{description}"
            status: ACTIVE
        }}) {{
            id
            name
        }}
    }}
    """
    return gql(mutation)["createCatalog"]

def build_physical_input():
    name, description, code = random.choice(PHYSICAL_PRODUCTS)
    return {
        "name": name,
        "description": description,
        "priceAmount": random_price(24.90, 899.90),
        "priceCurrency": random.choice(CURRENCIES),
        "sku": next_sku(code),
        "status": random.choice(STATUSES),
        "stockQuantity": random.randint(5, 250),
        "dimensions": {
            "length": round(random.uniform(8.0, 65.0), 1),
            "width": round(random.uniform(8.0, 55.0), 1),
            "height": round(random.uniform(2.0, 40.0), 1),
        },
    }

def build_digital_input():
    name, description, file_format, code = random.choice(DIGITAL_PRODUCTS)
    safe_name = name.lower().replace(" ", "-")
    file_size = random.randint(512, 25000)
    return {
        "name": name,
        "description": description,
        "priceAmount": random_price(9.90, 199.90),
        "priceCurrency": random.choice(CURRENCIES),
        "sku": next_sku(code),
        "status": random.choice(STATUSES),
        "fileFormat": file_format,
        "fileSize": file_size,
        "downloadUrl": f"https://downloads.example.com/{safe_name}-{random.randint(1000,9999)}.{file_format}",
    }

def graphql_string(value: str) -> str:
    return value.replace("\\", "\\\\").replace('"', '\\"')

def render_physical_inputs(inputs: list[dict]) -> str:
    chunks = []
    for p in inputs:
        chunks.append(f"""
        {{
            name: "{graphql_string(p['name'])}"
            description: "{graphql_string(p['description'])}"
            priceAmount: {p['priceAmount']}
            priceCurrency: "{p['priceCurrency']}"
            sku: "{p['sku']}"
            status: {p['status']}
            stockQuantity: {p['stockQuantity']}
            dimensions: {{
                length: {p['dimensions']['length']}
                width: {p['dimensions']['width']}
                height: {p['dimensions']['height']}
            }}
        }}
        """)
    return ",\n".join(chunks)

def render_digital_inputs(inputs: list[dict]) -> str:
    chunks = []
    for p in inputs:
        chunks.append(f"""
        {{
            name: "{graphql_string(p['name'])}"
            description: "{graphql_string(p['description'])}"
            priceAmount: {p['priceAmount']}
            priceCurrency: "{p['priceCurrency']}"
            sku: "{p['sku']}"
            status: {p['status']}
            fileFormat: "{p['fileFormat']}"
            fileSize: {p['fileSize']}
            downloadUrl: "{graphql_string(p['downloadUrl'])}"
        }}
        """)
    return ",\n".join(chunks)

def add_physical_products(catalog_id: str, products: list[dict]):
    if not products:
        return
    mutation = f"""
    mutation {{
        addPhysicalProductsToCatalog(
            catalogId: "{catalog_id}"
            inputs: [
                {render_physical_inputs(products)}
            ]
        ) {{
            id
            name
        }}
    }}
    """
    gql(mutation)

def add_digital_products(catalog_id: str, products: list[dict]):
    if not products:
        return
    mutation = f"""
    mutation {{
        addDigitalProductsToCatalog(
            catalogId: "{catalog_id}"
            inputs: [
                {render_digital_inputs(products)}
            ]
        ) {{
            id
            name
        }}
    }}
    """
    gql(mutation)

def seed_catalogs(total_catalogs: int = 100):
    for i in range(1, total_catalogs + 1):
        catalog = create_catalog(i)
        catalog_id = catalog["id"]

        total_products = random.randint(10, 30)
        physical_count = random.randint(max(3, total_products // 3), total_products - 2)
        digital_count = total_products - physical_count

        physical_products = [build_physical_input() for _ in range(physical_count)]
        digital_products = [build_digital_input() for _ in range(digital_count)]

        add_physical_products(catalog_id, physical_products)
        add_digital_products(catalog_id, digital_products)

        print(
            f"[{i:03d}/{total_catalogs}] Created catalog '{catalog['name']}' "
            f"with {physical_count} physical and {digital_count} digital products"
        )

        time.sleep(0.05)

if __name__ == "__main__":
    seed_catalogs(100)