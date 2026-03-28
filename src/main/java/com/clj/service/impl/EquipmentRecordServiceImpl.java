package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Equipment;
import com.clj.domain.EquipmentRecord;
import com.clj.domain.EquipmentType;
import com.clj.domain.User;
import com.clj.domain.dto.EquipmentRecordDto;
import com.clj.domain.vo.EquipmentRecordVo;
import com.clj.service.EquipmentRecordService;
import com.clj.mapper.EquipmentRecordMapper;
import com.clj.service.EquipmentService;
import com.clj.service.EquipmentTypeService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author ajie
* @description 针对表【equipment_record(设备记录表)】的数据库操作 Service 实现
* @createDate 2026-03-02 20:08:31
*/
@Service
@RequiredArgsConstructor
public class EquipmentRecordServiceImpl extends ServiceImpl<EquipmentRecordMapper, EquipmentRecord>
    implements EquipmentRecordService{

    private final EquipmentService equipmentService;
    private final EquipmentTypeService equipmentTypeService;
    private final UserService userService;

    @Override
    public Result add(EquipmentRecord equipmentRecord) {
        return this.save(equipmentRecord)? Result.ok() : Result.error("添加失败");
    }

    @Override
    @Transactional
    public Result delete(Long equipmentRecordId) {
        EquipmentRecord one = this.lambdaQuery().eq(EquipmentRecord::getRecordId, equipmentRecordId).one();
        Long equipmentId = one.getEquipmentId();
        //删除设备记录
        this.removeById(equipmentRecordId);
        //修改设备表的状态为正常闲置
        equipmentService.lambdaUpdate().eq(Equipment::getEquipmentId,equipmentId)
                .set(Equipment::getStatus,0).update();
        return Result.ok();
    }

    @Override
    public Result getByPage(Integer pageNum, Integer pageSize) {
        // 分页查询设备记录
        Page<EquipmentRecord> page = this.page(new Page<>(pageNum, pageSize));
        
        // 收集所有的设备 ID 和隶属人 ID
        ArrayList<Long> equipmentIds = new ArrayList<>();
        ArrayList<Long> ownerIds = new ArrayList<>();
        for (EquipmentRecord record : page.getRecords()) {
            if (record.getEquipmentId() != null) {
                equipmentIds.add(record.getEquipmentId());
            }
            if (record.getOwnerId() != null) {
                ownerIds.add(record.getOwnerId());
            }
        }

        // 批量查询设备信息
        ArrayList<Equipment> equipments = new ArrayList<>();
        if (!equipmentIds.isEmpty()) {
            equipments = (ArrayList<Equipment>) equipmentService.listByIds(equipmentIds);
        }

        // 批量查询用户信息
        ArrayList<User> users = new ArrayList<>();
        if (!ownerIds.isEmpty()) {
            users = (ArrayList<User>) userService.listByIds(ownerIds);
        }

        // 转换为 Map 便于快速查找
        Map<Long, Equipment> equipmentMap = equipments.stream()
                .collect(Collectors.toMap(Equipment::getEquipmentId, e -> e));
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // 构建 VO 对象
        ArrayList<EquipmentRecordVo> voList = new ArrayList<>();
        for (EquipmentRecord record : page.getRecords()) {
            EquipmentRecordVo vo = new EquipmentRecordVo();
            
            // 设置基本信息
            vo.setRecordId(record.getRecordId());
            vo.setStatus(record.getStatus());
            
            // 从 Map 中获取设备信息
            Equipment equipment = equipmentMap.get(record.getEquipmentId());
            if (equipment != null) {
                vo.setEquipmentName(equipment.getEquipmentName());
                
                // 查询设备类型信息
                EquipmentType equipmentType = equipmentTypeService.getById(equipment.getEquipmentTypeId());
                if (equipmentType != null) {
                    vo.setTypeName(equipmentType.getEquipmentTypeName());
                }
            }

            // 从 Map 中获取用户信息
            User user = userMap.get(record.getOwnerId());
            if (user != null) {
                vo.setOwnerName(user.getName());
                vo.setOwnerPhone(user.getPhone());
            }

            voList.add(vo);
        }

        Page<EquipmentRecordVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(voList);
        return Result.ok(voPage);
    }

    @Override
    public Result searchByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return this.getByPage(pageNum, pageSize);
        }
        
        // 根据承包人姓名模糊查询用户
        List<User> matchedUsers = userService.lambdaQuery()
                .like(User::getName, keyword)
                .list();
        
        // 如果没有匹配的用户，返回空结果
        if (matchedUsers.isEmpty()) {
            Page<EquipmentRecordVo> emptyPage = new Page<>(pageNum, pageSize, 0);
            emptyPage.setRecords(new ArrayList<>());
            return Result.ok(emptyPage);
        }
        
        // 获取匹配的用户 ID 列表
        ArrayList<Long> ownerIds = matchedUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toCollection(ArrayList::new));

        // 根据用户 ID 分页查询设备记录
        Page<EquipmentRecord> page = this.lambdaQuery()
                .in(EquipmentRecord::getOwnerId, ownerIds)
                .page(new Page<>(pageNum, pageSize));

        // 收集所有的设备 ID
        ArrayList<Long> equipmentIds = new ArrayList<>();
        for (EquipmentRecord record : page.getRecords()) {
            if (record.getEquipmentId() != null) {
                equipmentIds.add(record.getEquipmentId());
            }
        }

        // 批量查询设备信息
        ArrayList<Equipment> equipments = new ArrayList<>();
        if (!equipmentIds.isEmpty()) {
            equipments = (ArrayList<Equipment>) equipmentService.listByIds(equipmentIds);
        }

        // 转换为 Map 便于快速查找
        Map<Long, Equipment> equipmentMap = equipments.stream()
                .collect(Collectors.toMap(Equipment::getEquipmentId, e -> e));
        Map<Long, User> userMap = matchedUsers.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // 构建 VO 对象
        ArrayList<EquipmentRecordVo> voList = new ArrayList<>();
        for (EquipmentRecord record : page.getRecords()) {
            EquipmentRecordVo vo = new EquipmentRecordVo();
            
            // 设置基本信息
            vo.setRecordId(record.getRecordId());
            vo.setStatus(record.getStatus());
            
            // 从 Map 中获取设备信息
            Equipment equipment = equipmentMap.get(record.getEquipmentId());
            if (equipment != null) {
                vo.setEquipmentName(equipment.getEquipmentName());
                
                // 查询设备类型信息
                EquipmentType equipmentType = equipmentTypeService.getById(equipment.getEquipmentTypeId());
                if (equipmentType != null) {
                    vo.setTypeName(equipmentType.getEquipmentTypeName());
                }
            }

            // 从 Map 中获取用户信息
            User user = userMap.get(record.getOwnerId());
            if (user != null) {
                vo.setOwnerName(user.getName());
                vo.setOwnerPhone(user.getPhone());
            }

            voList.add(vo);
        }

        Page<EquipmentRecordVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(voList);
        return Result.ok(voPage);
    }

    @Override
    @Transactional
    public Result updateStatus(EquipmentRecordDto equipmentRecordDto) {
        //获取设备ID
        EquipmentRecord one = this.lambdaQuery().eq(EquipmentRecord::getRecordId, equipmentRecordDto.getRecordId()).one();
        if (one==null){
            return Result.error("设备记录不存在");
        }
        Long equipmentId = one.getEquipmentId();
        int status = equipmentRecordDto.getStatus();
        //修改设备记录为报修中
        if(status==1){
            //修改设备为正在维修
            equipmentService.lambdaUpdate().eq(Equipment::getEquipmentId,equipmentId)
                    .set(Equipment::getStatus,2).update();
        }
        if (status==0){
            //修改设备为已被借用
            equipmentService.lambdaUpdate().eq(Equipment::getEquipmentId,equipmentId)
                    .set(Equipment::getStatus,1).update();
        }
        return this.lambdaUpdate().eq(EquipmentRecord::getRecordId,equipmentRecordDto.getRecordId())
                .set(EquipmentRecord::getStatus,equipmentRecordDto.getStatus())
                .update()?Result.ok():Result.error("更新失败");
    }
}
