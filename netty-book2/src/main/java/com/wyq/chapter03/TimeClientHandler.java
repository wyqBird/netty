package com.wyq.chapter03;

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

    private final ByteBuf firstMessage;

    public TimeClientHandler() {
        byte[] req = "QUERY TIME ORDER".getBytes();
        firstMessage = Unpooled.buffer(req.length);
        firstMessage.writeBytes(req);

    }

    /**
     * 当客户端和服务端 TCP 链路建立成功之后，Netty 的 NIO 线程会调用 channelActive 方法，
     * 发送查询时间的指令给服务端
     *
     * @param ctx
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(firstMessage);
    }

    /**
     * 当服务端返回应答消息时，channelRead 方法被调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, "UTF-8");
        System.out.println("Now is : " + body);
    }

    /**
     * 当发生异常时，打印异常日志，释放客户端资源
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 释放资源
        logger.warning("Unexpected exception from downstream : "
                + cause.getMessage());
        ctx.close();
    }
}