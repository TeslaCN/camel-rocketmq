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
import org.springframework.stereotype.Component;

/**
 * @author wuweijie
 */
@Component
public class MultiRocketRoute extends RouteBuilder {

    private final Logger logger = LoggerFactory.getLogger(MultiRocketRoute.class);

    @Override
    public void configure() throws Exception {
        from("rocketmq:{{multi.rocketmq.topic.from}}?namesrvAddr={{rocketmq.namesrv.addr}}&consumerGroup={{multi.rocketmq.consumer.group}}")
                .process(exchange -> {
                    logger.info("exchange1 {}", exchange);
                    logger.info("exchange1.in {}", exchange.getIn());
                    logger.info("exchange1.in.body {}", exchange.getIn().getBody());
                })
                .to("rocketmq:{{multi.rocketmq.topic.to1}}?namesrvAddr={{rocketmq.namesrv.addr}}&producerGroup={{multi.rocketmq.producer.group1}}")
                .process(exchange -> {
                    logger.info("exchange2 {}", exchange);
                    logger.info("exchange2.in {}", exchange.getIn());
                    logger.info("exchange2.in.body {}", exchange.getIn().getBody());
                })
                .to("rocketmq:{{multi.rocketmq.topic.to2}}?namesrvAddr={{rocketmq.namesrv.addr}}&producerGroup={{multi.rocketmq.producer.group2}}")
        ;
    }
}
