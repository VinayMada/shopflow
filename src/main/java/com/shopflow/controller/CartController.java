package com.shopflow.controller;

import com.shopflow.entity.Cart;
import com.shopflow.entity.User;
import com.shopflow.repository.UserRepository;
import com.shopflow.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Cart> getCart() {
        Cart cart = cartService.getCart(getUserId());
        return ResponseEntity.ok().body(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<Cart> addItemToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
        Cart cart = cartService.addItem(productId, getUserId(), quantity);
        URI location = URI.create("/api/cart/items/" + cart.getId());
        return ResponseEntity.created(location).body(cart);
    }

    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Cart> removeItemFromCart(@PathVariable Long cartItemId) {
        cartService.removeItem(getUserId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Cart> removeAllItemFromCart() {
        cartService.clearCart(getUserId());
        return ResponseEntity.noContent().build();
    }

    private Long getUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        return user.getId();
    }
}
