package com.jramoyo.katarn;

import static com.google.common.base.Preconditions.checkArgument;
import static com.jramoyo.katarn.Util.getBodyAsJson;
import static com.jramoyo.katarn.Util.getValue;
import static java.lang.String.format;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.annotations.VisibleForTesting;
import com.jramoyo.katarn.annotation.Content;
import com.jramoyo.katarn.annotation.Property;

public class MsgHandlerMethodInvoker {

    @Inject
    private ApplicationContext appContext;

    public void invoke(MethodContext methodContext, Message message) throws MsgHandlingException {
        checkArgument(methodContext != null, "Method cannot be null.");
        checkArgument(message != null, "Message cannot be null.");

        try {
            Method method = methodContext.getMethod();

            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            Object[] args = resolveArguments(message, parameterTypes, parameterAnnotations);

            Object object = appContext.getBean(methodContext.getOwner());
            method.invoke(object, args);
        } catch (Exception ex) {
            throw new MsgHandlingException(ex);
        }
    }

    private Object[] resolveArguments(Message message, Class<?>[] parameterTypes, Annotation[][] parameterAnnotations) throws MsgHandlingException {
        int paramCount = parameterTypes.length;
        Object[] args = new Object[paramCount];

        try {
            for (int i = 0; i < paramCount; i++) {
                Class<?> type = parameterTypes[i];
                Annotation[] annotations = parameterAnnotations[i];

                // Message instance can be passed to the method
                if (annotations.length == 0 && type.isAssignableFrom(Message.class)) {
                    args[i] = message;
                }

                // JSON body instance can be passed to the method
                else if (annotations.length == 0 && type.isAssignableFrom(JsonNode.class)) {
                    args[i] = getBodyAsJson(message);
                }

                else {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Property) {
                            args[i] = resolveMsgPropertyArgument(message, type, (Property) annotation);
                        }

                        else if (annotation instanceof Content) {
                            args[i] = resolveBodyPropertyArgument(message, type, (Content) annotation);
                        }
                    }
                }
            }
        } catch (JsonProcessingException ex) {
            throw new MsgHandlingException("Unable to read message body as JSON", ex);
        } catch (Exception ex) {
            throw new MsgHandlingException(ex);
        }

        return args;
    }

    @SuppressWarnings("boxing")
    private Object resolveBodyPropertyArgument(Message message, Class<?> type, Content bodyProperty) throws JMSException, IOException, JsonProcessingException, MsgHandlingException {
        String propertyName = bodyProperty.value();
        boolean isRequired = bodyProperty.required();

        JsonNode json = getBodyAsJson(message);
        JsonNode value = getValue(propertyName, json);

        if (isRequired && value == null) {
            throw new MsgHandlingException(format("Required body property '%s' is missing", propertyName));
        }

        else if (value == null) {
            return null;
        }

        else if (type.isAssignableFrom(JsonNode.class)) {
            return value;
        }

        else if (type.isAssignableFrom(ArrayNode.class)) {
            if (value.isArray()) {
                return value;
            } else {
                throw new MsgHandlingException(format("Body property '%s' is not an array", propertyName));
            }
        }

        else if (type.isAssignableFrom(String.class)) {
            return value.asText();
        }

        else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
            if (value.asText().equalsIgnoreCase("true") || value.asText().equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(value.asText());
            } else {
                throw new MsgHandlingException(format("Body property '%s' is not assignable to Boolean", propertyName));
            }
        }

        else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
            try {
                return Double.parseDouble(value.asText());
            } catch (NumberFormatException ex) {
                throw new MsgHandlingException(format("Body property '%s' is not assignable to Double", propertyName), ex);
            }
        }

        else if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
            try {
                return Integer.parseInt(value.asText());
            } catch (NumberFormatException ex) {
                throw new MsgHandlingException(format("Body property '%s' is not assignable to Integer", propertyName), ex);
            }
        }

        else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
            try {
                return Long.parseLong(value.asText());
            } catch (NumberFormatException ex) {
                throw new MsgHandlingException(format("Body property '%s' is not assignable to Long", propertyName), ex);
            }
        }

        else {
            throw new MsgHandlingException(format("Cannot body message property '%s' to an unsupported type: %s", propertyName, type.getName()));
        }
    }

    @SuppressWarnings("boxing")
    private Object resolveMsgPropertyArgument(Message message, Class<?> type, Property msgProperty) throws JMSException, MsgHandlingException {
        String key = msgProperty.value();
        boolean isRequired = msgProperty.required();
        if (isRequired && !message.propertyExists(key)) {
            throw new MsgHandlingException(format("Required message property '%s' is missing", key));
        }

        if (type.isAssignableFrom(Object.class)) {
            return message.getObjectProperty(key);
        }

        else if (type.isAssignableFrom(String.class)) {
            return message.getStringProperty(key);
        }

        else if (type.isAssignableFrom(byte.class) || type.isAssignableFrom(Byte.class)) {
            return message.getByteProperty(key);
        }

        else if (type.isAssignableFrom(boolean.class) || type.isAssignableFrom(Boolean.class)) {
            return message.getBooleanProperty(key);
        }

        else if (type.isAssignableFrom(short.class) || type.isAssignableFrom(Short.class)) {
            return message.getShortProperty(key);
        }

        else if (type.isAssignableFrom(int.class) || type.isAssignableFrom(Integer.class)) {
            return message.getIntProperty(key);
        }

        else if (type.isAssignableFrom(long.class) || type.isAssignableFrom(Long.class)) {
            return message.getLongProperty(key);
        }

        else if (type.isAssignableFrom(float.class) || type.isAssignableFrom(Float.class)) {
            return message.getFloatProperty(key);
        }

        else if (type.isAssignableFrom(double.class) || type.isAssignableFrom(Double.class)) {
            return message.getDoubleProperty(key);
        }

        else {
            throw new MsgHandlingException(format("Cannot map message property '%s' to an unsupported type: %s", key, type.getName()));
        }
    }

    @VisibleForTesting
    void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }
}
