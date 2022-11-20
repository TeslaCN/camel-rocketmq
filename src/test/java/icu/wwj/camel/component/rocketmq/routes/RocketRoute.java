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

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuweijie
 */
public class RocketRoute extends RouteBuilder {

    private final Logger logger = LoggerFactory.getLogger(RocketRoute.class);

    @Override
    public void configure() throws Exception {
        from("rocketmq:{{rocketmq.topic.from}}?namesrvAddr={{rocketmq.namesrv.addr}}&consumerGroup={{rocketmq.consumer.group}}")
                .process(exchange -> {
                    logger.info("exchange {}", exchange);
                    logger.info("exchange.in {}", exchange.getIn());
                    logger.info("exchange.in.body {}", exchange.getIn().getBody());
                })
                .to("rocketmq:{{rocketmq.topic.to}}"
                                + "?namesrvAddr={{rocketmq.namesrv.addr}}"
                                + "&producerGroup={{rocketmq.producer.group}}"
//                        + "&waitForSendResult=true"
                )
                .to("log:RocketRoute?showAll=true")
        ;
    }
}
