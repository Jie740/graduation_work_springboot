package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.EquipmentType;
import com.clj.mapper.EquipmentTypeMapper;
import com.clj.service.EquipmentTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author ajie
* @description 针对表【equipment_type(设备类型表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:27
*/
@Service
public class EquipmentTypeServiceImpl extends ServiceImpl<EquipmentTypeMapper, EquipmentType>
    implements EquipmentTypeService{


    @Override
    public void add(String equipmentTypeName) {
        EquipmentType equipmentType = new EquipmentType();
        equipmentType.setEquipmentTypeName(equipmentTypeName);
        if (!this.save(equipmentType)) {
            throw new BusinessException("添加失败");
        }
    }

    @Override
    public void delete(Long equipmentTypeId) {
        if (!this.removeById(equipmentTypeId)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updateEquipmentType(EquipmentType equipmentType) {
        System.out.println(equipmentType);
        if (!this.updateById(equipmentType)) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public List<EquipmentType> getEquipmentTypes() {
        return this.list();
    }
}




