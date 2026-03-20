package com.clj.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class MaterialVo {
    /**
     * 农资ID
     */
    @TableId(type = IdType.AUTO)
    private Long materialId;

    /**
     * 农资名
     */
    private String materialName;

    /**
     * 农资类型ID
     */
    private Long typeId;


    /*农资类型名*/
    private String typeName;
    /**
     * 库存
     */
    private Integer stock;

    /**
     * 预警库存
     */
    private Integer warningStock;

    /**
     * 创建时间
     */
    private Date createTime;
}
