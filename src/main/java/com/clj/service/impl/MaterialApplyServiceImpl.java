package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.MaterialApply;
import com.clj.service.MaterialApplyService;
import com.clj.mapper.MaterialApplyMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【material_apply(农资申请表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:14
*/
@Service
public class MaterialApplyServiceImpl extends ServiceImpl<MaterialApplyMapper, MaterialApply>
    implements MaterialApplyService{

}




