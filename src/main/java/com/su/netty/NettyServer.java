package com.su.netty;


import com.su.netty.handler.WebSocketServerHandler;
import com.su.netty.strategy.HandlerMessage;
import com.su.service.DialogService;
import com.su.service.GroupmessageService;
import com.su.service.OnemessageService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.util.concurrent.ForkJoinPool;

@Component
@Slf4j
public class NettyServer {

    @Autowired
    private HandlerMessage handlerMessage;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private DialogService dialogService;

    @Autowired
    private OnemessageService onemessageService;

    @Autowired
    private GroupmessageService groupmessageService;

    //工作线程池，主从线程池模型
    private EventLoopGroup bossGroup=new NioEventLoopGroup();
    private EventLoopGroup workGroup=new NioEventLoopGroup();

    private int port=8181;

    //启动netty服务
    public void startServer(){
        try {
            ServerBootstrap server=new ServerBootstrap()
                    .group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .localAddress(new InetSocketAddress(port))
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            log.info("客户端连接请求过来");
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            /** 解析Http请求 */
                            pipeline.addLast(new HttpServerCodec());
                            //将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            //处理大数据流
                            pipeline.addLast(new ChunkedWriteHandler());

                            /** 解析WebSocket请求 */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/chatServer"));    //Inbound

                            //自定义消息处理器
                            pipeline.addLast(new WebSocketServerHandler(handlerMessage,redisTemplate,
                                    dialogService,onemessageService,groupmessageService));
                        }
                    });
            server.bind(port).addListener(future -> {
                log.info("netty服务端启动，端口号为：{}",port);
            }).sync();
        } catch (InterruptedException e) {
            log.info("netty服务器异常，请及时处理");
        }
    }

    //服务端启动
    @PostConstruct
    public void start(){
        ForkJoinPool.commonPool().submit(this::startServer);
    }

    //spring销毁前，关闭主从线程
    @PreDestroy
    public void destroy(){
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }


}
