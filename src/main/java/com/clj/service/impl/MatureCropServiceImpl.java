package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Crop;
import com.clj.domain.Land;
import com.clj.domain.MatureCrop;
import com.clj.domain.PlantingRecord;
import com.clj.domain.vo.MatureCropVo;
import com.clj.service.CropService;
import com.clj.service.LandService;
import com.clj.service.MatureCropService;
import com.clj.mapper.MatureCropMapper;
import com.clj.mapper.PlantingRecordMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
* @author ajie
* @description 针对表【mature_crop(成熟作物表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:07
*/
@Service
public class MatureCropServiceImpl extends ServiceImpl<MatureCropMapper, MatureCrop>
    implements MatureCropService{
    
    private final PlantingRecordMapper plantingRecordMapper;
    private final LandService landService;
    private final CropService cropService;
    
    public MatureCropServiceImpl(PlantingRecordMapper plantingRecordMapper, 
                                  LandService landService, 
                                  CropService cropService) {
        this.plantingRecordMapper = plantingRecordMapper;
        this.landService = landService;
        this.cropService = cropService;
    }
    @Override
    public Result add(MatureCrop matureCrop) {
        return this.save(matureCrop)?Result.ok():Result.error("添加失败");
    }

    @Override
    public Result getMatureCropsByPage(Integer pageNum, Integer pageSize) {
        Page<MatureCrop> matureCropPage = new Page<>(pageNum, pageSize);
        Page<MatureCrop> page = this.lambdaQuery().page(matureCropPage);
            
        // 收集所有的种植记录 ID、地块 ID 和农作物 ID
        ArrayList<Long> recordIds = new ArrayList<>();
        for (MatureCrop matureCrop : page.getRecords()) {
            if (matureCrop.getRecordId() != null) {
                recordIds.add(matureCrop.getRecordId());
            }
        }
            
        // 批量查询种植记录（使用 Mapper 避免循环依赖）
        ArrayList<PlantingRecord> plantingRecords = new ArrayList<>();
        if (!recordIds.isEmpty()) {
            plantingRecords = (ArrayList<PlantingRecord>) plantingRecordMapper.selectBatchIds(recordIds);
        }
            
        // 从种植记录中提取地块 ID 和农作物 ID
        ArrayList<Long> landIds = new ArrayList<>();
        ArrayList<Long> cropIds = new ArrayList<>();
        for (PlantingRecord plantingRecord : plantingRecords) {
            if (plantingRecord.getLandId() != null) {
                landIds.add(plantingRecord.getLandId());
            }
            if (plantingRecord.getCropId() != null) {
                cropIds.add(plantingRecord.getCropId());
            }
        }
            
        // 批量查询地块信息
        ArrayList<Land> lands = new ArrayList<>();
        if (!landIds.isEmpty()) {
            lands = (ArrayList<Land>) landService.listByIds(landIds);
        }
            
        // 批量查询农作物信息
        ArrayList<Crop> crops = new ArrayList<>();
        if (!cropIds.isEmpty()) {
            crops = (ArrayList<Crop>) cropService.listByIds(cropIds);
        }
            
        // 转换为 Map 便于快速查找
        java.util.Map<Long, PlantingRecord> recordMap = plantingRecords.stream()
                .collect(java.util.stream.Collectors.toMap(PlantingRecord::getRecordId, record -> record));
        java.util.Map<Long, Land> landMap = lands.stream()
                .collect(java.util.stream.Collectors.toMap(Land::getLandId, land -> land));
        java.util.Map<Long, Crop> cropMap = crops.stream()
                .collect(java.util.stream.Collectors.toMap(Crop::getCropId, crop -> crop));
            
        // 构建 VO 对象
        ArrayList<MatureCropVo> matureCropVos = new ArrayList<>();
        for (MatureCrop matureCrop : page.getRecords()) {
            MatureCropVo matureCropVo = new MatureCropVo();
            BeanUtils.copyProperties(matureCrop, matureCropVo);
                
            // 从 Map 中获取种植记录
            PlantingRecord plantingRecord = recordMap.get(matureCrop.getRecordId());
            if (plantingRecord != null) {
                // 从 Map 中获取地块信息
                Land land = landMap.get(plantingRecord.getLandId());
                if (land != null) {
                    matureCropVo.setLandName(land.getLandName());
                    matureCropVo.setLocation(land.getLocation());
                }
                    
                // 从 Map 中获取农作物信息
                Crop crop = cropMap.get(plantingRecord.getCropId());
                if (crop != null) {
                    matureCropVo.setCropName(crop.getCropName());
                }
            }
                
            matureCropVos.add(matureCropVo);
        }
            
        Page<MatureCropVo> matureCropVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        matureCropVoPage.setRecords(matureCropVos);
        return Result.ok(matureCropVoPage);
    }
}




