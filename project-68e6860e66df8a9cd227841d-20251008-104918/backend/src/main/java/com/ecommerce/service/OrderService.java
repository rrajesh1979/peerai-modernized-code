package com.ecommerce.service;

import com.ecommerce.exception.OrderNotFoundException;
import com.ecommerce.exception.ProductOutOfStockException;
import com.ecommerce.exception.UserNotFoundException;
import com.ecommerce.model.Order;
import com.ecommerce.model.OrderItem;
import com.ecommerce.model.OrderStatus;
import com.ecommerce.model.Product;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.InventoryRepository;
import com.ecommerce.dto.OrderDTO;
import com.ecommerce.dto.OrderItemDTO;
import com.ecommerce.dto.OrderSummaryDTO;
import com.ecommerce.event.OrderCreatedEvent;
import com.ecommerce.event.OrderStatusChangedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for managing orders in the e-commerce system.
 * Handles order creation, retrieval, updates, and status management.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public OrderService(OrderRepository orderRepository, 
                        UserRepository userRepository,
                        ProductRepository productRepository,
                        InventoryRepository inventoryRepository,
                        ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new order for a user with the specified items.
     * Validates product availability, reserves inventory, and creates the order record.
     *
     * @param userId the ID of the user placing the order
     * @param orderItems list of items to be ordered
     * @return the created order
     * @throws UserNotFoundException if the user doesn't exist
     * @throws ProductOutOfStockException if any product is out of stock
     */
    @Transactional
    public Order createOrder(String userId, List<OrderItemDTO> orderItems) {
        logger.info("Creating new order for user: {}", userId);
        
        // Validate user exists
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        // Validate products and check inventory
        List<OrderItem> validatedItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemDTO itemDTO : orderItems) {
            Product product = productRepository.findById(itemDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + itemDTO.getProductId()));
            
            // Check inventory availability
            boolean isAvailable = inventoryRepository.checkAvailability(
                product.getId(), itemDTO.getQuantity());
            
            if (!isAvailable) {
                throw new ProductOutOfStockException("Product out of stock: " + product.getName());
            }
            
            // Reserve inventory
            inventoryRepository.reserveInventory(product.getId(), itemDTO.getQuantity());
            
            // Create order item
            OrderItem item = new OrderItem();
            item.setProductId(product.getId());
            item.setSku(product.getSku());
            item.setName(product.getName());
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            
            validatedItems.add(item);
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        
        // Create the order
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setUserId(userId);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(validatedItems);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(null); // Will be set later during checkout
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        Order savedOrder = orderRepository.save(order);
        
        // Publish order created event
        eventPublisher.publishEvent(new OrderCreatedEvent(savedOrder));
        
        logger.info("Order created successfully: {}", savedOrder.getOrderNumber());
        return savedOrder;
    }

    /**
     * Retrieves an order by its ID.
     *
     * @param orderId the ID of the order to retrieve
     * @return the order
     * @throws OrderNotFoundException if the order doesn't exist
     */
    public Order getOrderById(String orderId) {
        logger.debug("Retrieving order with ID: {}", orderId);
        return orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
    }

    /**
     * Retrieves an order by its order number.
     *
     * @param orderNumber the order number
     * @return the order
     * @throws OrderNotFoundException if the order doesn't exist
     */
    public Order getOrderByOrderNumber(String orderNumber) {
        logger.debug("Retrieving order with order number: {}", orderNumber);
        return orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with order number: " + orderNumber));
    }

    /**
     * Retrieves all orders for a specific user.
     *
     * @param userId the ID of the user
     * @param pageable pagination information
     * @return a page of orders
     */
    public Page<Order> getOrdersByUserId(String userId, Pageable pageable) {
        logger.debug("Retrieving orders for user: {}", userId);
        return orderRepository.findByUserId(userId, pageable);
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId the ID of the order
     * @param newStatus the new status
     * @return the updated order
     * @throws OrderNotFoundException if the order doesn't exist
     */
    @Transactional
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        logger.info("Updating order status. Order ID: {}, New Status: {}", orderId, newStatus);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        OrderStatus oldStatus = order.getStatus();
        
        // Handle inventory based on status change
        if (oldStatus != newStatus) {
            if (newStatus == OrderStatus.CANCELLED) {
                // Release reserved inventory
                for (OrderItem item : order.getItems()) {
                    inventoryRepository.releaseInventory(item.getProductId(), item.getQuantity());
                }
            } else if (newStatus == OrderStatus.COMPLETED && oldStatus != OrderStatus.SHIPPED) {
                // Deduct from inventory if order is completed without being shipped first
                for (OrderItem item : order.getItems()) {
                    inventoryRepository.deductInventory(item.getProductId(), item.getQuantity());
                }
            } else if (newStatus == OrderStatus.SHIPPED && oldStatus == OrderStatus.PROCESSING) {
                // Deduct from inventory when order is shipped
                for (OrderItem item : order.getItems()) {
                    inventoryRepository.deductInventory(item.getProductId(), item.getQuantity());
                }
            }
            
            order.setStatus(newStatus);
            order.setUpdatedAt(LocalDateTime.now());
            
            Order updatedOrder = orderRepository.save(order);
            
            // Publish order status changed event
            eventPublisher.publishEvent(new OrderStatusChangedEvent(updatedOrder, oldStatus, newStatus));
            
            logger.info("Order status updated successfully. Order: {}, New Status: {}", 
                    updatedOrder.getOrderNumber(), newStatus);
            
            return updatedOrder;
        }
        
        return order;
    }

    /**
     * Updates the shipping address for an order.
     *
     * @param orderId the ID of the order
     * @param shippingAddress the new shipping address
     * @return the updated order
     * @throws OrderNotFoundException if the order doesn't exist
     */
    @Transactional
    public Order updateShippingAddress(String orderId, String shippingAddress) {
        logger.info("Updating shipping address for order: {}", orderId);
        
        if (!StringUtils.hasText(shippingAddress)) {
            throw new IllegalArgumentException("Shipping address cannot be empty");
        }
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        // Only allow address update for orders that haven't been shipped
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Cannot update shipping address for orders that have been shipped or delivered");
        }
        
        order.setShippingAddress(shippingAddress);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderRepository.save(order);
    }

    /**
     * Cancels an order if it's in a cancellable state.
     *
     * @param orderId the ID of the order to cancel
     * @return the cancelled order
     * @throws OrderNotFoundException if the order doesn't exist
     * @throws IllegalStateException if the order cannot be cancelled
     */
    @Transactional
    public Order cancelOrder(String orderId) {
        logger.info("Cancelling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));
        
        // Check if order can be cancelled
        if (order.getStatus() == OrderStatus.SHIPPED || 
            order.getStatus() == OrderStatus.DELIVERED || 
            order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }
        
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    /**
     * Retrieves order statistics for a user.
     *
     * @param userId the ID of the user
     * @return summary of user's order statistics
     */
    public OrderSummaryDTO getUserOrderSummary(String userId) {
        logger.debug("Retrieving order summary for user: {}", userId);
        
        // Validate user exists
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        
        List<Order> userOrders = orderRepository.findAllByUserId(userId);
        
        OrderSummaryDTO summary = new OrderSummaryDTO();
        summary.setUserId(userId);
        summary.setTotalOrders(userOrders.size());
        
        // Calculate total spent on completed orders
        BigDecimal totalSpent = userOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.DELIVERED)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        summary.setTotalSpent(totalSpent);
        
        // Count orders by status
        summary.setPendingOrders((int) userOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.PENDING).count());
        
        summary.setProcessingOrders((int) userOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.PROCESSING).count());
        
        summary.setCompletedOrders((int) userOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.COMPLETED || 
                             order.getStatus() == OrderStatus.DELIVERED).count());
        
        summary.setCancelledOrders((int) userOrders.stream()
            .filter(order -> order.getStatus() == OrderStatus.CANCELLED).count());
        
        return summary;
    }

    /**
     * Converts an Order entity to an OrderDTO.
     *
     * @param order the order entity
     * @return the order DTO
     */
    public OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUserId());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
            .map(item -> {
                OrderItemDTO itemDTO = new OrderItemDTO();
                itemDTO.setProductId(item.getProductId());
                itemDTO.setSku(item.getSku());
                itemDTO.setName(item.getName());
                itemDTO.setQuantity(item.getQuantity());
                itemDTO.setUnitPrice(item.getUnitPrice());
                itemDTO.setSubtotal(item.getSubtotal());
                return itemDTO;
            })
            .collect(Collectors.toList());
        
        dto.setItems(itemDTOs);
        return dto;
    }

    /**
     * Generates a unique order number.
     *
     * @return a unique order number
     */
    private String generateOrderNumber() {
        // Format: ORD-YYYYMMDD-XXXX (where XXXX is a random alphanumeric string)
        String datePart = LocalDateTime.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE);
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + datePart + "-" + randomPart;
    }
}