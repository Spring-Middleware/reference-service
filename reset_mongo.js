use("catalog");
if (db.getCollectionNames().includes("catalogs")) {
    db.catalogs.drop();
    print("Dropped collection: catalog.catalogs");
} else {
    print("Collection not found: catalog.catalogs");
}

use("product");
if (db.getCollectionNames().includes("products")) {
    db.products.drop();
    print("Dropped collection: product.products");
} else {
    print("Collection not found: product.products");
}

use("review");
if (db.getCollectionNames().includes("reviews")) {
    db.reviews.drop();
    print("Dropped collection: review.reviews");
} else {
    print("Collection not found: review.reviews");
}