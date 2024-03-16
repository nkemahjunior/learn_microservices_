package com.zeco.orderService.service;



import com.zeco.orderService.dto.InventoryResponse;
import com.zeco.orderService.dto.OrderLineItemsDto;
import com.zeco.orderService.dto.OrderRequest;
import com.zeco.orderService.model.Order;
import com.zeco.orderService.model.OrderLineItems;
import com.zeco.orderService.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
//@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;


    public OrderService(OrderRepository orderRepository, WebClient webClient) {
        this.orderRepository = orderRepository;
        this.webClient = webClient;

    }


    public void placeOrder(OrderRequest orderRequest){//order request is a json array

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        /** call inventory service and place order if product is in stock**/

        InventoryResponse[] inventoryResponses = webClient.get()
                        .uri("http://localhost:8082/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                                .retrieve()
                                        .bodyToMono(InventoryResponse[].class)
                                                .block(); // this block method will make this operation synchronous


        /***the allMatch method checks if all elements in an array/list meet a certain condition, it any of the elements dont meet the condition, it returns false***/
       boolean allProductsIsInStock =  Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if(allProductsIsInStock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("product is not in stock, please try again later");
        }

    }


    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        OrderLineItems orderLineItems = new OrderLineItems();

        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());

        return orderLineItems;
    }

}
