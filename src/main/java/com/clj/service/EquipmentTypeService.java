package com.clj.service;

import com.clj.domain.EquipmentType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【equipment_type(设备类型表)】的数据库操作Service
* @createDate 2026-03-02 20:08:27
*/
public interface EquipmentTypeService extends IService<EquipmentType> {

    Result add(String equipmentTypeName);
    Result delete(Long equipmentTypeId);
    Result updateEquipmentType(EquipmentType equipmentType);
    Result getEquipmentTypes();
}
