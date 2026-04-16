package com.clj.service;

import com.clj.domain.MaterialApply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.domain.dto.MaterialApplyDto;
import com.clj.domain.vo.MaterialApplyVo;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【material_apply(农资申请表)】的数据库操作Service
* @createDate 2026-03-02 20:08:14
*/
public interface MaterialApplyService extends IService<MaterialApply> {

    Result getMaterialApplyByPage(Integer pageNum, Integer pageSize);

    Result add(MaterialApplyDto materialApplyDto);

    Result searchMaterialApplyByPage(String keyword, Integer pageNum, Integer pageSize);

    Result delete(Long applyId);

    Result updateMaterialApplyStatus(Long applyId, Integer status);

    MaterialApplyVo getMaterialApplyVoById(Long applyId);

    Result updateApply(MaterialApplyDto materialApplyDto);

    Result getMyApplies(String keyword, Integer pageNum, Integer pageSize);
}
