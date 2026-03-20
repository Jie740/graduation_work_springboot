package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.FarmOperationRecord;
import com.clj.domain.Material;
import com.clj.domain.User;
import com.clj.domain.dto.FarmOperationDto;
import com.clj.domain.vo.FarmOperationRecordVo;
import com.clj.service.ContractorMaterialStockService;
import com.clj.service.FarmOperationRecordService;
import com.clj.mapper.FarmOperationRecordMapper;
import com.clj.service.MaterialService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;









import java.util.ArrayList;

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
            return this.save(record)? Result.ok() : Result.error("保存失败");
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
        return this.save(record)? Result.ok() : Result.error("保存失败");
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
        FarmOperationRecord record = new FarmOperationRecord();
        BeanUtils.copyProperties(farmOperationDto, record);

        //查询用户是否存在
        User one = this.userService.lambdaQuery().eq(User::getPhone, farmOperationDto.getPhone())
                .eq(User::getName, farmOperationDto.getOperatorName())
                .one();
                if (one == null){
                    return Result.error("用户不存在");
                }
                Long userId = one.getUserId();
                record.setUserId(userId);

        System.out.println("farmOperationDto = " + farmOperationDto);
                if ( farmOperationDto.getMaterialId() == null){
                    this.lambdaUpdate()
                            .eq(FarmOperationRecord::getOperationId, farmOperationDto.getOperationId())
                            .set(FarmOperationRecord::getMaterialId, null).update();
                }
                return this.updateById(record)? Result.ok() : Result.error("更新失败");
    }
}




