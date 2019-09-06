package com.wyq.chapter12;

/**
 * @author Yongqian Wang
 * @version 1.0
 */
public enum MessageType {
    /**
     * 3：握手请求消息
     */
    SERVICE_REQ((byte) 0),
    SERVICE_RESP((byte) 1),
    ONE_WAY((byte) 2),
    LOGIN_REQ((byte) 3),
    LOGIN_RESP((byte) 4),
    HEARTBEAT_REQ((byte) 5),
    HEARTBEAT_RESP((byte) 6);

    private byte value;

    private MessageType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }
}
