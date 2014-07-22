package com.jramoyo.katarn.scenarios.resolver.success.package1;

import org.springframework.stereotype.Component;

import com.jramoyo.katarn.annotation.Destination;

@Component
public class Success1 {

    @Destination(value = "mapping1")
    public void get() {
    }
}
