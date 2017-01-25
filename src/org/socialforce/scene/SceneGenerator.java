package org.socialforce.scene;

import org.socialforce.scene.impl.SimpleValueSet;

import java.util.Iterator;

/**
 * Generator a Scene
 */
public interface SceneGenerator {
    static Scene generate(Scene scene, ValueSet values) {
        if (values instanceof SimpleValueSet)
        for (int i = ((SimpleValueSet) values).getMaxPriority(); i > ((SimpleValueSet) values).getMinPriority();i--) {
            for (Iterator<SceneValue> iterator = values.iterator(); iterator.hasNext(); ) {
                SceneValue sceneValue = iterator.next();
                int priority = sceneValue.getPriority();
                if (sceneValue == null ) {
                    break;
                } else if ( priority > i -1 && priority <= i){
                    sceneValue.apply(scene);
                }
            }
        }
        return scene;
    }
}
