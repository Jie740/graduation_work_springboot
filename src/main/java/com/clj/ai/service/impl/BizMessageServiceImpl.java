package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.ai.domain.BizMessage;
import com.clj.ai.service.BizMessageService;
import com.clj.ai.mapper.BizMessageMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【biz_message(AI聊天消息表)】的数据库操作Service实现
* @createDate 2026-08-18 21:00:23
*/
@Service
public class BizMessageServiceImpl extends ServiceImpl<BizMessageMapper, BizMessage>
    implements BizMessageService{

}




