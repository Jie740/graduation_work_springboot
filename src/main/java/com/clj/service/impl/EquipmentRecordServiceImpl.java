package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.EquipmentRecord;
import com.clj.service.EquipmentRecordService;
import com.clj.mapper.EquipmentRecordMapper;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【equipment_record(设备记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:31
*/
@Service
public class EquipmentRecordServiceImpl extends ServiceImpl<EquipmentRecordMapper, EquipmentRecord>
    implements EquipmentRecordService{

    @Override
    public Result add(EquipmentRecord equipmentRecord) {
        return this.save(equipmentRecord)? Result.ok() : Result.error("添加失败");
    }
}




