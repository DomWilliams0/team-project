package com.b3.gui.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * A {@link ButtonComponent} that is present in {@link MenuComponent}
 * Multiple MenuItemComponents are in one {@link MenuComponent}
 * {@link MenuComponent} only accepts MenuItemComponent not {@link ButtonComponent}
 *
 * @author oxe410
 */
public class MenuItemComponent extends ButtonComponent implements Component {

	/**
	 * Creates a new menu item with a specific skin, font and text
	 *
	 * @param skin The button's skin ({@link Skin})
	 * @param font The font of {@link BitmapFont} that the text will be
	 * @param text The text to be contained in this label
	 */
	public MenuItemComponent(Skin skin, BitmapFont font, String text) {
		super(skin, font, text);
	}

	/**
	 * Adds a {@link ChangeListener} to the dropdown.
	 *
	 * @param listener The {@link ChangeListener} to add.
	 */
	public void addListener(ChangeListener listener) {
		getComponent().addListener(listener);
	}

}
