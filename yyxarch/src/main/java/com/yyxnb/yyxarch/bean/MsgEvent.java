package com.yyxnb.yyxarch.bean;

/**
 * RxBus data
 */
public class MsgEvent {
    private int id;
    private String status;
    private Object msg;

    public MsgEvent() {
    }

    public MsgEvent(int id, Object msg) {
        this.id = id;
        this.msg = msg;
    }

    public MsgEvent(String status, Object msg) {
        this.status = status;
        this.msg = msg;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}