package com.lzb.echo.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * 事件处理类
 * 事件会派发给ChannelHandler
 * EchoServerHandler核心业务逻辑 最上层实现ChannelHandler接口
 * EchoServerHandler实现ChannelInboundHandlerAdapter来定义响应入站事件的方法
 *
 * Sharable注解，对所有的客户端连接来说，都会使用同一个EchoServerHandler
 * @author lizhibiao
 * @date 2019/6/12 19:59
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("server received :" + in.toString(CharsetUtil.UTF_8));
        ctx.write(in);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //调用writeAndFlush才会释放指向保存该消息的ByteBuf内存引用
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
                .addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 需要捕获异常，不然接受的异常将会被传递到ChannelPipeline的尾端并被记录
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
