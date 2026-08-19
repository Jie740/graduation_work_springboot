package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.common.exception.BusinessException;
import com.clj.domain.Crop;
import com.clj.service.CropService;
import com.clj.mapper.CropMapper;
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
    public void add(Crop crop) {
        if (!this.save(crop)) {
            throw new BusinessException("添加失败");
        }
    }

    @Override
    public void delete(Integer cropId) {
        if (!this.removeById(cropId)) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public void updateCrop(Crop crop) {
        if (!this.updateById(crop)) {
            throw new BusinessException("更新失败");
        }
    }

    @Override
    public Page<Crop> getCropsByPage(Integer pageNum, Integer pageSize) {
        Page<Crop> cropPage = new Page<Crop>(pageNum, pageSize);
        return this.lambdaQuery().page(cropPage);
    }

    @Override
    public Page<Crop> searchCropsByPage(String keyword, Integer pageNum, Integer pageSize) {
        if (keyword==null){
            return getCropsByPage(pageNum, pageSize);
        }
        Page<Crop> page = new Page<>(pageNum, pageSize);
        return this.lambdaQuery()
                .likeRight(Crop::getCropName, keyword)
                .likeRight(Crop::getCropType, keyword)
                .page(page);
    }
}
