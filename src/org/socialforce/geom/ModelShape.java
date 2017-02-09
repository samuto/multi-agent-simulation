package org.socialforce.geom;

import org.socialforce.drawer.Drawable;
import org.socialforce.model.InteractiveEntity;

import java.io.Serializable;

/**
 * 代表坐标系中一个可以被圆弧和线段唯一表示的几何形状
 * 在一个二维空间中, 可能是一个圆，线，三角形，矩形等.
 * 在一个三维空间中，可能是一个球面，球体、长方体等.
 * 特别注意，一个空的形状不包含任何东西.
 * @author Ledenel
 * @see Box
 * @see InteractiveEntity
 * Created by Ledenel on 2016/7/28.
 */
public interface ModelShape extends Shape,Drawable {
    /**
     * 检查一个点是否属于 <code>ModelShape</code>.
     *
     * @param point 将被检查的点.
     * @return 如果该点是该形状上的一部分，就返回真，否则返回假.
     */
    boolean contains(Point point);

    /**
     * 获取一个点到这条直线的距离矢量.
     * 如果距离为负，就说明这个点在这个形状上.
     * 空的形状为 Double.NaN .
     *
     * @param point 将被检查的点.
     * @return 该距离 .
     */
    double getDistanceToPoint(Point point);

    /**
     * 获取该点到另一个点的方向矢量.
     * 这个向量的模长为1.
     *
     * @param point 将被检查的点.
     * @return 该距离矢量.
     */
    Vector getDirectionToPoint(Point point);

    /**
     * 获取该形状的参考点.
     * 通常一个参考点是该形状的的中心点.
     * 对于球面/球体，它就是中心.
     *
     * @return 参考点. 如果这个形状是控的话，就返回空.
     */
    Point getReferencePoint();

    /**
     * 获取这个形状的边界.
     * 如果这个形状不能放到一个box里的话，就返回空.
     * 如果这个形状是空的，就返回一个空的形状.
     *
     * @return 代表这个形状的box.
     */
    Box getBounds();

    /**
     * 检查这个形状是否与一个hitbox的box相交.
     *
     * @param hitbox 将要被检查的box.
     * @return 如果相交，返回真，否则返回假.
     */
    boolean hits(Box hitbox);

    /**
     * 移动这个形状到一个指定的位置.
     * 对于非空形状，参考点是等于位置点.
     * 对于空形状，什么也不做.
     *
     * @param location 指定的位置
     */
    void moveTo(Point location);

    /**
     * 创建并返回该形状的副本.
     *
     * @return 该形状的副本.
     */
    ModelShape clone();

    /**
     * 分解为边界
     */
    PrimitiveShape[] breakdown();

    /**
     * 抽象为更高级的图形
     * 比如一个多边形恰好为正方形，则返回正方形。
     * 如果不是，则返回自身。
     */
    ModelShape abstractShape();

    /**
     * 把一个图形切成多个图形。
     * @param clipper 用来切的图形
     * @return 被切成的图形
     */
    ModelShape[] minus(ModelShape clipper);

    ModelShape[] And(ModelShape other);

    ModelShape intersect(ModelShape other);

    void Not();

    Point getInsidePoint();
    Point getOutsidePoint();
}