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

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuweijie
 */
@UriEndpoint(firstVersion = "2.25.0", scheme = "rocketmq", syntax = "rocketmq:topicName", title = "RocketMQ", label = "messaging")
public class RocketMQEndpoint extends DefaultEndpoint implements AsyncEndpoint {

    private final Logger logger = LoggerFactory.getLogger(RocketMQEndpoint.class);

    @UriPath
    @Metadata(required = "true")
    private String topicName;
    @UriParam(label = "producer")
    private String producerGroup;
    @UriParam(label = "consumer")
    private String consumerGroup;
    private final RocketMQMessageConverter messageConverter = new RocketMQMessageConverter();
    @UriParam(label = "consumer", defaultValue = "*")
    private String subscribeTags = "*";
    @UriParam(label = "producer", defaultValue = "")
    private String sendTag = "";
    @UriParam(label = "producer")
    private String replyToTopic;
    @UriParam(label = "producer")
    private String replyToConsumerGroup;
    @UriParam(label = "common", defaultValue = "localhost:9876")
    private String namesrvAddr = "localhost:9876";
    @UriParam(label = "advance", defaultValue = "10000")
    private Long requestTimeout = 10000L;
    @UriParam(label = "advance", defaultValue = "1000")
    private Long requestTimeoutCheckerInterval = 1000L;
    @UriParam(label = "producer", defaultValue = "false")
    private Boolean waitForSendResult = false;

    public RocketMQEndpoint() {
    }

    public RocketMQEndpoint(String endpointUri, RocketMQComponent component) {
        super(endpointUri, component);
    }

    @Override
    public Producer createProducer() throws Exception {
        return new RocketMQProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        RocketMQConsumer consumer = new RocketMQConsumer(this, processor);
        configureConsumer(consumer);
        return consumer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Exchange createRocketExchange(byte[] body) {
        Exchange exchange = super.createExchange();
        DefaultMessage message = new DefaultMessage(exchange.getContext());
        message.setBody(body);
        exchange.setIn(message);
        return exchange;
    }


    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getSubscribeTags() {
        return subscribeTags;
    }

    public void setSubscribeTags(String subscribeTags) {
        this.subscribeTags = subscribeTags;
    }

    public String getSendTag() {
        return sendTag;
    }

    public void setSendTag(String sendTag) {
        this.sendTag = sendTag;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getProducerGroup() {
        return producerGroup;
    }

    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }

    public String getConsumerGroup() {
        return consumerGroup;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public String getReplyToTopic() {
        return replyToTopic;
    }

    public void setReplyToTopic(String replyToTopic) {
        this.replyToTopic = replyToTopic;
    }

    public String getReplyToConsumerGroup() {
        return replyToConsumerGroup;
    }

    public void setReplyToConsumerGroup(String replyToConsumerGroup) {
        this.replyToConsumerGroup = replyToConsumerGroup;
    }

    public RocketMQMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public Long getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Long requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Long getRequestTimeoutCheckerInterval() {
        return requestTimeoutCheckerInterval;
    }

    public void setRequestTimeoutCheckerInterval(Long requestTimeoutCheckerInterval) {
        this.requestTimeoutCheckerInterval = requestTimeoutCheckerInterval;
    }

    public Boolean getWaitForSendResult() {
        return waitForSendResult;
    }

    public void setWaitForSendResult(Boolean waitForSendResult) {
        this.waitForSendResult = waitForSendResult;
    }
}
