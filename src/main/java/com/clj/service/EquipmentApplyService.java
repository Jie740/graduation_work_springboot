package com.clj.service;

import com.clj.domain.EquipmentApply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.EquipmentApplyDto;
import com.clj.domain.vo.EquipmentApplyVo;
import com.clj.domain.vo.MaterialApplyVo;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【equipment_apply(设备申请表)】的数据库操作Service
* @createDate 2026-03-02 20:08:34
*/
public interface EquipmentApplyService extends IService<EquipmentApply> {

    Result add(EquipmentApplyDto equipmentApplyDto);

    Result delete(Integer applyId);

    Result updateApply(EquipmentApplyDto equipmentApplyDto);

    Result getApplyByPage(Integer pageNum, Integer pageSize);

    Result searchApplyByPage(String keyword, Integer pageNum, Integer pageSize);

    Result updateApplyStatus(Long applyId, Integer status);

    EquipmentApplyVo getApplyVoById(Long applyId);

    Result getEquipmentNameAndTypeNameById(Long applyId);
}
