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

import org.apache.camel.spi.Metadata;
import org.apache.camel.support.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author wuweijie
 */
public class RocketMQComponent extends DefaultComponent {

    private final Logger logger = LoggerFactory.getLogger(RocketMQComponent.class);

    @Metadata(required = true)
    private String topicName;

    @Metadata(label = "producer")
    private String producerGroup;

    @Metadata(label = "consumer")
    private String consumerGroup;

    @Metadata(label = "consumer", defaultValue = "*")
    private String subscribeTags = "*";

    @Metadata(label = "common", defaultValue = "")
    private String sendTag = "";

    @Metadata(label = "common", defaultValue = "localhost:9876")
    private String namesrvAddr = "localhost:9876";

    @Metadata(label = "producer")
    private String replyToTopic;

    @Metadata(label = "producer")
    private String replyToConsumerGroup;

    @Metadata(label = "advance", defaultValue = "10000")
    private Long requestTimeout = 10000L;

    @Metadata(label = "advance", defaultValue = "1000")
    private Long requestTimeoutCheckerInterval = 1000L;

    @Metadata(label = "producer", defaultValue = "false")
    private Boolean waitForSendResult = false;

    @Override
    protected RocketMQEndpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {

        RocketMQEndpoint endpoint = new RocketMQEndpoint(uri, this);
        endpoint.setTopicName(remaining);
        setProperties(endpoint, parameters);
        if (endpoint.getNamesrvAddr() == null) {
            endpoint.setNamesrvAddr("localhost:9876");
        }
        return endpoint;
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
