package com.su.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.su.common.Errors;
import com.su.netty.protocol.MyMessage;
import com.su.pojo.Dialog;
import com.su.mapper.DialogMapper;
import com.su.pojo.Onemessage;
import com.su.pojo.view.OffLIneCache;
import com.su.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.su.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.websocket.OnMessage;
import java.util.*;
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
@Slf4j
public class DialogServiceImp extends ServiceImpl<DialogMapper, Dialog> implements DialogService {

    @Autowired
    private OnemessageService onemessageService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private GroupService groupService;

    //返回单聊，群聊 合并后列表
    @Override
    public List<Dialog> getDialogByMyId(Integer userId) {
        //redis的key
        String redisKey= CommonUtils.dialog_prefix +userId;
        String groupKey=CommonUtils.groupDialog_prefix+userId;
        List<Dialog> dialogList=new ArrayList<>();
        //合并后会话列表
        redisTemplate.opsForZSet().unionAndStore(redisKey,groupKey,CommonUtils.union_dialog);
        //获取并集会话列表
        Set<Object> set = redisTemplate.opsForZSet().
                reverseRange(CommonUtils.union_dialog, 0, -1);
        if (set==null){
            throw new Errors("500","你的会话列表空空");
        }
        dialogList=set.stream().map(item->(Dialog)item).collect(Collectors.toList());
        return dialogList;
    }

    //会话信息更新
    @Override
    public void updateDialog(MyMessage message,Integer type) {
        Integer sendUserId = message.getSendUserId();
        Integer receiverId = message.getReceiverId();
        //根据type获取rediskey
        String redisKey=type==1?CommonUtils.dialog_prefix+receiverId:
                CommonUtils.groupDialog_prefix+receiverId;
        //获取接收人的会话信息
        Set<Object> set = redisTemplate.opsForZSet().range(redisKey, 0, -1);
        if (set==null){
            throw new Errors("500","信息异常");
        }
        List<Dialog> dialogList = set.stream().
                map(item -> (Dialog) item).collect(Collectors.toList());
        //获取会话中 接收者信息数量
        long count = dialogList.stream().filter(item -> item.getFriendId().equals(sendUserId)).count();
        //接收者会话信息不包含发送者,数据库中查询
        Dialog dialog=null;
        if (count==0){
            dialog = baseMapper.selectOne(Wrappers.<Dialog>lambdaQuery()
                    .eq(Dialog::getMyId, receiverId).eq(Dialog::getFriendId, sendUserId));
            //添加到会话列表中
            saveDialogToRedis(redisKey,dialog,type);
        }else{//存在,更新会话信息
            //获取改会话信息
            List<Dialog> collect = dialogList.stream().
                    filter(item -> item.getFriendId().equals(sendUserId))
                    .collect(Collectors.toList());
            dialog=collect.get(0);
            //redis会话列表中移除该会话
            Long remove = redisTemplate.opsForZSet().remove(redisKey, dialog);
            log.info("删除记录数："+remove+"------");
            //从新插入新会话
            dialog.setLastTime(new Date());
            dialog.setTimeMillis(dialog.getLastTime().getTime());
            redisTemplate.opsForZSet().add(redisKey,dialog,dialog.getTimeMillis());
        }
    }

    //将dialog信息保存到redis中
    private void saveDialogToRedis(String redisKey,Dialog dialog,Integer type){
        //对方id
        Integer friendId=dialog.getFriendId();
        //获取未读消息量
        long count = onemessageService.count(Wrappers.<Onemessage>lambdaQuery()
                .eq(Onemessage::getSendUserid, friendId)
                .eq(Onemessage::getStatus, 1));
        dialog.setNotReadMsgCount((int) count);
        //获取好友/群名字
        dialog.setFriendName(type==1?userService.getById(dialog.getFriendId()).getUserName()
                :groupService.getById(dialog.getFriendId()).getGroupName());
        //毫秒数做评分
        dialog.setTimeMillis(dialog.getLastTime().getTime());
        //缓存到redis的zset中
        redisTemplate.opsForZSet().add(redisKey,dialog,dialog.getTimeMillis());
    }


    //根据用户id和类型保存会话到redis中，启动连接时
    @Override
    public void getDialogByTypeToRedis(Integer userId, Integer type) {
        List<Dialog> dialogList = baseMapper.selectList(Wrappers.<Dialog>lambdaQuery()
                .eq(Dialog::getMyId, userId).eq(Dialog::getType, type));
        //保存的rediskey
        String redisKey=type==1?CommonUtils.dialog_prefix+userId
                :CommonUtils.groupDialog_prefix+userId;
        //删除原来缓存中旧的key,防止会话重复
        redisTemplate.delete(redisKey);
        //保存会话信息
        dialogList.forEach(dialog -> {
            saveDialogToRedis(redisKey,dialog,type);
        });
    }


    //保存离线缓存日志到redis中
    @Override
    public void saveOffLineInfo(MyMessage message,Integer type) {
        String redisKey=type==1?CommonUtils.one_offLine:
                CommonUtils.group_offLine;
        //保存单聊离线日志到缓存中
        //保存格式  map<离线用户id,Map<发送id,OfflineCache>>
        //判断自谦是否发送过
        Map<Integer,Map<Integer, OffLIneCache>> map = (Map<Integer, Map<Integer, OffLIneCache>>)
                redisTemplate.opsForValue().get(redisKey);
        //缓存中已经存在离线值
        if (map!=null&& map.get(message.getReceiverId())!=null
                &&map.get(message.getReceiverId()).get(message.getSendUserId())!=null){
            Map<Integer, OffLIneCache> offLIneCacheMap = map.get(message.getReceiverId());
            //更新缓存改会话离线消息最晚时间
            OffLIneCache offLIneCache = offLIneCacheMap.get(message.getSendUserId());
            offLIneCache.setLastTime(new Date());
            map.get(message.getReceiverId()).put(message.getSendUserId(),offLIneCache);
        }else{
            if (map==null){
                map=new HashMap<>();
            }
            //保存离线消息缓存
            OffLIneCache offLIneCache = new OffLIneCache();
            offLIneCache.setType(type);//根据单聊，群聊类型进行判断
            offLIneCache.setSendUserId(message.getSendUserId());
            offLIneCache.setLastTime(new Date());
            Map<Integer,OffLIneCache> offLIneCacheMap=new HashMap<>();
            offLIneCacheMap.put(message.getSendUserId(),offLIneCache);
            //保存到map中
            map.put(message.getReceiverId(),offLIneCacheMap);
        }
        //更新redis
        redisTemplate.delete(redisKey);
        redisTemplate.opsForValue().set(redisKey,map);
    }



}
