package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.MaterialType;
import com.clj.service.MaterialTypeService;
import com.clj.mapper.MaterialTypeMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【material_type(农资类型表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:10
*/
@Service
public class MaterialTypeServiceImpl extends ServiceImpl<MaterialTypeMapper, MaterialType>
    implements MaterialTypeService{

}




