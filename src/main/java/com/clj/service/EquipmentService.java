package com.clj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.Equipment;
import com.clj.domain.vo.EquipmentVo;

import java.util.Map;

/**
* @author ajie
* @description 针对表【equipment(设备表)】的数据库操作Service
* @createDate 2026-03-02 20:08:36
*/
public interface EquipmentService extends IService<Equipment> {

    void add(Equipment equipment);
    void delete(Long equipmentId);

    void updateEquipment(Equipment equipment);

    Page<EquipmentVo> getEquipmentByPage(Integer pageNum, Integer pageSize);

    Page<EquipmentVo> searchEquipmentByPage(String keyword, Integer pageNum, Integer pageSize);

    Map<String, String> getEquipmentTypeNameById(Long equipmentId);

}
