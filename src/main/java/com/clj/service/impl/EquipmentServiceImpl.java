package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Equipment;
import com.clj.service.EquipmentService;
import com.clj.mapper.EquipmentMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【equipment(设备表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:36
*/
@Service
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment>
    implements EquipmentService{

}




