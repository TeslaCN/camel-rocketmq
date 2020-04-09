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

/**
 * @author wuweijie
 */
public class RocketMQConstants {

    public static final String BROKER_NAME = "rocketmq.BROKER_NAME";
    public static final String QUEUE_ID = "rocketmq.QUEUE_ID";
    public static final String STORE_SIZE = "rocketmq.STORE_SIZE";
    public static final String QUEUE_OFFSET = "rocketmq.QUEUE_OFFSET";
    public static final String SYS_FLAG = "rocketmq.SYS_FLAG";
    public static final String BORN_TIMESTAMP = "rocketmq.BORN_TIMESTAMP";
    public static final String BORN_HOST = "rocketmq.BORN_HOST";
    public static final String STORE_TIMESTAMP = "rocketmq.STORE_TIMESTAMP";
    public static final String STORE_HOST = "rocketmq.STORE_HOST";
    public static final String MSG_ID = "rocketmq.MSG_ID";
    public static final String COMMIT_LOG_OFFSET = "rocketmq.COMMIT_LOG_OFFSET";
    public static final String BODY_CRC = "rocketmq.BODY_CRC";
    public static final String RECONSUME_TIMES = "rocketmq.RECONSUME_TIMES";
    public static final String PREPARED_TRANSACTION_OFFSET = "rocketmq.PREPARED_TRANSACTION_OFFSET";

    public static final String OVERRIDE_TOPIC_NAME = "rocketmq.OVERRIDE_TOPIC_NAME";
    public static final String OVERRIDE_TAG = "rocketmq.OVERRIDE_TAG";
    public static final String OVERRIDE_MESSAGE_KEY = "rocketmq.OVERRIDE_MESSAGE_KEY";

    public static final String TAG = "rocketmq.TAG";
    public static final String TOPIC = "rocketmq.TOPIC";
    public static final String KEY = "rocketmq.KEY";

    private RocketMQConstants() {
    }
}
