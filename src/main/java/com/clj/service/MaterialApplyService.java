package com.clj.service;

import com.clj.domain.MaterialApply;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.MaterialApplyDto;
import com.clj.domain.vo.MaterialApplyVo;

/**
* @author ajie
* @description 针对表【material_apply(农资申请表)】的数据库操作Service
* @createDate 2026-03-02 20:08:14
*/
public interface MaterialApplyService extends IService<MaterialApply> {

    Page<MaterialApplyVo> getMaterialApplyByPage(Integer pageNum, Integer pageSize);

    void add(MaterialApplyDto materialApplyDto);

    Page<MaterialApplyVo> searchMaterialApplyByPage(String keyword, Integer pageNum, Integer pageSize);

    void delete(Long applyId);

    void updateMaterialApplyStatus(Long applyId, Integer status);

    MaterialApplyVo getMaterialApplyVoById(Long applyId);

    void updateApply(MaterialApplyDto materialApplyDto);

    Page<MaterialApplyVo> getMyApplies(String keyword, Integer pageNum, Integer pageSize);
}
