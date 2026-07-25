package com.fashionstore.bootstrap;

import com.fashionstore.entity.Product;
import com.fashionstore.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class SampleDataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;

    public SampleDataLoader(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() > 0) {
            return;
        }
        productRepository.save(Product.builder()
                .name("Linen Overshirt")
                .description("Lightweight neutral overshirt for spring layering.")
                .price(new BigDecimal("79.00"))
                .imageUrl("https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&auto=format&fit=crop")
                .category("shirts")
                .brand("atelier")
                .stock(40)
                .build());
        productRepository.save(Product.builder()
                .name("Tailored Wool Trousers")
                .description("High-rise straight leg in charcoal wool blend.")
                .price(new BigDecimal("129.00"))
                .imageUrl("https://images.unsplash.com/photo-1506629082955-511b1aa562c8?w=600&auto=format&fit=crop")
                .category("pants")
                .brand("noir")
                .stock(25)
                .build());
        productRepository.save(Product.builder()
                .name("Minimal Sneakers")
                .description("Leather upper, cushioned sole, everyday white.")
                .price(new BigDecimal("98.00"))
                .imageUrl("https://images.unsplash.com/photo-1549298916-b41d501d3772?w=600&auto=format&fit=crop")
                .category("shoes")
                .brand("stride")
                .stock(60)
                .build());
        productRepository.save(Product.builder()
                .name("Merino Crewneck")
                .description("Soft merino knit in forest green.")
                .price(new BigDecimal("68.00"))
                .imageUrl("https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&auto=format&fit=crop")
                .category("knitwear")
                .brand("atelier")
                .stock(35)
                .build());
        productRepository.save(Product.builder()
                .name("Crossbody Bag")
                .description("Compact leather crossbody with adjustable strap.")
                .price(new BigDecimal("145.00"))
                .imageUrl("https://images.unsplash.com/photo-1590874103328-eac38a683ce7?w=600&auto=format&fit=crop")
                .category("accessories")
                .brand("noir")
                .stock(20)
                .build());
                productRepository.save(Product.builder()
        .name("Wireless Headphones")
        .description("Noise-cancelling over-ear headphones.")
        .price(new BigDecimal("3499.00"))
       .imageUrl("https://images.unsplash.com/photo-1518441902117-66a5a0c3f7b4?w=600&auto=format&fit=crop")
        .category("electronics")
        .brand("sony")
        .stock(25)
        .build());
//         productRepository.save(Product.builder()
//         .name("UV Protection Sunglasses")
//         .description("Stylish sunglasses with UV400 protection.")
//         .price(new BigDecimal("999.00"))
//         .imageUrl("https://images.unsplash.com/photo-1511499767150-a48a237f0083?w=600&auto=format&fit=crop")
//         .category("accessories")
//         .brand("rayban")
//         .stock(60)
//         .build());
//         productRepository.save(Product.builder()
//         .name("Automatic Coffee Maker")
//         .description("Brew fresh coffee with one-touch operation.")
//         .price(new BigDecimal("5599.00"))
//         .imageUrl("https://images.unsplash.com/photo-1509042239860-f550ce710b93?w=600&auto=format&fit=crop")
//         .category("home_appliances")
//         .brand("philips")
//         .stock(20)
//         .build());
//         productRepository.save(Product.builder()
//         .name("Insulated Water Bottle")
//         .description("Keeps drinks cold for 24 hours and hot for 12 hours.")
//         .price(new BigDecimal("599.00"))
//         .imageUrl("https://images.unsplash.com/photo-1526401485004-2fda9f0b2d02?w=600&auto=format&fit=crop")
//         .category("lifestyle")
//         .brand("milton")
//         .stock(80)
//         .build());
    }
}
