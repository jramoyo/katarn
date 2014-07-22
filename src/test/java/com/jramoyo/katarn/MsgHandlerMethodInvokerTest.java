package com.jramoyo.katarn;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.jms.MessageFormatException;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.jramoyo.katarn.scenarios.invoker.Handler;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class MsgHandlerMethodInvokerTest {

    @Autowired
    private MsgHandlerMethodInvoker invoker;

    @Autowired
    private MsgHandlerMethodResolver resolver;

    @Mock
    private ApplicationContext appContext;

    @Mock
    private Handler handler;

    @Mock
    private TextMessage message;

    @Before
    public void setup() {
        initMocks(this);
        when(appContext.getBean(Handler.class)).thenReturn(handler);
        invoker.setAppContext(appContext);
    }

    @Test
    public void invoke_messageParam_populatesParam() throws Exception {
        MethodContext methodContext = (resolver.resolve("prefix_messageParam"));
        invoker.invoke(methodContext, message);

        verify(handler).messageParam(message);
    }

    @Test
    public void invoke_jsonParam_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_jsonParam"));
        invoker.invoke(methodContext, message);

        JsonNode expected = new ObjectMapper().readTree("{ \"property\": \"value\" }");
        verify(handler).jsonParam(expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invoke_nullMethod_throwsException() throws Exception {
        invoker.invoke(null, message);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invoke_nullMessage_throwsException() throws Exception {
        MethodContext methodContext = Mockito.mock(MethodContext.class);
        invoker.invoke(methodContext, null);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_requiredProperty_noValue_throwsException() throws Exception {
        MethodContext methodContext = (resolver.resolve("prefix_requiredProperty"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test
    public void invoke_optionalProperty_noValue_passesNullParam() throws Exception {
        MethodContext methodContext = (resolver.resolve("prefix_optionalProperty"));
        invoker.invoke(methodContext, message);

        verify(handler).optionalProperty(null);
    }

    @Test(expected = MsgHandlingException.class)
    @SuppressWarnings("boxing")
    public void invoke_propertyWrongType_throwsException() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getStringProperty("key")).thenThrow(new MessageFormatException("fake exception"));

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsString"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test(expected = MsgHandlingException.class)
    @SuppressWarnings("boxing")
    public void invoke_propertyAsUnsupportedType_throwsException() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsUnsupportedType"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsObject_populatesParam() throws Exception {
        Object obj = new Object();
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getObjectProperty("key")).thenReturn(obj);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsObject"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsObject(obj);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsString_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getStringProperty("key")).thenReturn("value");

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsString"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsString("value");
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsByte_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getByteProperty("key")).thenReturn((byte) 1);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsByte"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsByte((byte) 1);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsBoolean_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getBooleanProperty("key")).thenReturn(true);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsBoolean"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsBoolean(true);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsShort_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getShortProperty("key")).thenReturn((short) 1);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsShort"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsShort((short) 1);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsInt_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getIntProperty("key")).thenReturn(1);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsInt"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsInt(1);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsLong_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getLongProperty("key")).thenReturn(2L);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsLong"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsLong(2L);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsFloat_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getFloatProperty("key")).thenReturn(3f);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsFloat"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsFloat(3f);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyAsDouble_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getDoubleProperty("key")).thenReturn(4d);

        MethodContext methodContext = (resolver.resolve("prefix_propertyAsDouble"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyAsDouble(4d);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyMixedType_populatesParams() throws Exception {
        when(message.propertyExists("key1")).thenReturn(true);
        when(message.propertyExists("key2")).thenReturn(true);

        when(message.getStringProperty("key1")).thenReturn("value");
        when(message.getIntProperty("key2")).thenReturn(1);

        MethodContext methodContext = (resolver.resolve("prefix_propertyMixedType"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyMixedType("value", 1);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_propertyWithMessage_populatesParams() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getStringProperty("key")).thenReturn("value");

        MethodContext methodContext = (resolver.resolve("prefix_propertyWithMessage"));
        invoker.invoke(methodContext, message);

        verify(handler).propertyWithMessage("value", message);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_bodyIsNotJson_throwsException() throws Exception {
        when(message.getText()).thenReturn("<xml>incorrect</xml>");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsString"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_requiredBody_missing_throwsException() throws Exception {
        when(message.getText()).thenReturn("{ \"xxx\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_requiredBody"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test
    public void invoke_optionalBody_missing_passesNullParam() throws Exception {
        when(message.getText()).thenReturn("{ \"xxx\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_optionalBody"));
        invoker.invoke(methodContext, message);

        verify(handler).optionalBody(null);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_bodyWrongType_throwsException() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsInt"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_bodyWrongType_throwsException2() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsLong"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_bodyWrongType_throwsException3() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsDouble"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_bodyWrongType_throwsException4() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsBoolean"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_bodyAsUnsupportedType_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsUnsupportedType"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test
    public void invoke_bodyAsString_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsString"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyAsString("value");
    }

    @Test
    public void invoke_bodyAsBoolean_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"true\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsBoolean"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyAsBoolean(true);
    }

    @Test
    public void invoke_bodyAsDouble_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"1\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsDouble"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyAsDouble(1d);
    }

    @Test
    public void invoke_bodyAsInt_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"2\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsInt"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyAsInt(2);
    }

    @Test
    public void invoke_bodyAsLong_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"3\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsLong"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyAsLong(3L);
    }

    @Test
    public void invoke_bodyAsJson_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": { \"x\": \"value\" } }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsJson"));
        invoker.invoke(methodContext, message);

        JsonNode expected = new ObjectMapper().readTree("{ \"x\": \"value\" }");
        verify(handler).bodyAsJson(expected);
    }

    @Test
    public void invoke_bodyAsJsonArray_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": [\"value1\", \"value2\"] }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsJsonArray"));
        invoker.invoke(methodContext, message);

        JsonNode expected = new ObjectMapper().readTree("[\"value1\", \"value2\"]");
        verify(handler).bodyAsJsonArray((ArrayNode) expected);
    }

    @Test(expected = MsgHandlingException.class)
    public void invoke_bodyAsJsonArray_notArray_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value1\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsJsonArray"));
        invoker.invoke(methodContext, message);

        verifyNoMoreInteractions(handler);
    }

    @Test
    public void invoke_bodyAsNestedProperty_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": { \"x\": \"value\" } }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyAsNestedProperty"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyAsNestedProperty("value");
    }

    @Test
    public void invoke_bodyMixedType_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"x\": \"value\", \"y\": \"1\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyMixedType"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyMixedType("value", 1);
    }

    @Test
    public void invoke_bodyWithMessage_populatesParam() throws Exception {
        when(message.getText()).thenReturn("{ \"property\": \"value\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyWithMessage"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyWithMessage("value", message);
    }

    @Test
    @SuppressWarnings("boxing")
    public void invoke_bodyWithPropertyAndMessage_populatesParam() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getStringProperty("key")).thenReturn("propertyValue");
        when(message.getText()).thenReturn("{ \"property\": \"bodyValue\" }");

        MethodContext methodContext = (resolver.resolve("prefix_bodyWithPropertyAndMessage"));
        invoker.invoke(methodContext, message);

        verify(handler).bodyWithPropertyAndMessage("bodyValue", "propertyValue", message);
    }
}
