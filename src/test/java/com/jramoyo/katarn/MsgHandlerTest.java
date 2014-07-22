package com.jramoyo.katarn;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import javax.jms.TextMessage;
import javax.jms.Topic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Config.class)
public class MsgHandlerTest {

    @Autowired
    private MsgHandler msgHandler;

    @Mock
    private TextMessage message;

    @Mock
    private Topic topic;

    @Before
    public void setup() throws Exception {
        initMocks(this);
        when(message.getJMSDestination()).thenReturn(topic);
        when(topic.getTopicName()).thenReturn("prefix_get_bodyWithPropertyAndMessage");
    }

    @Test
    @SuppressWarnings("boxing")
    public void onMessage() throws Exception {
        when(message.propertyExists("key")).thenReturn(true);
        when(message.getStringProperty("key")).thenReturn("propertyValue");
        when(message.getText()).thenReturn("{ \"property\": \"bodyValue\" }");

        msgHandler.onMessage(message);
    }
}
