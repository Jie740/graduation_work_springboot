package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Crop;
import com.clj.service.CropService;
import com.clj.mapper.CropMapper;
import com.clj.utils.Result;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【crop(农作物信息表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:41
*/
@Service
public class CropServiceImpl extends ServiceImpl<CropMapper, Crop>
    implements CropService{

    @Override
    public Result add(Crop crop) {
        return this.save(crop)?Result.ok():Result.error("添加失败");
    }

    @Override
    public Result delete(Integer cropId) {
        return this.removeById(cropId)?Result.ok():Result.error("删除失败");
    }

    @Override
    public Result updateCrop(Crop crop) {
        return this.updateById(crop)?Result.ok():Result.error("更新失败");
    }

    @Override
    public Result getCropsByPage(Integer pageNum, Integer pageSize) {
        Page<Crop> cropPage = new Page<Crop>(pageNum, pageSize);
        return Result.ok(this.lambdaQuery().page(cropPage));
    }

    @Override
    public Result searchCropsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword==null){
            return getCropsByPage(pageNum, pageSize);
        }
        Page<Crop> page = new Page<>(pageNum, pageSize);
        return Result.ok(this.lambdaQuery()
                .likeRight(Crop::getCropName, keyword)
                .likeRight(Crop::getCropType, keyword)
                .page(page));
    }
}




