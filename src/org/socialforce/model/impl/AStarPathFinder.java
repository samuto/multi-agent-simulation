package org.socialforce.model.impl;

import org.socialforce.app.Scene;
import org.socialforce.container.EntityPool;
import org.socialforce.geom.Box;
import org.socialforce.geom.Shape;
import org.socialforce.geom.Point;
import org.socialforce.geom.impl.Point2D;
import org.socialforce.model.Agent;
import org.socialforce.model.InteractiveEntity;
import org.socialforce.model.Path;
import org.socialforce.model.PathFinder;

import java.util.LinkedList;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by sunjh1999 on 2016/10/21.
 */
public class AStarPathFinder implements PathFinder {
    static final int min_div = 1;
    private double map[][];
    double distance[][];
    Point previous[][];

    public AStarPathFinder(){

    }
    /**
     * assign map directly
     */
    public AStarPathFinder(double[][] map){
        this.map = map;
        distance = new double[map.length][map[0].length];
        previous = new Point[map.length][map[0].length];
        for(int i =0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                Point temp = new Point2D();
                previous[i][j] = temp;
            }
        }
    }

    @Override
    public Path plan(Scene targetScene, Agent agent, Point goal) {
        Point start_point = new Point2D((int) agent.getShape().getReferencePoint().getX(), (int) agent.getShape().getReferencePoint().getY());
        EntityPool all_blocks = targetScene.getStaticEntities();
        for (InteractiveEntity entity : all_blocks) {
            assert (!entity.getShape().contains(start_point));

        }
        map = map_initiate(targetScene, agent, goal);
        distance = new double[map.length][map[0].length];
        previous = new Point[map.length][map[0].length];
        for(int i =0; i<map.length; i++){
            for(int j=0; j<map[0].length; j++){
                Point temp = new Point2D();
                previous[i][j] = temp;
            }
        }
        return plan_for(start_point, goal);
    }

    public double[][] map_initiate(Scene targetScene, Agent agent, Point goal) {
        double x_range = targetScene.getBounds().getEndPoint().getX() - targetScene.getBounds().getStartPoint().getX(),
                y_range = targetScene.getBounds().getEndPoint().getY() - targetScene.getBounds().getStartPoint().getY();
        double[][] map = new double[(int) x_range / min_div][(int) y_range / min_div];
        EntityPool all_blocks = targetScene.getStaticEntities();
        Shape agent_shape = agent.getShape();
        for (int i = 0; i < (int) x_range / min_div; i++) {
            for (int j = 0; j < (int) y_range / min_div; j++) {
                map[i][j] = 0;
                for (InteractiveEntity entity : all_blocks) {
                    agent_shape.moveTo(new Point2D(i, j));
                    //assert( == entity.getShape().getClass());
                    if (agent_shape.hits((Box) entity.getShape())) {
                        map[i][j] = 1;
                    }
                    else {
                        map[i][j] = 0;        //暂定1为有障碍物占领
                    }
                }
            }
        }
        return map;
    }


    public AStarPath plan_for(Point start_point, Point goal) {
        LinkedBlockingQueue<Point> points = new LinkedBlockingQueue<Point>();
        points.offer(start_point);
        Point curr_point = points.poll();
        previous[(int)start_point.getX()][(int)start_point.getY()] = start_point;
        while(curr_point != null ){
            points = get_surroundings(start_point, curr_point ,points);
            curr_point = points.poll();
        }

        Stack<Point> point_stack = new Stack<Point>();
        curr_point = goal;
        point_stack.push(curr_point);
        while(!curr_point.equals(start_point)){
            curr_point = previous[(int)curr_point.getX()][(int)curr_point.getY()];
            point_stack.push(curr_point);
        }
        Point[] goals = new Point[point_stack.size()];
        for(int i = 0; i<goals.length; i++){
            goals[i] = point_stack.pop();
        }
        AStarPath path = new AStarPath(goals);
        return path;
    }

    public LinkedBlockingQueue<Point> get_surroundings(Point start, Point center, LinkedBlockingQueue<Point> point_set){
        int x = (int)center.getX();
        int y = (int)center.getY();
        for(int i = x-1; i <= x+1; i++){
            for(int j = y-1; j<=y+1; j++){
                if(i==x && j==y) continue;
                if(i==start.getX() && j==start.getY()) continue;
                Point tmp_point = new Point2D(i,j);
                if(available(i,j) && map[i][j]==0 && (distance[i][j]==0 || distance[i][j] > distance[x][y]+center.distanceTo(tmp_point) )){
                    distance[i][j] = distance[x][y]+center.distanceTo(tmp_point);
                    point_set.add(tmp_point);
                    previous[i][j] = center;
                }

            }
        }
        return point_set;

    }

    public boolean available(int x, int y){
        return x>=0 && y>=0 && x<map.length && y<map[0].length;
    }
}