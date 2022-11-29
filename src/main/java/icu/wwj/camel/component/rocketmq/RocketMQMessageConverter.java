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

package icu.wwj.camel.component.rocketmq;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.support.DefaultMessage;
import org.apache.rocketmq.common.message.MessageExt;

import static icu.wwj.camel.component.rocketmq.RocketMQConstants.BODY_CRC;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.BORN_HOST;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.BORN_TIMESTAMP;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.BROKER_NAME;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.COMMIT_LOG_OFFSET;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.KEY;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.MSG_ID;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.PREPARED_TRANSACTION_OFFSET;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.QUEUE_ID;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.QUEUE_OFFSET;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.RECONSUME_TIMES;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.STORE_HOST;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.STORE_SIZE;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.STORE_TIMESTAMP;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.SYS_FLAG;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.TAG;
import static icu.wwj.camel.component.rocketmq.RocketMQConstants.TOPIC;

public class RocketMQMessageConverter {

    public void setExchangeHeadersByMessageExt(Exchange exchange, MessageExt messageExt) {
        populateMessageByMessageExt(exchange.getIn(), messageExt);
    }

    public void populateRocketExchange(Exchange exchange, MessageExt messageExt, final boolean out) {
        Message message = resolveMessageFrom(exchange, out);
        populateRoutingInfoHeaders(message, messageExt);
        message.setBody(messageExt.getBody());
    }

    private void populateRoutingInfoHeaders(final Message message, final MessageExt messageExt) {
        if (messageExt == null) {
            return;
        }
        message.setHeader(TOPIC, messageExt.getTopic());
        message.setHeader(TAG, messageExt.getTags());
        message.setHeader(KEY, messageExt.getKeys());
        populateMessageByMessageExt(message, messageExt);
    }

    private Message resolveMessageFrom(final Exchange exchange, final boolean out) {
        Message message;
        if (out) {
            message = exchange.getOut();
        } else {
            if ((message = exchange.getIn()) == null) {
                message = new DefaultMessage(exchange.getContext());
                exchange.setIn(message);
            }
        }
        return message;
    }

    private void populateMessageByMessageExt(final Message message, final MessageExt messageExt) {
        message.setHeader(BROKER_NAME, messageExt.getBrokerName());
        message.setHeader(QUEUE_ID, messageExt.getQueueId());
        message.setHeader(STORE_SIZE, messageExt.getStoreSize());
        message.setHeader(QUEUE_OFFSET, messageExt.getQueueOffset());
        message.setHeader(SYS_FLAG, messageExt.getSysFlag());
        message.setHeader(BORN_TIMESTAMP, messageExt.getBornTimestamp());
        message.setHeader(BORN_HOST, messageExt.getBornHost());
        message.setHeader(STORE_TIMESTAMP, messageExt.getStoreTimestamp());
        message.setHeader(STORE_HOST, messageExt.getStoreHost());
        message.setHeader(MSG_ID, messageExt.getMsgId());
        message.setHeader(COMMIT_LOG_OFFSET, messageExt.getCommitLogOffset());
        message.setHeader(BODY_CRC, messageExt.getBodyCRC());
        message.setHeader(RECONSUME_TIMES, messageExt.getReconsumeTimes());
        message.setHeader(PREPARED_TRANSACTION_OFFSET, messageExt.getPreparedTransactionOffset());
    }
}
