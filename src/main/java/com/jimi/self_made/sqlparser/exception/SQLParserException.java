package com.jimi.self_made.sqlparser.exception;

/**
 * Created by lixinjian on 17/6/30.
 */
public class SQLParserException extends RuntimeException {
    public SQLParserException() {
    }

    public SQLParserException(String message) {
        super(message);
    }

    public SQLParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLParserException(Throwable cause) {
        super(cause);
    }

}
