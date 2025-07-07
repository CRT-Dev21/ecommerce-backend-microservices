package com.example.order_service.domain.service;

import com.example.basedomains.events.OrderCreatedEvent;
import com.example.order_service.application.dto.request.OrderRequest;
import com.example.order_service.application.dto.response.OrderResponse;
import com.example.order_service.application.mapper.OrderMapper;
import com.example.order_service.domain.exceptions.OrderNotFoundException;
import com.example.order_service.infrastructure.messaging.OrderEventProducer;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderEventProducer eventProducer;
    private final OrderMapper orderMapper;

    public OrderResponse createOrder(OrderRequest orderRequest, String userId) {
        Order order = orderMapper.requestToOrder(orderRequest, userId);
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        eventProducer.sendOrderCreatedEvent(
                new OrderCreatedEvent(
                        savedOrder.getId(),
                        savedOrder.getUserId(),
                        orderRequest.items()
                )
        );

        return orderMapper.toOrderResponse(savedOrder);
    }

    public List<OrderResponse> getOrdersByUserId (String userId){
        return orderRepository.findByUserId(UUID.fromString(userId)).stream().map(orderMapper::toOrderResponse).toList();
    }

    public OrderResponse getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(orderMapper::toOrderResponse)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
