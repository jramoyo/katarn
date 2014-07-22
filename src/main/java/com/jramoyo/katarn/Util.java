package com.jramoyo.katarn;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class Util {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getBodyAsText(Message message) throws JMSException {
        if (message instanceof TextMessage) {
            return ((TextMessage) message).getText();
        }

        throw new IllegalArgumentException(String.format("Unsupported message type: '%s'", message.getClass().getName()));
    }

    public static JsonNode getBodyAsJson(Message message) throws JMSException, IOException, JsonProcessingException {
        return objectMapper.readTree(getBodyAsText(message));
    }

    public static JsonNode getValue(String propertyName, JsonNode root) {
        JsonNode value;
        if (propertyName.contains(".")) {
            String[] tokens = propertyName.split("\\.");
            value = root;
            for (String token : tokens) {
                value = value.get(token);
            }
        } else {
            value = root.get(propertyName);
        }

        return value;
    }
}
