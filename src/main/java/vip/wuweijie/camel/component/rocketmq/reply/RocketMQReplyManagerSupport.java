/*
 *    Copyright 2020  Wu Weijie
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package vip.wuweijie.camel.component.rocketmq.reply;

import org.apache.camel.AsyncCallback;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangeTimedOutException;
import org.apache.camel.support.ExchangeHelper;
import org.apache.camel.support.service.ServiceHelper;
import org.apache.camel.support.service.ServiceSupport;
import org.apache.camel.util.ObjectHelper;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vip.wuweijie.camel.component.rocketmq.RocketMQEndpoint;
import vip.wuweijie.camel.component.rocketmq.RocketMQMessageConverter;
import vip.wuweijie.camel.component.rocketmq.RocketMQProducer;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wuweijie
 */
public class RocketMQReplyManagerSupport extends ServiceSupport implements ReplyManager {

    private static final int CLOSE_TIMEOUT = 30 * 1000;

    protected final Logger log = LoggerFactory.getLogger(RocketMQReplyManagerSupport.class);
    protected final CamelContext camelContext;
    protected final CountDownLatch replyToLatch = new CountDownLatch(1);
    protected final long replyToTimeout = 1000;
    private final RocketMQMessageConverter messageConverter = new RocketMQMessageConverter();
    protected ScheduledExecutorService executorService;
    protected RocketMQEndpoint endpoint;
    protected String replyToTopic;
    protected DefaultMQPushConsumer mqPushConsumer;
    protected ReplyTimeoutMap timeoutMap;

    public RocketMQReplyManagerSupport(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    @Override
    protected void doStart() throws Exception {
        ObjectHelper.notNull(executorService, "executorService", this);
        ObjectHelper.notNull(endpoint, "endpoint", this);

        log.debug("Using timeout checker interval with {} millis", endpoint.getRequestTimeoutCheckerInterval());
        timeoutMap = new ReplyTimeoutMap(executorService, endpoint.getRequestTimeoutCheckerInterval());
        ServiceHelper.startService(timeoutMap);

        mqPushConsumer = createConsumer();
        mqPushConsumer.start();

        log.debug("Using executor {}", executorService);
    }

    protected DefaultMQPushConsumer createConsumer() throws MQClientException {
        setReplyToTopic(endpoint.getReplyToTopic());
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
        consumer.setConsumerGroup(endpoint.getReplyToConsumerGroup());
        consumer.setNamesrvAddr(endpoint.getNamesrvAddr());
        consumer.subscribe(replyToTopic, "*");
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            MessageExt messageExt = msgs.get(0);
            onMessage(messageExt);
            log.trace("Consume message {}", messageExt);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        return consumer;
    }

    public void onMessage(MessageExt messageExt) {
        String messageKey = Arrays.stream(messageExt.getKeys().split(MessageConst.KEY_SEPARATOR))
                .filter(s -> s.startsWith(RocketMQProducer.GENERATE_MESSAGE_KEY_PREFIX)).findFirst().orElse(null);
        if (messageKey == null) {
            log.warn("Ignoreing message with no messageKey: {}", messageExt);
            return;
        }

        log.debug("Received reply message with messageKey [{}] -> {}", messageKey, messageExt);
        handleReplyMessage(messageKey, messageExt);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(timeoutMap);

        if (mqPushConsumer != null) {
            log.debug("Closing connection: {} with timeout: {} ms.", mqPushConsumer, CLOSE_TIMEOUT);
            mqPushConsumer.shutdown();
            mqPushConsumer = null;
        }

        if (executorService != null) {
            camelContext.getExecutorServiceManager().shutdownGraceful(executorService);
            executorService = null;
        }
    }

    @Override
    public void setEndpoint(RocketMQEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getReplyToTopic() {
        if (replyToTopic != null) {
            return replyToTopic;
        }
        try {
            log.trace("Waiting for replyToTopic to be set");
            boolean done = replyToLatch.await(replyToTimeout, TimeUnit.MILLISECONDS);
            if (!done) {
                log.warn("ReplyToTopic was not set and timeout occurred");
            } else {
                log.trace("Waiting for replyToTopic to be set done");
            }
        } catch (InterruptedException ignored) {
        }
        return replyToTopic;
    }

    @Override
    public void setReplyToTopic(String replyToTopic) {
        log.debug("ReplyToTopic: {}", replyToTopic);
        this.replyToTopic = replyToTopic;
        replyToLatch.countDown();
    }

    @Override
    public String registerReply(ReplyManager replyManager, Exchange exchange, AsyncCallback callback, String messageKey, long requestTimeout) {
        RocketMQReplyHandler handler = new RocketMQReplyHandler(replyManager, exchange, callback, messageKey, requestTimeout);
        ReplyHandler result = timeoutMap.putIfAbsent(messageKey, handler, requestTimeout);
        if (result != null) {
            String logMessage = String.format("The messageKey [%s] is not unique.", messageKey);
            throw new IllegalArgumentException(logMessage);
        }
        return messageKey;
    }

    @Override
    public void setScheduledExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public void updateMessageKey(String messageKey, String newMessageKey, long requestTimeout) {
        log.trace("Updated messageKey [{}] to [{}]", messageKey, newMessageKey);
        Optional.ofNullable(timeoutMap.remove(messageKey)).ifPresent(handler -> {
            timeoutMap.put(newMessageKey, handler, requestTimeout);
        });
    }

    @Override
    public void processReply(ReplyHolder holder) {
        if (holder != null && isRunAllowed()) {
            try {
                Exchange exchange = holder.getExchange();
                boolean timeout = holder.isTimeout();
                if (timeout) {
                    if (log.isWarnEnabled()) {
                        log.warn("Timeout occurred after {} millis waiting for reply message with messageKey [{}] on topic {}." +
                                        " Setting ExchangeTimedOutException on {} and continue routing.",
                                holder.getTimeout(), holder.getMessageKey(), replyToTopic, ExchangeHelper.logIds(exchange));
                    }

                    String msg = "reply message with messageKey: " + holder.getMessageKey() + " not received on topic: " + replyToTopic;
                    exchange.setException(new ExchangeTimedOutException(exchange, holder.getTimeout(), msg));
                } else {
                    messageConverter.populateRocketExchange(exchange, holder.getMessageExt(), true);

                    // restore message key
//                    if (holder.getMessageKey() != null) {
//                        exchange.getMessage().setHeader(RocketMQConstants.KEY, holder.getMessageKey());
//                    }
                }
            } finally {
                AsyncCallback callback = holder.getCallback();
                callback.done(false);
            }
        }
    }

    @Override
    public void cancelMessageKey(String messageKey) {
        Optional.ofNullable(timeoutMap.get(messageKey)).ifPresent(replyHandler -> {
            log.warn("Cancelling messageKey: {}", messageKey);
            timeoutMap.remove(messageKey);
        });

    }

    protected ReplyHandler createReplyHandler(ReplyManager replyManager, Exchange exchange, AsyncCallback callback, String messageKey, long requestTimeout) {
        return new RocketMQReplyHandler(replyManager, exchange, callback, messageKey, requestTimeout);
    }


    protected void handleReplyMessage(String messageKey, MessageExt messageExt) {
        ReplyHandler handler = timeoutMap.get(messageKey);
        if (handler != null) {
            timeoutMap.remove(messageKey);
            handler.onReply(messageKey, messageExt);
        } else {
            log.warn("Reply received for unknown messageKey [{}]. The message will be ignored: {}", messageKey, messageExt);
        }
    }


}
