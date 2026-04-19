package com.clj.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.Material;
import com.clj.domain.MaterialApply;
import com.clj.domain.MaterialStockRecord;
import com.clj.domain.MaterialType;
import com.clj.domain.vo.MaterialVo;
import com.clj.mapper.ContractorMaterialStockMapper;
import com.clj.mapper.MaterialApplyMapper;
import com.clj.service.MaterialService;
import com.clj.mapper.MaterialMapper;
import com.clj.service.MaterialStockRecordService;
import com.clj.service.MaterialTypeService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

import static com.clj.constants.MaterialStockRecordConstants.INPUT;

/**
* @author ajie
* @description 针对表【material(农资表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:17
*/
@Service
@RequiredArgsConstructor
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material>
    implements MaterialService{
    final MaterialTypeService materialTypeService;
    final MaterialStockRecordService materialStockRecordService;
    final MaterialApplyMapper materialApplyMapper;
    final ContractorMaterialStockMapper contractorMaterialStockMapper;


    @Override
    public Result add(Material material) {
        //根据农资名和类型ID查询是否存在
        Material one = this.lambdaQuery().eq(Material::getMaterialName, material.getMaterialName())
                .eq(Material::getTypeId, material.getTypeId())
                .one();
        if (one != null){
            //存在，增加库存
            this.lambdaUpdate().eq(Material::getMaterialName, material.getMaterialName())
                    .eq(Material::getTypeId, material.getTypeId())
                    .set(Material::getStock, one.getStock()+material.getStock())
                    .update();
            //添加入库记录
            MaterialStockRecord materialStockRecord = new MaterialStockRecord();
            materialStockRecord.setMaterialId(one.getMaterialId());
            materialStockRecord.setType(INPUT);
            materialStockRecord.setQuantity(material.getStock());
            materialStockRecordService.add(materialStockRecord);
            return Result.ok("更新库存成功");
        }
        //先保存农资获取 ID
        boolean saved = this.save(material);
        if (!saved || material.getMaterialId() == null) {
            return Result.error("添加失败");
        }
        //添加入库记录
        MaterialStockRecord materialStockRecord = new MaterialStockRecord();
        materialStockRecord.setMaterialId(material.getMaterialId());
        materialStockRecord.setType(INPUT);
        materialStockRecord.setQuantity(material.getStock());
        materialStockRecordService.add(materialStockRecord);
        return Result.ok();
    }

    @Override
    public Result delete(Long materialId) {
        // 删除农资审批表中对应的记录
        materialApplyMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MaterialApply>()
                .eq(MaterialApply::getMaterialId, materialId)
        );
        
        // 删除承包人农资库存表中对应的记录
        contractorMaterialStockMapper.delete(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ContractorMaterialStock>()
                .eq(ContractorMaterialStock::getMaterialId, materialId)
        );
        
        // 删除农资记录
        return this.removeById(materialId)? Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result updateMaterial(Material material) {
        return this.updateById(material)? Result.ok() : Result.error("更新失败");
    }

    @Override
    public Result searchMaterialsByPage(Long typeId, String keyword, Integer pageNum, Integer pageSize) {
        // 无条件查询：返回所有农资
        if (typeId == null && (keyword == null|| keyword.isEmpty())) {
            return getMaterialsByPage(pageNum, pageSize);
        }
        
        // 只按类型查询
        if (typeId != null && (keyword == null||keyword.isEmpty())) {
            return searchMaterialsPageByTypeId(typeId, pageNum, pageSize);
        }
        
        // 只按名称查询
        if (typeId == null) {
            return searchMaterialsByName(keyword, pageNum, pageSize);
        }
        
        // 组合查询：类型 + 名称
        return searchMaterialsPageByTypeIdAndName(typeId, keyword, pageNum, pageSize);
    }

    @Override
    public Result getMaterialsByPage(Integer pageNum, Integer pageSize) {
        Page<Material> materialPage = new Page<>(pageNum,pageSize);
        Page<Material> page = this.page(materialPage);
        ArrayList<MaterialVo> materialVos = convertToMaterialVos(page.getRecords());
        fillTypeNames(materialVos);
        Page<MaterialVo> materialVoPage = new Page<MaterialVo>(pageNum, pageSize, page.getTotal()).setRecords(materialVos);
        return Result.ok(materialVoPage);
    }

    @Override
    public Result getAll() {
        return Result.ok(this.list());
    }

    @Override
    public Result getMaterialTypeById(Long materialId) {
        Material material = this.getById(materialId);
        if (material == null){
            return Result.error("该农资不存在");
        }
        MaterialType one = materialTypeService.lambdaQuery().eq(MaterialType::getTypeId, material.getTypeId())
                .one();
        HashMap<String, String> map = new HashMap<>();
        map.put("typeName", one.getTypeName());
        return Result.ok(map);
    }

    //根据类型 ID 查询
    public Result searchMaterialsPageByTypeId(Long typeId,Integer pageNum, Integer pageSize) {
        Page<Material> page = this.lambdaQuery()
                .eq(Material::getTypeId, typeId)
                .page(new Page<>(pageNum, pageSize));
        ArrayList<MaterialVo> materialVos = convertToMaterialVos(page.getRecords());
        fillTypeNamesByTypeId(materialVos, typeId);
        Page<MaterialVo> materialVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        materialVoPage.setRecords(materialVos);
        return Result.ok(materialVoPage);
    }
        
    /**
     * 根据农资名称模糊查询
     */
    public Result searchMaterialsByName(String keyword, Integer pageNum, Integer pageSize) {
        Page<Material> page = this.lambdaQuery()
                .like(Material::getMaterialName, keyword)
                .page(new Page<>(pageNum, pageSize));
        ArrayList<MaterialVo> materialVos = convertToMaterialVos(page.getRecords());
        fillTypeNames(materialVos);
        Page<MaterialVo> materialVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        materialVoPage.setRecords(materialVos);
        return Result.ok(materialVoPage);
    }
        
    /**
     * 根据类型 ID 和农资名称组合查询（同时满足两个条件）
     */
    public Result searchMaterialsPageByTypeIdAndName(Long typeId, String keyword, Integer pageNum, Integer pageSize) {
        Page<Material> page = this.lambdaQuery()
                .eq(Material::getTypeId, typeId)
                .like(Material::getMaterialName, keyword)
                .page(new Page<>(pageNum, pageSize));
        ArrayList<MaterialVo> materialVos = convertToMaterialVos(page.getRecords());
        fillTypeNamesByTypeId(materialVos, typeId);
        Page<MaterialVo> materialVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        materialVoPage.setRecords(materialVos);
        return Result.ok(materialVoPage);
    }
    
    /**
     * 将 Material 列表转换为 MaterialVo 列表
     */
    private ArrayList<MaterialVo> convertToMaterialVos(java.util.List<Material> materials) {
        ArrayList<MaterialVo> materialVos = new ArrayList<>();
        for (Material record : materials) {
            MaterialVo materialVo = new MaterialVo();
            BeanUtils.copyProperties(record, materialVo);
            materialVos.add(materialVo);
        }
        return materialVos;
    }
    
    /**
     * 批量填充类型名称（适用于多种类型的情况）
     */
    private void fillTypeNames(ArrayList<MaterialVo> materialVos) {
        if (materialVos.isEmpty()) {
            return;
        }
        // 提取所有不重复的 typeId
        java.util.Set<Long> typeIds = new java.util.HashSet<>();
        for (MaterialVo vo : materialVos) {
            if (vo.getTypeId() != null) {
                typeIds.add(vo.getTypeId());
            }
        }
            
        // 批量查询类型信息
        if (!typeIds.isEmpty()) {
            java.util.List<MaterialType> materialTypes = materialTypeService.listByIds(typeIds);
            java.util.Map<Long, String> typeNameMap = new java.util.HashMap<>();
            for (MaterialType type : materialTypes) {
                typeNameMap.put(type.getTypeId(), type.getTypeName());
            }
                
            // 填充类型名称
            for (MaterialVo vo : materialVos) {
                if (vo.getTypeId() != null && typeNameMap.containsKey(vo.getTypeId())) {
                    vo.setTypeName(typeNameMap.get(vo.getTypeId()));
                }
            }
        }
    }
    
    /**
     * 根据单一 typeId 填充类型名称（适用于已知所有记录都是同一类型的情况）
     */
    private void fillTypeNamesByTypeId(ArrayList<MaterialVo> materialVos, Long typeId) {
        if (materialVos.isEmpty() || typeId == null) {
            return;
        }
        MaterialType materialType = materialTypeService.getById(typeId);
        if (materialType != null) {
            for (MaterialVo vo : materialVos) {
                vo.setTypeName(materialType.getTypeName());
            }
        }
    }
}




