package com.lzb.echo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 服务端
 * @author lizhibiao
 * @date 2019/6/12 20:28
 */
public class EchoServer {

    /**
     * 监听端口
     */
    private int port;

    public EchoServer(int port)
    {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(8080).start();
    }

    /**
     * 启动方法
     */
    private void start() throws Exception {

        //事件处理类
        final EchoServerHandler serverHandler = new EchoServerHandler();

        //因为使用的NIO传输，所以指定NioEventLoopGroup来接受和处理新的连接
        //例如：接受新连接以及读写数据
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            //new一个启动辅助类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //绑定事件组
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    //这里ChannelInitializer会把一个EchoServerHandler实例添加到该channel的ChannelPipeline中
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //这里事件处理类有Sharable注解，对所有的客户端连接来说，都会使用同一个EchoServerHandler
                            socketChannel.pipeline().addLast(serverHandler);
                        }
                    });

            //绑定服务器，sync()的调用将导致当前Thread阻塞，一直到绑定完成
            ChannelFuture future = serverBootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            //最后关闭EventLoopGroup是否所有资源
            group.shutdownGracefully().sync();
        }
    }
}
