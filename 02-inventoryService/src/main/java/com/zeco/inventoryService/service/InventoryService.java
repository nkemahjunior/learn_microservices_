package com.zeco.inventoryService.service;


import com.zeco.inventoryService.dto.InventoryResponse;
import com.zeco.inventoryService.model.Inventory;
import com.zeco.inventoryService.repository.InventoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService  {

    @Autowired
    private  InventoryRepository inventoryRepository;


    @Transactional
    public List<InventoryResponse> isInStock(List<String> skuCode){

        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(el ->
                    InventoryResponse.builder()
                            .skuCode(el.getSkuCode())
                            .isInStock(el.getQuantity() > 0)
                            .build()
                )
                .toList();
    }


}
