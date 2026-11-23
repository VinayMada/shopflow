package com.shopflow.service;

import com.shopflow.entity.Cart;
import com.shopflow.entity.CartItem;
import com.shopflow.entity.Order;
import com.shopflow.entity.OrderStatus;
import com.shopflow.entity.Product;
import com.shopflow.entity.User;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.OrderRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long PRODUCT_ID = 10L;

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks private OrderService orderService;

    private User user;
    private Cart cart;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(USER_ID);
        user.setEmail("test@shopflow.com");

        cart = new Cart();
        cart.setUser(user);

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
    }

    @Test
    void placeOrder_shouldThrow_whenCartIsEmpty() {
        assertThatThrownBy(() -> orderService.placeOrder(USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cart is empty");
    }

    @Test
    void placeOrder_shouldThrow_whenStockInsufficient() {
        Product product = product(5);
        addToCart(product, 10);

        assertThatThrownBy(() -> orderService.placeOrder(USER_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient stock");
        assertThat(product.getStock()).isEqualTo(5);
    }

    @Test
    void placeOrder_shouldDeductStock_whenSuccessful() {
        Product product = product(10);
        addToCart(product, 2);

        Order order = orderService.placeOrder(USER_ID);

        assertThat(product.getStock()).isEqualTo(8);
        verify(productRepository).save(product);
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(order.getTotalAmount()).isEqualByComparingTo("200.00");
        assertThat(order.getItems()).hasSize(1);
        assertThat(cart.getItems()).isEmpty();
    }

    private Product product(int stock) {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("100.00"));
        product.setStock(stock);
        return product;
    }

    private void addToCart(Product product, int quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(quantity);
        cart.getItems().add(item);
    }
}
