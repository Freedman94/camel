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
package org.apache.camel.component.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileConsumerPreMoveTest extends ContextTestSupport {
    private static final String TEST_FILE_NAME = "hello." + UUID.randomUUID() + ".txt";

    @Test
    public void testPreMove() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);

        template.sendBodyAndHeader(fileUri(), "Hello World", Exchange.FILE_NAME, TEST_FILE_NAME);

        assertMockEndpointsSatisfied();
    }

    @Test
    public void testPreMoveSameFileTwice() throws Exception {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedBodiesReceived("Hello World");

        template.sendBodyAndHeader(fileUri(), "Hello World", Exchange.FILE_NAME, TEST_FILE_NAME);

        assertMockEndpointsSatisfied();
        oneExchangeDone.matchesWaitTime();

        // reset and drop the same file again
        mock.reset();
        mock.expectedBodiesReceived("Hello Again World");

        template.sendBodyAndHeader(fileUri(), "Hello Again World", Exchange.FILE_NAME, TEST_FILE_NAME);
        assertMockEndpointsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        context.getRegistry().bind("testDirectory", testDirectory());
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(fileUri("?preMove=work/work-${file:name}&initialDelay=0&delay=10"))
                        .process(new MyPreMoveCheckerProcessor()).to("mock:result");
            }
        };
    }

    public static class MyPreMoveCheckerProcessor implements Processor {

        @Override
        public void process(Exchange exchange) {
            Path testDirectory = (Path) exchange.getContext().getRegistry()
                    .lookupByName("testDirectory");
            Path file = testDirectory.resolve("work/work-" + TEST_FILE_NAME);
            assertTrue(Files.exists(file), "Pre move file should exist");
        }
    }
}
