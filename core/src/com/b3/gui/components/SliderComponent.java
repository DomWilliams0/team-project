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
     * @param skin The skin from which to pick the drawables
     * @param min Minimum value
     * @param max Maximum value
     * @param stepSize Step size
     */
    public SliderComponent(Skin skin, float min, float max, float stepSize) {
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = skin.getDrawable("slider_back_hor");
        sliderStyle.knob = skin.getDrawable("knob_01");

        slider = new Slider(min, max, stepSize, false, sliderStyle);
    }

    public Slider getSlider() {
        return slider;
    }

    public float getValue() {
        return slider.getValue();
    }

    public void setValue(float value) {
        slider.setValue(value);
    }

    public void addListener(ChangeListener listener) {
        slider.addListener(listener);
    }

}
