package com.wyq.chapter07;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author Yongqian Wang
 * @version 1.0
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    private int count;
    private int sendNumber;

    public EchoClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User[] users = getUsers();
        for (User u : users) {
            ctx.write(u);
        }
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("this is client receive msg【  " + ++count + "  】times:【" + msg + "】");
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    private User[] getUsers() {
        User[] users = new User[sendNumber];
        for (int i = 0; i < sendNumber; i++) {
            User user = new User();
            user.setId(String.valueOf(i));
            user.setAge(18 + i);
            user.setName("电脑" + i);
            user.setSex("男" + String.valueOf(i * 2));
            users[i] = user;
        }
        return users;
    }

}
