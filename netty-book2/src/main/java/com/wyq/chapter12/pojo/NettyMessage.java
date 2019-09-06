package com.wyq.chapter12.pojo;

/**
 * Netty 消息
 *
 * @author Yongqian Wang
 * @version 1.0
 */
public final class NettyMessage {
    /**
     * 消息头
     */
    private Header header;
    /**
     * 消息体
     */
    private Object body;

    public final Header getHeader() {
        return header;
    }

    public final void setHeader(Header header) {
        this.header = header;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" + "header=" + header + '}';
    }
}
