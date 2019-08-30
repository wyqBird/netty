package com.wyq.chapter05.delimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Yongqian Wang
 * @version 1.0
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelHandlerAdapter {

    int counter = 0;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        // 直接将接收到的消息打印出来
        System.out.println("This is " + ++counter + " times receive client : ["
                + body + "]");
        // 之前设置 DelimiterBasedFrameDecoder 过滤掉了分隔符，返回给客户端时需要在请求消息尾部拼接 “$_”
        body += "$_";
        // 创建 ByteBuf ，将原始消息重新返回给客户端
        ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
        ctx.writeAndFlush(echo);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();// 发生异常，关闭链路
    }
}

