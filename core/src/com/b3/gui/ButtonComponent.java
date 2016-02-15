package com.b3.gui;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class ButtonComponent {

    private TextButton.TextButtonStyle textButtonStyle;
    private TextButton textButton;

    public ButtonComponent(Skin skin, BitmapFont font, String text) {

        textButtonStyle = new TextButton.TextButtonStyle();
        skin.add("default", font, BitmapFont.class);
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.up = skin.getDrawable("button_04");
        textButtonStyle.down = skin.getDrawable("button_03");
        skin.add("default", textButtonStyle);

        textButton = new TextButton(text, skin);
    }

    public TextButton getTextButton() {
        return textButton;
    }

    public void addListener(ChangeListener listener) {
        textButton.addListener(listener);
    }

}
