package org.socialforce.scene;

/**
 * the parameter of a scene
 */
public interface SceneParameter<T extends Comparable<T>> extends Comparable<SceneParameter<T>> {
    String getName();
    void setName(String name);
    boolean isValid(SceneValue<T> value);
    int getPreferedSize();
    Iterable<SceneValue<T>> sample(int size);
    Iterable<SceneValue<T>> sample();
    void addValue(SceneValue value);
    SceneValue removeValue();
}