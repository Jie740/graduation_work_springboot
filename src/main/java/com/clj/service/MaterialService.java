package com.clj.service;

import com.clj.domain.Material;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clj.domain.vo.MaterialVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
* @author ajie
* @description 针对表【material(农资表)】的数据库操作Service
* @createDate 2026-03-02 20:08:17
*/
public interface MaterialService extends IService<Material> {


    String add(Material material);

    void delete(Long materialId);

    void updateMaterial(Material material);

    Page<MaterialVo> searchMaterialsByPage(Long typeId,String keyword,Integer pageNum, Integer pageSize);

    Page<MaterialVo> getMaterialsByPage(Integer pageNum, Integer pageSize);

    List<Material> getAll();

    Map<String, String> getMaterialTypeById(Long materialId);
}
