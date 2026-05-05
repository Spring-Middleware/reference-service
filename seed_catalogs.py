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
    "Everyday Tech Essentials",
    "Professional Office Setup",
    "Student Success Kit",
    "Hybrid Work Collection",
    "Compact Workspace Picks",
    "High Performance Gear",
    "Smart Productivity Tools",
    "Digital Nomad Essentials",
    "Streaming Starter Pack",
    "Photography Workflow Kit",
    "Video Editing Collection",
    "Podcast Studio Essentials",
    "Minimal Desk Setup",
    "Ergonomic Workstation",
    "Family Tech Favorites",
    "Connected Home Bundle",
    "Mobile Accessories Hub",
    "Cloud Learning Resources",
    "Software Starter Library",
    "Developer Productivity Pack",
    "Architecture Review Toolkit",
    "Backend Engineering Bundle",
    "Frontend Design Collection",
    "Data & Analytics Essentials",
    "AI Experimentation Kit",
    "Cybersecurity Basics Pack",
    "Networking Essentials Catalog",
    "Storage & Backup Selection",
    "Power User Toolkit",
    "Quiet Workspace Collection",
    "Meeting Room Essentials",
    "Small Business Tech Pack",
    "Enterprise Starter Catalog",
    "Open Source Developer Kit",
    "Code Review Essentials",
    "System Design Library",
    "Agile Team Toolkit",
    "Remote Collaboration Pack",
    "Onboarding Resources Hub",
    "Performance Testing Kit",
    "Observability Starter Pack",
    "DevOps Essentials Bundle",
    "Cloud Architecture Collection",
    "Kubernetes Learning Pack",
    "Microservices Toolkit",
    "Database Essentials Catalog",
    "API Design Resources",
    "GraphQL Developer Pack",
    "Event Driven Architecture Kit",
    "Kafka Learning Collection",
    "Spring Boot Essentials",
    "Java Developer Collection",
    "Testing & Quality Toolkit",
    "CI CD Productivity Pack",
    "Documentation Starter Hub",
    "Technical Writing Toolkit",
    "Knowledge Base Collection",
    "Research & Reading Pack",
    "Personal Productivity Kit",
    "Focus Mode Essentials",
    "Deep Work Collection",
    "Creative Writing Toolkit",
    "Design Thinking Resources",
    "UX Research Collection",
    "Marketing Content Bundle",
    "Ecommerce Operations Pack",
    "Customer Support Toolkit",
    "Sales Enablement Library",
    "Finance Office Essentials",
    "Legal Document Toolkit",
    "HR Onboarding Collection",
    "Recruiting Workflow Pack",
    "Training Materials Hub",
    "Workshop Facilitation Kit",
    "Conference Travel Pack",
    "Executive Workspace",
    "Premium Audio Setup",
    "Home Entertainment Picks",
    "Smart Lighting Collection",
    "Energy Saving Essentials",
    "Home Organization Tech",
    "Wellness & Focus Kit",
    "Fitness Tech Selection",
    "Kitchen Smart Gadgets",
    "Outdoor Mobility Pack",
    "Travel Charger Collection",
    "Laptop Upgrade Bundle",
    "Tablet Productivity Pack",
    "Printer & Scanner Essentials",
    "Backup Power Collection",
    "Cable Management Kit",
    "Budget Tech Deals",
    "Premium Tech Deals",
    "Last Minute Work Kit",
    "Holiday Gift Catalog",
    "New Year Productivity Pack",
]

CATALOG_DESCRIPTIONS_POOL = [
    "A curated selection of products designed for everyday use.",
    "Hand-picked items for professionals, creators, and modern teams.",
    "A balanced mix of physical and digital products for real-world workflows.",
    "A practical catalog focused on quality, usability, and value.",
    "Selected products for productivity, learning, and digital experiences.",
]

CATALOG_DESCRIPTIONS = [
    CATALOG_DESCRIPTIONS_POOL[i % len(CATALOG_DESCRIPTIONS_POOL)]
    for i in range(len(CATALOG_NAMES))
]

