package com.wyq.chapter02.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO 的主要问题：
 * 每当有一个新的客户端请求接入时，服务端必须创建一个新的线程处理新接入的客户端链路。
 * 一个线程只能处理一个客户端连接。
 * 该模型无法满足高性能、高并发接入的场景。
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
            // 创建 ServerSocket
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while (true) {
                // 监听客户端连接，没有连接时，阻塞在 accept
                socket = server.accept();
                // 有接入时，创建新的客户端线程处理这条 Socket 链路
                new Thread(new TimeServerHandler(socket)).start();
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
