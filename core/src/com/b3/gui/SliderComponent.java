package com.b3.gui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SliderComponent {

    private Slider slider;

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
