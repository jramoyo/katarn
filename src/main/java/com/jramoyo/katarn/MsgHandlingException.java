package com.jramoyo.katarn;

public class MsgHandlingException extends Exception {
    private static final long serialVersionUID = -4342407364679678029L;

    public MsgHandlingException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public MsgHandlingException(String msg) {
        super(msg);
    }

    public MsgHandlingException(Throwable throwable) {
        super(throwable);
    }
}
