package org.tinygame.herostory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceMain {
    static private final Logger LOGGER = LoggerFactory.getLogger(ServiceMain.class);

    static public void main(String[] args) {
        PropertyConfigurator.configure(ServiceMain.class.getClassLoader().getResourceAsStream("log4j.properties"));

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            //服务器信道处理方式
            b.channel(NioServerSocketChannel.class);
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            //http 服务器编解码器
                            new HttpServerCodec(),
                            //内容长度限制
                            new HttpObjectAggregator(65535),
                            //webSocket 协议处理器，用于实现握手 ping pong等消息
                            new WebSocketServerProtocolHandler("/websocket"),
                            //自定义消息解码器
                            new GameMsgDecoder(),
                            //自定义的消息编码器
                            new GameMsgEncoder(),
                            //自定义消息处理器
                            new GameMsgHandler()
                    );
                }
            });

            b.option(ChannelOption.SO_BACKLOG, 128);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            //绑定端口
            ChannelFuture f = b.bind(12345).sync();
            if (f.isSuccess()) {
                LOGGER.info("游戏服务器启动成功");
            }

            //等待服务器信道关闭，也就是说不立即结束程序，，让程序可以一直提供服务
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
