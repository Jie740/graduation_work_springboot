package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.*;
import com.clj.domain.dto.MaterialApplyDto;
import com.clj.domain.vo.MaterialApplyVo;
import com.clj.service.*;
import com.clj.mapper.MaterialApplyMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.clj.constants.MaterialStockRecordConstants.OUTPUT;

/**
* @author ajie
* @description 针对表【material_apply(农资申请表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:14
*/
@Service
@RequiredArgsConstructor
public class MaterialApplyServiceImpl extends ServiceImpl<MaterialApplyMapper, MaterialApply>
    implements MaterialApplyService{
    final UserService userService;
    final MaterialService materialService;
    final MaterialStockRecordService materialStockRecordService;
    final ContractorMaterialStockService contractorMaterialStockService;
    final MaterialTypeService materialTypeService;

    @Override
    public Result getMaterialApplyByPage(Integer pageNum, Integer pageSize) {
        // 分页查询申请记录
        Page<MaterialApply> page = this.lambdaQuery().page(new Page<>(pageNum, pageSize));
        
        if (page.getRecords().isEmpty()) {
            return Result.ok(new Page<>(pageNum, pageSize, 0).setRecords(new ArrayList<>()));
        }
        
        // 提取所有不重复的 materialId 和 applicantId
        Set<Long> materialIds = page.getRecords().stream()
                .map(MaterialApply::getMaterialId)
                .collect(Collectors.toSet());
        Set<Long> applicantIds = page.getRecords().stream()
                .map(MaterialApply::getApplicantId)
                .collect(Collectors.toSet());
        
        // 批量查询农资信息
        List<Material> materials = materialIds.isEmpty() ? new ArrayList<>() : 
                materialService.listByIds(materialIds);
        // 批量查询用户信息
        List<User> users = applicantIds.isEmpty() ? new ArrayList<>() : 
                userService.listByIds(applicantIds);
        
        // 转换为 Map 方便快速查找
        java.util.Map<Long, String> materialNameMap = materials.stream()
                .collect(Collectors.toMap(Material::getMaterialId, Material::getMaterialName, (k1, k2) -> k1));
        java.util.Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u, (k1, k2) -> k1));
        java.util.Map<Long, Long> materialTypeIdMap = materials.stream()
                .collect(Collectors.toMap(Material::getMaterialId, Material::getTypeId, (k1, k2) -> k1));
        
        // 批量查询农资类型信息
        Set<Long> typeIds = materials.stream()
                .map(Material::getTypeId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        List<MaterialType> materialTypes = typeIds.isEmpty() ? new ArrayList<>() :
                materialTypeService.listByIds(typeIds);
        java.util.Map<Long, String> typeNameMap = materialTypes.stream()
                .collect(Collectors.toMap(MaterialType::getTypeId, MaterialType::getTypeName, (k1, k2) -> k1));
        
        // 转换为 VO 列表
        ArrayList<MaterialApplyVo> materialApplies = new ArrayList<>();
        for (MaterialApply record : page.getRecords()) {
            MaterialApplyVo materialApplyVo = new MaterialApplyVo();
            BeanUtils.copyProperties(record, materialApplyVo);
            
            // 设置农资名称
            if (record.getMaterialId() != null && materialNameMap.containsKey(record.getMaterialId())) {
                materialApplyVo.setMaterialName(materialNameMap.get(record.getMaterialId()));
                // 设置农资类型名称
                Long typeId = materialTypeIdMap.get(record.getMaterialId());
                if (typeId != null && typeNameMap.containsKey(typeId)) {
                    materialApplyVo.setTypeName(typeNameMap.get(typeId));
                }
            }
            
            // 设置申请人信息
            if (record.getApplicantId() != null && userMap.containsKey(record.getApplicantId())) {
                User user = userMap.get(record.getApplicantId());
                materialApplyVo.setApplicant(user.getName());
                materialApplyVo.setPhone(user.getPhone());
            }
            
            materialApplies.add(materialApplyVo);
        }
        
        Page<MaterialApplyVo> materialApplyVoPage = new Page<>(pageNum, pageSize, page.getTotal());
        materialApplyVoPage.setRecords(materialApplies);
        return Result.ok(materialApplyVoPage);
    }

    @Override
    public Result add(MaterialApplyDto materialApplyDto) {
        User one = userService.lambdaQuery().eq(User::getPhone, materialApplyDto.getPhone())
                .eq(User::getName, materialApplyDto.getApplicant())
                .one();
        if (one == null){
            return Result.error("用户不存在");
        }

        MaterialApply materialApply = new MaterialApply();
        materialApply.setApplicantId(one.getUserId());
        BeanUtils.copyProperties(materialApplyDto,materialApply);

        return this.save(materialApply)? Result.ok() : Result.error("添加失败");
    }

    //根据农资名和申请人获取列表
    @Override
    public Result searchMaterialApplyByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return this.getMaterialApplyByPage(pageNum,pageSize);
        }
        
        //根据 keyword 查询农资 ID 列表（模糊匹配农资名称）
        List<Material> materials = materialService.lambdaQuery()
                .like(Material::getMaterialName, keyword)
                .list();
        Set<Long> materialIds = materials.stream()
                .map(Material::getMaterialId)
                .collect(Collectors.toSet());
        
        //根据 keyword 查询用户 ID 列表（模糊匹配姓名或手机号）
        List<User> users = userService.lambdaQuery()
                .like(User::getName, keyword)
                .list();
        Set<Long> userIds = users.stream()
                .map(User::getUserId)
                .collect(Collectors.toSet());
        
        //根据农资 ID 和用户 ID 分页查询 material_apply
        Page<MaterialApply> page = new Page<>(pageNum, pageSize);
        Page<MaterialApply> applyPage = this.lambdaQuery()
                .in(!materialIds.isEmpty(), MaterialApply::getMaterialId, materialIds)
                .in(!userIds.isEmpty(), MaterialApply::getApplicantId, userIds)
                .page(page);
        
        // 转换为 VO 列表
        ArrayList<MaterialApplyVo> materialApplyVos = convertToMaterialApplyVos(applyPage.getRecords());
        fillUserInfo(materialApplyVos);
        fillMaterialInfo(materialApplyVos);
        
        Page<MaterialApplyVo> voPage = new Page<>(pageNum, pageSize, applyPage.getTotal());
        voPage.setRecords(materialApplyVos);
        return Result.ok(voPage);
    }

    @Override
    public Result delete(Long applyId) {
        return this.removeById(applyId)? Result.ok() : Result.error("删除失败");
    }

    @Override
    @Transactional
    public Result updateMaterialApplyStatus(Long applyId, Integer status) {

        MaterialApply apply = this.lambdaQuery().eq(MaterialApply::getApplyId, applyId).one();
        //审核未通过
        if (status==0){
            return this.lambdaUpdate().eq(MaterialApply::getApplyId, applyId)
                    .set(MaterialApply::getStatus, status)
                    .update()? Result.ok() : Result.error("更新失败");
        }

        //审核通过
        if (status==1){
            //修改 material 表记录的库存数量
            //先查询material表农资库存是否足够
            Material material = materialService.lambdaQuery().eq(Material::getMaterialId, apply.getMaterialId())
                    .one();
                    if (material.getStock()<apply.getQuantity()){
                        return Result.error("库存不足");
                    }
                    //修改库存
            materialService.lambdaUpdate().eq(Material::getMaterialId, apply.getMaterialId())
                    .set(Material::getStock, material.getStock()-apply.getQuantity())
                    .update();
            //添加 material_stock_record 表出库记录
            MaterialStockRecord materialStockRecord = new MaterialStockRecord();
            BeanUtils.copyProperties(apply, materialStockRecord);
            materialStockRecord.setType(OUTPUT);
            materialStockRecordService.add(materialStockRecord);
            //添加contractor_material_stock记录
            //先查询是否存在
            ContractorMaterialStock one = contractorMaterialStockService.lambdaQuery()
                    .eq(ContractorMaterialStock::getMaterialId, apply.getMaterialId())
                    .eq(ContractorMaterialStock::getUserId, apply.getApplyId())
                    .one();
            if (one==null){
                ContractorMaterialStock contractorMaterialStock = new ContractorMaterialStock();
                BeanUtils.copyProperties(apply, contractorMaterialStock);
                contractorMaterialStock.setStock(apply.getQuantity());
                contractorMaterialStock.setUserId(apply.getApplicantId());
                contractorMaterialStockService.add(contractorMaterialStock);
                //修改 material_apply 表记录的状态
                return this.lambdaUpdate().eq(MaterialApply::getApplyId, applyId)
                        .set(MaterialApply::getStatus, status)
                        .update()? Result.ok() : Result.error("更新失败");
            }
            //修改contractor_material_stock表的库存
            contractorMaterialStockService.lambdaUpdate().eq(ContractorMaterialStock::getMaterialId, apply.getMaterialId())
                    .eq(ContractorMaterialStock::getUserId, apply.getApplyId())
                    .set(ContractorMaterialStock::getStock, one.getStock()+apply.getQuantity())
                    .update();
        }
        //修改 material_apply 表记录的状态
        return this.lambdaUpdate().eq(MaterialApply::getApplyId, applyId)
                .set(MaterialApply::getStatus, status)
                .update()? Result.ok() : Result.error("更新失败");
    }

    @Override
    public MaterialApplyVo getMaterialApplyVoById(Long applyId) {
        MaterialApply one = this.lambdaQuery().eq(MaterialApply::getApplyId, applyId).one();
        Material material = materialService.lambdaQuery().eq(Material::getMaterialId, one.getMaterialId()).one();
        MaterialApplyVo vo = new MaterialApplyVo();
        BeanUtils.copyProperties(one, vo);
        vo.setMaterialName(material.getMaterialName());
        
        // 设置农资类型名称
        if (material.getTypeId() != null) {
            MaterialType materialType = materialTypeService.getById(material.getTypeId());
            if (materialType != null) {
                vo.setTypeName(materialType.getTypeName());
            }
        }
        
        User user = userService.lambdaQuery().eq(User::getUserId, one.getApplicantId())
                .one();
        vo.setApplicant(user.getName());
        vo.setPhone(user.getPhone());
        return vo;
    }

    @Override
    public Result updateApply(MaterialApplyDto materialApplyDto) {
        User one = userService.lambdaQuery().eq(User::getName, materialApplyDto.getApplicant())
                .eq(User::getPhone, materialApplyDto.getPhone())
                .one();
        if (one==null){
            return Result.error("用户不存在");
        }
        Long userId = one.getUserId();
        MaterialApply materialApply = new MaterialApply();
        BeanUtils.copyProperties(materialApplyDto, materialApply);
        materialApply.setApplicantId(userId);
        return this.updateById(materialApply)? Result.ok() : Result.error("更新失败");
    }

    /**
     * 将 MaterialApply 列表转换为 MaterialApplyVo 列表
     */
    private ArrayList<MaterialApplyVo> convertToMaterialApplyVos(List<MaterialApply> applies) {
        ArrayList<MaterialApplyVo> vos = new ArrayList<>();
        for (MaterialApply apply : applies) {
            MaterialApplyVo vo = new MaterialApplyVo();
            BeanUtils.copyProperties(apply, vo);
            vos.add(vo);
        }
        return vos;
    }
    
    /**
     * 批量填充用户信息
     */
    private void fillUserInfo(ArrayList<MaterialApplyVo> vos) {
        if (vos.isEmpty()) {
            return;
        }
        
        // 提取所有不重复的 applicantId
        Set<Long> applicantIds = vos.stream()
                .map(MaterialApplyVo::getApplicantId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        
        if (!applicantIds.isEmpty()) {
            // 批量查询用户信息
            List<User> users = userService.listByIds(applicantIds);
            java.util.Map<Long, User> userMap = users.stream()
                    .collect(Collectors.toMap(User::getUserId, u -> u, (k1, k2) -> k1));
            
            // 填充用户信息
            for (MaterialApplyVo vo : vos) {
                if (vo.getApplicantId() != null && userMap.containsKey(vo.getApplicantId())) {
                    User user = userMap.get(vo.getApplicantId());
                    vo.setApplicant(user.getName());
                    vo.setPhone(user.getPhone());
                }
            }
        }
    }
    
    /**
     * 批量填充农资信息
     */
    private void fillMaterialInfo(ArrayList<MaterialApplyVo> vos) {
        if (vos.isEmpty()) {
            return;
        }
        
        // 提取所有不重复的 materialId
        Set<Long> materialIds = vos.stream()
                .map(MaterialApplyVo::getMaterialId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        
        if (!materialIds.isEmpty()) {
            // 批量查询农资信息
            List<Material> materials = materialService.listByIds(materialIds);
            java.util.Map<Long, Material> materialMap = materials.stream()
                    .collect(Collectors.toMap(Material::getMaterialId, m -> m, (k1, k2) -> k1));
            
            // 提取所有不重复的 typeId
            Set<Long> typeIds = materials.stream()
                    .map(Material::getTypeId)
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());
            
            // 批量查询农资类型信息
            List<MaterialType> materialTypes = typeIds.isEmpty() ? new ArrayList<>() :
                    materialTypeService.listByIds(typeIds);
            java.util.Map<Long, String> typeNameMap = materialTypes.stream()
                    .collect(Collectors.toMap(MaterialType::getTypeId, MaterialType::getTypeName, (k1, k2) -> k1));
            
            // 填充农资信息和类型名称
            for (MaterialApplyVo vo : vos) {
                if (vo.getMaterialId() != null && materialMap.containsKey(vo.getMaterialId())) {
                    Material material = materialMap.get(vo.getMaterialId());
                    vo.setMaterialName(material.getMaterialName());
                    // 设置农资类型名称
                    if (material.getTypeId() != null && typeNameMap.containsKey(material.getTypeId())) {
                        vo.setTypeName(typeNameMap.get(material.getTypeId()));
                    }
                }
            }
        }
    }

    @Override
    public Result getMyApplies(String keyword, Integer pageNum, Integer pageSize) {
        // 1. 从 ThreadLocal 获取当前用户ID
        Long userId = com.clj.utils.UserHolder.getUserId();
        if (userId == null) {
            return Result.error("未登录或登录已过期");
        }

        // 2. 构建查询条件
        var queryWrapper = this.lambdaQuery()
                .eq(MaterialApply::getApplicantId, userId)
                .orderByDesc(MaterialApply::getApplyTime);

        // 3. 如果有关键词，根据农资名模糊查询
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 先查询匹配的农资ID
            List<Material> matchedMaterials = materialService.lambdaQuery()
                    .like(Material::getMaterialName, keyword)
                    .list();
            
            if (matchedMaterials.isEmpty()) {
                Page<MaterialApplyVo> emptyPage = new Page<>(pageNum, pageSize, 0);
                emptyPage.setRecords(new ArrayList<>());
                return Result.ok(emptyPage);
            }
            
            Set<Long> materialIds = matchedMaterials.stream()
                    .map(Material::getMaterialId)
                    .collect(Collectors.toSet());
            
            queryWrapper.in(MaterialApply::getMaterialId, materialIds);
        }

        // 4. 分页查询
        Page<MaterialApply> page = queryWrapper.page(new Page<>(pageNum, pageSize));

        // 5. 转换为 VO 列表
        ArrayList<MaterialApplyVo> materialApplyVos = convertToMaterialApplyVos(page.getRecords());
        fillUserInfo(materialApplyVos);
        fillMaterialInfo(materialApplyVos);

        Page<MaterialApplyVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(materialApplyVos);
        return Result.ok(voPage);
    }
}




