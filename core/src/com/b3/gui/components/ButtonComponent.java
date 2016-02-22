package com.b3.gui.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Represents a button component
 */
public class ButtonComponent extends Component {

    private TextButton textButton;

    /**
     * Creates an instance of a ButtonComponent
     * @param skin The button libGDX skin
     * @param font The font to apply
     * @param text The text
     */
    public ButtonComponent(Skin skin, BitmapFont font, String text) {

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        skin.add("default", font, BitmapFont.class);
        textButtonStyle.font = skin.getFont("default");
        textButtonStyle.up = skin.getDrawable("button_04");
        textButtonStyle.down = skin.getDrawable("button_03");
        skin.add("default", textButtonStyle);

        textButton = new TextButton(text, skin);
    }

    /**
     * @return The inner button representation
     */
    public TextButton getComponent() {
        return textButton;
    }

    @Override
    public void addListener(ChangeListener listener) {
        textButton.addListener(listener);
    }

}
