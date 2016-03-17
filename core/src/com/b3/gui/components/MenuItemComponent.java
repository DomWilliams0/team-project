package com.b3.gui.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class MenuItemComponent extends ButtonComponent {

    public MenuItemComponent(Skin skin, BitmapFont font, String text) {
        super(skin, font, text);
    }

    public void addListener(ChangeListener listener) {
        getComponent().addListener(listener);
    }

}
