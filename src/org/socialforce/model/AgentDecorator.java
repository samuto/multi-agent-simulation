package org.socialforce.model;

import org.socialforce.geom.Point;

/**
 * Created by Whatever on 2016/11/15.
 */
public interface AgentDecorator {
    Agent createAgent(Point position);
}