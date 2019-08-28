package com.wyq.chapter02.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Yongqian Wang
 * @version 1.0
 */
public class MultiplexerTimeServer implements Runnable {

    private Selector selector;

    private ServerSocketChannel servChannel;

    private volatile boolean stop;

    /**
     * 初始化多路复用器、绑定监听端口
     * 构造方法中进行资源初始化
     *
     * @param port
     */
    public MultiplexerTimeServer(int port) {
        try {
            // 创建多路复用器 Selector、ServerSocketChannel
            selector = Selector.open();
            servChannel = ServerSocketChannel.open();
            // 对 channel 和 TCP 参数进行配置
            servChannel.configureBlocking(false);
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            // 将 ServerSocketChannel 注册到 Selector， 监听 SelectionKey.OP_ACCEPT 操作位
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        // 循环遍历 selector， 休眠时间是 1s，无论是否有读写事件发生，selector 每隔 1s 都被唤醒一次
        while (!stop) {
            try {
                selector.select(1000);
                // 当有处于就绪状态的 Channel 时，selector 将返回 Channel 的 SelectionKey 集合
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectedKeys.iterator();
                SelectionKey key = null;
                // 对就绪状态的 Channel 集合进行迭代，可进行网络的异步读写
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        // 处理请求信息
                        handleInput(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null)
                                key.channel().close();
                        }
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        // 多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
        if (selector != null)
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void handleInput(SelectionKey key) throws IOException {

        if (key.isValid()) {
            // 处理新接入的请求消息
            if (key.isAcceptable()) {
                // Accept the new connection
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                // 接收客户端连接请求，创建 SocketChannel 实例
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                // Add the new connection to the selector
                sc.register(selector, SelectionKey.OP_READ);
            }
            if (key.isReadable()) {
                // 读取客户端的请求消息
                SocketChannel sc = (SocketChannel) key.channel();
                // 创建 ByteBuf
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if (readBytes > 0) {
                    // 返回值 > 0：读到了字节，对字节进行编码
                    readBuffer.flip();
                    byte[] bytes = new byte[readBuffer.remaining()];
                    readBuffer.get(bytes);
                    String body = new String(bytes, "UTF-8");
                    System.out.println("The time server receive order : "
                            + body);
                    String currentTime = "QUERY TIME ORDER"
                            .equalsIgnoreCase(body) ? new java.util.Date(
                            System.currentTimeMillis()).toString()
                            : "BAD ORDER";

                    // 将应答消息异步发送给 客户端
                    doWrite(sc, currentTime);
                } else if (readBytes < 0) {
                    // 返回值为 -1 < 0，链路已经关闭，需要关闭 SocketChannel，释放资源
                    key.cancel();
                    sc.close();
                } else {
                    ; // 读到0字节，忽略
                }
            }
        }
    }

    private void doWrite(SocketChannel channel, String response)
            throws IOException {
        if (response != null && response.trim().length() > 0) {
            // 将字符串编码成字节数组
            byte[] bytes = response.getBytes();
            // 根据字节数组的容量创建 ByteBuffer
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            // 将字节数组复制到缓冲区
            writeBuffer.put(bytes);
            writeBuffer.flip();
            // 将缓冲区中的字节数组发送出去
            channel.write(writeBuffer);
        }
    }
}

