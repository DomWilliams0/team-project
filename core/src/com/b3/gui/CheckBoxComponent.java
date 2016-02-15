package com.b3.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class CheckBoxComponent {

    private CheckBox checkBox;

    public CheckBoxComponent(Skin skin, BitmapFont font, String text) {
        CheckBox.CheckBoxStyle cbs = new CheckBox.CheckBoxStyle();
        skin.add("default", font, BitmapFont.class);
        cbs.font = skin.getFont("default");
        cbs.checkboxOff = skin.getDrawable( "checkbox_off");
        cbs.checkboxOn = skin.getDrawable( "checkbox_on");
        skin.add("default", cbs);

        checkBox = new CheckBox(text, skin);
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setText(String text) {
        checkBox.setText(text);
    }

    public void addListener(ChangeListener listener) {
        checkBox.addListener(listener);
    }
}
