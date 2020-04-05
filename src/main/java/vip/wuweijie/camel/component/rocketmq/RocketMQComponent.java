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

import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.spi.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author wuweijie
 */
public class RocketMQComponent extends DefaultComponent {

    private final Logger logger = LoggerFactory.getLogger(RocketMQComponent.class);

    @Metadata(required = "true")
    private String topicName;

    @Metadata(label = "producer")
    private String producerGroup;

    @Metadata(label = "consumer")
    private String consumerGroup;

    @Metadata(label = "common")
    private String tag;

    @Metadata(label = "common")
    private String key;

    @Metadata(label = "common")
    private String namesrvAddr;

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

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
}
