package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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

}




