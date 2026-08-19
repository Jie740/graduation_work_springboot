package com.clj.service;

import com.clj.domain.Land;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author ajie
* @description 针对表【land(地块信息表)】的数据库操作Service
* @createDate 2026-03-02 20:08:21
*/
public interface LandService extends IService<Land> {

    void addLand(Land land);

    void deleteLand(Long landId);

    void updateLand(Land land);

    Page<Land> getLandsByPage(Integer pageNum, Integer pageSize);

    Page<Land> searchLandsByPage(String keyword, Integer pageNum, Integer pageSize);

//    Boolean updateLandStatus(Long landId, Integer status);

    List<Land> getAll();

}
