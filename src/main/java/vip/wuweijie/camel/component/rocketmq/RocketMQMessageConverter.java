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

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.support.DefaultMessage;
import org.apache.rocketmq.common.message.MessageExt;

import static vip.wuweijie.camel.component.rocketmq.RocketMQConstants.*;

/**
 * @author wuweijie
 */
public class RocketMQMessageConverter {

    public void setExchangeHeadersByMessageExt(Exchange exchange, MessageExt messageExt) {
        exchange.getIn().setHeader(BROKER_NAME, messageExt.getBrokerName());
        exchange.getIn().setHeader(QUEUE_ID, messageExt.getQueueId());
        exchange.getIn().setHeader(STORE_SIZE, messageExt.getStoreSize());
        exchange.getIn().setHeader(QUEUE_OFFSET, messageExt.getQueueOffset());
        exchange.getIn().setHeader(SYS_FLAG, messageExt.getSysFlag());
        exchange.getIn().setHeader(BORN_TIMESTAMP, messageExt.getBornTimestamp());
        exchange.getIn().setHeader(BORN_HOST, messageExt.getBornHost());
        exchange.getIn().setHeader(STORE_TIMESTAMP, messageExt.getStoreTimestamp());
        exchange.getIn().setHeader(STORE_HOST, messageExt.getStoreHost());
        exchange.getIn().setHeader(MSG_ID, messageExt.getMsgId());
        exchange.getIn().setHeader(COMMIT_LOG_OFFSET, messageExt.getCommitLogOffset());
        exchange.getIn().setHeader(BODY_CRC, messageExt.getBodyCRC());
        exchange.getIn().setHeader(RECONSUME_TIMES, messageExt.getReconsumeTimes());
        exchange.getIn().setHeader(PREPARED_TRANSACTION_OFFSET, messageExt.getPreparedTransactionOffset());
    }

    public void populateRocketExchange(Exchange exchange, MessageExt messageExt, final boolean out) {
        Message message = resolveMessageFrom(exchange, out);
        populateRoutingInfoHeaders(message, messageExt);
    }

    private void populateRoutingInfoHeaders(final Message message, final MessageExt messageExt) {
        if (messageExt != null) {
            message.setHeader(TOPIC, messageExt.getTopic());
            message.setHeader(TAG, messageExt.getTags());
            message.setHeader(KEY, messageExt.getKeys());
        }
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
}
