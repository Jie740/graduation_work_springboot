package com.clj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.EquipmentType;

import java.util.List;

/**
* @author ajie
* @description 针对表【equipment_type(设备类型表)】的数据库操作Service
* @createDate 2026-03-02 20:08:27
*/
public interface EquipmentTypeService extends IService<EquipmentType> {

    void add(String equipmentTypeName);
    void delete(Long equipmentTypeId);
    void updateEquipmentType(EquipmentType equipmentType);
    List<EquipmentType> getEquipmentTypes();
}
