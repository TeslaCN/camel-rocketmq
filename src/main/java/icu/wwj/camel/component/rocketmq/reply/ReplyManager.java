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

package icu.wwj.camel.component.rocketmq.reply;

import icu.wwj.camel.component.rocketmq.RocketMQEndpoint;
import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;

import java.util.concurrent.ScheduledExecutorService;

public interface ReplyManager {

    void setEndpoint(RocketMQEndpoint endpoint);

    void setReplyToTopic(String replyToTopic);

    String registerReply(ReplyManager replyManager, Exchange exchange, AsyncCallback callback, String messageKey, long requestTimeout);

    void setScheduledExecutorService(ScheduledExecutorService executorService);

    void processReply(ReplyHolder holder);

    void cancelMessageKey(String messageKey);
}
