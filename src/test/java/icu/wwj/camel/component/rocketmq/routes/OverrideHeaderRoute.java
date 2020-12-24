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

package icu.wwj.camel.component.rocketmq.routes;

import icu.wwj.camel.component.rocketmq.RocketMQConstants;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author wuweijie
 */
@Component
public class OverrideHeaderRoute extends RouteBuilder {

    private final Logger logger = LoggerFactory.getLogger(OverrideHeaderRoute.class);

    @Override
    public void configure() throws Exception {
        from("rocketmq:{{override.rocketmq.topic.from}}?namesrvAddr={{rocketmq.namesrv.addr}}&consumerGroup={{override.rocketmq.consumer.group}}")
                .process(exchange -> {
                    exchange.getMessage().setHeader(RocketMQConstants.OVERRIDE_TOPIC_NAME, "OVERRIDE_TO");
                    exchange.getMessage().setHeader(RocketMQConstants.OVERRIDE_TAG, "OVERRIDE_TAG");
                    exchange.getMessage().setHeader(RocketMQConstants.OVERRIDE_MESSAGE_KEY, "OVERRIDE_MESSAGE_KEY");
                })
                .to("rocketmq:{{override.rocketmq.topic.to}}"
                        + "?namesrvAddr={{rocketmq.namesrv.addr}}"
                        + "&producerGroup={{override.rocketmq.producer.group}}"
                )
                .to("log:RocketRoute?showAll=true")
        ;
    }
}
