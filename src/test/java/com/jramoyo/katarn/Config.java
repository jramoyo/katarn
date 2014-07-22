package com.jramoyo.katarn;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.jramoyo.katarn.scenarios")
public class Config {

    @Bean
    public MsgHandlerMethodResolver msgMethodResolver() throws MsgHandlingException {
        MsgHandlerMethodResolver resolver = new MsgHandlerMethodResolver("prefix_");
        resolver.init("com.jramoyo.katarn.scenarios.invoker");

        return resolver;
    }

    @Bean
    public MsgHandlerMethodInvoker msgHandlerMethodInvoker() {
        return new MsgHandlerMethodInvoker();
    }

    @Bean
    public MsgHandler msgHandler() {
        return new MsgHandler();
    }
}
