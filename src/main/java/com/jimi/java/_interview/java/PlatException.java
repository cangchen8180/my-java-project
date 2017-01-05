package com.jimi.java._interview.java;

/**
 * @author jimi
 * @version 2017-01-05 22:35.
 */
public class PlatException extends Exception {

    public PlatException() {
    }

    public PlatException(String message) {
        super(message);
    }

    public PlatException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlatException(Throwable cause) {
        super(cause);
    }

    public PlatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * 去掉异常栈构造
     如果你的Exception是自定义类型的，你很清楚什么情况哪行代码会抛出这个Exception，
     例如限流或者拒绝服务了。那么你可以给你的Exception Class重写fillInStackTrace()这个方法，
     搞一个空的实现就可以了，这样构造函数去调的时候就不会真正去调那个native的方法。
     抛异常的开销就没那么大了。
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
//        return super.fillInStackTrace();
        return this;
    }
}
