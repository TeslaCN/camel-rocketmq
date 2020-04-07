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

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author wuweijie
 */
public class RocketMQTest {

    public static final String PRODUCER_GROUP = "ROCKET_TEST_PRODUCER";
    public static final String CONSUMER_GROUP = "ROCKET_TEST_CONSUMER";
    public static final String TOPIC = "ROCKET_TEST_TOPIC";
    public static final String TAGS = "TEST_TAGS";
    public static final String KEYS = "TEST_KEYS";
    public static final String TAG_EXPR = "*";
    public static final String NAMESRV_ADDR = "rpi3.lo:9876";

    @Test
    public void produce() throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQProducer producer = new DefaultMQProducer();
        producer.setNamesrvAddr(NAMESRV_ADDR);
        producer.setProducerGroup(PRODUCER_GROUP);
        producer.setSendMsgTimeout(3000);
        producer.start();
        Message message = new Message(TOPIC, TAGS, KEYS, String.format("%s", new Date()).getBytes(StandardCharsets.UTF_8));
        SendResult sendResult = producer.send(message);
        System.out.println(sendResult);
    }

}
