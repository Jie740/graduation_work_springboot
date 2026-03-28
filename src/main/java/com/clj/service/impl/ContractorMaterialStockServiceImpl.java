package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.ContractorMaterialStock;
import com.clj.domain.Material;
import com.clj.domain.MaterialType;
import com.clj.domain.User;
import com.clj.domain.dto.ContractorMaterialStockDto;
import com.clj.domain.vo.ContractorMaterialStockVo;
import com.clj.service.ContractorMaterialStockService;
import com.clj.mapper.ContractorMaterialStockMapper;
import com.clj.service.MaterialService;
import com.clj.service.MaterialTypeService;
import com.clj.service.UserService;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author ajie
* @description 针对表【contractor_material_stock(承包人农资库存表)】的数据库操作 Service 实现
* @createDate 2026-03-02 20:08:44
*/
@Service
@RequiredArgsConstructor
public class ContractorMaterialStockServiceImpl extends ServiceImpl<ContractorMaterialStockMapper, ContractorMaterialStock>
    implements ContractorMaterialStockService{

    private final MaterialService materialService;
    private final MaterialTypeService materialTypeService;
    private final UserService userService;

    @Override
    public Result add(ContractorMaterialStock contractorMaterialStock) {
        ContractorMaterialStock one = this.lambdaQuery().eq(ContractorMaterialStock::getMaterialId, contractorMaterialStock.getMaterialId())
                .eq(ContractorMaterialStock::getUserId, contractorMaterialStock.getUserId())
                .one();
        //增加库存
        if (one != null){
           return this.lambdaUpdate().eq(ContractorMaterialStock::getMaterialId, contractorMaterialStock.getMaterialId())
                    .eq(ContractorMaterialStock::getUserId, contractorMaterialStock.getUserId())
                    .set(ContractorMaterialStock::getStock, one.getStock()+contractorMaterialStock.getStock())
                    .update()? Result.ok() : Result.error("保存失败");
        }
        return this.save(contractorMaterialStock)? Result.ok() : Result.error("保存失败");
    }

    @Override
    public Result delete(Long contractorMaterialId) {
        return this.removeById(contractorMaterialId)? Result.ok() : Result.error("删除失败");
    }

    @Override
    public Result updateContractorMaterialStock(ContractorMaterialStockDto contractorMaterialStockDto) {
        return this.lambdaUpdate().eq(ContractorMaterialStock::getContractorMaterialId, contractorMaterialStockDto.getContractorMaterialId())
                .set(ContractorMaterialStock::getStock, contractorMaterialStockDto.getStock())
                .set(ContractorMaterialStock::getWarningStock, contractorMaterialStockDto.getWarningStock())
                .update()? Result.ok() : Result.error("更新失败");
    }

    @Override
    public Result getByPage(Integer pageNum, Integer pageSize) {
        // 分页查询承包人农资库存记录
        Page<ContractorMaterialStock> page = this.page(new Page<>(pageNum, pageSize));
        
        // 收集所有的农资 ID 和用户 ID
        ArrayList<Long> materialIds = new ArrayList<>();
        ArrayList<Long> userIds = new ArrayList<>();
        for (ContractorMaterialStock stock : page.getRecords()) {
            if (stock.getMaterialId() != null) {
                materialIds.add(stock.getMaterialId());
            }
            if (stock.getUserId() != null) {
                userIds.add(stock.getUserId());
            }
        }

        // 批量查询农资信息
        ArrayList<Material> materials = new ArrayList<>();
        if (!materialIds.isEmpty()) {
            materials = (ArrayList<Material>) materialService.listByIds(materialIds);
        }

        // 批量查询用户信息
        ArrayList<User> users = new ArrayList<>();
        if (!userIds.isEmpty()) {
            users = (ArrayList<User>) userService.listByIds(userIds);
        }

        // 转换为 Map 便于快速查找
        Map<Long, Material> materialMap = materials.stream()
                .collect(Collectors.toMap(Material::getMaterialId, m -> m));
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // 构建 VO 对象
        ArrayList<ContractorMaterialStockVo> voList = new ArrayList<>();
        for (ContractorMaterialStock stock : page.getRecords()) {
            ContractorMaterialStockVo vo = new ContractorMaterialStockVo();
            
            // 设置基本信息
            vo.setContractorMaterialId(stock.getContractorMaterialId());
            vo.setStock(stock.getStock());
            vo.setWarningStock(stock.getWarningStock());
            
            // 从 Map 中获取农资信息
            Material material = materialMap.get(stock.getMaterialId());
            if (material != null) {
                vo.setMaterialName(material.getMaterialName());
                
                // 查询农资类型信息
                MaterialType materialType = materialTypeService.getById(material.getTypeId());
                if (materialType != null) {
                    vo.setType(materialType.getTypeName());
                }
            }

            // 从 Map 中获取用户信息
            User user = userMap.get(stock.getUserId());
            if (user != null) {
                vo.setContractorName(user.getName());
                vo.setPhone(user.getPhone());
            }

            voList.add(vo);
        }

        Page<ContractorMaterialStockVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(voList);
        return Result.ok(voPage);
    }

    @Override
    public Result searchByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return this.getByPage(pageNum, pageSize);
        }
        
        // 根据承包人姓名模糊查询用户
        List<User> matchedUsers = userService.lambdaQuery()
                .like(User::getName, keyword)
                .list();
        
        // 如果没有匹配的用户，返回空结果
        if (matchedUsers.isEmpty()) {
            Page<ContractorMaterialStockVo> emptyPage = new Page<>(pageNum, pageSize, 0);
            emptyPage.setRecords(new ArrayList<>());
            return Result.ok(emptyPage);
        }
        
        // 获取匹配的用户 ID 列表
        ArrayList<Long> userIds = matchedUsers.stream()
                .map(User::getUserId)
                .collect(Collectors.toCollection(ArrayList::new));

        // 根据用户 ID 分页查询承包人农资库存记录
        Page<ContractorMaterialStock> page = this.lambdaQuery()
                .in(ContractorMaterialStock::getUserId, userIds)
                .page(new Page<>(pageNum, pageSize));

        // 收集所有的农资 ID
        ArrayList<Long> materialIds = new ArrayList<>();
        for (ContractorMaterialStock stock : page.getRecords()) {
            if (stock.getMaterialId() != null) {
                materialIds.add(stock.getMaterialId());
            }
        }

        // 批量查询农资信息
        ArrayList<Material> materials = new ArrayList<>();
        if (!materialIds.isEmpty()) {
            materials = (ArrayList<Material>) materialService.listByIds(materialIds);
        }

        // 转换为 Map 便于快速查找
        Map<Long, Material> materialMap = materials.stream()
                .collect(Collectors.toMap(Material::getMaterialId, m -> m));
        Map<Long, User> userMap = matchedUsers.stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));

        // 构建 VO 对象
        ArrayList<ContractorMaterialStockVo> voList = new ArrayList<>();
        for (ContractorMaterialStock stock : page.getRecords()) {
            ContractorMaterialStockVo vo = new ContractorMaterialStockVo();
            
            // 设置基本信息
            vo.setContractorMaterialId(stock.getContractorMaterialId());
            vo.setStock(stock.getStock());
            vo.setWarningStock(stock.getWarningStock());
            
            // 从 Map 中获取农资信息
            Material material = materialMap.get(stock.getMaterialId());
            if (material != null) {
                vo.setMaterialName(material.getMaterialName());
                
                // 查询农资类型信息
                MaterialType materialType = materialTypeService.getById(material.getTypeId());
                if (materialType != null) {
                    vo.setType(materialType.getTypeName());
                }
            }

            // 从 Map 中获取用户信息
            User user = userMap.get(stock.getUserId());
            if (user != null) {
                vo.setContractorName(user.getName());
                vo.setPhone(user.getPhone());
            }

            voList.add(vo);
        }

        Page<ContractorMaterialStockVo> voPage = new Page<>(pageNum, pageSize, page.getTotal());
        voPage.setRecords(voList);
        return Result.ok(voPage);
    }
}
