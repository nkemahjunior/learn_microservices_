package com.zeco.orderService.service;


import com.zeco.orderService.dto.OrderLineItemsDto;
import com.zeco.orderService.dto.OrderRequest;
import com.zeco.orderService.model.Order;
import com.zeco.orderService.model.OrderLineItems;
import com.zeco.orderService.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    //private final WebClient.Builder webClientBuilder;
    //private final ObservationRegistry observationRegistry;
    //private final ApplicationEventPublisher applicationEventPublisher;

    public void placeOrder(OrderRequest orderRequest){//order request is a json array

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        orderRepository.save(order);
    }


    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }

}
