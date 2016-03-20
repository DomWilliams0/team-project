package com.b3.gui.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

/**
 * Represents a slider
 *
 * @author oxe410
 */
public class SliderComponent extends GUIComponent {

	private Slider slider;

	/**
	 * Creates an instance of a SliderComponent
	 *
	 * @param skin     The skin from which to pick the drawables
	 * @param min      Minimum value
	 * @param max      Maximum value
	 * @param stepSize Step size
	 */
	public SliderComponent(Skin skin, float min, float max, float stepSize) {
		Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
		sliderStyle.background = skin.getDrawable("slider_back_hor");
		sliderStyle.knob = skin.getDrawable("knob_01");

		slider = new Slider(min, max, stepSize, false, sliderStyle);
	}

	/**
	 * @return The value the {@link Slider} is pointing to.
	 */
	public float getValue() {
		return slider.getValue();
	}

	/**
	 * Changes the value the {@link Slider} is pointing to.
	 *
	 * @param value The value to set the {@link Slider} to.
	 */
	public void setValue(float value) {
		slider.setValue(value);
	}

	/**
	 * @return The inner component
	 */
	@Override
	public Slider getComponent() {
		return slider;
	}
}
