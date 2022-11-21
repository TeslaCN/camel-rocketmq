# Camel RocketMQ component

[![GitHub Actions](https://img.shields.io/github/workflow/status/TeslaCN/camel-rocketmq/Java%20CI%20with%20Gradle)](https://github.com/TeslaCN/camel-rocketmq/actions?query=workflow%3A%22Java+CI+with+Gradle%22)
[![Maven Central](https://img.shields.io/maven-central/v/icu.wwj.camel/camel-rocketmq)](https://repo.maven.apache.org/maven2/icu/wwj/camel/camel-rocketmq/)
[![GitHub camel-rocketmq](https://img.shields.io/github/repo-size/TeslaCN/camel-rocketmq)](https://github.com/TeslaCN/camel-rocketmq)

[![EN doc](https://img.shields.io/badge/document-English-blue.svg)](https://github.com/TeslaCN/camel-rocketmq/blob/master/README.md)
[![CN doc](https://img.shields.io/badge/文档-中文版-blue.svg)](https://github.com/TeslaCN/camel-rocketmq/blob/master/README_ZH.md)

## 快速入门

Maven:

```xml

<dependency>
    <groupId>icu.wwj.camel</groupId>
    <artifactId>camel-rocketmq</artifactId>
    <version>3.2.0</version>
</dependency>
```

版本对应:

| Camel 版本 | 组件版本 | Release |
|---|---|---|
| 3.2.0 | 3.2.0-\* | 3.2.0 |
| 2.25.0 | 2.25.0-\* | 2.25.0-0.0.1 |

历史版本 groupId 为 `vip.wuweijie.camel`。可以在中央仓库查看：

[https://repo.maven.apache.org/maven2/vip/wuweijie/camel/camel-rocketmq/](https://repo.maven.apache.org/maven2/vip/wuweijie/camel/camel-rocketmq/)

### 基本使用

```java
from("rocketmq:from_topic?namesrvAddr=localhost:9876&consumerGroup=consumer")
    .to("rocketmq:to_topic?namesrvAddr=localhost:9876&producerGroup=producer");
```

### InOut 模式

InOut 模式的实现借助了 Message Key，Producer 在发送消息的时候，会生成一个 messageKey 追加到消息的 key 部分。

Producer 消息发送后，启动一个 Consumer 监听 `ReplyToTopic` 参数配置的 Topic。

当 `ReplyToTpic` 中的消息包含发送消息时生成的 Key，则对应消息的 Reply 已收到，继续执行后续路由。

如果超过 `requestTimeout` 毫秒后仍然没有收到 Reply，则抛出异常。

```java
from("rocketmq:{{inout.rocketmq.topic.from}}?namesrvAddr={{rocketmq.namesrv.addr}}"+
        "&consumerGroup={{inout.rocketmq.consumer.group}}"+
        "&requestTimeout=10000")

.inOut("rocketmq:{{inout.rocketmq.topic.to}}?namesrvAddr={{rocketmq.namesrv.addr}}" +
        "&producerGroup={{inout.rocketmq.producer.group}}" +
        "&replyToTopic={{inout.rocketmq.reply.to.topic}}" +
        "&requestTimeout={{inout.request.timeout}}" +
        "&replyToConsumerGroup={{inout.rocketmq.reply.to.consumer}}"
)

.to("log:InOutRoute?showAll=true")
```

注意：**在 InOut 模式下，只有收到 Reply 才会继续路由**

## 组件参数

### InOnly 模式

| 参数 | 类型 | 含义 | 默认值 |
|---|---|---|---|
| topicName | common | （必须）消费或生产消息的 topic 名称 |  | 
| namesrvAddr | common | NameServer 地址，英文逗号分隔 | localhost:9876 |
| accessKey | common | Rocketmq acl accessKey |  |
| secretKey | common | Rocketmq acl secretKey |  |
| consumerGroup | consumer | 消费者组名称 |  |
| subscribeTags | consumer | 订阅消息 Tag 表达式 | * |
| producerGroup | producer | 生产者组名称 |  | 
| sendTag | producer | 发送消息 Tag |  |
| waitForSendResult | producer | 是否阻塞发送消息 | false |

### InOut 模式

| 参数 | 类型 | 含义 | 默认值 |
|---|---|---|---|
| replyToTopic | producer | 监听回复的 Topic ||
| replyToConsumerGroup | producer | 监听回复的消费者组 ||
| requestTimeout | producer | 等待回复时间 | 10000 |
| requestTimeoutCheckerInterval | advance | 回复超时检查间隔 | 1000 |

## Exchange Header

| 常量名 | 常量值 | 含义 |
|---|---|---| 
| `RocketMQConstants.OVERRIDE_TOPIC_NAME` | `rocketmq.OVERRIDE_TOPIC_NAME` | 覆盖路由配置的消息 Topic |
| `RocketMQConstants.OVERRIDE_TAG` | `rocketmq.OVERRIDE_TAG` | 覆盖路由配置的消息 Tag |
| `RocketMQConstants.OVERRIDE_MESSAGE_KEY` | `rocketmq.OVERRIDE_MESSAGE_KEY` | 设置消息 Key |

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
