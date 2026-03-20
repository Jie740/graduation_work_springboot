package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.constants.PlantingPlanAdjustConstants;
import com.clj.constants.PlantingPlanConstants;
import com.clj.domain.*;
import com.clj.domain.dto.PlantingPlanAdjustDto;
import com.clj.domain.vo.PlantingPlanAdjustDetailVo;
import com.clj.domain.vo.PlantingPlanAdjustVo;
import com.clj.service.*;
import com.clj.mapper.PlantingPlanAdjustMapper;
import com.clj.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

/**
* @author ajie
* @description 针对表【planting_plan_adjust(种植计划调整申请表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:02
*/
@Service
@RequiredArgsConstructor
public class PlantingPlanAdjustServiceImpl extends ServiceImpl<PlantingPlanAdjustMapper, PlantingPlanAdjust>
    implements PlantingPlanAdjustService{

    final UserService userService;
    final PlantingPlanService plantingPlanService;
    final LandService landService;
    final CropService cropService;
    @Override
    @Transactional
    public Result add(PlantingPlanAdjustDto plantingPlanAdjustDto) {
        //根据申请人名和手机号查询用户ID
        User one = userService.lambdaQuery().eq(User::getName, plantingPlanAdjustDto.getApplicant())
                .eq(User::getPhone, plantingPlanAdjustDto.getPhone())
                .one();
        if (one == null){
            return Result.error("用户不存在");
        }
        Long userId = one.getUserId();

        PlantingPlanAdjust plantingPlanAdjust = new PlantingPlanAdjust();
        plantingPlanAdjust.setApplicantId(userId);
        BeanUtils.copyProperties(plantingPlanAdjustDto, plantingPlanAdjust);
        //添加计划调整记录
        //更新计划表的状态，改为调整中
        boolean save= this.save(plantingPlanAdjust)&&
                plantingPlanService.lambdaUpdate().eq(PlantingPlan::getPlanId, plantingPlanAdjust.getPlanId())
                .set(PlantingPlan::getStatus, PlantingPlanConstants.ADJUST)
                .update();
        return save? Result.ok() : Result.error("添加失败");
    }

    @Override
    public Result delete(Long id) {
        return this.removeById(id)? Result.ok() : Result.error("删除失败");
    }

    @Override
    @Transactional
    public Result updateStatus(Long id, Integer status) {
        //更新计划调整记录状态
        if (id== null){
            return Result.error("参数错误");
        }
        this.lambdaUpdate().eq(PlantingPlanAdjust::getAdjustId, id)
                .set(PlantingPlanAdjust::getStatus, status)
                .update();
        PlantingPlanAdjust plantingPlanAdjust = this.lambdaQuery().eq(PlantingPlanAdjust::getAdjustId, id).one();
        //审核通过
        if (Objects.equals(status, PlantingPlanAdjustConstants.APPROVED)){
            //修改计划表信息及状态
            Long planId = plantingPlanAdjust.getPlanId();
            plantingPlanService.lambdaUpdate().eq(PlantingPlan::getPlanId, planId)
                    .set(PlantingPlan::getStatus, PlantingPlanConstants.PUBLISH)
                    .set(PlantingPlan::getLandId, plantingPlanAdjust.getLandId())
                    .set(PlantingPlan::getCropId, plantingPlanAdjust.getCropId())
                    .set(PlantingPlan::getExpectedOutput, plantingPlanAdjust.getExpectedOutput())
                    .set(PlantingPlan::getStartTime, plantingPlanAdjust.getStartTime())
                    .set(PlantingPlan::getEndTime, plantingPlanAdjust.getEndTime())
                    .update();
        }
        //审核拒绝通过
        if (Objects.equals(status, PlantingPlanAdjustConstants.REJECTED)){
            //修改计划表状态
            plantingPlanService.lambdaUpdate().eq(PlantingPlan::getPlanId, plantingPlanAdjust.getPlanId())
                    .set(PlantingPlan::getStatus, PlantingPlanConstants.PUBLISH)
                    .update();
        }
       return Result.ok();
    }

    @Override
    public Result getPlantingPlanAdjustsByPage(Integer pageNum, Integer pageSize) {
        Page<PlantingPlanAdjust> page = new Page<>(pageNum, pageSize);
        Page<PlantingPlanAdjust> plantingPlanAdjustPage = this.lambdaQuery().page(page);

        ArrayList<PlantingPlanAdjustVo> vos = new ArrayList<>();
        for (PlantingPlanAdjust plantingPlanAdjust : plantingPlanAdjustPage.getRecords()){
            PlantingPlanAdjustVo vo = new PlantingPlanAdjustVo();
            BeanUtils.copyProperties(plantingPlanAdjust, vo);
            //根据计划Id查询计划名称
            if (plantingPlanAdjust.getPlanId() != null){
                PlantingPlan plantingPlan = plantingPlanService.lambdaQuery().eq(PlantingPlan::getPlanId, plantingPlanAdjust.getPlanId())
                        .one();
                if (plantingPlan != null){
                    vo.setPlanName(plantingPlan.getPlanName());
                }
            }
            //根据用户ID查询用户名、手机号
            if (plantingPlanAdjust.getApplicantId() != null){
                User user = userService.lambdaQuery().eq(User::getUserId, plantingPlanAdjust.getApplicantId())
                        .one();
                if (user != null){
                    vo.setApplicant(user.getName());
                    vo.setPhone(user.getPhone());
                }
            }

            vos.add(vo);
        }

        Page<PlantingPlanAdjustVo> voPage = new Page<>(pageNum, pageSize, plantingPlanAdjustPage.getTotal());
        voPage.setRecords(vos);
        return Result.ok(voPage);
    }

    @Override
    public Result searchPlantingPlanAdjustsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return getPlantingPlanAdjustsByPage(pageNum, pageSize);
        }
        
        ArrayList<PlantingPlanAdjustVo> vos = new ArrayList<>();
        
        // 1. 根据用户名模糊查询获取用户 ID 列表，再查找对应的计划调整记录
        Page<User> userPage = new Page<>(pageNum, pageSize);
        Page<User> users = userService.lambdaQuery()
                .like(User::getName, keyword)
                .page(userPage);
        
        for (User user : users.getRecords()){
            PlantingPlanAdjust plantingPlanAdjust = this.lambdaQuery()
                    .eq(PlantingPlanAdjust::getApplicantId, user.getUserId())
                    .one();
                
            if (plantingPlanAdjust != null){
                PlantingPlanAdjustVo vo = buildVoFromRecord(plantingPlanAdjust);
                vo.setApplicant(user.getName());
                vo.setPhone(user.getPhone());
                vos.add(vo);
            }
        }
        
        // 2. 根据计划名模糊查询获取计划 ID 列表，再查找对应的计划调整记录
        Page<PlantingPlan> planPage = new Page<>(pageNum, pageSize);
        Page<PlantingPlan> plantingPlans = plantingPlanService.lambdaQuery()
                .like(PlantingPlan::getPlanName, keyword)
                .page(planPage);
        
        for (PlantingPlan plantingPlan : plantingPlans.getRecords()){
            PlantingPlanAdjust plantingPlanAdjust = this.lambdaQuery()
                    .eq(PlantingPlanAdjust::getPlanId, plantingPlan.getPlanId())
                    .one();
                
            if (plantingPlanAdjust != null){
                PlantingPlanAdjustVo vo = buildVoFromRecord(plantingPlanAdjust);
                vo.setPlanName(plantingPlan.getPlanName());
                
                // 避免重复添加（如果该记录已经通过用户查询添加过）
                boolean exists = vos.stream()
                        .anyMatch(v -> v.getAdjustId().equals(vo.getAdjustId()));
                if (!exists) {
                    vos.add(vo);
                }
            }
        }
    
        // 计算总记录数（去重后的）
        long total = users.getTotal() + plantingPlans.getTotal();
        Page<PlantingPlanAdjustVo> voPage = new Page<>(pageNum, pageSize, total);
        voPage.setRecords(vos);
        return Result.ok(voPage);
    }

    @Override
    public Result updatePlantingPlanAdjust(PlantingPlanAdjustDto plantingPlanAdjustDto) {
        User user = userService.lambdaQuery().eq(User::getName, plantingPlanAdjustDto.getApplicant())
                .eq(User::getPhone, plantingPlanAdjustDto.getPhone())
                .one();
        if (user == null){
            return Result.error("用户不存在");
        }
        Long userId = user.getUserId();
        PlantingPlanAdjust plantingPlanAdjust = new PlantingPlanAdjust();
        plantingPlanAdjust.setApplicantId(userId);
        BeanUtils.copyProperties(plantingPlanAdjustDto, plantingPlanAdjust);
        return this.updateById(plantingPlanAdjust) ? Result.ok() : Result.error("更新失败");
    }

    @Override
    public Result getPlantingPlanAdjustsByAdjustId(Long adjustId) {
        PlantingPlanAdjust plantingPlanAdjust = this.lambdaQuery().eq(PlantingPlanAdjust::getAdjustId, adjustId).one();
        if (plantingPlanAdjust == null){
            return Result.error("记录不存在");
        }
        PlantingPlanAdjustDetailVo vo = new PlantingPlanAdjustDetailVo();
        //根据计划ID查询计划名
        PlantingPlan plantingPlan = plantingPlanService.lambdaQuery()
                .eq(PlantingPlan::getPlanId, plantingPlanAdjust.getPlanId()).one();
        if (plantingPlan != null){
            vo.setPlanName(plantingPlan.getPlanName());
            String name = userService.lambdaQuery().eq(User::getUserId, plantingPlan.getCreatorId()).one().getName();
            vo.setCreator(name);
        }
        //根据地块ID查询地块名、地块位置、地块面积
        Land land = landService.lambdaQuery().eq(Land::getLandId, plantingPlanAdjust.getLandId()).one();
        if (land != null){
            vo.setLandName(land.getLandName());
            vo.setLandLocation(land.getLocation());
            vo.setLandArea(land.getArea());
        }
        //根据作物ID查询作物名
        Crop crop = cropService.lambdaQuery().eq(Crop::getCropId, plantingPlanAdjust.getCropId()).one();
        if (crop != null){
            vo.setCropName(crop.getCropName());
        }
        BeanUtils.copyProperties(plantingPlanAdjust, vo);
        System.out.println(vo);
        return Result.ok(vo);
    }

    /**
     * 构建 VO 对象的基础信息（从计划调整记录和关联表）
     */
    private PlantingPlanAdjustVo buildVoFromRecord(PlantingPlanAdjust plantingPlanAdjust) {
        PlantingPlanAdjustVo vo = new PlantingPlanAdjustVo();
        BeanUtils.copyProperties(plantingPlanAdjust, vo);
        
        // 根据申请人 ID 查询用户名和手机号
        if (plantingPlanAdjust.getApplicantId() != null){
            User user = userService.lambdaQuery()
                    .eq(User::getUserId, plantingPlanAdjust.getApplicantId())
                    .one();
            if (user != null){
                vo.setApplicant(user.getName());
                vo.setPhone(user.getPhone());
            }
        }
        
        // 根据计划 ID 查询计划名称
        if (plantingPlanAdjust.getPlanId() != null){
            PlantingPlan plantingPlan = plantingPlanService.lambdaQuery()
                    .eq(PlantingPlan::getPlanId, plantingPlanAdjust.getPlanId())
                    .one();
            if (plantingPlan != null){
                vo.setPlanName(plantingPlan.getPlanName());
            }
        }
        
        return vo;
    }
}




