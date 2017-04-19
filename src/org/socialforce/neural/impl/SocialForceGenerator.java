package org.socialforce.neural.impl;

import org.socialforce.geom.Point;
import org.socialforce.geom.impl.Point2D;
import org.socialforce.geom.impl.Vector2D;
import org.socialforce.scene.Scene;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 * Created by sunjh1999 on 2017/3/31.
 */
public class SocialForceGenerator extends WallForceGenerator{
    LinkedList<double[][]> W; //权值矩阵
    double p = 3; //行人的影响系数
    double w = 1; //墙的影响系数
    double expectV = 6; //期望速度

    public SocialForceGenerator(double timestep, int intercept, double min_div) {
        super(timestep, intercept, min_div);
    }

    /**
     * 生成行人在每一时刻的位置矩阵
     */
    private void genW(){
        W = new LinkedList<>();
        for(int t = 0; t < matrix.get(matrix.size()-1).size(); t++){
            double[][] tmp = new double[map.length][map[0].length];
            for(int i = 0; i < map.length; i++){
                for(int j = 0; j < map[i].length; j++){
                    tmp[i][j] = map[i][j] * w;
                }
            }
            for(LinkedList<Point2D> positions: matrix){
                Point2D position = positions.get(t); //找到第t时间的数据
                if(position != null){
                    int x = (int)((position.getX() - dX)/min_div);
                    int y = (int)((position.getY() - dY)/min_div);
                    tmp[x][y] += p;
                }
            }
            W.add(tmp);
        }
    }

    private Vector2D getNext(Point2D c){
        Point2D nextStep = (Point2D) path.nextStep(new Point2D(c.getX(),c.getY()));
        nextStep.moveBy(-c.getX(), -c.getY()).scaleBy(1/nextStep.length());
        return nextStep;
    }

    /**
     * 行人在t时刻下的周围权值矩阵情况
     * @param c 行人坐标
     * @param t t时刻
     * @return
     */
    public double[] getSurrounding(Point2D c, int t){
        int x = (int)((c.getX() - dX)/min_div), y = (int)((c.getY() - dY)/min_div);  //计算坐标c在map中的坐标
        int range = (int)(this.range / min_div);   //计算map中的真实范围 上下左右各1
        double[][] map = W.get(t);
        //计算影响范围
        double[] surroundings = new double[(range * 2 + 1) * (range * 2 + 1)];

        for(int i = x - range; i <= x + range; i++){
            for(int j = y - range; j <= y + range; j++){
                if(i >= 0 && j >= 0 && i < map.length && j < map[0].length)
                    surroundings[(i - (x - range))*(range * 2 + 1) + (j - (y - range))] = map[i][j];
            }
        }
        surroundings[surroundings.length/2] -= p; //抛去行人本身
        return surroundings;
    }

    @Override
    public void genOutput(Scene scene) {
        setMap(scene);
        genW();
        for (int i = 0 ; i < matrix.size() ; i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                if (available(i, j)) {
                    Point2D prePoint = matrix.get(i).get(j);
                    double[] surroundings = getSurrounding(prePoint, j), tempA = new double[surroundings.length + 5];
                    Vector2D nextStep = getNext(prePoint), acc = calAcc(velocity.get(i).get(j + 1), velocity.get(i).get(j));
                    Vector2D thisVelocity = this.velocity.get(i).get(j);

                    /* 获取旋转角并旋转所有输入输出向量 */
                    double angle = Vector2D.getRotateAngle(new Vector2D(1,0), thisVelocity);
                    acc.rotate(angle);
                    nextStep.rotate(angle);
                    thisVelocity.rotate(angle);

                    /* 旋转输入矩阵 */
                    long rotateNum = Math.round(angle/(Math.PI/4));
                    for(;rotateNum > 0;rotateNum--){
                        rotateSurroundings(surroundings);
                    }
                    tempA[0] = acc.getX();
                    tempA[1] = acc.getY();
                    tempA[2] = nextStep.getX();
                    tempA[3] = nextStep.getY();
                    tempA[4] = thisVelocity.getX();
                    //tempA[5] = thisVelocity.getY();  旋转之后恒为0
                    for(int t = 0; t < surroundings.length; t++){
                        tempA[5 + t] = surroundings[t];
                    }
                    outputs.add(tempA);
                }
            }
        }
    }

    @Override
    public void addOutput(double[] output, int times){
        for(; times > 0; times --){
            double[] tempA = new double[output.length - 1];
            Vector2D nextStep = new Vector2D(output[2], output[3]), acc = new Vector2D(output[0], output[1]);
            Vector2D thisVelocity = new Vector2D(output[4], output[5]);
            double[] surroundings = new double[9];
            for(int i = 6; i < 15; i++){
                surroundings[i - 6] = output[i];
            }
            /* 获取旋转角并旋转所有输入输出向量 */
            double angle = Vector2D.getRotateAngle(new Vector2D(1,0), thisVelocity);
            acc.rotate(angle);
            nextStep.rotate(angle);
            thisVelocity.rotate(angle);

            /* 旋转输入矩阵 */
            long rotateNum = Math.round(angle/(Math.PI/4));
            for(;rotateNum > 0;rotateNum--){
                rotateSurroundings(surroundings);
            }
            tempA[0] = acc.getX();
            tempA[1] = acc.getY();
            tempA[2] = nextStep.getX();
            tempA[3] = nextStep.getY();
            tempA[4] = thisVelocity.getX();
            //tempA[5] = thisVelocity.getY();  旋转之后恒为0
            for(int t = 0; t < surroundings.length; t++){
                tempA[5 + t] = surroundings[t];
            }
            outputs.add(tempA);
        }
    }
    /**
     * 逆时针旋转一次W矩阵
     * @param surroundings
     */
    private void rotateSurroundings(double[] surroundings){
        int[] nextV = new int[]{1,2,5,0,4,8,3,6,7};
        double[] temp = surroundings.clone();
        for(int i = 0; i < 9; i++){
            surroundings[i] = temp[nextV[i]];
        }
    }
}