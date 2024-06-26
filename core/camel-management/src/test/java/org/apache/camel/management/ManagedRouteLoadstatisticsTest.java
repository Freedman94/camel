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
package org.apache.camel.management;

import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.apache.camel.management.DefaultManagementObjectNameStrategy.TYPE_ROUTE;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisabledOnOs(OS.AIX)
public class ManagedRouteLoadstatisticsTest extends ManagementTestSupport {

    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }

    @Test
    public void testLoadStatisticsAreDisabledByDefault() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                // must be a bit more than 1 sec
                from("direct:start").to("log:foo").delay(1200).to("mock:result");
            }
        });
        context.start();

        boolean load = context.getManagementStrategy().getManagementAgent().getLoadStatisticsEnabled() != null
                && context.getManagementStrategy().getManagementAgent().getLoadStatisticsEnabled();
        assertFalse(load);
        // get the stats for the route
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = getCamelObjectName(TYPE_ROUTE, context.getRoutes().get(0).getRouteId());
        getMockEndpoint("mock:result").expectedMessageCount(1);

        template.asyncSendBody("direct:start", "Hello World");

        assertMockEndpointsSatisfied();

        String load01 = (String) mbeanServer.getAttribute(on, "Load01");
        String load05 = (String) mbeanServer.getAttribute(on, "Load05");
        String load15 = (String) mbeanServer.getAttribute(on, "Load15");
        assertEquals("", load01);
        assertEquals("", load05);
        assertEquals("", load15);
    }

    @Test
    public void testEnableLoadStatistics() throws Exception {
        context.getManagementStrategy().getManagementAgent().setLoadStatisticsEnabled(true);

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                // must be a bit more than 1 sec
                from("direct:start").to("log:foo").delay(1200).to("mock:result");
            }
        });
        context.start();

        // get the stats for the route
        MBeanServer mbeanServer = getMBeanServer();
        ObjectName on = getCamelObjectName(TYPE_ROUTE, context.getRoutes().get(0).getRouteId());

        getMockEndpoint("mock:result").expectedMessageCount(1);

        template.asyncSendBody("direct:start", "Hello World");

        assertMockEndpointsSatisfied();

        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            String load01 = (String) mbeanServer.getAttribute(on, "Load01");
            String load05 = (String) mbeanServer.getAttribute(on, "Load05");
            String load15 = (String) mbeanServer.getAttribute(on, "Load15");
            assertNotNull(load01);
            assertNotNull(load05);
            assertNotNull(load15);
            assertTrue(Double.parseDouble(load01.replace(',', '.')) >= 0);
            assertTrue(Double.parseDouble(load05.replace(',', '.')) >= 0);
            assertTrue(Double.parseDouble(load15.replace(',', '.')) >= 0);
        });
    }

}
