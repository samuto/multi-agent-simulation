package org.socialforce.strategy.impl;

import org.socialforce.geom.Point;
import org.socialforce.model.Agent;
import org.socialforce.scene.Scene;
import org.socialforce.strategy.Path;
import org.socialforce.strategy.PathFinder;
import org.socialforce.strategy.StaticStrategy;

import java.util.Iterator;

/**
 * Created by sunjh1999 on 2017/2/26.
 */
public class FurthestGoalStrategy implements StaticStrategy {
    Point[] goals;
    Scene scene;
    PathFinder pathFinder;

    public FurthestGoalStrategy(Scene scene, PathFinder pathFinder){
        this.scene = scene;
        this.pathFinder = pathFinder;
        this.goals = pathFinder.getGoals();
    }


    @Override
    public void pathDecision(){
        Agent agent;
        for (Iterator iter = scene.getAllAgents().iterator(); iter.hasNext(); ) {
            agent = (Agent) iter.next();
            Path designed_path = null;
            double path_length = 0;
            for (Point goal : goals) {
                //设置最优path
                Path path = pathFinder.plan_for(goal);
                double pathLength = path.length(agent.getPhysicalEntity().getReferencePoint());
                if (pathLength > path_length) {
                    path_length = pathLength;
                    designed_path = path;
                }
            }
            agent.setPath(designed_path);
        }
    }
}
