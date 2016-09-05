package org.socialforce.app;

import org.socialforce.container.AgentPool;
import org.socialforce.container.EntityPool;
import org.socialforce.container.impl.LinkListAgentPool;
import org.socialforce.container.impl.LinkListEntityPool;
import org.socialforce.container.impl.LinkListPool;
import org.socialforce.drawer.Drawer;
import org.socialforce.drawer.impl.SceneDrawer;
import org.socialforce.geom.Box;
import org.socialforce.geom.ClipperShape;
import org.socialforce.geom.Shape;
import org.socialforce.geom.impl.Box2D;
import org.socialforce.model.Agent;
import org.socialforce.model.PathFinder;
import org.socialforce.model.impl.Air;
import org.socialforce.model.impl.Wall;

/**
 * Created by Ledenel on 2016/8/22.
 */
public class SimpleScene implements Scene {
    protected LinkListPool<Wall>walls = new LinkListPool<>();
    public void setWalls(int num,Wall wall){
        walls.set(num, wall);
    }
    public void setWallsByShape(int num,Shape shape){
        setWalls(num,new Wall(shape));
    }

    protected LinkListPool<Air>gates = new LinkListPool<>();
    public void setGates(int num,Shape shape){
        gates.set(num,new Air(shape));
    }

    /**
     *
     * @param num 选定要挖的墙
     * @param clipper 挖的洞的形状
     */
    public void Clippe(int num, ClipperShape clipper){
        if (walls.get(num).getShape() instanceof Box2D){
        clipper.clip((Box2D)walls.get(num).getShape());}
        else throw new IllegalArgumentException("暂时不支持一般的挖洞，只实现了简单矩形之间的挖洞");
    }
    /**
     * set the drawer for the drawable.
     *
     * @param drawer the drawer.
     */
    @Override
    public void setDrawer(Drawer drawer) {
        this.drawer = (SceneDrawer) drawer;
    }

    public SimpleScene(Box bounds) {
        this.bounds = bounds;
        this.statics = new LinkListEntityPool();
        this.allAgents = new LinkListAgentPool();
    }

    protected Box bounds;
    protected SceneDrawer drawer;

    /**
     * get the current drawer the object is using.
     *
     * @return the drawer.
     */
    @Override
    public Drawer getDrawer() {
        return drawer;
    }

    /**
     * calculate the next time step of the scene.
     * the time step will also forward 1 unit.
     */
    @Override
    public void stepNext() {
        for(Agent agent : allAgents) {
            agent.determineNext();
        }
        for (Agent agent : allAgents) {
            agent.act();
        }
        currentStep++;
        // 2016/8/23 add step for all agent and statics.
    }

    public AgentEscapeListener getListener() {
        return listener;
    }

    public void setListener(AgentEscapeListener listener) {
        this.listener = listener;
    }

    public void setAllAgents(AgentPool allAgents) {
        this.allAgents = allAgents;
    }


    AgentPool allAgents;
    EntityPool statics;

    /**
     * get a set of agents the scene is managing.
     *
     * @return all agents.
     */
    @Override
    public AgentPool getAllAgents() {
        return allAgents;
    }

    /**
     * get a set of static entities the scene is managing.
     *
     * @return all static entitites.
     */
    @Override
    public EntityPool getStaticEntities() {
        return statics;
    }

    public void setStaticEntities(EntityPool statics) {
        this.statics = statics;
    }
    /**
     * get the bound of the scene.
     *
     * @return the bounds.
     */
    @Override
    public Box getBounds() {
        return bounds;
    }

    protected int currentStep = 0;

    /**
     * get current time step in this scene.
     *
     * @return the current step.
     */
    @Override
    public int getCurrentSteps() {
        return currentStep;
    }


    PathFinder finder;
    /**
     * get the path finder for this scene.
     *
     * @return the path finder.
     */
    @Override
    public PathFinder getPathFinder() {
        return finder;
    }

    /**
     * set a path finder for this scene.
     *
     * @param finder the path finder for this scene.
     */
    @Override
    public void setPathFinder(PathFinder finder) {
        this.finder = finder;
    }


    AgentEscapeListener listener;
    /**
     * triggers while an agent is escaped.
     *
     * @param agent the escaped agent.
     */
    @Override
    public void onAgentEscape(Agent agent) {
        if(listener != null) {
            listener.onAgentEscape(this,agent);
        }
    }

    SocialForceApplication application;
    @Override
    public SocialForceApplication getApplication() {
        return application;
    }

    @Override
    public void setApplication(SocialForceApplication application) {
        this.application = application;
    }

    //  2016/8/24 add visible settings and valueset.

    /**
     * judege if it is visible
     * @return
     */

    // added valueset settings.
    @Override
    public boolean isVisible() {
        return drawer.getDevice().isEnable();
    }

    /**
     * set the application be visible
     * @param visible
     */
    @Override
    public void setVisible(boolean visible) {
        if(visible) {
            drawer.getDevice().enable();
        } else {
            drawer.getDevice().disable();
        }
    }

    private ValueSet valueSet;

    /**
     * get the value have been set
     * @return
     * @see ValueSet
     */
    @Override
    public ValueSet getValueSet() {
        return valueSet;
    }

    public void setValueSet(ValueSet valueSet) {
        this.valueSet = valueSet;
    }
}
