package com.b3.gui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Function;

/**
 * An abstract class of component that forms a base for any of the components.
 * A component is an on screen object of a UI type, e.g. a button, label or checkbox
 *
 * @author oxe410
 */
public abstract class BaseComponent implements Observer, Component {

	private Function<Observable, Void> updateListener;

	/**
	 * @return The inner component
	 */
	public abstract Table getComponent();

	/**
	 * Adds a listener for the button
	 *
	 * @param listener The listener
	 */
	public void addListener(ChangeListener listener) {
	}

	/**
	 * Sets a {@link Function} to listen to updates to the component.
	 * Will override any previous calls to this method.
	 *
	 * @param updateListener The {@link Function} that will receive the updates.
	 */
	public void setUpdateListener(Function<Observable, Void> updateListener) {
		this.updateListener = updateListener;
	}

	/**
	 * Updates the observers to match the current state of the Component
	 *
	 * @param o   the observable object apply to the listener
	 * @param arg the arguments of any type
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (updateListener != null) {
			updateListener.apply(o);
		}
	}
}
