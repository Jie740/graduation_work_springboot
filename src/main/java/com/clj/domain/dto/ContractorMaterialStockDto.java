package com.clj.domain.dto;

import lombok.Data;

@Data
public class ContractorMaterialStockDto {
    private Long contractorMaterialId;
    private int stock;
    private int warningStock;
}
