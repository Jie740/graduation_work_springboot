package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.EquipmentType;
import com.clj.service.EquipmentTypeService;
import com.clj.mapper.EquipmentTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【equipment_type(设备类型表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:27
*/
@Service
public class EquipmentTypeServiceImpl extends ServiceImpl<EquipmentTypeMapper, EquipmentType>
    implements EquipmentTypeService{

}




