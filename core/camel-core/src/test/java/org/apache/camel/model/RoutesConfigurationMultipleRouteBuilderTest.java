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
package org.apache.camel.model;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.RouteConfigurationBuilder;
import org.junit.jupiter.api.Test;

public class RoutesConfigurationMultipleRouteBuilderTest extends ContextTestSupport {

    @Override
    public boolean isUseRouteBuilder() {
        return false;
    }

    @Test
    public void testRoutesConfiguration() throws Exception {
        context.addRoutesConfigurations(new RouteConfigurationBuilder() {
            @Override
            public void configuration() {
                // global configuration for all routes
                routeConfiguration().onException(Exception.class).handled(true).to("mock:error");
            }
        });
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start")
                        .throwException(new IllegalArgumentException("Foo"));
            }
        });
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:start2")
                        .throwException(new IllegalArgumentException("Foo2"));
            }
        });

        context.start();

        getMockEndpoint("mock:error").expectedBodiesReceived("Hello World", "Bye World");

        template.sendBody("direct:start", "Hello World");
        template.sendBody("direct:start2", "Bye World");

        assertMockEndpointsSatisfied();
    }

}
