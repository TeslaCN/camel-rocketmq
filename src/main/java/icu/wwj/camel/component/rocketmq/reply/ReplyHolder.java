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

import org.apache.camel.AsyncCallback;
import org.apache.camel.Exchange;
import org.apache.rocketmq.common.message.MessageExt;

public class ReplyHolder {

    private final Exchange exchange;
    private final AsyncCallback callback;
    private final String messageKey;
    private final MessageExt messageExt;
    private long timeout;

    public ReplyHolder(Exchange exchange, AsyncCallback callback, String messageKey, MessageExt messageExt) {
        this.exchange = exchange;
        this.callback = callback;
        this.messageExt = messageExt;
        this.messageKey = messageKey;
    }

    public ReplyHolder(Exchange exchange, AsyncCallback callback, String messageKey, long timeout) {
        this(exchange, callback, messageKey, null);
        this.timeout = timeout;
    }

    public boolean isTimeout() {
        return messageExt == null;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public AsyncCallback getCallback() {
        return callback;
    }

    public MessageExt getMessageExt() {
        return messageExt;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public String toString() {
        return "ReplyHolder{" +
                "exchange=" + exchange +
                ", callback=" + callback +
                ", messageKey='" + messageKey + '\'' +
                ", messageExt=" + messageExt +
                ", timeout=" + timeout +
                '}';
    }
}
