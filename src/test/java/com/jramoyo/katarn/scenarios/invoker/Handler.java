package com.jramoyo.katarn.scenarios.invoker;

import java.io.File;

import javax.jms.Message;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jramoyo.katarn.annotation.Content;
import com.jramoyo.katarn.annotation.Destination;
import com.jramoyo.katarn.annotation.Property;

@Component
public class Handler {

    // -- Message

    @Destination(value = "messageParam")
    public void messageParam(Message message) {
    }

    // -- JsonNode

    @Destination(value = "jsonParam")
    public void jsonParam(JsonNode body) {
    }

    // -- @MsgProperty

    @Destination(value = "requiredProperty")
    public void requiredProperty(@Property(value = "key", required = true) String propertyValue) {
    }

    @Destination(value = "optionalProperty")
    public void optionalProperty(@Property(value = "key", required = false) String propertyValue) {
    }

    @Destination(value = "propertyAsUnsupportedType")
    public void propertyAsUnsupportedType(@Property(value = "key") File propertyValue) {
    }

    @Destination(value = "propertyAsObject")
    public void propertyAsObject(@Property(value = "key") Object propertyValue) {
    }

    @Destination(value = "propertyAsString")
    public void propertyAsString(@Property(value = "key") String propertyValue) {
    }

    @Destination(value = "propertyAsByte")
    public void propertyAsByte(@Property(value = "key") byte propertyValue) {
    }

    @Destination(value = "propertyAsBoolean")
    public void propertyAsBoolean(@Property(value = "key") boolean propertyValue) {
    }

    @Destination(value = "propertyAsShort")
    public void propertyAsShort(@Property(value = "key") short propertyValue) {
    }

    @Destination(value = "propertyAsInt")
    public void propertyAsInt(@Property(value = "key") int propertyValue) {
    }

    @Destination(value = "propertyAsLong")
    public void propertyAsLong(@Property(value = "key") long propertyValue) {
    }

    @Destination(value = "propertyAsFloat")
    public void propertyAsFloat(@Property(value = "key") float propertyValue) {
    }

    @Destination(value = "propertyAsDouble")
    public void propertyAsDouble(@Property(value = "key") double propertyValue) {
    }

    @Destination(value = "propertyMixedType")
    public void propertyMixedType(@Property(value = "key1") String propertyValue1, @Property(value = "key2") int propertyValue2) {
    }

    @Destination(value = "propertyWithMessage")
    public void propertyWithMessage(@Property(value = "key") String propertyValue, Message message) {
    }

    // -- @BodyProperty

    @Destination(value = "requiredBody")
    public void requiredBody(@Content(value = "property", required = true) String bodyProperty) {
    }

    @Destination(value = "optionalBody")
    public void optionalBody(@Content(value = "property", required = false) String bodyProperty) {
    }

    @Destination(value = "bodyAsUnsupportedType")
    public void bodyAsUnsupportedType(@Content(value = "property") File bodyProperty) {
    }

    @Destination(value = "bodyAsString")
    public void bodyAsString(@Content(value = "property") String bodyProperty) {
    }

    @Destination(value = "bodyAsBoolean")
    public void bodyAsBoolean(@Content(value = "property") boolean bodyProperty) {
    }

    @Destination(value = "bodyAsDouble")
    public void bodyAsDouble(@Content(value = "property") double bodyProperty) {
    }

    @Destination(value = "bodyAsInt")
    public void bodyAsInt(@Content(value = "property") int bodyProperty) {
    }

    @Destination(value = "bodyAsLong")
    public void bodyAsLong(@Content(value = "property") long bodyProperty) {
    }

    @Destination(value = "bodyAsJson")
    public void bodyAsJson(@Content(value = "property") JsonNode bodyProperty) {
    }

    @Destination(value = "bodyAsJsonArray")
    public void bodyAsJsonArray(@Content(value = "property") ArrayNode bodyProperty) {
    }

    @Destination(value = "bodyAsNestedProperty")
    public void bodyAsNestedProperty(@Content(value = "property.x") String bodyProperty) {
    }

    @Destination(value = "bodyMixedType")
    public void bodyMixedType(@Content(value = "x") String x, @Content(value = "y") int y) {
    }

    @Destination(value = "bodyWithMessage")
    public void bodyWithMessage(@Content(value = "property") String bodyProperty, Message message) {
    }

    @Destination(value = "bodyWithPropertyAndMessage")
    public void bodyWithPropertyAndMessage(@Content(value = "property") String bodyProperty, @Property(value = "key") String propertyValue, Message message) {
    }
}
