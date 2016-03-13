package com.b3.gui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Represents a checkbox component
 */
public class CheckBoxComponent extends Component {

    private CheckBox checkBox;

    /**
     * Creates an instance of a CheckBoxComponent
     * @param skin The checkbox libGDX skin
     * @param font The font to apply
     * @param text The text beside the checkbox
     */
    public CheckBoxComponent(Skin skin, BitmapFont font, String text) {
        this(skin, font, text, Color.BLACK);
    }

    public CheckBoxComponent(Skin skin, BitmapFont font, String text, Color fontColor) {
        CheckBox.CheckBoxStyle cbs = new CheckBox.CheckBoxStyle();
        cbs.font = font; // skin.getFont("default");
        cbs.fontColor = fontColor;
        cbs.checkboxOff = skin.getDrawable( "checkbox_off");
        cbs.checkboxOn = skin.getDrawable( "checkbox_on");
        skin.add("default", cbs);

        checkBox = new CheckBox(text, skin);
    }

    public void setText(String text) {
        checkBox.setText(text);
    }

    public CharSequence getText() {
        return checkBox.getText();
    }

    @Override
    public CheckBox getComponent() {
        return checkBox;
    }

    @Override
    public void addListener(ChangeListener listener) {
        checkBox.addListener(listener);
    }
}
