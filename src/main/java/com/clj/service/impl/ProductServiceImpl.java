package com.clj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.Product;
import com.clj.service.ProductService;
import com.clj.mapper.ProductMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【product(产品表)】的数据库操作Service实现
* @createDate 2026-03-02 20:07:51
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
    implements ProductService{

}




