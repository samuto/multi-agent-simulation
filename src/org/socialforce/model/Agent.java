package org.socialforce.model;

import org.socialforce.app.Scene;
import org.socialforce.geom.DistanceShape;
import org.socialforce.geom.Shape;
import org.socialforce.geom.Vector;
import org.socialforce.geom.Velocity;

/**
 * represent an agent in social force model.
 * @author Ledenel
 * Created by Ledenel on 2016/7/28.
 */
public interface Agent extends InteractiveEntity ,Moveable, Blockable {

    /**
     * get the Shape of this entity.
     * line, square, circle etc.
     *
     * @return the shape.
     */
    @Override
    DistanceShape getShape();

    /**
     * get the view of a agent.
     * agent only interact with other entities inside its view.
     * @return a shape represent the view area.
     * @see Shape
     */
    Shape getView();
    
    /**
     * get the expecting moving velocity of agent.
     * the velocity usually depends on the agent itself and its goal.
     * @return the expected velocity
     * @see Velocity
     */
    Velocity expect();
    
    /**
     * determine the next point to move.
     * the agent will also be pushed by social force in determination.
     * the determined result will be applied in act() method.
     * if the current time step is not synchronized with the agent,
     * the agent will try to catch up with that time
     * (or ignore it if current time step is fall behind).
     * @param currSteps the current timestep.
     * @return the vector represent the direction and distance to move.
     */
    Vector determineNext(int currSteps);
    
    /**
     * determine the next point to move.
     * the agent will also be pushed by social force in determination.
     * the determined result will be applied in act() method.
     * @return the vector represent the direction and distance to move.
     */
    Vector determineNext();
    
    /**
     * get the current timestep of this agent.
     * the timestep begin at 0(start of the simulation)
     * @return the current timestep.
     */
    int getCurrentSteps();
    
    /**
     * apply the determination made by determineNext() method.
     * this method will also push the time forward 1 step.
     * when act() succeed, the previous determinations will be cleared.
     * nothing happened if there are not available determinations in this agent.
     * nothing happened if the agent reach its goal.
     */
    void act();

    /**
     * get the path of the agent.
     * @return the path object.
     */
    Path getPath();

    /**
     * set the path for the agent.
     * @param path the path to be set.
     */
    void setPath(Path path);

    /**
     * get the context the scene is in.
     * @return the scene.
     */
    Scene getScene();

    /**
     * set the scene for the agent.
     * @param scene the scene to be set.
     */
    void setScene(Scene scene);

    /**
     * notify the agent which is escaped.
     */
    void escape();
}