package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.MaterialType;
import com.clj.service.MaterialTypeService;
import com.clj.mapper.MaterialTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author ajie
* @description 针对表【material_type(农资类型表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:10
*/
@Service
public class MaterialTypeServiceImpl extends ServiceImpl<MaterialTypeMapper, MaterialType>
    implements MaterialTypeService{

    @Override
    public void add(String materialTypeName) {
        MaterialType materialType = new MaterialType();
        materialType.setTypeName(materialTypeName);
        System.out.println(materialType);
        if (!this.save(materialType)) {
            throw new BusinessException("添加失败");
        }
    }

    @Override
    public void delete(Long materialTypeId) {
        if (!this.removeById(materialTypeId)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updateMaterialType(MaterialType materialType) {
        if (!this.updateById(materialType)) {
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public List<MaterialType> getAll() {
        return this.list();
    }

}




