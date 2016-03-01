package com.commonrail.mtf.mvp.model.entity;

import java.io.Serializable;

/**
 * Created by wengyiming on 2016/1/6.
 */
public class Result<Type> implements Serializable {
    private int status = 0;
    private Type data;
    private String msg;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Type getData() {
        return data;
    }

    public void setData(Type data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
