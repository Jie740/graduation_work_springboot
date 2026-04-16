package com.clj.service;

import com.clj.domain.EquipmentRepairApply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.EquipmentRepairApplyDto;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【equipment_repair_apply(设备报修申请表)】的数据库操作Service
* @createDate 2026-03-02 20:08:29
*/
public interface EquipmentRepairApplyService extends IService<EquipmentRepairApply> {

    Result add(EquipmentRepairApplyDto equipmentRepairApplyDto);

    Result getRepairApplyByRecordId(Long recordId, String applicantName, String phone);
}
