package com.clj.service;

import com.clj.domain.Equipment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【equipment(设备表)】的数据库操作Service
* @createDate 2026-03-02 20:08:36
*/
public interface EquipmentService extends IService<Equipment> {

    Result add(Equipment equipment);
    Result delete(Long equipmentId);

    Result updateEquipment(Equipment equipment);

    Result getEquipmentByPage(Integer pageNum, Integer pageSize);

    Result searchEquipmentByPage(String keyword, Integer pageNum, Integer pageSize);

    Result getEquipmentTypeNameById(Long equipmentId);

}
