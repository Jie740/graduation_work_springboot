package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Equipment;
import com.clj.domain.EquipmentApply;
import com.clj.domain.EquipmentRecord;
import com.clj.domain.EquipmentType;
import com.clj.domain.vo.EquipmentVo;
import com.clj.mapper.EquipmentApplyMapper;
import com.clj.mapper.EquipmentMapper;
import com.clj.mapper.EquipmentRecordMapper;
import com.clj.service.EquipmentService;
import com.clj.service.EquipmentTypeService;
import com.clj.utils.Result;
import com.clj.utils.UserHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author ajie
* @description 针对表【equipment(设备表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:36
*/
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment>
    implements EquipmentService{
    final EquipmentTypeService equipmentTypeService;
    final EquipmentRecordMapper equipmentRecordMapper;
    final EquipmentApplyMapper equipmentApplyMapper;

    @Override
    public Result add(Equipment equipment) {
        // 检查设备名是否已存在
        Equipment existingEquipment = this.lambdaQuery()
                .eq(Equipment::getEquipmentName, equipment.getEquipmentName())
                .one();
        if (existingEquipment != null) {
            return Result.error("设备名已存在");
        }
        return this.save(equipment) ? Result.ok() : Result.error("添加失败");
    }

    @Override
    @Transactional
    public Result delete(Long equipmentId) {
        // 先删除该设备的承包人借用记录
        equipmentRecordMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EquipmentRecord>()
                        .eq(EquipmentRecord::getEquipmentId, equipmentId)
        );
        
        // 删除该设备的申请记录
        equipmentApplyMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EquipmentApply>()
                        .eq(EquipmentApply::getEquipmentId, equipmentId)
        );
        
        // 再删除设备
        return this.removeById(equipmentId) ? Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result updateEquipment(Equipment equipment) {
        return this.updateById(equipment)?Result.ok():Result.error("修改失败");
    }

    @Override
    public Result getEquipmentByPage(Integer pageNum, Integer pageSize) {
        // 分页查询设备
        Page<Equipment> equipmentPage = new Page<>(pageNum, pageSize);
        Page<Equipment> page = this.page(equipmentPage);
        
        // 转换为 VO 列表
        List<EquipmentVo> voList = new ArrayList<>();
        for (Equipment equipment : page.getRecords()) {
            EquipmentVo vo = new EquipmentVo();
            // 复制基本属性
            BeanUtils.copyProperties(equipment, vo);
            
            // 根据 equipmentTypeId 查询设备类型信息
            if (equipment.getEquipmentTypeId() != null) {
                EquipmentType equipmentType = equipmentTypeService.getById(equipment.getEquipmentTypeId());
                if (equipmentType != null) {
                    vo.setEquipmentTypeName(equipmentType.getEquipmentTypeName());
                }
            }
            
            voList.add(vo);
        }
        
        // 创建新的分页对象，包含转换后的 VO 数据
        Page<EquipmentVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(voList);
        
        return Result.ok(voPage);
    }

    @Override
    public Result searchEquipmentByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return this.getEquipmentByPage(pageNum,pageSize);
        }
        // 根据设备名模糊查询
        Page<Equipment> page = this.lambdaQuery()
                .like(Equipment::getEquipmentName, keyword)
                .page(new Page<>(pageNum, pageSize));

        // 转换为 VO 列表并批量填充类型名称
        List<EquipmentVo> voList = convertToEquipmentVos(page.getRecords());
        fillTypeName(voList);

        // 创建新的分页对象，包含转换后的 VO 数据
        Page<EquipmentVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(voList);

        return Result.ok(voPage);
    }

    @Override
    public Result getEquipmentTypeNameById(Long equipmentId) {
        Equipment equipment = this.lambdaQuery().eq(Equipment::getEquipmentId, equipmentId)
                .one();
        if (equipment == null){
            return Result.error("设备不存在");
        }
        Long equipmentTypeId = equipment.getEquipmentTypeId();
        String typeName = null;
        if (equipmentTypeId != null) {
            EquipmentType equipmentType = equipmentTypeService.getById(equipmentTypeId);
            if (equipmentType != null) {
                typeName = equipmentType.getEquipmentTypeName();
            }
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("typeName", typeName);
        return Result.ok(map);
    }




    /**
     * 将 Equipment 列表转换为 EquipmentVo 列表
     */
    private List<EquipmentVo> convertToEquipmentVos(List<Equipment> equipments) {
        List<EquipmentVo> voList = new ArrayList<>();
        for (Equipment equipment : equipments) {
            EquipmentVo vo = new EquipmentVo();
            BeanUtils.copyProperties(equipment, vo);
            voList.add(vo);
        }
        return voList;
    }

    /**
     * 批量填充设备类型名称
     */
    private void fillTypeName(List<EquipmentVo> voList) {
        if (voList.isEmpty()) {
            return;
        }

        // 提取所有不重复的 equipmentTypeId
        Set<Long> typeIds = voList.stream()
                .map(EquipmentVo::getEquipmentTypeId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (!typeIds.isEmpty()) {
            // 批量查询类型信息
            List<EquipmentType> equipmentTypes = equipmentTypeService.listByIds(typeIds);
            java.util.Map<Long, String> typeNameMap = equipmentTypes.stream()
                    .collect(Collectors.toMap(EquipmentType::getEquipmentTypeId, EquipmentType::getEquipmentTypeName, (k1, k2) -> k1));

            // 填充类型名称
            for (EquipmentVo vo : voList) {
                if (vo.getEquipmentTypeId() != null && typeNameMap.containsKey(vo.getEquipmentTypeId())) {
                    vo.setEquipmentTypeName(typeNameMap.get(vo.getEquipmentTypeId()));
                }
            }
        }
    }
}




