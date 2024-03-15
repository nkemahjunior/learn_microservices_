package com.zeco.inventoryService.service;


import com.zeco.inventoryService.model.Inventory;
import com.zeco.inventoryService.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService  {

    @Autowired
    private  InventoryRepository inventoryRepository;


    @Transactional
    public boolean isInStock(String skuCode){

        return inventoryRepository.findBySkuCode(skuCode).isPresent();
    }


}
