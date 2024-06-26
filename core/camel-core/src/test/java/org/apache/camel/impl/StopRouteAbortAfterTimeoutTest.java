/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.impl;

import java.util.concurrent.TimeUnit;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

@DisabledOnOs(OS.WINDOWS)
public class StopRouteAbortAfterTimeoutTest extends ContextTestSupport {

    @Test
    public void testStopRouteWithAbortAfterTimeoutTrue() throws Exception {
        MockEndpoint mockEP = getMockEndpoint("mock:result");
        mockEP.setExpectedMessageCount(10);

        // send some message through the route
        for (int i = 0; i < 5; i++) {
            template.sendBody("seda:start", "message-" + i);
        }

        // stop route with a 1s timeout and abortAfterTimeout=true (should abort
        // after 1s)
        boolean stopRouteResponse = context.getRouteController().stopRoute("start", 1, TimeUnit.SECONDS, true);

        // confirm that route is still running
        assertFalse(stopRouteResponse, "stopRoute response should be False");
        assertTrue(context.getRouteController().getRouteStatus("start").isStarted(), "route should still be started");

        // send some more messages through the route
        for (int i = 5; i < 10; i++) {
            template.sendBody("seda:start", "message-" + i);
        }

        mockEP.assertIsSatisfied();
    }

    @Test
    public void testStopRouteWithAbortAfterTimeoutFalse() throws Exception {
        MockEndpoint mockEP = getMockEndpoint("mock:result");

        // send some message through the route
        for (int i = 0; i < 5; i++) {
            template.sendBody("seda:start", "message-" + i);
        }

        // stop route with a 1s timeout and abortAfterTimeout=false (normal
        // timeout behavior)
        boolean stopRouteResponse = context.getRouteController().stopRoute("start", 1, TimeUnit.SECONDS, false);

        // the route should have been forced stopped
        assertTrue(stopRouteResponse, "stopRoute response should be True");
        assertTrue(context.getRouteController().getRouteStatus("start").isStopped(), "route should be stopped");

        int before = mockEP.getExchanges().size();

        // send some more messages through the route
        for (int i = 5; i < 10; i++) {
            template.sendBody("seda:start", "message-" + i);
        }

        int after = mockEP.getExchanges().size();
        assertEquals(before, after, "Should not route messages");
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                // shutdown this test faster
                context.getShutdownStrategy().setTimeout(3);

                from("seda:start").routeId("start").delay(100).to("mock:result");
            }
        };
    }
}
