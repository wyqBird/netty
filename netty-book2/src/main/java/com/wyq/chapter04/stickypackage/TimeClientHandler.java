package com.wyq.chapter04.stickypackage;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.util.logging.Logger;

/**
 * @author Yongqian Wang
 * @version 1.0
 */
public class TimeClientHandler extends ChannelHandlerAdapter {

    private static final Logger logger = Logger
            .getLogger(TimeClientHandler.class.getName());

    private int counter;

    private byte[] req;

    public TimeClientHandler() {
        req = ("QUERY TIME ORDER" + System.getProperty("line.separator"))
                .getBytes();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ByteBuf message = null;
        // 客户端和服务端建立连接后，循环发送100条消息，梅发送一条就刷新一次，保证每条消息都会写入 Channel中
        for (int i = 0; i < 100; i++) {
            message = Unpooled.buffer(req.length);
            message.writeBytes(req);
            ctx.writeAndFlush(message);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        // 客户端每接收到服务端一条应答消息之后，就打印一次计数器
        String body = new String(req, "UTF-8");
        System.out.println("Now is : " + body + " ; the counter is : "
                + ++counter);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 释放资源
        logger.warning("Unexpected exception from downstream : "
                + cause.getMessage());
        ctx.close();
    }
}