package icu.wwj.camel.component.rocketmq;

import icu.wwj.camel.component.rocketmq.infra.EmbeddedRocketMQServer;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.namesrv.NamesrvController;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RocketMQRouteTest extends CamelTestSupport {

    private static final int NAMESRV_PORT = 59876;

    private static final String NAMESRV_ADDR = "127.0.0.1:" + NAMESRV_PORT;

    private static final String START_ENDPOINT_URI = "rocketmq:START_TOPIC?producerGroup=p1&consumerGroup=c1";

    private static final String RESULT_ENDPOINT_URI = "mock:result";

    public static final String EXPECTED_MESSAGE = "hello, RocketMQ.";

    private static NamesrvController namesrvController;

    private static BrokerController brokerController;

    private MockEndpoint resultEndpoint;

    @BeforeAll
    static void beforeAll() throws Exception {
        namesrvController = EmbeddedRocketMQServer.createAndStartNamesrv(NAMESRV_PORT);
        brokerController = EmbeddedRocketMQServer.createAndStartBroker(NAMESRV_ADDR);
        EmbeddedRocketMQServer.createTopic(NAMESRV_ADDR, "DefaultCluster", "START_TOPIC");
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
        RocketMQComponent rocketMQComponent = new RocketMQComponent();
        rocketMQComponent.setNamesrvAddr(NAMESRV_ADDR);
        camelContext.addComponent("rocketmq", rocketMQComponent);
        return camelContext;
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {

            @Override
            public void configure() {
                from(START_ENDPOINT_URI).to(RESULT_ENDPOINT_URI);
            }
        };
    }

    @Test
    public void testRouteWithTextMessage() throws Exception {
        resultEndpoint.expectedBodiesReceived(EXPECTED_MESSAGE);
        resultEndpoint.message(0).predicate(exchange -> !exchange.getIn().getHeader(RocketMQConstants.MSG_ID, String.class).isBlank());

        template.sendBody(START_ENDPOINT_URI, EXPECTED_MESSAGE);

        resultEndpoint.assertIsSatisfied();
    }

    @AfterAll
    public static void afterAll() {
        brokerController.shutdown();
        namesrvController.shutdown();
    }
}
