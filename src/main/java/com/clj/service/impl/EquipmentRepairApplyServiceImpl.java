package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Equipment;
import com.clj.domain.EquipmentRecord;
import com.clj.domain.EquipmentRepairApply;
import com.clj.domain.User;
import com.clj.domain.dto.EquipmentRepairApplyDto;
import com.clj.domain.vo.EquipmentRepairApplyVo;
import com.clj.service.EquipmentRepairApplyService;
import com.clj.mapper.EquipmentRepairApplyMapper;
import com.clj.service.UserService;
import com.clj.service.EquipmentService;
import com.clj.service.EquipmentRecordService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author ajie
* @description 针对表【equipment_repair_apply(设备报修申请表)】的数据库操作 Service 实现
* @createDate 2026-03-02 20:08:29
*/
@Service
@RequiredArgsConstructor
public class EquipmentRepairApplyServiceImpl extends ServiceImpl<EquipmentRepairApplyMapper, EquipmentRepairApply>
    implements EquipmentRepairApplyService{

    private final UserService userService;
    private final EquipmentService equipmentService;
    private final EquipmentRecordService equipmentRecordService;

    @Override
    @Transactional
    public Result add(EquipmentRepairApplyDto equipmentRepairApplyDto) {
        // 根据姓名和电话查询用户是否存在
        User user = userService.lambdaQuery()
                .eq(User::getName, equipmentRepairApplyDto.getApplicantName())
                .eq(User::getPhone, equipmentRepairApplyDto.getApplicantPhone())
                .one();
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 获取用户 ID
        Long applicantId = user.getUserId();

        //根据用户ID查询设备是否存在
        EquipmentRecord one = equipmentRecordService.lambdaQuery().eq(EquipmentRecord::getOwnerId, applicantId)
                .eq(EquipmentRecord::getEquipmentId, equipmentRepairApplyDto.getEquipmentId())
                .one();
        if (one == null){
            return Result.error("设备非该用户借用");
        }

        // 创建 EquipmentRepairApply 对象并复制属性
        EquipmentRepairApply equipmentRepairApply = new EquipmentRepairApply();
        BeanUtils.copyProperties(equipmentRepairApplyDto, equipmentRepairApply);
        equipmentRepairApply.setApplicantId(applicantId);
        
        // 添加报修申请
        boolean saved = this.save(equipmentRepairApply);
        if (!saved) {
            return Result.error("添加报修申请失败");
        }
        
        // 修改设备表对应设备的状态为 2（正在维修）
        Equipment equipment = equipmentService.getById(equipmentRepairApplyDto.getEquipmentId());
        if (equipment != null) {
            equipment.setStatus(2);
            equipmentService.updateById(equipment);
        }
        
        // 修改设备记录表对应设备的状态为 1（报修中）
        EquipmentRecord equipmentRecord = equipmentRecordService.lambdaQuery()
                .eq(EquipmentRecord::getEquipmentId, equipmentRepairApplyDto.getEquipmentId())
                .one();
        if (equipmentRecord != null) {
            equipmentRecord.setStatus(1);
            equipmentRecordService.updateById(equipmentRecord);
        }
        
        return Result.ok();
    }

    @Override
    public Result getRepairApplyByRecordId(Long recordId, String applicantName, String phone) {
        // 1. 根据设备记录ID查询设备ID
        EquipmentRecord equipmentRecord = equipmentRecordService.lambdaQuery()
                .eq(EquipmentRecord::getRecordId, recordId)
                .one();
        
        if (equipmentRecord == null) {
            return Result.error("设备记录不存在");
        }
        
        Long equipmentId = equipmentRecord.getEquipmentId();
        
        // 2. 根据用户姓名和电话查询用户ID
        User user = userService.lambdaQuery()
                .eq(User::getName, applicantName)
                .eq(User::getPhone, phone)
                .one();
        
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        Long applicantId = user.getUserId();
        
        // 3. 根据设备ID和申请人ID查询报修申请记录
        EquipmentRepairApply repairApply = this.lambdaQuery()
                .eq(EquipmentRepairApply::getEquipmentId, equipmentId)
                .eq(EquipmentRepairApply::getApplicantId, applicantId)
                .orderByDesc(EquipmentRepairApply::getApplyTime)
                .last("LIMIT 1")
                .one();
        
        if (repairApply == null) {
            return Result.error("未找到报修申请记录");
        }
        
        // 4. 查询设备名称
        Equipment equipment = equipmentService.getById(equipmentId);
        if (equipment == null) {
            return Result.error("设备不存在");
        }
        
        // 5. 构建 VO 对象
        EquipmentRepairApplyVo vo = new EquipmentRepairApplyVo();
        vo.setApplicantName(applicantName);
        vo.setPhone(phone);
        vo.setEquipmentName(equipment.getEquipmentName());
        vo.setFaultDescription(repairApply.getFaultDescription());
        
        return Result.ok(vo);
    }
}




