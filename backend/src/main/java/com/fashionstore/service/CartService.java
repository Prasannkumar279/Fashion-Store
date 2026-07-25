package com.fashionstore.service;

import com.fashionstore.dto.AddToCartRequest;
import com.fashionstore.dto.CartItemDto;
import com.fashionstore.dto.UpdateCartRequest;
import com.fashionstore.entity.CartItem;
import com.fashionstore.entity.Product;
import com.fashionstore.entity.User;
import com.fashionstore.repository.CartItemRepository;
import com.fashionstore.repository.ProductRepository;
import com.fashionstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<CartItemDto> getCart(String email) {
        User user = userByEmail(email);
        return cartItemRepository.findByUserOrderByIdAsc(user).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CartItemDto add(String email, AddToCartRequest req) {
        User user = userByEmail(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        if (product.getStock() < req.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock");
        }
        CartItem item = cartItemRepository.findByUserAndProductId(user, product.getId())
                .orElse(null);
        if (item == null) {
            item = CartItem.builder()
                    .user(user)
                    .product(product)
                    .quantity(req.getQuantity())
                    .build();
        } else {
            int next = item.getQuantity() + req.getQuantity();
            if (product.getStock() < next) {
                throw new IllegalArgumentException("Not enough stock");
            }
            item.setQuantity(next);
        }
        cartItemRepository.save(item);
        return toDto(item);
    }

    @Transactional
    public CartItemDto updateQuantity(String email, Long cartItemId, UpdateCartRequest req) {
        User user = userByEmail(email);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cart item not found");
        }
        Product product = item.getProduct();
        if (product.getStock() < req.getQuantity()) {
            throw new IllegalArgumentException("Not enough stock");
        }
        item.setQuantity(req.getQuantity());
        cartItemRepository.save(item);
        return toDto(item);
    }

    @Transactional
    public void remove(String email, Long cartItemId) {
        User user = userByEmail(email);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Cart item not found");
        }
        cartItemRepository.delete(item);
    }

    private User userByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private CartItemDto toDto(CartItem c) {
        BigDecimal line = c.getProduct().getPrice()
                .multiply(BigDecimal.valueOf(c.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
        return CartItemDto.builder()
                .id(c.getId())
                .productId(c.getProduct().getId())
                .productName(c.getProduct().getName())
                .unitPrice(c.getProduct().getPrice())
                .imageUrl(c.getProduct().getImageUrl())
                .quantity(c.getQuantity())
                .lineTotal(line)
                .build();
    }
}
