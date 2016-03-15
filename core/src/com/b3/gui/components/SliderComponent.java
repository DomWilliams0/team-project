package com.b3.gui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Represents a slider
 */
public class SliderComponent {

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
	 * @return The actual {@link Slider} object this component holds.
	 */
	public Slider getSlider() {
		return slider;
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
	 * Adds a {@link ChangeListener} to the {@link Slider}.
	 *
	 * @param listener The {@link ChangeListener} to add.
	 */
	public void addListener(ChangeListener listener) {
		slider.addListener(listener);
	}

}
