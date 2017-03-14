package org.socialforce.app.Applications;

import org.socialforce.geom.DistanceShape;
import org.socialforce.geom.impl.Box2D;
import org.socialforce.geom.impl.Circle2D;
import org.socialforce.geom.impl.Point2D;
import org.socialforce.model.InteractiveEntity;
import org.socialforce.model.impl.Monitor;
import org.socialforce.model.impl.Wall;
import org.socialforce.scene.ParameterPool;
import org.socialforce.scene.Scene;
import org.socialforce.scene.SceneLoader;
import org.socialforce.scene.impl.*;
import org.socialforce.strategy.GoalStrategy;
import org.socialforce.strategy.PathFinder;
import org.socialforce.strategy.impl.AStarPathFinder;
import org.socialforce.strategy.impl.FurthestGoalStrategy;
import org.socialforce.strategy.impl.NearestGoalStrategy;
import org.socialforce.strategy.impl.StraightPathFinder;

import java.util.Iterator;

import static org.socialforce.scene.SceneLoader.genParameter;

/**
 * Created by sunjh1999 on 2017/2/26.
 */
public class ApplicationForCrossFlow extends SimpleApplication {
    DistanceShape template;

    public ApplicationForCrossFlow (){
    }

    /**
     * start the application immediately.
     */
    @Override
    public void start() {
        for (Iterator<Scene> iterator = scenes.iterator(); iterator.hasNext();){
            Scene scene = iterator.next();
            PathFinder pathFinder = new AStarPathFinder(scene, template);
            GoalStrategy strategy = new FurthestGoalStrategy(scene, pathFinder);
            strategy.pathDecision();
            while (!scene.getAllAgents().isEmpty()) {
                long start = System.currentTimeMillis(), span, fps = 16;
                scene.stepNext();
                long l = System.currentTimeMillis() - start;
                span = l > fps? 0: fps - l;
                try {
                    Thread.sleep(span); //锁帧大法
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 需要根据parameter的map来生成一系列scene
     */
    @Override
    public void setUpScenes(){
        template = new Circle2D(new Point2D(0,0),0.486/2);
        SceneLoader loader = new StandardSceneLoader(new SimpleScene(new Box2D(-50, -50, 100, 100)),
                new Wall[]{
                        new Wall(new Box2D(20,-3,1,15))
                });
        ParameterPool parameters = new SimpleParameterPool();
        parameters.addLast(genParameter(new SV_RandomAgentGenerator(50,new Box2D(3,1,3,8),template)));
        parameters.addLast(genParameter(new SV_RandomAgentGenerator(60,new Box2D(33,1,3,8),template)));
        parameters.addLast(genParameter(new SV_Exit(new Box2D[]{new Box2D(19,3,3,3)})));
        parameters.addLast(genParameter(new SV_SafetyRegion(new Box2D(46,1,1,8))));
        parameters.addLast(genParameter(new SV_SafetyRegion(new Box2D(-6,1,1,8))));
        loader.readParameterSet(parameters);
        scenes = loader.readScene(this);
    }

}