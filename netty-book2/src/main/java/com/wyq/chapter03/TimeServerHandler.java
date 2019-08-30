package com.wyq.chapter03;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * TimeServerHandler 用于对网络事件进行读写
 *
 * @author Yongqian Wang
 * @version 1.0
 */
public class TimeServerHandler extends ChannelHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        // 将缓冲区中的字节数组f复制到新建的 byte 数组中
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("The time server receive order : " + body);
        String currentTime =
                "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                        new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";

        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        // 将待发送的消息发送到缓冲数组中
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 将缓冲数组中的消息写入到 SocketChannel 中发送给对方
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
