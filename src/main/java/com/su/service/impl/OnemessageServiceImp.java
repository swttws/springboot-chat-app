package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.pojo.Onemessage;
import com.su.mapper.OnemessageMapper;
import com.su.service.OnemessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.service.UserService;
import com.su.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author swt 2023-3-18
 * @since 2023-03-21
 */
@Service
public class OnemessageServiceImp extends ServiceImpl<OnemessageMapper, Onemessage> implements OnemessageService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private UserService userService;

    //根据自己id和好友id获取消息
    @Override
    public List<Onemessage> getMessageList(Integer myId, Integer friendId) {
        List<Onemessage> result=new ArrayList<>();
        //一次查询5条
        int count=10;
        String redisKey= CommonUtils.message_prefix;
        //查询redis数据，是否有消息
        List<Onemessage> onemessageList = (List<Onemessage>)
                redisTemplate.opsForValue().get(redisKey);
        if (onemessageList!=null&&onemessageList.size()>0) {
            //根据时间从大到小排
            result = onemessageList.stream()
                    .filter(onemessage ->
                            (onemessage.getReceiverUserid().equals(myId) && onemessage.getSendUserid().equals(friendId))
                                    || (onemessage.getSendUserid().equals(myId) && onemessage.getReceiverUserid().equals(friendId)))
                    .sorted(Comparator.comparing(Onemessage::getCreateTime).reversed())
                    .collect(Collectors.toList());
            if (result.size()>count){
                List<Onemessage> collect = result.stream().limit(5).collect(Collectors.toList());
                Collections.reverse(collect);
            }
        }
        //根据redis查询的条数，数据库查询剩余的消息
        List<Onemessage> list = baseMapper.selectList(Wrappers.<Onemessage>lambdaQuery().
                eq(Onemessage::getReceiverUserid, myId).eq(Onemessage::getSendUserid, friendId).or()
                .eq(Onemessage::getReceiverUserid, friendId).eq(Onemessage::getSendUserid, myId).
                        orderByDesc(Onemessage::getCreateTime).last("limit " + count));
        result.addAll(list);

        //10条消息从新排序，取时间后5条(防止数据库存在离线消息)
        List<Onemessage> collect = result.stream().
                sorted(Comparator.comparing(Onemessage::getCreateTime).reversed())
                .limit(count)
                .collect(Collectors.toList());
        //结果集反转,从小到大排
        Collections.reverse(collect);
        return collect;
    }
}
