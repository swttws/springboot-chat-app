# springboot-chat-app
及时通信APP，使用技术为springboot+Netty+Redis+RabbitMQ
使用Netty主从Reactor多线程模型，负责读取消息、业务处理、消息转发，实现在线用户消息的实时获取。
使用Redis的Zset数据结构，根据scope评分维护用户会话列表的顺序，实现会话列表的动态更新。
使用推拉模式实现单聊、群聊新消息的获取，防止消息丢失，使用RabbitMQ异步保存消息到数据库，从而缓解数据库的压力，降低业务的耦合度。
使用策略模式，实现对不同消息事件，选择合适策略进行处理，提高系统的扩展性。
使用SpringSecurity+JWT+Redis实现用户登录认证、密码加密，防止身份伪造攻击。
