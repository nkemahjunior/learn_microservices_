package com.zeco.orderService.controller;


import com.zeco.orderService.dto.OrderRequest;
import com.zeco.orderService.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/test")
    public String test(){
        return  "all good";
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name = "inventory")
    //using completableFuture because of @TimeLimiter
    //make sure the name "inventory" is the same as the name used in the resillence properties in the app.properties. e.g :resilience4j.circuitbreaker.instances.inventory.bla bla
    public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest){
        return  CompletableFuture.supplyAsync( ()-> orderService.placeOrder(orderRequest));

    }

    public CompletableFuture<String> fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException){
        return CompletableFuture.supplyAsync(() -> "oops! something went wrong, please order after some time");
    }
}
