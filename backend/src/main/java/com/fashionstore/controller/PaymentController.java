package com.fashionstore.controller;

import com.fashionstore.dto.CheckoutRequest;
import com.fashionstore.dto.OrderDto;
import com.fashionstore.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final CheckoutService checkoutService;

    public PaymentController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/checkout")
    public OrderDto checkout(Authentication auth, @Valid @RequestBody CheckoutRequest body) {
        return checkoutService.checkout(auth.getName(), body);
    }

    @GetMapping("/orders")
    public List<OrderDto> orders(Authentication auth) {
        return checkoutService.myOrders(auth.getName());
    }
}
