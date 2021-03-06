package org.socialforce.geom.impl;

import org.socialforce.geom.*;

/**
 * Created by Ledenel on 2016/8/16.
 */
public class Force2D extends Vector2D implements Force {
    public Force2D() {
    }

    public Force2D(double x, double y) {
        super(x, y);
    }

    /**
     * 累积的力和获取速度的增量 .
     *
     * @param mass 实体的质量.
     * @param time 累积的时间.
     * @return 速度的增量.
     */
    @Override
    public Velocity deltaVelocity(double mass, double time) {
        double scale = time / mass;
        return new Velocity2D(values[0] * scale, values[1] * scale);
    }

    /**
     * 创建并返回此向量的副本.
     * “复制”的确切含义可能取决于向量的类.
     * 对于任意向量x，它的一般含义表达式是: <br>
     * x.clone() != x <br>
     * 将是真.
     *
     * @return 向量的副本.
     */
    @Override
    public Force2D clone() {
        Force2D force = new Force2D();
        force.values[0] = values[0];
        force.values[1] = values[1];
        return force;
    }

    @Override
    public Force2D getRefVector(){
        Force2D ref;
        if (values[0] == 0 && values[1] == 0){
            return new Force2D(0,0);
        }
        else
            ref = new Force2D(values[0],values[1]);
        ref.scale(1/ref.length());
        return ref;
    }

    @Override
    public Moment CalculateMoment(Point pushPoint, Point axis) {
        Vector2D distance = (Vector2D) axis.directionTo(pushPoint);
        distance.scale(axis.distanceTo(pushPoint));
        distance.rotate(Math.PI/2);
        Vector2D pro = (Vector2D) this.clone();
        pro.project(distance);
        return new Moment2D(distance.dot(pro));
    }

    @Override
    public Force getForce() {
        return this;
    }

    @Override
    public Moment getMoment() {
        return new Moment2D(0);
    }

    @Override
    public void add(Affection affection) {
        super.add(affection.getForce());
    }

    public void add(Force force) {
        super.add(force);
    }
}
