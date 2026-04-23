import argparse
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

REVIEW_COMMENTS = {
    5: [
        "Excellent product!",
        "Fantastic quality and very useful.",
        "Really happy with this purchase.",
        "Exceeded expectations.",
        "Highly recommended.",
    ],
    4: [
        "Very good, but could be improved.",
        "Works really well overall.",
        "Solid product with good value.",
        "Pretty satisfied with it.",
        "Good quality and easy to use.",
    ],
    3: [
        "It does the job.",
        "Average experience overall.",
        "Decent, but nothing special.",
        "Works fine for the price.",
        "Some good points, some weak ones.",
    ],
    2: [
        "Below expectations.",
        "Usable, but has noticeable issues.",
        "Would not buy again.",
        "Quality could be much better.",
        "Not very impressed.",
    ],
    1: [
        "Poor quality.",
        "Very disappointing.",
        "Had several issues with it.",
        "Would not recommend.",
        "Bad experience overall.",
    ],
}

STATUSES = ["ACTIVE"]
CURRENCIES = ["EUR"]
sku_counter = 1


def parse_args():
    parser = argparse.ArgumentParser(description="Seed catalogs, products and reviews via GraphQL.")
    parser.add_argument("--url", default=GRAPHQL_URL, help="GraphQL endpoint URL")
    parser.add_argument("--catalogs", type=int, default=100, help="Number of catalogs to create")
    parser.add_argument("--min-products", type=int, default=10, help="Minimum products per catalog")
    parser.add_argument("--max-products", type=int, default=30, help="Maximum products per catalog")
    parser.add_argument("--min-reviews", type=int, default=0, help="Minimum reviews per product")
    parser.add_argument("--max-reviews", type=int, default=5, help="Maximum reviews per product")
    parser.add_argument("--sleep-ms", type=int, default=50, help="Sleep between catalogs in milliseconds")
    return parser.parse_args()


def validate_args(args):
    if args.catalogs <= 0:
        raise ValueError("--catalogs must be > 0")
    if args.min_products < 1:
        raise ValueError("--min-products must be >= 1")
    if args.max_products < args.min_products:
        raise ValueError("--max-products must be >= --min-products")
    if args.min_reviews < 0:
        raise ValueError("--min-reviews must be >= 0")
    if args.max_reviews < args.min_reviews:
        raise ValueError("--max-reviews must be >= --min-reviews")
    if args.sleep_ms < 0:
        raise ValueError("--sleep-ms must be >= 0")


def gql(query: str, graphql_url: str):
    response = requests.post(
        graphql_url,
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


def graphql_string(value: str) -> str:
    return value.replace("\\", "\\\\").replace('"', '\\"').replace("\n", "\\n")


def create_catalog(i: int, graphql_url: str) -> dict:
    name = random_catalog_name(i)
    description = random_catalog_description()
    mutation = f"""
    mutation {{
        createCatalog(input: {{
            name: "{graphql_string(name)}"
            description: "{graphql_string(description)}"
            status: ACTIVE
        }}) {{
            id
            name
        }}
    }}
    """
    return gql(mutation, graphql_url)["createCatalog"]


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
        "downloadUrl": f"https://downloads.example.com/{safe_name}-{random.randint(1000, 9999)}.{file_format}",
    }


def build_review_inputs(min_reviews: int, max_reviews: int):
    review_count = random.randint(min_reviews, max_reviews)
    reviews = []

    for _ in range(review_count):
        rating = random.choices(
            population=[1, 2, 3, 4, 5],
            weights=[5, 10, 20, 30, 35],
            k=1,
        )[0]
        comment = random.choice(REVIEW_COMMENTS[rating])
        reviews.append({
            "rating": rating,
            "comment": comment,
        })

    return reviews


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


def render_review_inputs(inputs: list[dict]) -> str:
    chunks = []
    for r in inputs:
        chunks.append(f"""
        {{
            rating: {r['rating']}
            comment: "{graphql_string(r['comment'])}"
        }}
        """)
    return ",\n".join(chunks)


