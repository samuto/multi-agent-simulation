package org.socialforce.model;

import org.socialforce.geom.PhysicalEntity;

/**
 * 可影响Agent的实体
 * @see Agent
 * Created by sunjh1999 on 2017/3/6.
 */
public interface Influential extends InteractiveEntity {
    /**
     * 获取一个Affectable的可影响范围。
     * 该Affectable只和位于该视域范围内的Affected进行交互
     * @return 一个表示该视域范围的形状
     * @see PhysicalEntity
     */
    PhysicalEntity getView();

    /**
     * 定义一个实体如何影响一个Agent
     * 若target能影响实体本身，则相应影响也在此处定义
     * @see Agent
     * @param target
     */
    void affect(Agent target);

    /**
     * 定义Influential如何影响所有可能影响的实体
     * 主要为了affect过程受两个及以上Agent影响而设计，从而避免设计更高层策略
     * 若对所有Agent影响模式相同，可遍历并调用affect方法影响
     * @param affectableAgents
     */
    void affectAll(Iterable<Agent> affectableAgents);
}
