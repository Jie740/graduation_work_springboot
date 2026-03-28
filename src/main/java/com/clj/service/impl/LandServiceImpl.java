package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Land;
import com.clj.service.LandService;
import com.clj.mapper.LandMapper;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【land(地块信息表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:21
*/
@Service
public class LandServiceImpl extends ServiceImpl<LandMapper, Land>
    implements LandService{

    @Override
    public Result addLand(Land land) {
        return this.save(land)?Result.ok():Result.error("添加失败");
    }

    @Override
    public Result deleteLand(Long landId) {
        return this.removeById(landId)?Result.ok():Result.error("删除失败");
    }

    @Override
    public Result updateLand(Land land) {
        return this.updateById(land)?Result.ok():Result.error("修改失败");
    }

    @Override
    public Result getLandsByPage(Integer pageNum, Integer pageSize) {
        Page<Land> page=new Page<>(pageNum,pageSize);
        return Result.ok(this.lambdaQuery().page(page));
    }

    //查询条件：地块名、地块位置
    @Override
    public Result searchLandsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword == null){
            return getLandsByPage(pageNum, pageSize);
        }
        Page<Land> page = new Page<>(pageNum, pageSize);
        return Result.ok(this.lambdaQuery().likeRight(Land::getLandName, keyword)
                .or()
                .likeRight(Land::getLocation, keyword)
                .page(page));
    }


//    @Override
//    public Boolean updateLandStatus(Long landId, Integer status) {
//        return this.lambdaUpdate()
//                .eq(Land::getLandId, landId)
//                .set(Land::getStatus, status)
//                .update();
//    }

    @Override
    public Result getAll() {
        return Result.ok(this.list());
    }
}




