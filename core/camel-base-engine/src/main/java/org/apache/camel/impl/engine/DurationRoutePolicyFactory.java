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
package org.apache.camel.impl.engine;

import org.apache.camel.CamelContext;
import org.apache.camel.NamedNode;
import org.apache.camel.spi.Configurer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.RoutePolicy;
import org.apache.camel.spi.RoutePolicyFactory;
import org.apache.camel.support.PatternHelper;

/**
 * {@link org.apache.camel.spi.RoutePolicyFactory} which executes for a duration and then triggers an action.
 * <p/>
 * This can be used to stop a set of routes (or CamelContext) after they have processed a number of messages, or has
 * been running for N seconds.
 */
@Metadata(label = "bean",
          description = "RoutePolicyFactory which executes for a duration and then triggers an action."
                        + " This can be used to stop a set of routes after they have processed a number of messages, or has been running for N seconds.",
          annotations = { "interfaceName=org.apache.camel.spi.RoutePolicyFactory" })
@Configurer(metadataOnly = true)
public class DurationRoutePolicyFactory implements RoutePolicyFactory {

    @Metadata(description = "Route pattern to select a set of routes (by their route id). By default all routes are selected")
    private String fromRouteId;
    @Metadata(description = "Maximum seconds Camel is running before the action is triggered")
    private int maxSeconds;
    @Metadata(description = "Maximum number of messages to process before the action is triggered")
    private int maxMessages;
    @Metadata(description = "Action to perform", enums = "STOP_CAMEL_CONTEXT,STOP_ROUTE,SUSPEND_ROUTE,SUSPEND_ALL_ROUTES",
              defaultValue = "STOP_ROUTE")
    private DurationRoutePolicy.Action action = DurationRoutePolicy.Action.STOP_ROUTE;

    @Override
    public RoutePolicy createRoutePolicy(CamelContext camelContext, String routeId, NamedNode route) {
        DurationRoutePolicy policy = null;

        if (fromRouteId == null || PatternHelper.matchPattern(routeId, fromRouteId)) {
            policy = new DurationRoutePolicy(camelContext, routeId);
            policy.setMaxMessages(maxMessages);
            policy.setMaxSeconds(maxSeconds);
            policy.setAction(action);
        }

        return policy;
    }

    public String getFromRouteId() {
        return fromRouteId;
    }

    /**
     * Limit the route policy to the route which matches this pattern
     *
     * @see PatternHelper#matchPattern(String, String)
     */
    public void setFromRouteId(String fromRouteId) {
        this.fromRouteId = fromRouteId;
    }

    public int getMaxMessages() {
        return maxMessages;
    }

    /**
     * Maximum number of messages to process before the action is triggered
     */
    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public int getMaxSeconds() {
        return maxSeconds;
    }

    /**
     * Maximum seconds Camel is running before the action is triggered
     */
    public void setMaxSeconds(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }

    public DurationRoutePolicy.Action getAction() {
        return action;
    }

    /**
     * What action to perform when maximum is triggered.
     */
    public void setAction(DurationRoutePolicy.Action action) {
        this.action = action;
    }

}
