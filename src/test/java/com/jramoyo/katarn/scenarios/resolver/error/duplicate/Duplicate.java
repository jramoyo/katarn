package com.jramoyo.katarn.scenarios.resolver.error.duplicate;

import org.springframework.stereotype.Component;

import com.jramoyo.katarn.annotation.Destination;

@Component
public class Duplicate {

    @Destination(value = "duplicate")
    public void duplicate1() {
    }

    @Destination(value = "duplicate")
    public void duplicate2() {
    }
}
