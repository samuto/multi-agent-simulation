package org.socialforce.geom.impl;

import org.socialforce.drawer.Drawer;
import org.socialforce.geom.*;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ledenel on 2016/8/8.
 */
public class Box2D extends Shape2D implements Box {
    /**
     * 表明是否有其他对象“等于”这一个.
     * <p>
     * {@code equals} 在对非空对象引用上，该方法实现了一个等价关系:
     * <ul>
     * <li> 它是 <i> 它的对立是 </i>: 对于任何非空参考值
     * {@code x}, {@code x.equals(x)} 应该返回
     * {@code true}.
     * <li>它是 <i> 它的对称是</i>: 对于任何非空参考值
     * {@code x} 和 {@code y}, {@code x.equals(y)}
     * 应该返回 {@code true} 当且仅当
     * {@code y.equals(x)} 返回 {@code true}.
     * <li> 它是 <i> 它的传递是 </i>: 对于任何非空参考值
     * {@code x}, {@code y}, 和 {@code z}, 如果
     * {@code x.equals(y)} 返回 {@code true} 和
     * {@code y.equals(z)} 返回 {@code true}, 然后
     * {@code x.equals(z)} 应该返回 {@code true}.
     * <li>它是 <i> 它的连续是 </i>: 对于任何非空参考值
     * {@code x} 和 {@code y}, 多个连续的调用
     * {@code x.equals(y)} 返回 {@code true}
     * 或者连续返回 {@code false}, 在对对象的改进上，没有在提供信息用于 {@code equals} 的比较
     * <li>对于任何非空参考值 {@code x},
     * {@code x.equals(null)} 应该返回 {@code false}.
     * </ul>
     * <p>
     * {@code Object}类的 {@code equals} 方法实现了对象的差别可能性最大的等价关系;
     * 也就是说, 对于任何非空参考值 {@code x} 和
     * {@code y}, 这种方法返回 {@code true} 当且仅当
     *  {@code x} 和 {@code y} 参照相同的对象
     * ({@code x == y} 有值 {@code true}).
     * <p>
     * 注意，通常当此方法被重写了，就有必要重写{@code hashCode}方法，目的是保持{@code hashCode}法的一般的约定，
     * 即平等的对象必须具有相同散列码
     * @param obj 要进行比较的参考对象.
     * @return  如果这个对象与该 obj 的参数相同就返回 {@code true} ，否则返回 {@code false}.
     * @see #hashCode()
     * @see HashMap
     */


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Box2D) {
            Box2D box = (Box2D) obj;
            return Math.abs(box.xmin-xmin) < 10e-7 &&
                    Math.abs(box.xmax-xmax) < 10e-7 &&
                    Math.abs(box.ymin-ymin) < 10e-7 &&
                    Math.abs(box.ymax-ymax) < 10e-7;
        }
        return false;
    }

    @Override
    public Drawer getDrawer() {
        return drawer;
    }

    @Override
    public void setDrawer(Drawer drawer) {
        this.drawer = drawer;
    }

    protected Drawer drawer;

    protected double xmin, ymin, xmax, ymax;

    public Box2D() {

    }

    public Box2D(double left, double bottom, double width, double height) {
        double right = left + width;
        double top = bottom + height;
        double temp;
        if (right < left) {
            temp = right;
            right = left;
            left = temp;
        }
        if (top < bottom) {
            temp = top;
            top = bottom;
            bottom = temp;
        }
        xmin = left;
        xmax = right;
        ymin = bottom;
        ymax = top;
    }

    public Box2D(Point start, Point end) {
        this(start.getX(),
                start.getY(),
                end.getX() - start.getX(),
                end.getY() - start.getY());
    }

    /**
     * 检查点是否属于 <code>PhysicalEntity</code>.
     *
     * @param point 将被检查的点.
     * @return 如果点是该形状的一部分，就为真，否则为假.
     */
    @Override
    public boolean contains(Point point) {
        return xmin <= point.getX()
                && point.getX() <= xmax
                && ymin <= point.getY()
                && point.getY() <= ymax;
    }

    /**
     * 获取点到形状的距离.
     * 如果点在形状上，距离就是0.
     * Double.NaN 表示空形状.
     *
     * @param point 将被检查的点.
     * @return 距离. 如果点不能到形状上话，返回 Double.NaN .
     */
    @Override
    public double getDistance(Point point) {
        double dsx = point.getX() - xmin;
        double dsy = point.getY() - ymin;
        double dex = point.getX() - xmax;
        double dey = point.getY() - ymax;
        if (contains(point)) {
            return -Math.min(Math.min(Math.abs(dsx),Math.abs(dsy)), Math.min(Math.abs(dex),Math.abs(dey)));
        }
        if (dsx > 0) {
            if (dsy > 0) {
                if (dex > 0) {
                    if (dey > 0) {
                        return point.distanceTo(new Point2D(xmax, ymax));
                    } else { // dey <= 0
                        return Math.abs(dex);
                    }
                } else { // dex <= 0
                    return Math.abs(dey);
                }
            } else { // dsy <= 0
                if (dex > 0) {
                    return point.distanceTo(new Point2D(xmax, ymin));
                } else { // dex <= 0
                    return Math.abs(dsy);
                }
            }
        } else { // dsx <= 0
            if (dsy > 0) {
                if (dey > 0) {
                    return point.distanceTo(new Point2D(xmin, ymax));
                } else { // dey <= 0
                    return Math.abs(dsx);
                }
            } else { // dsy <= 0
                return point.distanceTo(new Point2D(xmin, ymin));
            }
        }
        //return Double.NaN;
    }

    public Vector getDirection(Point point){
        Vector vector,temp;
        Point2D a,b,c,d;
        a = new Point2D(getStartPoint().getX(),getStartPoint().getY());
        b = new Point2D(getStartPoint().getX(),getEndPoint().getY());
        c = new Point2D(getEndPoint().getX(),getEndPoint().getY());
        d = new Point2D(getEndPoint().getX(),getStartPoint().getY());
        double dsx = point.getX() - xmin;
        double dsy = point.getY() - ymin;
        double dex = point.getX() - xmax;
        double dey = point.getY() - ymax;
        if (contains(point)) {
            double result = Math.min(Math.min(Math.abs(dsx),Math.abs(dsy)), Math.min(Math.abs(dex),Math.abs(dey)));
            if(result == new Segment2D(a,b).getDistance(point)) return new Segment2D(a,b).getDirection(point);
            if(result == new Segment2D(b,c).getDistance(point)) return new Segment2D(b,c).getDirection(point);
            if(result == new Segment2D(c,d).getDistance(point)) return new Segment2D(c,d).getDirection(point);
            if(result == new Segment2D(d,a).getDistance(point)) return new Segment2D(d,a).getDirection(point);
        }
        if (dsx > 0) {
            if (dsy > 0) {
                if (dex > 0) {
                    if (dey > 0) {
                        vector = point.directionTo(new Point2D(xmax, ymax));
                        vector.scale(-1);
                        return vector;
                    } else { // dey <= 0
                        return new Vector2D(dex,0).getRefVector();
                    }
                } else { // dex <= 0
                    return new Vector2D(0,dey).getRefVector();
                }
            } else { // dsy <= 0
                if (dex > 0) {
                    vector = point.directionTo(new Point2D(xmax, ymin));
                    vector.scale(-1);
                    return vector;
                } else { // dex <= 0
                    return new Vector2D(0,dsy).getRefVector();
                }
            }
        } else { // dsx <= 0
            if (dsy > 0) {
                if (dey > 0) {
                    vector = point.directionTo(new Point2D(xmin, ymax));
                    vector.scale(-1);
                    return vector;
                } else { // dey <= 0
                    return new Vector2D(dsx,0).getRefVector();
                }
            } else { // dsy <= 0
                vector = point.directionTo(new Point2D(xmin, ymin));
                vector.scale(-1);
                return vector;
            }
        }
        //return Double.NaN;
    }

    /**
     * 获取这个形状的参考点.
     * 参考点通常是这个形状的中心点.
     * 对于球面/球体，参考点就是中心点.
     *
     * @return 参考点.如果该形状是空的话，就返回空.
     */
    @Override
    public Point getReferencePoint() {
        return new Point2D((xmin + xmax) / 2, (ymin + ymax) / 2);
    }

    /**
     * 获取该形状的边界.
     * 如果该形状不能放进一个box的话，返回空.
     * 如果该形状是空的话，返回空的形状.
     *
     * @return 一个代表该形状边界的box.
     */
    @Override
    public Box getBounds() {
        return (Box) this.clone();
    }

    protected Box2D quiteConvert(Box box) {
        if (box instanceof Box2D) {
            return (Box2D) box;
        } else {
            throw new IllegalArgumentException("Box2D could only calculated with Box2D.");
        }
    }

    /**
     * 检查该形状是否与一个hitbox的box有交集.
     *
     * @param hitbox 将被检查的box.
     * @return 如果相交，为真，否则为假.
     */
    @Override
    public boolean hits(Box hitbox) {
        Box2D in = quiteConvert(hitbox);
        double zx = Math.abs(xmin + xmax - in.xmin - in.xmax); //两矩形重心在x轴上距离的2倍
        double zy = Math.abs(ymin + ymax - in.ymin - in.ymax); //两矩形重心在y轴上距离的2倍
        double x = (xmax - xmin) + (in.xmax - in.xmin); //两矩形在x轴上边长的和
        double y = (ymax - ymin) + (in.ymax - in.ymin); //两矩形在y轴上边长的和
        return zx<=x && zy<=y;
    }

    /**
     * 移动这个形状到一个指定的位置.
     * 对于一个非空的形状，参考点等于位置点.
     * 对于空的形状，什么也不做.
     *
     * @param location 指定的位置
     */
    @Override
    public void moveTo(Point location) {
        double xsum = location.getX() * 2;
        double ysum = location.getY() * 2;
        double xsub = xmin - xmax;
        double ysub = ymin - ymax;
        xmin = (xsum + xsub) / 2;
        ymin = (ysum + ysub) / 2;
        xmax = (xsum - xsub) / 2;
        ymax = (ysum - ysub) / 2;
        double temp;
        if(xmin > xmax) {
            temp = xmin;
            xmin = xmax;
            xmax = temp;
        }
        if(ymin > ymax) {
            temp = ymin;
            ymin = ymax;
            ymax = temp;
        }
    }

    /**
     * 创建并返回这个形状的副本.
     *
     * @return 这个形状的副本.
     */
    @Override
    public PhysicalEntity clone() {
        Box2D cloned = new Box2D();
        cloned.xmin = xmin;
        cloned.xmax = xmax;
        cloned.ymin = ymin;
        cloned.ymax = ymax;
        return cloned;
    }

    /**
     * 获取box的起点.
     *
     * @return 起点.
     */
    @Override
    public Point getStartPoint() {
        return new Point2D(xmin, ymin);
    }

    /**
     * 获取box的终点.
     *
     * @return 终点.
     */
    @Override
    public Point getEndPoint() {
        return new Point2D(xmax, ymax);
    }

    /**
     * 获取box的规模大小.
     *
     * @return 代表规模大小的向量.
     */
    @Override
    public Vector getSize() {
        return new Vector2D(xmax - xmin, ymax - ymin);
    }

    /**
     * 返回该box和另外box的交集.
     *
     * @param other 有交集的box.
     * @return 交集.
     */
    @Override
    public Box intersect(Box other) {
        Box2D in = quiteConvert(other);
        if (hits(in)) {
            double[] xarr = new double[]{xmin, xmax, in.xmin, in.xmax};
            double[] yarr = new double[]{ymin, ymax, in.ymin, in.ymax};
            Arrays.sort(xarr);
            Arrays.sort(yarr);
            Box2D box = new Box2D();
            box.xmin = xarr[1];
            box.xmax = xarr[2];
            box.ymin = yarr[1];
            box.ymax = yarr[2];
            return box;
        }
        return null;
    }

    public double getXmin() {
        return xmin;
    }

    public double getYmin() {
        return ymin;
    }

    public double getXmax() {
        return xmax;
    }

    public double getYmax() {
        return ymax;
    }


    /**
     * 获取维度实体的维度.
     *
     * @return 维度.
     */
    @Override
    public int dimension() {
        return 2;
    }


    /**
     * 目前只支持两个BOX以十字形互相切
     * 其它奇怪切暂时不支持
     * 等有支持的必要的时候再添加
     * @param shape
     * @return
     */
    @Override
    public PhysicalEntity[] clip(ClippablePhysicalEntity shape) {
        Point[][] dictionary = new Point[2][2];
        if(shape instanceof Box2D){
            PhysicalEntity[] Boxes;
            if (hits(shape.getBounds())){
                if((xmin <= ((Box2D) shape).getXmin() && ((Box2D) shape).getXmax() <= xmax && ymax <= ((Box2D) shape).getYmax() && ((Box2D) shape).getYmin() <= ymin)
                    || (xmin >= ((Box2D) shape).getXmin() && ((Box2D) shape).getXmax() >= xmax && ymax >= ((Box2D) shape).getYmax() && ((Box2D) shape).getYmin() >= ymin)){
                        dictionary[0][0] = ((Box2D) shape).getStartPoint();
                        dictionary[1][1] = ((Box2D) shape).getEndPoint();
                        if (((Box2D) shape).getStartPoint().getX()<getStartPoint().getX()){
                            dictionary[1][0] = new Point2D(getStartPoint().getX(),((Box2D) shape).getEndPoint().getY());
                        dictionary[0][1] = new Point2D(getEndPoint().getX(),((Box2D) shape).getStartPoint().getY());}
                        else {
                            dictionary[1][0] = new Point2D(((Box2D) shape).getEndPoint().getX(),getStartPoint().getY());
                            dictionary[0][1] = new Point2D(((Box2D) shape).getStartPoint().getX(),getEndPoint().getY());
                        }
                        Boxes = new PhysicalEntity[]{new Box2D((dictionary)[0][0],dictionary[1][0]),new Box2D(dictionary[0][1],dictionary[1][1])};
                        return Boxes;
                    }
                else{
                    //System.out.println("目前阶段不支持以一般方式进行矩形切割，只支持十字形切割");
                    return new PhysicalEntity[]{shape};
                }
                }
            else return new PhysicalEntity[]{shape};
        }
        else throw new IllegalArgumentException("目前阶段不支持其它图形的切割，只支持两个矩形的切割");
    }

   @Override
    public Box2D expandBy(double extent) {
        xmin -= extent;
        ymin -= extent;
        xmax += extent;
        ymax += extent;
       return this;
    }

    @Override
    public double getArea() {
        return (xmax - xmin)*(ymax - ymin);
    }

    public String toString(){
        return "左下右上坐标为： ("+xmin+"," +ymin+")，("+xmax+ ","+ymax+ ")";
    }
}
