package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Material;
import com.clj.domain.MaterialStockRecord;
import com.clj.domain.MaterialType;
import com.clj.domain.vo.MaterialStockRecordVo;
import com.clj.mapper.MaterialMapper;
import com.clj.mapper.MaterialTypeMapper;
import com.clj.service.MaterialStockRecordService;
import com.clj.mapper.MaterialStockRecordMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author ajie
* @description 针对表【material_stock_record(农资出入库记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:12
*/
@Service
@RequiredArgsConstructor
public class MaterialStockRecordServiceImpl extends ServiceImpl<MaterialStockRecordMapper, MaterialStockRecord>
    implements MaterialStockRecordService{

    private final MaterialMapper materialMapper;
    private final MaterialTypeMapper materialTypeMapper;

    @Override
    public Result add(MaterialStockRecord materialStockRecord) {
        return this.save(materialStockRecord) ? Result.ok() : Result.error("添加失败");
    }

    @Override
    public Result delete(Long stockRecordId) {
        return this.removeById(stockRecordId) ? Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result update(MaterialStockRecord materialStockRecord) {
        return this.updateById(materialStockRecord) ? Result.ok() : Result.error("更新失败");
    }

    @Override
    public Result getByPage(String keyword, Integer pageNum, Integer pageSize) {
        Page<MaterialStockRecord> page;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            page = this.page(new Page<>(pageNum, pageSize));
        } else {
            List<Material> materials = materialMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Material>()
                            .like(Material::getMaterialName, keyword)
            );
            
            if (materials.isEmpty()) {
                Page<MaterialStockRecordVo> emptyPage = new Page<>(pageNum, pageSize, 0);
                emptyPage.setRecords(new ArrayList<>());
                return Result.ok(emptyPage);
            }
            
            List<Long> materialIds = materials.stream()
                    .map(Material::getMaterialId)
                    .collect(Collectors.toList());
            
            page = this.lambdaQuery()
                    .in(MaterialStockRecord::getMaterialId, materialIds)
                    .page(new Page<>(pageNum, pageSize));
        }

        List<Long> materialIds = page.getRecords().stream()
                .map(MaterialStockRecord::getMaterialId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Material> materialMap = new java.util.HashMap<>();
        if (!materialIds.isEmpty()) {
            List<Material> materials = materialMapper.selectBatchIds(materialIds);
            materialMap = materials.stream()
                    .collect(Collectors.toMap(Material::getMaterialId, m -> m));
        }

        List<Long> typeIds = materialMap.values().stream()
                .map(Material::getTypeId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, MaterialType> typeMap = new java.util.HashMap<>();
        if (!typeIds.isEmpty()) {
            List<MaterialType> materialTypes = materialTypeMapper.selectBatchIds(typeIds);
            typeMap = materialTypes.stream()
                    .collect(Collectors.toMap(MaterialType::getTypeId, t -> t));
        }

        List<MaterialStockRecordVo> voList = new ArrayList<>();
        for (MaterialStockRecord record : page.getRecords()) {
            MaterialStockRecordVo vo = new MaterialStockRecordVo();
            vo.setStockRecordId(record.getStockRecordId());
            vo.setRecordType(record.getType());
            vo.setQuantity(record.getQuantity());
            vo.setCreateTime(record.getCreateTime());

            Material material = materialMap.get(record.getMaterialId());
            if (material != null) {
                vo.setMaterialName(material.getMaterialName());
                MaterialType materialType = typeMap.get(material.getTypeId());
                if (materialType != null) {
                    vo.setType(materialType.getTypeName());
                }
            }

            voList.add(vo);
        }

        Page<MaterialStockRecordVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(voList);
        return Result.ok(voPage);
    }
}




