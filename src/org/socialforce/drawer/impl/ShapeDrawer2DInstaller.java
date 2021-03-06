package org.socialforce.drawer.impl;

import org.socialforce.drawer.Drawable;
import org.socialforce.drawer.Drawer;
import org.socialforce.drawer.DrawerInstaller;
import org.socialforce.geom.impl.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Ledenel on 2016/8/25.
 */
public class ShapeDrawer2DInstaller implements DrawerInstaller {
    protected ShapeDrawer2DInstaller() {

    }

    public ShapeDrawer2DInstaller(Graphics2D graphics2D) {
        this();
        this.graphics2D = graphics2D;
        registerDrawer(new SolidBox2DDrawer(graphics2D),Box2D.class);
        registerDrawer(new SolidCircle2DDrawer(graphics2D),Circle2D.class);
        registerDrawer(new SolidRectangle2DDrawer(graphics2D),Rectangle2D.class);
        registerDrawer(new SolidSegment2DDrawer(graphics2D), Segment2D.class);
        registerDrawer(new SolidEllipse2DDrawer(graphics2D), Ellipse2D.class);
   }

    Map<Class<? extends Drawable>,Drawer> drawerMap = new HashMap<>(1000);

    protected Graphics2D graphics2D;


    /**
     * creates and set a proper drawer for a drawable.
     *
     * @param drawable the drawable.
     * @return true if the installer has a proper drawer for the drawable; otherwise false.
     */
    @Override
    public boolean addDrawerSupport(Drawable drawable) {
        Drawer supported = drawerMap.get(drawable.getClass());
        if(supported != null) {
            drawable.setDrawer(supported);
            return true;
        } else {
            return false;
        }
    }

    /**
     * register a drawer in this installer for a specific drawable type.
     * the drawer will be replaced while there is already a drawer registered for the type.
     *
     * @param registeredDrawer
     * @param drawableType
     */
    @Override
    public void registerDrawer(Drawer registeredDrawer, Class<? extends Drawable> drawableType) {
        drawerMap.put(drawableType,registeredDrawer);
    }

    @Override
    public void unregister(Class<? extends Drawable> type) {
        drawerMap.remove(type);
    }

    @Override
    public Iterable<Drawer> getRegisteredDrawers() {
        return drawerMap.values();
    }
}
