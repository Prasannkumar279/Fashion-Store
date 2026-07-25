package com.fashionstore.service;

import com.fashionstore.dto.CheckoutRequest;
import com.fashionstore.dto.OrderDto;
import com.fashionstore.dto.OrderLineDto;
import com.fashionstore.entity.CartItem;
import com.fashionstore.entity.OrderLine;
import com.fashionstore.entity.ShopOrder;
import com.fashionstore.entity.User;
import com.fashionstore.repository.CartItemRepository;
import com.fashionstore.repository.ShopOrderRepository;
import com.fashionstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CheckoutService {

    private final CartItemRepository cartItemRepository;
    private final ShopOrderRepository shopOrderRepository;
    private final UserRepository userRepository;

    public CheckoutService(
            CartItemRepository cartItemRepository,
            ShopOrderRepository shopOrderRepository,
            UserRepository userRepository
    ) {
        this.cartItemRepository = cartItemRepository;
        this.shopOrderRepository = shopOrderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Demo checkout: validates cart, creates a paid order, decrements stock, clears cart.
     * Card data is not persisted (demo only).
     */
    @Transactional
    public OrderDto checkout(String email, CheckoutRequest request) {
        if (request.getCardLast4() == null || !request.getCardLast4().matches("\\d{4}")) {
            throw new IllegalArgumentException("Card last 4 must be exactly 4 digits");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<CartItem> items = cartItemRepository.findByUserOrderByIdAsc(user);
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }

        BigDecimal total = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (CartItem c : items) {
            var p = c.getProduct();
            if (p.getStock() < c.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for: " + p.getName());
            }
            BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(c.getQuantity()));
            total = total.add(line);
        }

        ShopOrder order = ShopOrder.builder()
                .user(user)
                .totalAmount(total.setScale(2, RoundingMode.HALF_UP))
                .status("PAID")
                .build();

        for (CartItem c : items) {
            var p = c.getProduct();
            OrderLine line = OrderLine.builder()
                    .order(order)
                    .productId(p.getId())
                    .productName(p.getName())
                    .unitPrice(p.getPrice())
                    .quantity(c.getQuantity())
                    .build();
            order.getLines().add(line);
            p.setStock(p.getStock() - c.getQuantity());
        }

        shopOrderRepository.save(order);
        cartItemRepository.deleteByUser(user);

        return OrderDto.builder()
                .id(order.getId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .lines(order.getLines().stream()
                        .map(l -> OrderLineDto.builder()
                                .productId(l.getProductId())
                                .productName(l.getProductName())
                                .unitPrice(l.getUnitPrice())
                                .quantity(l.getQuantity())
                                .build())
                        .toList())
                .build();
    }

    @Transactional(readOnly = true)
    public List<OrderDto> myOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return shopOrderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(o -> OrderDto.builder()
                        .id(o.getId())
                        .totalAmount(o.getTotalAmount())
                        .status(o.getStatus())
                        .createdAt(o.getCreatedAt())
                        .lines(o.getLines().stream()
                                .map(l -> OrderLineDto.builder()
                                        .productId(l.getProductId())
                                        .productName(l.getProductName())
                                        .unitPrice(l.getUnitPrice())
                                        .quantity(l.getQuantity())
                                        .build())
                                .toList())
                        .build())
                .toList();
    }
}
