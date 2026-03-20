package com.clj.service;

import com.clj.domain.MaterialType;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【material_type(农资类型表)】的数据库操作Service
* @createDate 2026-03-02 20:08:10
*/
public interface MaterialTypeService extends IService<MaterialType> {

    Result add(String materialTypeName);

    Result delete(Long materialTypeId);

    Result updateMaterialType(MaterialType materialType);

    Result getAll();

}
