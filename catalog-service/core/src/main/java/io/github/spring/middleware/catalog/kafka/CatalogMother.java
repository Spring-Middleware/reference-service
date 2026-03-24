package io.github.spring.middleware.catalog.kafka;

import io.github.spring.middleware.catalog.domain.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CatalogMother {

    private static final Random RANDOM = new Random();
    private static final String[] WORDS = new String[]{"Summer", "Winter", "Autumn", "Spring", "Promo", "Sale", "Clearance", "Exclusive", "Limited", "2026"};

    public static Catalog randomCatalog() {
        Catalog c = new Catalog();
        c.setId(UUID.randomUUID());
        c.setName(randomName());
        c.setDescription("Auto-generated catalog");
        c.setStatus(randomStatus());
        c.setCreatedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        return c;
    }

    public static Catalog randomCatalogWithProducts(int productCount) {
        Catalog c = randomCatalog();
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < productCount; i++) {
            ids.add(UUID.randomUUID());
        }
        c.setProductIds(ids);
        return c;
    }

    public static Product randomProduct() {
        if (RANDOM.nextBoolean()) {
            return randomDigitalProduct();
        }
        return randomPhysicalProduct();
    }

    public static DigitalProduct randomDigitalProduct() {
        DigitalProduct p = new DigitalProduct();
        p.setId(UUID.randomUUID());
        p.setName("Digital " + randomWord() + " " + RANDOM.nextInt(1000));
        p.setSku("DIGI-" + Math.abs(RANDOM.nextInt()));
        p.setStatus(ProductStatus.ACTIVE);
        Money money = new Money();
        money.setCurrency("EUR");
        money.setAmount(BigDecimal.valueOf(9.99 + RANDOM.nextDouble() * 100));
        p.setPrice(money);
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());
        p.setFileFormat("pdf");
        p.setFileSize(1024L + RANDOM.nextInt(10000));
        try {
            p.setDownloadUrl(new URI("https://example.com/download/" + p.getId()));
        } catch (Exception e) {
            // ignore
        }
        return p;
    }

    public static PhysicalProduct randomPhysicalProduct() {
        PhysicalProduct p = new PhysicalProduct();
        p.setId(UUID.randomUUID());
        p.setName("Physical " + randomWord() + " " + RANDOM.nextInt(1000));
        p.setSku("PHYS-" + Math.abs(RANDOM.nextInt()));
        p.setStatus(ProductStatus.ACTIVE);
        Money money = new Money();
        money.setCurrency("EUR");
        money.setAmount(BigDecimal.valueOf(19.99 + RANDOM.nextDouble() * 200));
        p.setPrice(money);
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());
        Dimensions d = new Dimensions();
        d.setLength(10.0 + RANDOM.nextDouble() * 50);
        d.setWidth(10.0 + RANDOM.nextDouble() * 50);
        d.setHeight(1.0 + RANDOM.nextDouble() * 30);
        p.setDimensions(d);
        p.setStockQuantity(1 + RANDOM.nextInt(200));
        p.setShippable(RANDOM.nextBoolean());
        return p;
    }

    private static String randomName() {
        return randomWord() + " " + randomWord() + " " + Math.abs(RANDOM.nextInt(1000));
    }

    private static String randomWord() {
        return WORDS[RANDOM.nextInt(WORDS.length)];
    }

    private static CatalogStatus randomStatus() {
        CatalogStatus[] values = CatalogStatus.values();
        return values[RANDOM.nextInt(values.length)];
    }
}

