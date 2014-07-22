package com.jramoyo.katarn.scenarios.resolver.success.package2;

import org.springframework.stereotype.Component;

import com.jramoyo.katarn.annotation.Destination;

@Component
public class Success2 {

    @Destination(value = "mapping2")
    public void delete() {
    }
}
