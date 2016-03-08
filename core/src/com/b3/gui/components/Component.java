package com.b3.gui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;

public abstract class Component implements Observer {

    private Function<Observable, Void> updateListener;

    /**
     * @return The inner component
     */
    public abstract Table getComponent();

    /**
     * Adds a listener for the button
     * @param listener The listener
     */
    public void addListener(ChangeListener listener) {}

    public void setUpdateListener(Function<Observable, Void> updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (updateListener != null) {
            updateListener.apply(o);
        }
    }
}
