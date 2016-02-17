package com.b3.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelComponent {

    private Label label;

    public  LabelComponent(Skin skin, String text) {

        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("default"), Color.BLACK);
        /*textButtonStyle = new TextButton.TextButtonStyle();
        skin.add("default", font, BitmapFont.class);
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.up = skin.getDrawable("button_04");
        textButtonStyle.down = skin.getDrawable("button_03");
        skin.add("default", textButtonStyle);*/
        skin.add("default", labelStyle);

        label = new Label(text, labelStyle);
    }

    public Label getLabel() {
        return label;
    }

    public String getText() {
        return label.getText().toString();
    }

    public void setText(String str) {
        label.setText(str);
    }

}
