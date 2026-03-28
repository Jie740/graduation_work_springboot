package com.clj.service;

import com.clj.domain.Land;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【land(地块信息表)】的数据库操作Service
* @createDate 2026-03-02 20:08:21
*/
public interface LandService extends IService<Land> {

    Result addLand(Land land);

    Result deleteLand(Long landId);

    Result updateLand(Land land);

    Result getLandsByPage(Integer pageNum, Integer pageSize);

    Result searchLandsByPage(String keyword, Integer pageNum, Integer pageSize);

//    Boolean updateLandStatus(Long landId, Integer status);

    Result getAll();

}