PHYSICAL_PRODUCTS = [
    ("Mechanical Keyboard", "High-performance mechanical keyboard designed for daily productivity and comfort.", "KEY"),
    ("Ergonomic Keyboard", "Split ergonomic keyboard designed to reduce wrist strain during long work sessions.", "EKB"),
    ("Compact Keyboard", "Space-saving keyboard optimized for small desks and portable work setups.", "CKB"),
    ("4K Monitor", "Ultra HD monitor with crisp image quality and wide viewing angles.", "MON"),
    ("UltraWide Monitor", "Wide-screen monitor designed for multitasking, dashboards, and creative workflows.", "UWM"),
    ("Portable Monitor", "Lightweight external monitor for travel, presentations, and mobile productivity.", "PMO"),
    ("Wireless Mouse", "Ergonomic wireless mouse with precise tracking and long battery life.", "MOU"),
    ("Vertical Mouse", "Vertical ergonomic mouse designed to improve wrist posture and comfort.", "VMO"),
    ("Trackpad", "Large precision trackpad for gesture-based navigation and desk setups.", "TPD"),
    ("USB-C Docking Station", "Docking station with multi-port connectivity for laptops and desktops.", "DOC"),
    ("Thunderbolt Hub", "High-speed hub for connecting displays, storage, and peripherals.", "HUB"),
    ("Laptop Stand", "Aluminum stand for better posture and desk organization.", "STD"),
    ("Adjustable Monitor Arm", "Flexible monitor arm for ergonomic positioning and clean workspaces.", "ARM"),
    ("Office Chair", "Ergonomic office chair built for long working sessions.", "CHR"),
    ("Standing Desk Converter", "Desktop converter for switching between sitting and standing work.", "SDC"),
    ("Noise Cancelling Headphones", "Over-ear headphones with active noise cancellation and premium sound.", "AUD"),
    ("Wireless Earbuds", "Compact earbuds with clear audio, charging case, and portable design.", "EBD"),
    ("Conference Speakerphone", "Speakerphone designed for clear team calls and meeting rooms.", "SPK"),
    ("Webcam Full HD", "Full HD webcam for video calls, streaming, and remote collaboration.", "CAM"),
    ("4K Webcam", "Ultra HD webcam with sharp video quality for professional calls and content creation.", "WBC"),
    ("Streaming Microphone", "USB microphone for podcasts, calls, streaming, and voice recording.", "MIC"),
    ("Portable SSD", "High-speed external SSD for backups and portable storage.", "SSD"),
    ("External Hard Drive", "Large-capacity external drive for backups, archives, and media storage.", "HDD"),
    ("NAS Storage Device", "Network storage device for shared files, backups, and team collaboration.", "NAS"),
    ("Wi-Fi Router", "High-performance router for stable home and office connectivity.", "NET"),
    ("Mesh Wi-Fi System", "Multi-node Wi-Fi system for wide coverage across homes and offices.", "MWS"),
    ("Ethernet Switch", "Compact network switch for expanding wired connectivity.", "SWT"),
    ("Desk Lamp", "LED desk lamp with adjustable brightness and modern design.", "LMP"),
    ("Smart Light Bulb", "Connected light bulb with adjustable color and brightness controls.", "BLB"),
    ("Smart Plug", "Connected plug for automating appliances and monitoring energy usage.", "PLG"),
    ("Graphic Tablet", "Precision drawing tablet for designers and digital artists.", "TAB"),
    ("Pen Display Tablet", "Display tablet for illustration, design, and creative production.", "PDT"),
    ("Color Calibration Tool", "Calibration device for accurate monitor color in design and media workflows.", "CAL"),
    ("Laptop Backpack", "Protective backpack with compartments for laptops, chargers, and accessories.", "BAG"),
    ("Travel Charger", "Compact multi-port charger for phones, tablets, and laptops.", "CHG"),
    ("Power Bank", "Portable battery pack for charging devices while traveling.", "PWB"),
    ("Cable Management Kit", "Organizer kit for keeping desk cables clean, accessible, and tidy.", "CBL"),
    ("Surge Protector", "Power strip with surge protection for office and home electronics.", "SRG"),
    ("UPS Battery Backup", "Backup power unit for protecting devices during outages and voltage drops.", "UPS"),
    ("Label Printer", "Compact printer for shipping labels, office organization, and inventory workflows.", "LBL"),
    ("All-in-One Printer", "Printer and scanner for documents, forms, and office workflows.", "PRN"),
    ("Document Scanner", "High-speed scanner for digitizing documents and receipts.", "SCN"),
    ("Whiteboard", "Magnetic whiteboard for planning, workshops, and team collaboration.", "WBD"),
    ("Desk Organizer", "Desktop organizer for stationery, devices, and workspace accessories.", "ORG"),
    ("Footrest", "Adjustable footrest for improving posture during seated work.", "FTR"),
    ("Monitor Privacy Filter", "Privacy filter that limits side-angle visibility on laptop and monitor screens.", "PRV"),
    ("Laptop Cooling Pad", "Cooling pad designed to improve airflow during intensive laptop usage.", "CLP"),
    ("Smart Thermostat", "Connected thermostat for managing comfort and energy efficiency.", "THR"),
    ("Security Camera", "Indoor security camera for monitoring rooms, entrances, and office spaces.", "SEC"),
    ("Video Doorbell", "Smart doorbell with video, motion detection, and remote notifications.", "VDB"),
    ("Portable Projector", "Compact projector for presentations, entertainment, and mobile meetings.", "PRJ"),
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
    ("Kubernetes Operations Manual", "Practical manual for deploying and operating Kubernetes workloads.", "pdf", "KOM"),
    ("Docker Compose Cookbook", "Collection of Docker Compose examples for local development environments.", "zip", "DCC"),
    ("Kafka Messaging Workshop", "Hands-on workshop covering Kafka producers, consumers, topics, and reliability patterns.", "zip", "KMW"),
    ("RabbitMQ Integration Guide", "Guide for building event-driven integrations with RabbitMQ.", "pdf", "RIG"),
    ("MongoDB Data Modeling Guide", "Digital guide focused on schema design and query patterns for MongoDB.", "pdf", "MDM"),
    ("Redis Caching Playbook", "Operational playbook for caching strategies, eviction policies, and Redis usage.", "pdf", "RCP"),
    ("REST API Design Handbook", "Reference handbook for designing consistent and maintainable REST APIs.", "pdf", "RAD"),
    ("OpenAPI Documentation Kit", "Templates and examples for documenting APIs with OpenAPI specifications.", "zip", "OAD"),
    ("CI CD Pipeline Templates", "Reusable pipeline templates for build, test, quality, and deployment automation.", "zip", "CIC"),
    ("Git Workflow Guide", "Practical guide for branching strategies, pull requests, and release workflows.", "pdf", "GIT"),
    ("Code Review Checklist Pack", "Checklist pack for improving code review quality and consistency.", "pdf", "CRC"),
    ("Architecture Decision Records Kit", "Templates and examples for documenting architecture decisions.", "zip", "ADR"),
    ("Observability Fundamentals Course", "Recorded course covering logs, metrics, traces, and production diagnostics.", "mp4", "OFC"),
    ("Performance Testing Workbook", "Workbook for planning and executing backend performance tests.", "pdf", "PTW"),
    ("Security Basics for Developers", "Developer-focused guide covering authentication, authorization, and secure coding basics.", "pdf", "SBD"),
    ("OAuth2 Client Credentials Guide", "Technical guide explaining service-to-service authentication using OAuth2 client credentials.", "pdf", "OCG"),
    ("Domain Driven Design Notes", "Structured notes about bounded contexts, aggregates, entities, and domain modeling.", "epub", "DDD"),
    ("Event Storming Workshop Pack", "Workshop materials for collaborative domain discovery and event storming sessions.", "zip", "ESW"),
    ("Agile Delivery Playbook", "Practical playbook for planning, refinement, delivery, and team rituals.", "pdf", "ADP"),
    ("Technical Leadership Handbook", "Handbook for technical leads managing architecture, delivery, and team alignment.", "pdf", "TLH"),
    ("Engineering Onboarding Pack", "Reusable onboarding materials for backend engineers joining a platform team.", "zip", "EOB"),
    ("Interview Preparation Course", "Recorded course for technical interviews, architecture discussions, and coding rounds.", "mp4", "IPC"),
    ("System Reliability Notes", "Notes covering resilience, retries, timeouts, fallbacks, and operational stability.", "pdf", "SRN"),
    ("Cloud Cost Optimization Guide", "Guide for identifying and reducing unnecessary cloud infrastructure costs.", "pdf", "CCO"),
    ("Infrastructure as Code Templates", "Reusable templates for provisioning cloud resources and environments.", "zip", "IAC"),
    ("Terraform Starter Kit", "Starter kit with examples for managing infrastructure using Terraform.", "zip", "TFK"),
    ("Backend Testing Cookbook", "Recipe-style guide for unit, integration, contract, and end-to-end backend tests.", "pdf", "BTC"),
    ("WireMock Examples Pack", "Collection of WireMock examples for HTTP stubbing and integration testing.", "zip", "WMP"),
    ("JUnit Mockito Workbook", "Practical workbook for writing maintainable unit tests with JUnit and Mockito.", "pdf", "JMW"),
    ("Reactive Programming Guide", "Guide covering reactive streams, backpressure, and asynchronous programming patterns.", "pdf", "RPG"),
    ("WebClient Integration Notes", "Notes focused on HTTP client usage, retries, error handling, and service calls.", "pdf", "WIN"),
    ("LLM Prompt Engineering Guide", "Guide for designing prompts, constraints, and context windows for LLM applications.", "pdf", "PEG"),
    ("RAG Implementation Playbook", "Playbook for chunking, embeddings, retrieval, and answer generation workflows.", "pdf", "RAG"),
    ("Vector Search Fundamentals", "Introduction to vector stores, similarity search, embeddings, and retrieval patterns.", "pdf", "VSF"),
    ("Qdrant Integration Guide", "Technical guide for collections, points, filters, payloads, and vector search in Qdrant.", "pdf", "QIG"),
    ("AI Documentation Assistant Kit", "Templates and examples for building documentation-aware AI assistants.", "zip", "AID"),
    ("Markdown Knowledge Base Pack", "Structured markdown examples for building searchable documentation repositories.", "zip", "MKB"),
    ("Data Transformation Workbook", "Workbook focused on transforming structured data into retrieval-friendly text.", "pdf", "DTW"),
    ("Product Catalog API Dataset", "Sample dataset and schemas for product catalog APIs and search experiments.", "zip", "PCD"),
    ("GraphQL Federation Notes", "Notes covering schema composition, service ownership, and distributed GraphQL design.", "pdf", "GFN"),
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

STATUSES = ["ACTIVE","INACTIVE"]
CURRENCIES = ["EUR","USD", "GBP"]
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


def pick_from_pool(pool: list, requested_total: int, index: int):
    if requested_total <= len(pool):
        return pool[index % requested_total]

    return random.choice(pool)


def next_sku(prefix: str) -> str:
    global sku_counter
    sku = f"{prefix}-{sku_counter:06d}"
    sku_counter += 1
    return sku


def catalog_name_for(i: int, total_catalogs: int) -> str:
    base = pick_from_pool(CATALOG_NAMES, total_catalogs, i - 1)
    return f"{base} #{i:03d}"


def catalog_description_for(i: int, total_catalogs: int) -> str:
    return pick_from_pool(CATALOG_DESCRIPTIONS, total_catalogs, i - 1)


def random_price(min_value: float, max_value: float) -> float:
    return float(Decimal(str(random.uniform(min_value, max_value))).quantize(Decimal("0.01")))


def graphql_string(value: str) -> str:
    return value.replace("\\", "\\\\").replace('"', '\\"').replace("\n", "\\n")


def create_catalog(i: int, total_catalogs: int, graphql_url: str) -> dict:
    name = catalog_name_for(i, total_catalogs)
    description = catalog_description_for(i, total_catalogs)

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


def build_physical_input(product_index: int, total_physical_products: int):
    name, description, sku_template = pick_from_pool(
        PHYSICAL_PRODUCTS,
        total_physical_products,
        product_index,
    )

    prefix = sku_template.split("-")[0]

    return {
        "name": name,
        "description": description,
        "priceAmount": random_price(24.90, 899.90),
        "priceCurrency": random.choice(CURRENCIES),
        "sku": next_sku(prefix),
        "status": random.choice(STATUSES),
        "stockQuantity": random.randint(5, 250),
        "dimensions": {
            "length": round(random.uniform(8.0, 65.0), 1),
            "width": round(random.uniform(8.0, 55.0), 1),
            "height": round(random.uniform(2.0, 40.0), 1),
        },
    }


def build_digital_input(product_index: int, total_digital_products: int):
    name, description, file_format, sku_template = pick_from_pool(
        DIGITAL_PRODUCTS,
        total_digital_products,
        product_index,
    )

    prefix = sku_template.split("-")[0]
    safe_name = name.lower().replace(" ", "-")
    file_size = random.randint(512, 25000)

    return {
        "name": name,
        "description": description,
        "priceAmount": random_price(9.90, 199.90),
        "priceCurrency": random.choice(CURRENCIES),
        "sku": next_sku(prefix),
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

        reviews.append({
            "rating": rating,
            "comment": random.choice(REVIEW_COMMENTS[rating]),
        })

    return reviews


def render_physical_inputs(inputs: list[dict]) -> str:
    return ",\n".join(f"""
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
    """ for p in inputs)


def render_digital_inputs(inputs: list[dict]) -> str:
    return ",\n".join(f"""
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
    """ for p in inputs)


def render_review_inputs(inputs: list[dict]) -> str:
    return ",\n".join(f"""
        {{
            rating: {r['rating']}
            comment: "{graphql_string(r['comment'])}"
        }}
    """ for r in inputs)


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
            products(sort: null) {{
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
    return gql(mutation, graphql_url)["addPhysicalProductsToCatalog"].get("products", [])


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
            products(sort: null) {{
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
    return gql(mutation, graphql_url)["addDigitalProductsToCatalog"].get("products", [])


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
        catalog = create_catalog(i, total_catalogs, graphql_url)
        catalog_id = catalog["id"]

        total_products = random.randint(min_products, max_products)
        physical_count, digital_count = split_product_counts(total_products)

        physical_inputs = [
            build_physical_input(product_index, physical_count)
            for product_index in range(physical_count)
        ]

        digital_inputs = [
            build_digital_input(product_index, digital_count)
            for product_index in range(digital_count)
        ]

        products_after_physical = add_physical_products(catalog_id, physical_inputs, graphql_url)
        all_created_products = add_digital_products(catalog_id, digital_inputs, graphql_url)

        if not digital_inputs:
            all_created_products = products_after_physical

        created_product_count = len(all_created_products)
        catalog_reviews_created = 0
        products_with_reviews = 0

        for product in all_created_products:
            reviews = build_review_inputs(min_reviews, max_reviews)
            if reviews:
                created_reviews = create_reviews_for_product(product["id"], reviews, graphql_url)
                catalog_reviews_created += len(created_reviews)
                products_with_reviews += 1

        total_products_created += created_product_count
        total_reviews_created += catalog_reviews_created

        print(
            f"[{i:03d}/{total_catalogs}] Created catalog '{catalog['name']}' "
            f"with {len(physical_inputs)} physical, {len(digital_inputs)} digital, "
            f"{created_product_count} products, "
            f"{products_with_reviews} products with reviews, "
            f"{catalog_reviews_created} reviews total"
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