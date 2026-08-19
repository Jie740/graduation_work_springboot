package com.clj.service;

import com.clj.domain.Crop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* @author ajie
* @description 针对表【crop(农作物信息表)】的数据库操作Service
* @createDate 2026-03-02 20:08:41
*/
public interface CropService extends IService<Crop> {

    void add(Crop crop);
    void delete(Integer cropId);
    void updateCrop(Crop crop);
    Page<Crop> getCropsByPage(Integer pageNum, Integer pageSize);

    Page<Crop> searchCropsByPage(String keyword, Integer pageNum, Integer pageSize);
}
