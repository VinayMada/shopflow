package com.shopflow.service;

import com.shopflow.entity.*;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.OrderRepository;
import com.shopflow.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order placeOrder(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Cart cart = cartRepository.findByUserId(userId).orElseThrow();
        if(cart.getItems().isEmpty()){
            throw new IllegalStateException("Cart is empty");
        }
        Order order = new Order();
        order.setUser(user);
        order.setOrderStatus(OrderStatus.PENDING);
        BigDecimal totalPrice = BigDecimal.ZERO;
        for(CartItem item : cart.getItems()){
            OrderItem orderItem = new OrderItem(order,item.getProduct().getId(),item.getProduct().getName(),item.getProduct().getPrice(),item.getQuantity());
            order.getItems().add(orderItem);
            totalPrice = totalPrice.add(orderItem.getPriceAtPurchase().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        }
        order.setTotalAmount(totalPrice);
        orderRepository.save(order);
        cart.getItems().clear();
        cartRepository.save(cart);
        return order;
    }


    public Order updateStatus(Long orderId, OrderStatus newStatus){
        Order order = orderRepository.findById(orderId).orElseThrow();
        OrderStatus oldStatus = order.getOrderStatus();
        if(oldStatus.equals(OrderStatus.PENDING)){
            if(!newStatus.equals(OrderStatus.CONFIRMED) && !newStatus.equals(OrderStatus.CANCELLED)){
                throw new IllegalStateException("Invalid status transition: " + oldStatus + " → " + newStatus);
            }
        }
        else if(oldStatus.equals(OrderStatus.CONFIRMED)){
            if(!newStatus.equals(OrderStatus.CANCELLED) && !newStatus.equals(OrderStatus.PROCESSING)){
                throw new IllegalStateException("Invalid status transition: " + oldStatus + " → " + newStatus);
            }
        }
        else if(oldStatus.equals(OrderStatus.PROCESSING)){
            if(!newStatus.equals(OrderStatus.SHIPPED)){
                throw new IllegalStateException("Invalid status transition: " + oldStatus + " → " + newStatus);
            }
        }
        else if (oldStatus.equals(OrderStatus.SHIPPED)){
            if(!newStatus.equals(OrderStatus.DELIVERED)){
                throw new IllegalStateException("Invalid status transition: " + oldStatus + " → " + newStatus);
            }
        }
        else {
            throw new IllegalStateException("Invalid status of order " + order.getOrderStatus());
        }
        order.setOrderStatus(newStatus);
        orderRepository.save(order);
        return order;
    }
}
