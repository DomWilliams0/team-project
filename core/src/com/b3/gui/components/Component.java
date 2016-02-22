package com.b3.gui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public abstract class Component {

    /**
     * @return The inner component
     */
    public abstract Table getComponent();

    /**
     * Adds a listener for the button
     * @param listener The listener
     */
    public void addListener(ChangeListener listener) {}
}
