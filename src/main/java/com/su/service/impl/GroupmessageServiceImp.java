package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.pojo.Groupmessage;
import com.su.mapper.GroupmessageMapper;
import com.su.pojo.Onemessage;
import com.su.service.GroupmessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-22
 */
@Service
public class GroupmessageServiceImp extends ServiceImpl<GroupmessageMapper, Groupmessage> implements GroupmessageService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //根据自己的id和群号id，获取群聊最晚5条消息
    @Override
    public List<Groupmessage> getGroupMessage(Integer groupId) {
        List<Groupmessage> result=new ArrayList<>();
        //获取5条聊天记录
        int count=10;
        //redis中先获取
        List<Groupmessage> list = (List<Groupmessage>) redisTemplate.opsForValue().get(CommonUtils.group_message);
        if (list!=null){
            List<Groupmessage> redisMsgs = list.stream().
                    sorted(Comparator.comparing(Groupmessage::getCreateTime).reversed())
                    .limit(count).collect(Collectors.toList());
            result.addAll(redisMsgs);
            count=count-redisMsgs.size();
        }
        //，数据库获取
        List<Groupmessage> dbMsgs = baseMapper.selectList(Wrappers.<Groupmessage>lambdaQuery().
                eq(Groupmessage::getGroupsId, groupId).
                orderByDesc(Groupmessage::getCreateTime).
                last("limit " + count));
        result.addAll(dbMsgs);

        //结果反转
        Collections.reverse(result);
        return result;
    }
}
