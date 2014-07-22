package com.jramoyo.katarn;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Topic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MsgHandler implements MessageListener {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Inject
    private MsgHandlerMethodResolver resolver;

    @Inject
    private MsgHandlerMethodInvoker invoker;

    @Override
    public void onMessage(Message message) {
        try {
            MethodContext methodContext = resolver.resolve(getDestinationName(message));
            invoker.invoke(methodContext, message);
        } catch (MsgHandlingException ex) {
            logger.error("An error occurred while handling message.", ex);
            try {
                logger.warn("Unable to handle message:\n{}", Util.getBodyAsText(message));
            } catch (JMSException jmsex) {
            }
        }
    }

    private String getDestinationName(Message message) throws MsgHandlingException {
        try {
            if (message.getJMSDestination() instanceof Topic) {
                return ((Topic) message.getJMSDestination()).getTopicName();
            } else {
                return ((Queue) message.getJMSDestination()).getQueueName();
            }
        } catch (JMSException ex) {
            throw new MsgHandlingException("Unable to get destination name.", ex);
        }
    }
}
