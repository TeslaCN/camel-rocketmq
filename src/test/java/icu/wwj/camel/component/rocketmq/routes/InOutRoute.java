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
import org.apache.camel.spi.annotations.Component;

/**
 * @author wuweijie
 */
public class InOutRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("rocketmq:{{inout.rocketmq.topic.from}}?namesrvAddr={{rocketmq.namesrv.addr}}" +
                "&consumerGroup={{inout.rocketmq.consumer.group}}"
        )

                .inOut("rocketmq:{{inout.rocketmq.topic.to}}?namesrvAddr={{rocketmq.namesrv.addr}}" +
                        "&producerGroup={{inout.rocketmq.producer.group}}" +
                        "&replyToTopic={{inout.rocketmq.reply.to.topic}}" +
                        "&requestTimeout={{inout.request.timeout}}" +
                        "&replyToConsumerGroup={{inout.rocketmq.reply.to.consumer}}"
                        + "&requestTimeoutCheckerInterval={{inout.request.timeout.checker.interval}}"
                )

                .to("log:InOutRoute?showAll=true")
        ;
    }
}
