package com.zeco.inventoryService.controller;



import com.zeco.inventoryService.dto.InventoryResponse;
import com.zeco.inventoryService.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @Autowired
    private   InventoryService inventoryService;

    @GetMapping("/test")
    public String test(){
        return  "all good";
    }


   @GetMapping("/sku-code")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam(name = "skuCode")  List<String> skuCode){
       System.out.println("innnnnnnnnnnnnnnn");


       return  inventoryService.isInStock(skuCode);
   }


}
