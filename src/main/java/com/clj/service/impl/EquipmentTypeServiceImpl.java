package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.EquipmentType;
import com.clj.service.EquipmentTypeService;
import com.clj.mapper.EquipmentTypeMapper;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【equipment_type(设备类型表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:27
*/
@Service
public class EquipmentTypeServiceImpl extends ServiceImpl<EquipmentTypeMapper, EquipmentType>
    implements EquipmentTypeService{


    @Override
    public Result add(String equipmentTypeName) {
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setEquipmentTypeName(equipmentTypeName);
        return this.save(equipmentType)?Result.ok():Result.error("添加失败");
    }

    @Override
    public Result delete(Long equipmentTypeId) {
        return this.removeById(equipmentTypeId)?Result.ok():Result.error("删除失败");
    }

    @Override
    public Result updateEquipmentType(EquipmentType equipmentType) {
        System.out.println(equipmentType);
        return this.updateById(equipmentType)?Result.ok():Result.error("修改失败");
    }

    @Override
    public Result getEquipmentTypes() {
        return Result.ok(this.list());
    }
}




