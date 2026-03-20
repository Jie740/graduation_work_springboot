package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.*;
import com.clj.domain.dto.EquipmentApplyDto;
import com.clj.domain.vo.EquipmentApplyVo;
import com.clj.domain.vo.EquipmentDetailVo;
import com.clj.service.*;
import com.clj.mapper.EquipmentApplyMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author ajie
* @description 针对表【equipment_apply(设备申请表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:34
*/
@Service
@RequiredArgsConstructor
public class EquipmentApplyServiceImpl extends ServiceImpl<EquipmentApplyMapper, EquipmentApply>
    implements EquipmentApplyService{
    final UserService userService;
    final EquipmentService equipmentService;
    final EquipmentRecordService equipmentRecordService;
    final EquipmentTypeService equipmentTypeService;

    @Override
    public Result add(EquipmentApplyDto equipmentApplyDto) {
        User user = userService.lambdaQuery().eq(User::getPhone, equipmentApplyDto.getPhone())
                .eq(User::getName, equipmentApplyDto.getApplicant()).one();
        if (user == null) {
            return Result.error("用户不存在");
        }
        EquipmentApply equipmentApply = new EquipmentApply();
        equipmentApply.setApplicantId(user.getUserId());
        BeanUtils.copyProperties(equipmentApplyDto, equipmentApply);
        return save(equipmentApply) ? Result.ok() : Result.error("添加失败");
    }

    @Override
    public Result delete(Integer applyId) {
        return removeById(applyId) ? Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result updateApply(EquipmentApplyDto equipmentApplyDto) {
        User user = userService.lambdaQuery().eq(User::getPhone, equipmentApplyDto.getPhone())
                .eq(User::getName, equipmentApplyDto.getApplicant()).one();
        if (user == null) {
            return Result.error("用户不存在");
        }
        EquipmentApply equipmentApply = new EquipmentApply();
        equipmentApply.setApplicantId(user.getUserId());
        BeanUtils.copyProperties(equipmentApplyDto, equipmentApply);
        return updateById(equipmentApply) ? Result.ok() : Result.error("更新失败");
    }

    @Override
    public Result getApplyByPage(Integer pageNum, Integer pageSize) {
        Page<EquipmentApply> page = this.lambdaQuery().page(new Page<>(pageNum, pageSize));
        
        // 批量查询所有申请人 ID 和设备 ID
        List<EquipmentApply> applies = page.getRecords();
        Set<Long> applicantIds = applies.stream()
                .map(EquipmentApply::getApplicantId)
                .collect(Collectors.toSet());
        Set<Long> equipmentIds = applies.stream()
                .map(EquipmentApply::getEquipmentId)
                .collect(Collectors.toSet());
        
        // 批量查询用户列表和设备列表
        List<User> users = userService.lambdaQuery()
                .in(User::getUserId, applicantIds)
                .list();
        List<Equipment> equipments = equipmentService.lambdaQuery()
                .in(Equipment::getEquipmentId, equipmentIds)
                .list();
        
        // 构建映射关系
        java.util.Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));
        java.util.Map<Long, Equipment> equipmentMap = equipments.stream()
                .collect(Collectors.toMap(Equipment::getEquipmentId, equipment -> equipment));
        
        // 组装 VO 对象
        ArrayList<EquipmentApplyVo> equipmentApplyVos = new ArrayList<>();
        for (EquipmentApply apply : applies) {
            EquipmentApplyVo equipmentApplyVo = new EquipmentApplyVo();
            BeanUtils.copyProperties(apply, equipmentApplyVo);
            
            User user = userMap.get(apply.getApplicantId());
            if (user != null) {
                equipmentApplyVo.setApplicant(user.getName());
                equipmentApplyVo.setPhone(user.getPhone());
            }
            
            Equipment equipment = equipmentMap.get(apply.getEquipmentId());
            if (equipment != null) {
                equipmentApplyVo.setEquipmentName(equipment.getEquipmentName());
            }
            
            equipmentApplyVos.add(equipmentApplyVo);
        }
        
        Page<EquipmentApplyVo> equipmentApplyVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        equipmentApplyVoPage.setRecords(equipmentApplyVos);
        return Result.ok(equipmentApplyVoPage);
    }

    @Override
    public Result searchApplyByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return getApplyByPage(pageNum, pageSize);
        }
        
        // 根据 keyword 查询用户 ID 列表（模糊匹配姓名或手机号）
        List<User> users = userService.lambdaQuery()
                .like(User::getName, keyword)
                .list();
        Set<Long> userIds = users.stream()
                .map(User::getUserId)
                .collect(Collectors.toSet());
        
        // 根据 keyword 查询设备 ID 列表（模糊匹配设备名称）
        List<Equipment> equipments = equipmentService.lambdaQuery()
                .like(Equipment::getEquipmentName, keyword)
                .list();
        Set<Long> equipmentIds = equipments.stream()
                .map(Equipment::getEquipmentId)
                .collect(Collectors.toSet());
        
        // 根据用户 ID 和设备 ID 分页查询申请记录
        Page<EquipmentApply> page = new Page<>(pageNum, pageSize);
        Page<EquipmentApply> applyPage = this.lambdaQuery()
                .in(!userIds.isEmpty(), EquipmentApply::getApplicantId, userIds)
                .or()
                .in(!equipmentIds.isEmpty(), EquipmentApply::getEquipmentId, equipmentIds)
                .page(page);
        
        // 批量查询申请人和设备信息
        List<EquipmentApply> applies = applyPage.getRecords();
        Set<Long> applicantIds = applies.stream()
                .map(EquipmentApply::getApplicantId)
                .collect(Collectors.toSet());
        Set<Long> applyEquipmentIds = applies.stream()
                .map(EquipmentApply::getEquipmentId)
                .collect(Collectors.toSet());
        
        // 批量查询用户列表和设备列表
        List<User> userList = userService.lambdaQuery()
                .in(User::getUserId, applicantIds)
                .list();
        List<Equipment> equipmentList = equipmentService.lambdaQuery()
                .in(Equipment::getEquipmentId, applyEquipmentIds)
                .list();
        
        // 构建映射关系
        java.util.Map<Long, User> userMap = userList.stream()
                .collect(Collectors.toMap(User::getUserId, user -> user));
        java.util.Map<Long, Equipment> equipmentMap = equipmentList.stream()
                .collect(Collectors.toMap(Equipment::getEquipmentId, equipment -> equipment));
        
        // 组装 VO 对象
        ArrayList<EquipmentApplyVo> equipmentApplyVos = new ArrayList<>();
        for (EquipmentApply apply : applies) {
            EquipmentApplyVo equipmentApplyVo = new EquipmentApplyVo();
            BeanUtils.copyProperties(apply, equipmentApplyVo);
            
            User user = userMap.get(apply.getApplicantId());
            if (user != null) {
                equipmentApplyVo.setApplicant(user.getName());
                equipmentApplyVo.setPhone(user.getPhone());
            }
            
            Equipment equipment = equipmentMap.get(apply.getEquipmentId());
            if (equipment != null) {
                equipmentApplyVo.setEquipmentName(equipment.getEquipmentName());
            }
            
            equipmentApplyVos.add(equipmentApplyVo);
        }
        
        Page<EquipmentApplyVo> equipmentApplyVoPage = new Page<>(pageNum, pageSize, applyPage.getTotal());
        equipmentApplyVoPage.setRecords(equipmentApplyVos);
        return Result.ok(equipmentApplyVoPage);
    }

    @Override
    @Transactional
    public Result updateApplyStatus(Long applyId, Integer status) {
        EquipmentApply apply = this.lambdaQuery().eq(EquipmentApply::getApplyId, applyId).one();
        Long equipmentId = apply.getEquipmentId();
        //查询设备是否存在
        Equipment equipment = equipmentService.lambdaQuery().eq(Equipment::getEquipmentId, equipmentId).one();
        if (equipment == null) {
            return Result.error("设备不存在");
        }
        //审批通过
        if (status == 1) {
            Integer equipmentStatus = equipment.getStatus();
            if (equipmentStatus == 1) {
                return Result.error("设备已借出");
            }
            if (equipmentStatus == 2) {
                return Result.error("设备正在报修");
            }
            if (equipmentStatus == 3) {
                return Result.error("设备已损坏");
            }
            //更新设备状态
            equipmentService.lambdaUpdate().eq(Equipment::getEquipmentId, equipmentId)
                    .set(Equipment::getStatus, 1)
                    .update();
            //增加用户设备记录
            EquipmentRecord equipmentRecord = new EquipmentRecord();
            equipmentRecord.setEquipmentId(equipmentId);
            equipmentRecord.setOwnerId(apply.getApplicantId());
            equipmentRecordService.add(equipmentRecord);
            //修改申请状态
            this.lambdaUpdate().eq(EquipmentApply::getApplyId, applyId)
                    .set(EquipmentApply::getStatus, status)
                    .update();
            return Result.ok();
        }
        //审批未通过
        if (status == 2) {
            this.lambdaUpdate().eq(EquipmentApply::getApplyId, applyId)
                    .set(EquipmentApply::getStatus, status)
                    .update();
            return Result.ok();
        }
        return Result.error("参数错误");
    }

    @Override
    public EquipmentApplyVo getApplyVoById(Long applyId) {
        EquipmentApply apply = this.lambdaQuery().eq(EquipmentApply::getApplyId, applyId)
                .one();
        EquipmentApplyVo equipmentApplyVo = new EquipmentApplyVo();
        BeanUtils.copyProperties(apply, equipmentApplyVo);
        User one = userService.lambdaQuery().eq(User::getUserId, apply.getApplicantId()).one();
        equipmentApplyVo.setApplicant(one.getName());
        equipmentApplyVo.setPhone(one.getPhone());
        Equipment equipment = equipmentService.lambdaQuery().eq(Equipment::getEquipmentId, apply.getEquipmentId()).one();
        equipmentApplyVo.setEquipmentName(equipment.getEquipmentName());
        return equipmentApplyVo;
    }

    @Override
    public Result getEquipmentNameAndTypeNameById(Long applyId) {
        EquipmentDetailVo equipmentDetailVo = new EquipmentDetailVo();
        EquipmentApply one = this.lambdaQuery().eq(EquipmentApply::getApplyId, applyId).one();
        Long equipmentId = one.getEquipmentId();
        Equipment one1 = equipmentService.lambdaQuery().eq(Equipment::getEquipmentId, equipmentId).one();
        Long equipmentTypeId = one1.getEquipmentTypeId();
        EquipmentType one2 = equipmentTypeService.lambdaQuery().eq(EquipmentType::getEquipmentTypeId, equipmentTypeId).one();
        equipmentDetailVo.setEquipmentId(equipmentId);
        equipmentDetailVo.setEquipmentName(one1.getEquipmentName());
        equipmentDetailVo.setTypeName(one2.getEquipmentTypeName());
        return Result.ok(equipmentDetailVo);
    }
}




