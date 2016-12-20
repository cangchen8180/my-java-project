package com.jimi.exception;

/**
 * 业务执行过程中出现的异常
 * @author jimi
 * @version 2016-02-29 11:56.
 */
public class LgPlatBusinessException extends Exception {

    private String message;

    public LgPlatBusinessException() {
    }

    public LgPlatBusinessException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
