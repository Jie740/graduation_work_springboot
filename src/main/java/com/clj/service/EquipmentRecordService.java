package com.clj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.Equipment;
import com.clj.domain.EquipmentRecord;
import com.clj.domain.dto.EquipmentRecordDto;
import com.clj.domain.vo.EquipmentRecordVo;

import java.util.List;

/**
* @author ajie
* @description 针对表【equipment_record(设备记录表)】的数据库操作Service
* @createDate 2026-03-02 20:08:31
*/
public interface EquipmentRecordService extends IService<EquipmentRecord> {

    void add(EquipmentRecord equipmentRecord);

    void delete(Long equipmentRecordId);

    Page<EquipmentRecordVo> getByPage(Integer pageNum, Integer pageSize);

    Page<EquipmentRecordVo> searchByPage(String keyword, Integer pageNum, Integer pageSize);

    void updateStatus(EquipmentRecordDto equipmentRecordDto);

    Page<EquipmentRecordVo> getByUserId(String keyword, Integer pageNum, Integer pageSize);

    List<Equipment> getMyEquipment();
}
