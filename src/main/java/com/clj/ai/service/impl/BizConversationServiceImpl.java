package com.clj.ai.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.clj.domain.BizConversation;
import com.clj.ai.service.BizConversationService;
import com.clj.ai.mapper.BizConversationMapper;
import org.springframework.stereotype.Service;

/**
* @author ajie
* @description 针对表【biz_conversation(AI对话会话表)】的数据库操作Service实现
* @createDate 2026-08-18 21:00:10
*/
@Service
public class BizConversationServiceImpl extends ServiceImpl<BizConversationMapper, BizConversation>
    implements BizConversationService{

}




