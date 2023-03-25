package com.su.netty.protocol;

public enum Type {

    //客户端->服务端
    CONNECT_EVENT("CONNECT_EVENT"),//建立连接事件
    ADD_FRIEND_EVENT("ADD_FRIEND_EVENT"),//添加好友申请
    AGREE_APPLICATION("AGREE_APPLICATION"),//同意好友申请
    GET_FRIEND_LIST("GET_FRIEND_LIST"),
    PULL_MSG("PULL_MSG"),//拉去申请列表
    CHAT_ONLY("CHAT_ONLY"),//单聊消息发送
    DISCONNECT_EVENT("DISCONNECT_EVENT"),//客户端断开连接
    ADD_GROUP("ADD_GROUP"),//建立群聊
    GROUP_MESSAGE("GROUP_MESSAGE"),//群聊信息


    //服务端->客户端
    NEW_FRIEND("NEW_FRIEND"),//新好友的提示请求
    FRIEND_ONLINE("FRIEND_ONLINE"),//好友上线
    PULL_FRIEND("PULL_FRIEND"),//拉取聊天页好友信息
    MESSAGE_ERROR("MESSAGE_ERROR"),//错误提示通知
    NEW_MESSAGE("NEW_MESSAGE"),//新消息到来，会话列表刷新
    ;

    private String type;

    public String getType(){
        return type;
    }

    Type(String type){
        this.type=type;
    }

}
