package org.socialforce.entity;

/**
 * Created by Ledenel on 2016/7/28.
 */
public interface Agent extends InteractiveEntity {
    void determine(int currSteps);
    void determineNext();
    int getCurrentSteps();
    void act();
}
