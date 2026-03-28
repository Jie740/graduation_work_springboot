package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Crop;
import com.clj.domain.Land;
import com.clj.domain.MatureCrop;
import com.clj.domain.PlantingRecord;
import com.clj.domain.vo.MatureCropStatisticsVo;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author ajie
* @description 针对表【mature_crop(成熟作物表)】的数据库操作 Service 实现
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
            
        // 收集所有的种植记录 ID、地块ID 和农作物 ID
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
            
        // 从种植记录中提取地块ID 和农作物 ID
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

    @Override
    public Result getStatistics(Long landId, String startDate, String endDate) {
        // 查询所有成熟作物记录
        List<MatureCrop> matureCrops = this.lambdaQuery()
                .select(MatureCrop::getRecordId, MatureCrop::getOutputQuantity, MatureCrop::getHarvestTime)
                .list();
        
        if (matureCrops.isEmpty()) {
            return Result.ok(new MatureCropStatisticsVo());
        }
        
        // 收集所有的种植记录 ID
        Set<Long> recordIds = matureCrops.stream()
                .map(MatureCrop::getRecordId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        // 批量查询种植记录
        List<PlantingRecord> plantingRecords = plantingRecordMapper.selectBatchIds(new ArrayList<>(recordIds));
        
        // 构建种植记录 Map
        Map<Long, PlantingRecord> recordMap = plantingRecords.stream()
                .collect(Collectors.toMap(PlantingRecord::getRecordId, r -> r));
        
        // 收集地块ID 和农作物 ID
        Set<Long> landIds = plantingRecords.stream()
                .map(PlantingRecord::getLandId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        Set<Long> cropIds = plantingRecords.stream()
                .map(PlantingRecord::getCropId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        
        // 批量查询地块信息
        List<Land> lands = landService.listByIds(new ArrayList<>(landIds));
        Map<Long, Land> landMap = lands.stream()
                .collect(Collectors.toMap(Land::getLandId, l -> l));
        
        // 批量查询农作物信息
        List<Crop> crops = cropService.listByIds(new ArrayList<>(cropIds));
        Map<Long, Crop> cropMap = crops.stream()
                .collect(Collectors.toMap(Crop::getCropId, c -> c));
        
        // 过滤数据（根据条件）
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startLocalDate = null;
        LocalDate endLocalDate = null;
        
        if (startDate != null && !startDate.isEmpty()) {
            startLocalDate = LocalDate.parse(startDate, formatter);
        }
        if (endDate != null && !endDate.isEmpty()) {
            endLocalDate = LocalDate.parse(endDate, formatter);
        }
        
        // 最终用于统计的数据列表
        List<MatureCropWithInfo> cropWithInfoList = new ArrayList<>();
        
        for (MatureCrop matureCrop : matureCrops) {
            PlantingRecord plantingRecord = recordMap.get(matureCrop.getRecordId());
            if (plantingRecord == null) {
                continue;
            }
            
            // 根据地块ID 过滤
            if (landId != null && !landId.equals(plantingRecord.getLandId())) {
                continue;
            }
            
            // 根据日期范围过滤
            if (matureCrop.getHarvestTime() != null) {
                LocalDate harvestDate = LocalDate.ofInstant(
                        matureCrop.getHarvestTime().toInstant(), 
                        java.time.ZoneId.systemDefault()
                );
                
                if (startLocalDate != null && harvestDate.isBefore(startLocalDate)) {
                    continue;
                }
                if (endLocalDate != null && harvestDate.isAfter(endLocalDate)) {
                    continue;
                }
            }
            
            // 获取关联信息
            Land land = landMap.get(plantingRecord.getLandId());
            Crop crop = cropMap.get(plantingRecord.getCropId());
            
            MatureCropWithInfo info = new MatureCropWithInfo();
            info.setMatureCrop(matureCrop);
            info.setPlantingRecord(plantingRecord);
            info.setLand(land);
            info.setCrop(crop);
            
            cropWithInfoList.add(info);
        }
        
        // 计算统计数据
        MatureCropStatisticsVo statisticsVo = buildStatisticsVo(cropWithInfoList);
        
        return Result.ok(statisticsVo);
    }
    
    /**
     * 构建统计数据 VO
     */
    private MatureCropStatisticsVo buildStatisticsVo(List<MatureCropWithInfo> cropWithInfoList) {
        MatureCropStatisticsVo vo = new MatureCropStatisticsVo();
        
        if (cropWithInfoList.isEmpty()) {
            return vo;
        }
        
        // 1. 总作物数量
        long totalCrops = cropWithInfoList.size();
        vo.setTotalCrops(totalCrops);
        
        // 2. 总产量
        BigDecimal totalOutput = cropWithInfoList.stream()
                .map(MatureCropWithInfo::getOutputQuantity)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalOutput(totalOutput);
        
        // 3. 总地块数（去重）
        long totalLands = cropWithInfoList.stream()
                .map(info -> info.getLand() != null ? info.getLand().getLandId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        vo.setTotalLands(totalLands);
        
        // 4. 平均产量
        BigDecimal avgOutput = totalLands > 0 
                ? totalOutput.divide(BigDecimal.valueOf(totalLands), 2, RoundingMode.HALF_UP) 
                : BigDecimal.ZERO;
        vo.setAvgOutput(avgOutput);
        
        // 5. 按作物分类的产量
        Map<String, Double> cropOutputMap = cropWithInfoList.stream()
                .collect(Collectors.groupingBy(
                        info -> info.getCrop() != null ? info.getCrop().getCropName() : "未知",
                        Collectors.summingDouble(info -> info.getOutputQuantity() != null ? info.getOutputQuantity().doubleValue() : 0.0)
                ));
        
        List<MatureCropStatisticsVo.CropOutputDto> cropOutputList = cropOutputMap.entrySet().stream()
                .map(entry -> {
                    MatureCropStatisticsVo.CropOutputDto dto = new MatureCropStatisticsVo.CropOutputDto();
                    dto.setCropName(entry.getKey());
                    dto.setOutput(BigDecimal.valueOf(entry.getValue()));
                    return dto;
                })
                .collect(Collectors.toList());
        vo.setCropOutput(cropOutputList);
        
        // 6. 按地块分类的产量
        Map<String, Double> landOutputMap = cropWithInfoList.stream()
                .collect(Collectors.groupingBy(
                        info -> info.getLand() != null ? info.getLand().getLandName() : "未知",
                        Collectors.summingDouble(info -> info.getOutputQuantity() != null ? info.getOutputQuantity().doubleValue() : 0.0)
                ));
        
        List<MatureCropStatisticsVo.LandOutputDto> landOutputList = landOutputMap.entrySet().stream()
                .map(entry -> {
                    MatureCropStatisticsVo.LandOutputDto dto = new MatureCropStatisticsVo.LandOutputDto();
                    dto.setLandName(entry.getKey());
                    dto.setOutput(BigDecimal.valueOf(entry.getValue()));
                    return dto;
                })
                .collect(Collectors.toList());
        vo.setLandOutput(landOutputList);
        
        // 7. 趋势数据（按月统计）
        Map<String, Double> monthOutputMap = cropWithInfoList.stream()
                .filter(info -> info.getMatureCrop().getHarvestTime() != null)
                .collect(Collectors.groupingBy(
                        info -> {
                            LocalDate date = LocalDate.ofInstant(
                                    info.getMatureCrop().getHarvestTime().toInstant(),
                                    java.time.ZoneId.systemDefault()
                            );
                            return date.getMonthValue() + "月";
                        },
                        Collectors.summingDouble(info -> info.getOutputQuantity() != null ? info.getOutputQuantity().doubleValue() : 0.0)
                ));
        
        // 按月份排序
        List<String> monthOrder = Arrays.asList("1 月", "2 月", "3 月", "4 月", "5 月", "6 月", 
                "7 月", "8 月", "9 月", "10 月", "11 月", "12 月");
        
        List<MatureCropStatisticsVo.TrendDataDto> trendDataList = monthOrder.stream()
                .filter(monthOutputMap::containsKey)
                .map(month -> {
                    MatureCropStatisticsVo.TrendDataDto dto = new MatureCropStatisticsVo.TrendDataDto();
                    dto.setMonth(month);
                    dto.setOutput(BigDecimal.valueOf(monthOutputMap.get(month)));
                    return dto;
                })
                .collect(Collectors.toList());
        vo.setTrendData(trendDataList);
        
        // 8. 饼图数据（与作物产量相同）
        List<MatureCropStatisticsVo.PieDataDto> pieDataList = cropOutputList.stream()
                .map(dto -> {
                    MatureCropStatisticsVo.PieDataDto pieDto = new MatureCropStatisticsVo.PieDataDto();
                    pieDto.setName(dto.getCropName());
                    pieDto.setValue(dto.getOutput());
                    return pieDto;
                })
                .collect(Collectors.toList());
        vo.setPieData(pieDataList);
        
        return vo;
    }
    
    /**
     * 内部类：带有完整信息的成熟作物
     */
    private static class MatureCropWithInfo {
        private MatureCrop matureCrop;
        private PlantingRecord plantingRecord;
        private Land land;
        private Crop crop;
        
        public BigDecimal getOutputQuantity() {
            return matureCrop != null ? matureCrop.getOutputQuantity() : null;
        }
        
        public MatureCrop getMatureCrop() {
            return matureCrop;
        }
        
        public void setMatureCrop(MatureCrop matureCrop) {
            this.matureCrop = matureCrop;
        }
        
        public PlantingRecord getPlantingRecord() {
            return plantingRecord;
        }
        
        public void setPlantingRecord(PlantingRecord plantingRecord) {
            this.plantingRecord = plantingRecord;
        }
        
        public Land getLand() {
            return land;
        }
        
        public void setLand(Land land) {
            this.land = land;
        }
        
        public Crop getCrop() {
            return crop;
        }
        
        public void setCrop(Crop crop) {
            this.crop = crop;
        }
    }

}



