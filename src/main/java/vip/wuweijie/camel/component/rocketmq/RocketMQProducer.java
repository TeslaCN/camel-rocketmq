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

package vip.wuweijie.camel.component.rocketmq;

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.camel.NoTypeConversionAvailableException;
import org.apache.camel.impl.DefaultAsyncProducer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;

/**
 * @author wuweijie
 */
public class RocketMQProducer extends DefaultAsyncProducer {

    private final Logger logger = LoggerFactory.getLogger(RocketMQProducer.class);

    private DefaultMQProducer mqProducer;

    public RocketMQProducer(RocketMQEndpoint endpoint) {
        super(endpoint);
    }

    @Override
    public RocketMQEndpoint getEndpoint() {
        return (RocketMQEndpoint) super.getEndpoint();
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (!isRunAllowed()) {
            if (exchange.getException() == null) {
                exchange.setException(new RejectedExecutionException());
            }
            callback.done(true);
            return true;
        }

        try {
            if (exchange.getPattern().isOutCapable()) {
                // TODO processInOut
                return true;
            } else {
                return processInOnly(exchange, callback);
            }
        } catch (Throwable e) {
            exchange.setException(e);
            callback.done(true);
            return true;
        }
    }

    protected boolean processInOnly(Exchange exchange, AsyncCallback callback) throws NoTypeConversionAvailableException,
            InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Message message = new Message(getEndpoint().getTopicName(), getEndpoint().getTag(), getEndpoint().getKey(),
                exchange.getContext().getTypeConverter().mandatoryConvertTo(byte[].class, exchange, exchange.getIn().getBody()));
        logger.debug("RocketMQ Producer sending {}", message);
        mqProducer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                callback.done(true);
            }

            @Override
            public void onException(Throwable e) {
                exchange.setException(e);
                callback.done(true);
            }
        });
        return true;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.mqProducer = new DefaultMQProducer(getEndpoint().getProducerGroup());
        this.mqProducer.setNamesrvAddr(getEndpoint().getNamesrvAddr());
        this.mqProducer.start();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        this.mqProducer.shutdown();
        this.mqProducer = null;
    }

}
