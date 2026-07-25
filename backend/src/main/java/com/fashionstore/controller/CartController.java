package com.fashionstore.controller;

import com.fashionstore.dto.AddToCartRequest;
import com.fashionstore.dto.CartItemDto;
import com.fashionstore.dto.UpdateCartRequest;
import com.fashionstore.service.CartService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public List<CartItemDto> get(Authentication auth) {
        return cartService.getCart(auth.getName());
    }

    @PostMapping
    public CartItemDto add(Authentication auth, @Valid @RequestBody AddToCartRequest body) {
        return cartService.add(auth.getName(), body);
    }

    @PatchMapping("/{id}")
    public CartItemDto update(
            Authentication auth,
            @PathVariable Long id,
            @Valid @RequestBody UpdateCartRequest body
    ) {
        return cartService.updateQuantity(auth.getName(), id, body);
    }

    @DeleteMapping("/{id}")
    public void remove(Authentication auth, @PathVariable Long id) {
        cartService.remove(auth.getName(), id);
    }
}
