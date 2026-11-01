package com.shopflow.service;

import com.shopflow.entity.Cart;
import com.shopflow.entity.CartItem;
import com.shopflow.entity.Product;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.InventoryRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.userRepository = userRepository;
    }

    public Cart getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(new Cart());
        userRepository.findById(userId).ifPresent(cart::setUser);
        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public Cart addItem(Long productId, Long userId, int quantity) {
        Cart cart = getCart(userId);
        Product product = productRepository.findById(productId).orElseThrow();
        boolean productFound = false;
        for(CartItem item : cart.getItems()){
            if(item.getProduct().getId().equals(product.getId())){
                productFound = true;
                item.setQuantity(item.getQuantity() + quantity);
                break;
            }
        }
        if(!productFound){
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setCart(cart);
            cart.getItems().add(item);
        }
        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public Cart removeItem(Long userId, Long cartItemId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getId().equals(cartItemId));
        cartRepository.save(cart);
        return cart;
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

}
