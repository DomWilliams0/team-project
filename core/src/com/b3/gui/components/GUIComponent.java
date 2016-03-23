package com.b3.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;

/**
 * A base component that holds a GUI actor
 *
 * @author dxw405 oxe410
 */
public abstract class GUIComponent {
	/**
	 * @return The inner component
	 */
	public abstract Actor getComponent();

	/**
	 * Adds a listener to this GUIComponent
	 *
	 * @param listener the {@link EventListener} to add to this component
	 */
	public void addListener(EventListener listener) {
		getComponent().addListener(listener);
	}

}
