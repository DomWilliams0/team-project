package com.b3.gui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class LabelComponent {

    private Label label;

    public LabelComponent(Skin skin, String text) {
        this(skin, text, Color.BLACK);
    }

    public LabelComponent(Skin skin, String text, Color color) {
        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("default"), color);
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

    /**
     * @return The inner button representation
     */
    public Label getComponent() {
        return label;
    }
}
