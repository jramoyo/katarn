package com.jramoyo.katarn;

import java.lang.reflect.Method;

class MethodContext {
    private final Method method;
    private final Class<?> owner;

    public MethodContext(Method method, Class<?> owner) {
        super();
        this.method = method;
        this.owner = owner;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "MethodContext [owner=" + owner.getSimpleName() + ", method=" + method.getName() + "]";
    }
}
