package com.wyq.chapter02.pseudoAsy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 伪异步 I/O 模型
 * 解决同步阻塞 IO 的一个链路需要一个线程来处理的问题；
 * 它后端是通过一个线程池来处理多个客户端的请求接入，形成客户端个数 M : 线程池最大线程数 N 的比例关系，其中 M 可以远远大于 N。
 * 通过线程池可以灵活低调配线程资源，设置线程的最大值，防止由于海量并发接入导致线程耗尽。
 * 但是底层通信依然是采用同步阻塞模型，因此无法从更本上解决问题，只是一个优化。
 *
 * @author Yongqian Wang
 * @version 1.0
 */
public class TimeServer {
    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args != null && args.length > 0) {

            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }

        }
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            // 创建IO任务线程池
            TimeServerHandlerExecutePool singleExecutor =
                    new TimeServerHandlerExecutePool(50, 10000);
            while (true) {
                socket = server.accept();
                // 当有新的客户端接入时，将客户端的 Socket 封装成一个 Task，投递到后端的线程池中进行处理
                singleExecutor.execute(new TimeServerHandler(socket));
            }
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                server.close();
                server = null;
            }
        }
    }
}
