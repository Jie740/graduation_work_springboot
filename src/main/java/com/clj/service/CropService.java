package com.clj.service;

import com.clj.domain.Crop;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【crop(农作物信息表)】的数据库操作Service
* @createDate 2026-03-02 20:08:41
*/
public interface CropService extends IService<Crop> {

    Result add(Crop crop);
    Result delete(Integer cropId);
    Result updateCrop(Crop crop);
    Result getCropsByPage(Integer pageNum, Integer pageSize);

    Result searchCropsByPage(String keyword, Integer pageNum, Integer pageSize);
}
