package com.yyxnb.yyxarch.base;

import java.io.Serializable;

/**
 * Description: 通用的数据解析类
 *
 * @author : yyx
 * @date ：2018/6/13
 */
public class BaseData<T> implements Serializable {

    /**
     * 状态码 一般为 state or code
     */
    private int state;

    private int code;
    /**
     * 描述
     */
    private String msg;
    /**
     * 错误 一般没有
     */
    private String failData;
    /**
     * 数据 泛型
     */
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFailData() {
        return failData;
    }

    public void setFailData(String failData) {
        this.failData = failData;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
