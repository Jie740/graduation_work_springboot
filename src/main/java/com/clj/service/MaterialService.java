package com.clj.service;

import com.clj.domain.Material;
import com.baomidou.mybatisplus.extension.service.IService;
import com.clj.utils.Result;

/**
* @author ajie
* @description 针对表【material(农资表)】的数据库操作Service
* @createDate 2026-03-02 20:08:17
*/
public interface MaterialService extends IService<Material> {


    Result add(Material material);

    Result delete(Long materialId);

    Result updateMaterial(Material material);

    Result searchMaterialsByPage(Long typeId,String keyword,Integer pageNum, Integer pageSize);

    Result getMaterialsByPage(Integer pageNum, Integer pageSize);

    Result getAll();

    Result getMaterialTypeById(Long materialId);
}
