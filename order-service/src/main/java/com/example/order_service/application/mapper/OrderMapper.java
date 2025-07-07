package com.example.order_service.application.mapper;

import com.example.basedomains.dto.request.ItemOrderRequest;
import com.example.order_service.application.dto.request.OrderRequest;
import com.example.order_service.application.dto.response.OrderResponse;
import com.example.order_service.model.Order;
import com.example.order_service.model.OrderItem;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Component
public class OrderMapper {

    public Order requestToOrder(OrderRequest orderRequest, String userId) {
        Order order = new Order();

        order.setUserId(UUID.fromString(userId));
        order.setTotal(orderRequest.total());
        order.setShippingAddress(orderRequest.shippingAddress());
        order.setItems(toOrderItems(orderRequest.items(), order));

        return order;
    }

    public OrderResponse toOrderResponse(Order order){

        String formattedCreatedAt = DateTimeFormatter
                .ofPattern("dd/MM/yyyy HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(order.getCreatedAt());

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getRejectionReason(),
                order.getTotal(),
                order.getShippingAddress(),
                order.getCreatedAt(),
                formattedCreatedAt
        );
    }

    private List<OrderItem> toOrderItems(List<ItemOrderRequest> items, Order order) {
        return items.stream()
                .map(item -> toOrderItem(item, order))
                .toList();
    }

    private OrderItem toOrderItem(ItemOrderRequest item, Order order) {
        OrderItem orderItem = new OrderItem();

        orderItem.setProductCode(item.productCode());
        orderItem.setProductName(item.productName());
        orderItem.setQty(item.qty());
        orderItem.setOrder(order);

        return orderItem;
    }
}
