package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Material;
import com.clj.service.MaterialService;
import com.clj.mapper.MaterialMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【material(农资表)】的数据库操作Service实现
* @createDate 2026-03-02 20:08:17
*/
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material>
    implements MaterialService{

}




