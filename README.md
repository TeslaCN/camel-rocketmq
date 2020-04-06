# Camel RocketMQ component

Usage:
```
from("rocketmq:from_topic?namesrvAddr=localhost:9876&consumerGroup=consumer")
    .to("rocketmq:to_topic?namesrvAddr=localhost:9876&producerGroup=producer");
```