def add_physical_products(catalog_id: str, products: list[dict], graphql_url: str) -> list[dict]:
    if not products:
        return []

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
            products {{
                __typename
                ... on PhysicalProduct {{
                    id
                    name
                }}
                ... on DigitalProduct {{
                    id
                    name
                }}
            }}
        }}
    }}
    """
    result = gql(mutation, graphql_url)["addPhysicalProductsToCatalog"]
    return result.get("products", [])


def add_digital_products(catalog_id: str, products: list[dict], graphql_url: str) -> list[dict]:
    if not products:
        return []

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
            products {{
                __typename
                ... on PhysicalProduct {{
                    id
                    name
                }}
                ... on DigitalProduct {{
                    id
                    name
                }}
            }}
        }}
    }}
    """
    result = gql(mutation, graphql_url)["addDigitalProductsToCatalog"]
    return result.get("products", [])


def create_reviews_for_product(product_id: str, reviews: list[dict], graphql_url: str):
    if not reviews:
        return []

    mutation = f"""
    mutation {{
        createReviewsForProduct(
            productId: "{product_id}"
            inputs: [
                {render_review_inputs(reviews)}
            ]
        ) {{
            id
            rating
            comment
        }}
    }}
    """
    return gql(mutation, graphql_url)["createReviewsForProduct"]


def split_product_counts(total_products: int) -> tuple[int, int]:
    if total_products == 1:
        return 1, 0
    if total_products == 2:
        return 1, 1

    physical_count = random.randint(1, total_products - 1)
    digital_count = total_products - physical_count
    return physical_count, digital_count


def seed_catalogs(
        graphql_url: str,
        total_catalogs: int,
        min_products: int,
        max_products: int,
        min_reviews: int,
        max_reviews: int,
        sleep_ms: int,
):
    total_products_created = 0
    total_reviews_created = 0

    for i in range(1, total_catalogs + 1):
        catalog = create_catalog(i, graphql_url)
        catalog_id = catalog["id"]

        total_products = random.randint(min_products, max_products)
        physical_count, digital_count = split_product_counts(total_products)

        physical_inputs = [build_physical_input() for _ in range(physical_count)]
        digital_inputs = [build_digital_input() for _ in range(digital_count)]

        add_physical_products(catalog_id, physical_inputs, graphql_url)
        all_created_products = add_digital_products(catalog_id, digital_inputs, graphql_url)

        created_physical_count = len(physical_inputs)
        created_digital_count = len(digital_inputs)

        catalog_reviews_created = 0
        products_with_reviews = 0

        for product in all_created_products:
            reviews = build_review_inputs(min_reviews, max_reviews)
            if reviews:
                created_reviews = create_reviews_for_product(product["id"], reviews, graphql_url)
                catalog_reviews_created += len(created_reviews)
                products_with_reviews += 1

        total_products_created += len(all_created_products)
        total_reviews_created += catalog_reviews_created

        print(
            f"[{i:03d}/{total_catalogs}] Created catalog '{catalog['name']}' "
            f"with {created_physical_count} physical, {created_digital_count} digital, "
            f"{len(all_created_products)} products with reviews, {catalog_reviews_created} reviews total"
        )

        if sleep_ms > 0:
            time.sleep(sleep_ms / 1000.0)

    print()
    print("Seeding finished")
    print(f"Catalogs created: {total_catalogs}")
    print(f"Products created: {total_products_created}")
    print(f"Reviews created: {total_reviews_created}")


if __name__ == "__main__":
    args = parse_args()
    validate_args(args)

    seed_catalogs(
        graphql_url=args.url,
        total_catalogs=args.catalogs,
        min_products=args.min_products,
        max_products=args.max_products,
        min_reviews=args.min_reviews,
        max_reviews=args.max_reviews,
        sleep_ms=args.sleep_ms,
    )