package com.clj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.EquipmentApply;
import com.clj.domain.dto.EquipmentApplyDto;
import com.clj.domain.vo.EquipmentApplyVo;
import com.clj.domain.vo.EquipmentDetailVo;

/**
* @author ajie
* @description 针对表【equipment_apply(设备申请表)】的数据库操作Service
* @createDate 2026-03-02 20:08:34
*/
public interface EquipmentApplyService extends IService<EquipmentApply> {

    void add(EquipmentApplyDto equipmentApplyDto);

    void delete(Integer applyId);

    void updateApply(EquipmentApplyDto equipmentApplyDto);

    Page<EquipmentApplyVo> getApplyByPage(Integer pageNum, Integer pageSize);

    Page<EquipmentApplyVo> searchApplyByPage(String keyword, Integer pageNum, Integer pageSize);

    void updateApplyStatus(Long applyId, Integer status);

    EquipmentApplyVo getApplyVoById(Long applyId);

    EquipmentDetailVo getEquipmentNameAndTypeNameById(Long applyId);

    Page<EquipmentApplyVo> getMyApplies(String keyword, Integer pageNum, Integer pageSize);
}
