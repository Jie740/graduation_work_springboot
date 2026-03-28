package com.clj.service;

import com.clj.domain.EquipmentRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.EquipmentRecordDto;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【equipment_record(设备记录表)】的数据库操作Service
* @createDate 2026-03-02 20:08:31
*/
public interface EquipmentRecordService extends IService<EquipmentRecord> {

    Result add(EquipmentRecord equipmentRecord);

    Result delete(Long equipmentRecordId);

    Result getByPage(Integer pageNum, Integer pageSize);

    Result searchByPage(String keyword, Integer pageNum, Integer pageSize);

    Result updateStatus(EquipmentRecordDto equipmentRecordDto);
}
