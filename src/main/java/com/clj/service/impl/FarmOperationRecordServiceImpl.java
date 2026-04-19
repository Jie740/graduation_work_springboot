package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.FarmOperationRecord;
import com.clj.domain.Material;
import com.clj.domain.MatureCrop;
import com.clj.domain.PlantingRecord;
import com.clj.domain.User;
import com.clj.domain.dto.FarmOperationDto;
import com.clj.domain.vo.FarmOperationRecordVo;
import com.clj.service.ContractorMaterialStockService;
import com.clj.service.FarmOperationRecordService;
import com.clj.mapper.FarmOperationRecordMapper;
import com.clj.service.MaterialService;
import com.clj.service.MatureCropService;
import com.clj.service.PlantingPlanService;
import com.clj.service.PlantingRecordService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;









import java.util.ArrayList;
import java.util.List;

/**
* @author ajie
* @description 针对表【farm_operation_record(农事活动记录表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:24
*/
@Service
@RequiredArgsConstructor
public class FarmOperationRecordServiceImpl extends ServiceImpl<FarmOperationRecordMapper, FarmOperationRecord>
    implements FarmOperationRecordService{
    final UserService userService;
    final MaterialService materialService;
    final ContractorMaterialStockService contractorMaterialStockService;
    final MatureCropService matureCropService;
    final PlantingRecordService plantingRecordService;
    final PlantingPlanService plantingPlanService;
    @Override
    @Transactional
    public Result add(FarmOperationDto farmOperationDto) {
        User one = this.userService.lambdaQuery().eq(User::getPhone, farmOperationDto.getPhone())
                .eq(User::getName, farmOperationDto.getOperatorName())
                .one();
        if (one == null){
            return Result.error("用户不存在");
        }
        Long userId = one.getUserId();
        FarmOperationRecord record = new FarmOperationRecord();
        BeanUtils.copyProperties(farmOperationDto, record);
        record.setUserId(userId);
        
        //如果没有用到农资
        if (farmOperationDto.getMaterialId() == null){
            boolean saveResult = this.save(record);
            if (!saveResult) {
                return Result.error("保存失败");
            }
            // 如果操作类型是收割，添加成熟作物记录
            if ("收割".equals(farmOperationDto.getOperationType())) {
                if (farmOperationDto.getOutputQuantity() == null || farmOperationDto.getOutputQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    return Result.error("产出数量必须大于0");
                }
                if (farmOperationDto.getOperationTime() == null) {
                    return Result.error("操作时间不能为空");
                }
                
                // 查询是否已存在该种植记录的成熟作物记录
                MatureCrop existingMatureCrop = matureCropService.lambdaQuery()
                        .eq(MatureCrop::getRecordId, farmOperationDto.getRecordId())
                        .one();
                
                if (existingMatureCrop != null) {
                    // 在原有产量基础上累加
                    java.math.BigDecimal newQuantity = existingMatureCrop.getOutputQuantity().add(farmOperationDto.getOutputQuantity());
                    existingMatureCrop.setOutputQuantity(newQuantity);
                    existingMatureCrop.setHarvestTime(farmOperationDto.getOperationTime());
                    boolean updateResult = matureCropService.updateById(existingMatureCrop);
                    if (!updateResult) {
                        return Result.error("更新成熟作物记录失败");
                    }
                } else {
                    // 添加新记录
                    MatureCrop matureCrop = new MatureCrop();
                    matureCrop.setRecordId(farmOperationDto.getRecordId());
                    matureCrop.setOutputQuantity(farmOperationDto.getOutputQuantity());
                    matureCrop.setHarvestTime(farmOperationDto.getOperationTime());
                    boolean addResult = matureCropService.save(matureCrop);
                    if (!addResult) {
                        return Result.error("添加成熟作物记录失败");
                    }
                }
                
                // 更新种植记录状态为已收割（状态1）
                boolean updatePlantingResult = plantingRecordService.lambdaUpdate()
                        .eq(PlantingRecord::getRecordId, farmOperationDto.getRecordId())
                        .set(PlantingRecord::getActualHarvestDate, farmOperationDto.getOperationTime())
                        .set(PlantingRecord::getStatus, 1)
                        .update();
                if (!updatePlantingResult) {
                    return Result.error("更新种植记录状态失败");
                }
                
                // 查询种植记录获取planId，更新计划状态为已完成（状态3）
                PlantingRecord plantingRecord = plantingRecordService.getById(farmOperationDto.getRecordId());
                if (plantingRecord != null && plantingRecord.getPlanId() != null) {
                    plantingPlanService.updateStatus(plantingRecord.getPlanId(), 3);
                }
            }
            return Result.ok();
        }
        
        //查询对应用户农资库存是否足够
        ContractorMaterialStock one1 = contractorMaterialStockService.lambdaQuery()
                .eq(ContractorMaterialStock::getMaterialId, farmOperationDto.getMaterialId())
                .eq(ContractorMaterialStock::getUserId, userId)
                .one();
        if (one1 == null || one1.getStock()<farmOperationDto.getQuantity()){
            return Result.error("农资库存不足");
        }
        // 库存足够,扣减库存
        contractorMaterialStockService.lambdaUpdate().eq(ContractorMaterialStock::getMaterialId, farmOperationDto.getMaterialId())
                .eq(ContractorMaterialStock::getUserId, userId)
                .set(ContractorMaterialStock::getStock, one1.getStock()-farmOperationDto.getQuantity())
                .update();
        // 保存农事活动记录
        boolean saveResult = this.save(record);
        if (!saveResult) {
            return Result.error("保存失败");
        }
        
        // 如果操作类型是收割，添加成熟作物记录
        if ("收割".equals(farmOperationDto.getOperationType())) {
            if (farmOperationDto.getOutputQuantity() == null || farmOperationDto.getOutputQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return Result.error("产出数量必须大于0");
            }
            if (farmOperationDto.getOperationTime() == null) {
                return Result.error("操作时间不能为空");
            }
            
            // 查询是否已存在该种植记录的成熟作物记录
            MatureCrop existingMatureCrop = matureCropService.lambdaQuery()
                    .eq(MatureCrop::getRecordId, farmOperationDto.getRecordId())
                    .one();
            
            if (existingMatureCrop != null) {
                // 在原有产量基础上累加
                java.math.BigDecimal newQuantity = existingMatureCrop.getOutputQuantity().add(farmOperationDto.getOutputQuantity());
                existingMatureCrop.setOutputQuantity(newQuantity);
                existingMatureCrop.setHarvestTime(farmOperationDto.getOperationTime());
                boolean updateResult = matureCropService.updateById(existingMatureCrop);
                if (!updateResult) {
                    return Result.error("更新成熟作物记录失败");
                }
            } else {
                // 添加新记录
                MatureCrop matureCrop = new MatureCrop();
                matureCrop.setRecordId(farmOperationDto.getRecordId());
                matureCrop.setOutputQuantity(farmOperationDto.getOutputQuantity());
                matureCrop.setHarvestTime(farmOperationDto.getOperationTime());
                boolean addResult = matureCropService.save(matureCrop);
                if (!addResult) {
                    return Result.error("添加成熟作物记录失败");
                }
            }
            
            // 更新种植记录状态为已收割（状态1）
            boolean updatePlantingResult = plantingRecordService.lambdaUpdate()
                    .eq(PlantingRecord::getRecordId, farmOperationDto.getRecordId())
                    .set(PlantingRecord::getActualHarvestDate, farmOperationDto.getOperationTime())
                    .set(PlantingRecord::getStatus, 1)
                    .update();
            if (!updatePlantingResult) {
                return Result.error("更新种植记录状态失败");
            }
            
            // 查询种植记录获取planId，更新计划状态为已完成（状态3）
            PlantingRecord plantingRecord = plantingRecordService.getById(farmOperationDto.getRecordId());
            if (plantingRecord != null && plantingRecord.getPlanId() != null) {
                plantingPlanService.updateStatus(plantingRecord.getPlanId(), 3);
            }
        }
        
        return Result.ok();
    }

    @Override
    public Result getFarmOperationRecordById(Long recordId, Integer pageNum, Integer pageSize) {
        Page<FarmOperationRecord> farmOperationRecordPage = new Page<>(pageNum, pageSize);
        Page<FarmOperationRecord> page = this.lambdaQuery().eq(FarmOperationRecord::getRecordId, recordId)
                .page(farmOperationRecordPage);
            
        // 收集所有的用户 ID 和农资 ID
        ArrayList<Long> userIds = new ArrayList<>();
        ArrayList<Long> materialIds = new ArrayList<>();
        for (FarmOperationRecord record : page.getRecords()) {
            if (record.getUserId() != null) {
                userIds.add(record.getUserId());
            }
            if (record.getMaterialId() != null) {
                materialIds.add(record.getMaterialId());
            }
        }
            
        // 批量查询用户信息
        ArrayList<User> users = new ArrayList<>();
        if (!userIds.isEmpty()) {
            users = (ArrayList<User>) userService.listByIds(userIds);
        }
            
        // 批量查询农资信息
        ArrayList<Material> materials = new ArrayList<>();
        if (!materialIds.isEmpty()) {
            materials = (ArrayList<Material>) materialService.listByIds(materialIds);
        }
            
        // 转换为 Map 便于快速查找
        java.util.Map<Long, User> userMap = users.stream()
                .collect(java.util.stream.Collectors.toMap(User::getUserId, user -> user));
        java.util.Map<Long, Material> materialMap = materials.stream()
                .collect(java.util.stream.Collectors.toMap(Material::getMaterialId, material -> material));
            
        // 构建 VO 对象
        ArrayList<FarmOperationRecordVo> farmOperationRecordVos = new ArrayList<>();
        for (FarmOperationRecord record : page.getRecords()) {
            FarmOperationRecordVo farmOperationRecordVo = new FarmOperationRecordVo();
            BeanUtils.copyProperties(record, farmOperationRecordVo);
                
            // 从 Map 中获取用户信息
            User user = userMap.get(record.getUserId());
            if (user != null) {
                farmOperationRecordVo.setOperatorName(user.getName());
                farmOperationRecordVo.setPhone(user.getPhone());
            }
                
            // 从 Map 中获取农资信息
            Material material = materialMap.get(record.getMaterialId());
            if (material != null) {
                farmOperationRecordVo.setMaterialId(material.getMaterialId());
                farmOperationRecordVo.setMaterialName(material.getMaterialName());
            }
                
            farmOperationRecordVos.add(farmOperationRecordVo);
        }
            
        Page<FarmOperationRecordVo> farmOperationRecordVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        farmOperationRecordVoPage.setRecords(farmOperationRecordVos);
        return Result.ok(farmOperationRecordVoPage);
    }

    @Override
    @Transactional
    public Result updateFamrOperation(FarmOperationDto farmOperationDto) {
        // 查询原记录
        FarmOperationRecord oldRecord = this.getById(farmOperationDto.getOperationId());
        if (oldRecord == null) {
            return Result.error("农事活动记录不存在");
        }
        
        // 查询用户是否存在
        User one = this.userService.lambdaQuery().eq(User::getPhone, farmOperationDto.getPhone())
                .eq(User::getName, farmOperationDto.getOperatorName())
                .one();
        if (one == null){
            return Result.error("用户不存在");
        }
        Long userId = one.getUserId();
        
        FarmOperationRecord record = new FarmOperationRecord();
        BeanUtils.copyProperties(farmOperationDto, record);
        record.setUserId(userId);
        
        // 处理农资库存
        if (farmOperationDto.getMaterialId() != null) {
            // 查询当前承包人农资库存
            ContractorMaterialStock currentStock = contractorMaterialStockService.lambdaQuery()
                    .eq(ContractorMaterialStock::getMaterialId, farmOperationDto.getMaterialId())
                    .eq(ContractorMaterialStock::getUserId, userId)
                    .one();
            
            if (currentStock == null) {
                return Result.error("农资库存不存在");
            }
            
            // 获取原记录的用量（如果原记录有农资）
            Integer oldQuantity = 0;
            if (oldRecord.getMaterialId() != null && oldRecord.getQuantity() != null) {
                oldQuantity = oldRecord.getQuantity();
            }
            
            // 计算需要检查的库存：当前库存 + 原扣减量 - 新用量
            Integer availableStock = currentStock.getStock() + oldQuantity;
            Integer newQuantity = farmOperationDto.getQuantity() != null ? farmOperationDto.getQuantity() : 0;
            
            if (availableStock < newQuantity) {
                return Result.error("农资库存不足");
            }
            
            // 更新库存：当前库存 + 原扣减量 - 新用量
            Integer updatedStock = currentStock.getStock() + oldQuantity - newQuantity;
            contractorMaterialStockService.lambdaUpdate()
                    .eq(ContractorMaterialStock::getMaterialId, farmOperationDto.getMaterialId())
                    .eq(ContractorMaterialStock::getUserId, userId)
                    .set(ContractorMaterialStock::getStock, updatedStock)
                    .update();
        } else {
            // 如果新记录没有农资，但原记录有农资，需要归还库存
            if (oldRecord.getMaterialId() != null && oldRecord.getQuantity() != null) {
                ContractorMaterialStock oldStock = contractorMaterialStockService.lambdaQuery()
                        .eq(ContractorMaterialStock::getMaterialId, oldRecord.getMaterialId())
                        .eq(ContractorMaterialStock::getUserId, userId)
                        .one();
                
                if (oldStock != null) {
                    // 归还原扣减的库存
                    Integer returnedStock = oldStock.getStock() + oldRecord.getQuantity();
                    contractorMaterialStockService.lambdaUpdate()
                            .eq(ContractorMaterialStock::getMaterialId, oldRecord.getMaterialId())
                            .eq(ContractorMaterialStock::getUserId, userId)
                            .set(ContractorMaterialStock::getStock, returnedStock)
                            .update();
                }
                
                // 清除农事记录中的农资ID
                this.lambdaUpdate()
                        .eq(FarmOperationRecord::getOperationId, farmOperationDto.getOperationId())
                        .set(FarmOperationRecord::getMaterialId, null)
                        .set(FarmOperationRecord::getQuantity, null)
                        .update();
            }
        }
        
        // 如果操作类型是收割，需要处理成熟作物表
        if ("收割".equals(farmOperationDto.getOperationType())) {
            if (farmOperationDto.getOutputQuantity() == null || farmOperationDto.getOutputQuantity().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return Result.error("产出数量必须大于0");
            }
            
            // 查询该种植记录的收割记录数量
            Long harvestCount = this.lambdaQuery()
                    .eq(FarmOperationRecord::getRecordId, farmOperationDto.getRecordId())
                    .eq(FarmOperationRecord::getOperationType, "收割")
                    .count();
            
            // 查询成熟作物记录
            MatureCrop matureCrop = matureCropService.lambdaQuery()
                    .eq(MatureCrop::getRecordId, farmOperationDto.getRecordId())
                    .one();
            
            if (matureCrop == null) {
                return Result.error("成熟作物记录不存在");
            }
            
            if (harvestCount == 1) {
                // 只有1条收割记录，直接更新产量
                matureCrop.setOutputQuantity(farmOperationDto.getOutputQuantity());
                matureCrop.setHarvestTime(farmOperationDto.getOperationTime());
                boolean updateResult = matureCropService.updateById(matureCrop);
                if (!updateResult) {
                    return Result.error("更新成熟作物记录失败");
                }
            } else {
                // 有多条收割记录，计算其他收割记录的总产量
                java.math.BigDecimal otherHarvestQuantity = this.lambdaQuery()
                        .eq(FarmOperationRecord::getRecordId, farmOperationDto.getRecordId())
                        .eq(FarmOperationRecord::getOperationType, "收割")
                        .ne(FarmOperationRecord::getOperationId, farmOperationDto.getOperationId())
                        .list()
                        .stream()
                        .map(FarmOperationRecord::getOutputQuantity)
                        .filter(q -> q != null)
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
                
                // 新总产量 = 其他记录产量 + 当前修改后的产量
                java.math.BigDecimal newTotalQuantity = otherHarvestQuantity.add(farmOperationDto.getOutputQuantity());
                matureCrop.setOutputQuantity(newTotalQuantity);
                matureCrop.setHarvestTime(farmOperationDto.getOperationTime());
                boolean updateResult = matureCropService.updateById(matureCrop);
                if (!updateResult) {
                    return Result.error("更新成熟作物记录失败");
                }
            }
        }
        
        return this.updateById(record) ? Result.ok() : Result.error("更新失败");
    }

    @Override
    @Transactional
    public Result delete(Long operationId) {
        // 查询要删除的记录
        FarmOperationRecord record = this.getById(operationId);
        if (record == null) {
            return Result.error("农事活动记录不存在");
        }
        
        // 如果操作类型是收割，需要处理成熟作物表
        if ("收割".equals(record.getOperationType())) {
            // 查询该种植记录的收割记录数量
            Long harvestCount = this.lambdaQuery()
                    .eq(FarmOperationRecord::getRecordId, record.getRecordId())
                    .eq(FarmOperationRecord::getOperationType, "收割")
                    .count();
            
            // 查询成熟作物记录
            MatureCrop matureCrop = matureCropService.lambdaQuery()
                    .eq(MatureCrop::getRecordId, record.getRecordId())
                    .one();
            
            if (matureCrop != null) {
                if (harvestCount == 1) {
                    // 只有1条收割记录，删除成熟作物记录
                    matureCropService.removeById(matureCrop.getMatureCropId());
                } else {
                    // 有多条收割记录，扣减产量
                    java.math.BigDecimal currentQuantity = record.getOutputQuantity();
                    if (currentQuantity != null) {
                        java.math.BigDecimal newQuantity = matureCrop.getOutputQuantity().subtract(currentQuantity);
                        // 确保产量不为负数
                        if (newQuantity.compareTo(java.math.BigDecimal.ZERO) < 0) {
                            newQuantity = java.math.BigDecimal.ZERO;
                        }
                        matureCrop.setOutputQuantity(newQuantity);
                        matureCropService.updateById(matureCrop);
                    }
                }
            }
        }
        
        // 删除农事活动记录
        return this.removeById(operationId) ? Result.ok() : Result.error("删除失败");
    }
}




