package com.wyq.chapter02.nio;

/**
 * @author Yongqian Wang
 * @version 1.0
 */
public class TimeClient {

    public static void main(String[] args) {

        int port = 8080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        // 通过创建 TimeClientHandle 线程来处理异步连接和写操作
        new Thread(new TimeClientHandle("127.0.0.1", port), "TimeClient-001").start();
    }
}
