# Camel RocketMQ component

[![GitHub Actions](https://img.shields.io/github/workflow/status/TeslaCN/camel-rocketmq/Java%20CI%20with%20Gradle)](https://github.com/TeslaCN/camel-rocketmq/actions?query=workflow%3A%22Java+CI+with+Gradle%22)
[![Maven Central](https://img.shields.io/maven-central/v/icu.wwj.camel/camel-rocketmq)](https://repo.maven.apache.org/maven2/icu/wwj/camel/camel-rocketmq/)
[![GitHub camel-rocketmq](https://img.shields.io/github/repo-size/TeslaCN/camel-rocketmq)](https://github.com/TeslaCN/camel-rocketmq)

[![EN doc](https://img.shields.io/badge/document-English-blue.svg)](https://github.com/TeslaCN/camel-rocketmq/blob/master/README.md)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg)](https://github.com/TeslaCN/camel-rocketmq/blob/master/README_ZH.md)

## Getting Started

Maven:

```xml

<dependency>
    <groupId>icu.wwj.camel</groupId>
    <artifactId>camel-rocketmq</artifactId>
    <version>3.2.0</version>
</dependency>
```

Versions:

| Camel version | Component version | Release |
|---|---|---|
| 3.2.0 | 3.2.0-\* | 3.2.0 |
| 2.25.0 | 2.25.0-\* | 2.25.0-0.0.1 |

Elder version's groupId is `vip.wuweijie.camel`, which can be found in:

[https://repo.maven.apache.org/maven2/vip/wuweijie/camel/camel-rocketmq/](https://repo.maven.apache.org/maven2/vip/wuweijie/camel/camel-rocketmq/)

### Basic Usage

```java
from("rocketmq:from_topic?namesrvAddr=localhost:9876&consumerGroup=consumer")
    .to("rocketmq:to_topic?namesrvAddr=localhost:9876&producerGroup=producer");
```

### InOut Pattern

InOut Pattern based on Message Key. When the producer sending the message, a messageKey will be generated and append to
the message's key.

After the message sent, a consumer will listen to the topic configured by the parameter `ReplyToTopic`.

When a message from `ReplyToTpic` contains the key, it means that the reply received and continue routing.

If `requestTimeout` elapsed and no reply received, an exception will be thrown.

```java
from("rocketmq:{{inout.rocketmq.topic.from}}?namesrvAddr={{rocketmq.namesrv.addr}}" +
        "&consumerGroup={{inout.rocketmq.consumer.group}}" +
        "&requestTimeout=10000")

.inOut("rocketmq:{{inout.rocketmq.topic.to}}?namesrvAddr={{rocketmq.namesrv.addr}}" +
        "&producerGroup={{inout.rocketmq.producer.group}}" +
        "&replyToTopic={{inout.rocketmq.reply.to.topic}}" +
        "&requestTimeout={{inout.request.timeout}}" +
        "&replyToConsumerGroup={{inout.rocketmq.reply.to.consumer}}"
)

.to("log:InOutRoute?showAll=true")
```

Notice: **In InOut pattern, the message won't be routed until reply received.**

## Component Parameters

### InOnly Pattern

| Name | Type | Description | Default |
|---|---|---|---|
| topicName | common | (Required) consumer/producer's topic |  | 
| namesrvAddr | common | NameServer (Separate by comma) | localhost:9876 |
| accessKey | common | Rocketmq acl accessKey |  |
| secretKey | common | Rocketmq acl secretKey |  |
| consumerGroup | consumer | Consumer group name |  |
| subscribeTags | consumer | Subscribe tags expression | * |
| producerGroup | producer | Producer group name |  | 
| sendTag | producer | Send message's tag |  |
| waitForSendResult | producer | Block until message sent | false |

### InOut Pattern

| Name | Type | Description | Default |
|---|---|---|---|
| replyToTopic | producer | The topic to listen for reply ||
| replyToConsumerGroup | producer | Consumer group ||
| requestTimeout | producer | Wait for milliseconds before timeout | 10000 |
| requestTimeoutCheckerInterval | advance | Timeout checker interval (milliseconds) | 1000 |

## Exchange Header

| Constant | Value | Description |
|---|---|---| 
| `RocketMQConstants.OVERRIDE_TOPIC_NAME` | `rocketmq.OVERRIDE_TOPIC_NAME` | Override the message's Topic |
| `RocketMQConstants.OVERRIDE_TAG` | `rocketmq.OVERRIDE_TAG` | Override the message's Tag |
| `RocketMQConstants.OVERRIDE_MESSAGE_KEY` | `rocketmq.OVERRIDE_MESSAGE_KEY` | Set the message's Key |

```java
from("rocketmq:{{override.rocketmq.topic.from}}?namesrvAddr={{rocketmq.namesrv.addr}}&consumerGroup={{override.rocketmq.consumer.group}}")
        .process(exchange -> {
            exchange.getMessage().setHeader(RocketMQConstants.OVERRIDE_TOPIC_NAME, "OVERRIDE_TO");
            exchange.getMessage().setHeader(RocketMQConstants.OVERRIDE_TAG, "OVERRIDE_TAG");
            exchange.getMessage().setHeader(RocketMQConstants.OVERRIDE_MESSAGE_KEY, "OVERRIDE_MESSAGE_KEY");
        }
)
.to("rocketmq:{{override.rocketmq.topic.to}}"
                + "?namesrvAddr={{rocketmq.namesrv.addr}}"
                + "&producerGroup={{override.rocketmq.producer.group}}"
)
.to("log:RocketRoute?showAll=true")
```
