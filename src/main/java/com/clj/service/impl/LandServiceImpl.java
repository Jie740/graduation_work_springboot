package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.Land;
import com.clj.domain.LandAllocation;
import com.clj.service.LandAllocationService;
import com.clj.service.LandService;
import com.clj.mapper.LandMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @author ajie
* @description 针对表【land(地块信息表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:21
*/
@Service
@RequiredArgsConstructor
public class LandServiceImpl extends ServiceImpl<LandMapper, Land>
    implements LandService{

    @Override
    public void addLand(Land land) {
        if (this.lambdaQuery().eq(Land::getLandName, land.getLandName()).exists()){
            throw new BusinessException("地块名已存在");
        }
        if (!this.save(land)){
            throw new BusinessException("添加失败");
        }
    }

    @Override
    public void deleteLand(Long landId) {
        if (!this.removeById(landId)){
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updateLand(Land land) {
        if (!this.updateById(land)){
            throw new BusinessException("修改失败");
        }
    }

    @Override
    public Page<Land> getLandsByPage(Integer pageNum, Integer pageSize) {
        Page<Land> page=new Page<>(pageNum,pageSize);
        return this.lambdaQuery().page(page);
    }

    //查询条件：地块名、地块位置
    @Override
    public Page<Land> searchLandsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return getLandsByPage(pageNum, pageSize);
        }
        Page<Land> page = new Page<>(pageNum, pageSize);
        return this.lambdaQuery().likeRight(Land::getLandName, keyword)
                .or()
                .likeRight(Land::getLocation, keyword)
                .page(page);
    }


//    @Override
//    public Boolean updateLandStatus(Long landId, Integer status) {
//        return this.lambdaUpdate()
//                .eq(Land::getLandId, landId)
//                .set(Land::getStatus, status)
//                .update();
//    }

    @Override
    public List<Land> getAll() {
        return this.list();
    }


}
