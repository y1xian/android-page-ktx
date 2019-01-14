package com.yyxnb.yyxarch.bean;

import com.yyxnb.yyxarch.annotation.LceeStatus;

public class Lcee<T> {

    @LceeStatus
    public final int status;
    public final T data;
    public final String error;

    public Lcee(int status, T data, String error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Lcee<T> content(T data) {
        return new Lcee<>(LceeStatus.Content, data, null);
    }

    public static <T> Lcee<T> error(T data, String error) {
        return new Lcee<>(LceeStatus.Error, data, error);
    }

    public static <T> Lcee<T> error(String error) {
        return error(null, error);
    }

    public static <T> Lcee<T> empty(T data) {
        return new Lcee<>(LceeStatus.Empty, data, null);
    }

    public static <T> Lcee<T> empty() {
        return empty(null);
    }

    public static <T> Lcee<T> loading(T data) {
        return new Lcee<>(LceeStatus.Loading, data, null);
    }

    public static <T> Lcee<T> loading() {
        return loading(null);
    }

}
