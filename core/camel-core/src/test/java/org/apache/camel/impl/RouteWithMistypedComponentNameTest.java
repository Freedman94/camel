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

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.NoSuchEndpointException;
import org.apache.camel.TestSupport;
import org.apache.camel.builder.RouteBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Testing for mistyped component name
 */
public class RouteWithMistypedComponentNameTest extends TestSupport {

    @Test
    public void testNoSuchEndpoint() {
        CamelContext context = new DefaultCamelContext();
        assertThrows(NoSuchEndpointException.class, () -> {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from("direct:hello").to("mock:result");

                    // unknown component
                    endpoint("mistyped:hello");
                }
            });
        }, "Should have thrown a NoSuchEndpointException");
    }

    @Test
    public void testNoSuchEndpointType() {
        CamelContext context = new DefaultCamelContext();

        assertThrows(NoSuchEndpointException.class, () -> {
            context.addRoutes(new RouteBuilder() {
                @Override
                public void configure() {
                    from("direct:hello").to("mock:result");

                    // unknown component
                    endpoint("mistyped:hello", Endpoint.class);
                }
            });
        }, "Should have thrown a NoSuchEndpointException");
    }
}
