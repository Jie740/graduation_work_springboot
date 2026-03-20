package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.MaterialType;
import com.clj.service.MaterialTypeService;
import com.clj.mapper.MaterialTypeMapper;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【material_type(农资类型表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:10
*/
@Service
public class MaterialTypeServiceImpl extends ServiceImpl<MaterialTypeMapper, MaterialType>
    implements MaterialTypeService{

    @Override
    public Result add(String materialTypeName) {
        MaterialType materialType = new MaterialType();
        materialType.setTypeName(materialTypeName);
        System.out.println(materialType);
        return this.save(materialType)?Result.ok():Result.error("添加失败");
    }

    @Override
    public Result delete(Long materialTypeId) {
        return this.removeById(materialTypeId)?Result.ok():Result.error("删除失败");
    }

    @Override
    public Result updateMaterialType(MaterialType materialType) {
        return this.updateById(materialType)?Result.ok():Result.error("修改失败");
    }

    @Override
    public Result getAll() {
        return Result.ok(this.list());
    }

}




