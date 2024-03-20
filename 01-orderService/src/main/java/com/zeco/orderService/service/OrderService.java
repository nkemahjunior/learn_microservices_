package com.zeco.orderService.service;



import com.zeco.orderService.dto.InventoryResponse;
import com.zeco.orderService.dto.OrderLineItemsDto;
import com.zeco.orderService.dto.OrderRequest;
import com.zeco.orderService.event.OrderPlacedEvent;
import com.zeco.orderService.model.Order;
import com.zeco.orderService.model.OrderLineItems;
import com.zeco.orderService.repository.OrderRepository;
import io.micrometer.tracing.Tracer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
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
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    private final KafkaTemplate<String,String> kafkaTemplate;




   @Autowired
    public OrderService(OrderRepository orderRepository, WebClient.Builder webClientBuilder, Tracer tracer, KafkaTemplate kafkaTemplate) {
        this.orderRepository = orderRepository;
        //this.webClient = webClient;

        this.webClientBuilder = webClientBuilder;
       this.tracer = tracer;
       this.kafkaTemplate = kafkaTemplate;
   }


    public String placeOrder(OrderRequest orderRequest){//order request is a json array



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



        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                        //.uri("http://localhost:8082/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                        //.uri("http://inventoryService/api/inventory/sku-code?skuCode=iphone_13&skuCode=iphone_131")
                            .uri("http://inventoryService/api/inventory/sku-code", uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                                .retrieve()
                                        .bodyToMono(InventoryResponse[].class)
                                                .block(); // this block method will make this operation synchronous





        /***the allMatch method checks if all elements in an array/list meet a certain condition, it any of the elements dont meet the condition, it returns false***/
       boolean allProductsIsInStock =  Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);

        if(allProductsIsInStock){
            //orderRepository.save(order);
           // kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            kafkaTemplate.send("notificationTopic", "sending testtttttttttt");


            return "order placed succesfully";
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
