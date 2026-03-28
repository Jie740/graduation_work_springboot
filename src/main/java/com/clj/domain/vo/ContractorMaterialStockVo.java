package com.clj.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ContractorMaterialStockVo {
    /**
     * 承包人农资ID
     */
    private Long contractorMaterialId;

    /**
     * 农资ID
     */
//    private Long materialId;
    private String materialName;
    private String type;

    /**
     * 用户ID
     */
//    private Long userId;
    private String contractorName;
    private String phone;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 预警库存
     */
    private Integer warningStock;
}
