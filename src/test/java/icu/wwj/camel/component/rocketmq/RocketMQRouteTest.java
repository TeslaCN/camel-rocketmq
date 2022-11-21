package icu.wwj.camel.component.rocketmq;

import icu.wwj.camel.component.rocketmq.infra.EmbeddedRocketMQServer;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RocketMQRouteTest extends CamelTestSupport {

    private static final String START_ENDPOINT_URI = "rocketmq:START_TOPIC?namesrvAddr=127.0.0.1:59876&producerGroup=p1&consumerGroup=c1";
    
    private static final String INTERMEDIATE_ENDPOINT_URI = "rocketmq:INTERMEDIATE_TOPIC?namesrvAddr=127.0.0.1:59876&producerGroup=p2&consumerGroup=c2";
    
    private static final String RESULT_ENDPOINT_URI = "mock:result";

    private MockEndpoint resultEndpoint;

    @BeforeAll
    static void beforeAll() throws Exception {
        EmbeddedRocketMQServer.createAndStartNamesrv(59876);
        EmbeddedRocketMQServer.createAndStartBroker("127.0.0.1:59876");
        EmbeddedRocketMQServer.createTopic("127.0.0.1:59876", "DefaultCluster","START_TOPIC");
        EmbeddedRocketMQServer.createTopic("127.0.0.1:59876", "DefaultCluster","IMTERMEDIATE_TOPIC");
    }

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        resultEndpoint = (MockEndpoint) context.getEndpoint(RESULT_ENDPOINT_URI);
    }

    @Override
    protected CamelContext createCamelContext() throws Exception {
        CamelContext camelContext = super.createCamelContext();
        camelContext.addComponent("rocketmq", new RocketMQComponent());
        return camelContext;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                from(START_ENDPOINT_URI).to(INTERMEDIATE_ENDPOINT_URI).to(RESULT_ENDPOINT_URI);
            }
        };
    }

    @Test
    public void testRouteWithTextMessage() throws Exception {
        resultEndpoint.expectedBodiesReceived("hello, RocketMQ.");
        resultEndpoint.message(0).predicate(exchange -> !exchange.getIn().getHeader(RocketMQConstants.MSG_ID, String.class).isBlank());
    
        template.sendBody(START_ENDPOINT_URI, "hello, RocketMQ.");

        resultEndpoint.assertIsSatisfied();
    }
}
