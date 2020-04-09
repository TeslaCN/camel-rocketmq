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

package vip.wuweijie.camel.component.rocketmq.reply;

import org.apache.camel.support.DefaultTimeoutMap;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author wuweijie
 */
public class ReplyTimeoutMap extends DefaultTimeoutMap<String, ReplyHandler> {

    public ReplyTimeoutMap(ScheduledExecutorService executor, long requestMapPollTimeMillis) {
        super(executor, requestMapPollTimeMillis);
    }

    @Override
    public boolean onEviction(String key, ReplyHandler value) {
        try {
            value.onTimeout(key);
        } catch (Throwable e) {
            log.warn("Error processing onTimeout for messageKey: " + key + " due: " + e.getLocalizedMessage() + ". This exception is ignored.", e);
        }

        log.trace("Ecivted messageKey: {}", key);
        return true;
    }

    @Override
    public ReplyHandler get(String key) {
        ReplyHandler handler = super.get(key);
        log.trace("Get messageKey: {} -> {}", key, handler != null);
        return handler;
    }

    @Override
    public ReplyHandler put(String key, ReplyHandler value, long timeoutMillis) {
        ReplyHandler handler;
        if (timeoutMillis <= 0) {
            handler = super.put(key, value, Integer.MAX_VALUE);
        } else {
            handler = super.put(key, value, timeoutMillis);
        }
        log.debug("Added messageKey: {} to timeout after : {} millis", key, timeoutMillis);
        return handler;
    }

    @Override
    public ReplyHandler putIfAbsent(String key, ReplyHandler value, long timeoutMillis) {
        log.trace("putIfAbsent with key {}", key);

        ReplyHandler handler;
        if (timeoutMillis <= 0) {
            handler = super.putIfAbsent(key, value, Integer.MAX_VALUE);
        } else {
            handler = super.putIfAbsent(key, value, timeoutMillis);
        }
        if (handler == null) {
            log.debug("Added messageKey: {} to timeout after : {} millis", key, timeoutMillis);
        } else {
            log.trace("Duplicate messageKey: {} detected", key);
        }
        return handler;
    }

    @Override
    public ReplyHandler remove(String key) {
        ReplyHandler handler = super.remove(key);
        log.trace("Removed messageKey: {} -> {}", key, handler != null);
        return handler;
    }
}
