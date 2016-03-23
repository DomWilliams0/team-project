package com.b3.gui.components;

import com.b3.util.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Represents an on screen label.
 * An on-screen label is defined as a piece of text, with colour that is not clickable, selectable or interactive in
 * any way.
 *
 * @author oxe410
 */
public class LabelComponent extends GUIComponent {

	protected Label label;

	/**
	 * Creates a new label with font colour being black
	 *
	 * @param skin The button's skin ({@link Skin})
	 * @param text The text to be contained in this label
	 */
	public LabelComponent(Skin skin, String text) {
		this(skin, text, Color.BLACK);
	}

	/**
	 * Creates a new label with font colour being of colour {@code color}
	 *
	 * @param skin  The button's skin ({@link Skin})
	 * @param text  The text to be contained in this label
	 * @param color The text's colour
	 */
	public LabelComponent(Skin skin, String text, Color color) {
		Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("default"), color);
		skin.add("default", labelStyle);

		label = new Label(text, labelStyle);
	}

	/**
	 * Creates a new label using a different colour, font and font size than default
	 *
	 * @param fontLocation the location of the font as a directory location
	 * @param fontSize     the size of the font
	 * @param text         the text to be contained in this label
	 * @param color        the text's colour
	 */
	public LabelComponent(String fontLocation, int fontSize, String text, Color color) {
		BitmapFont font = Utils.getFont(fontLocation, fontSize);
		font.getData().markupEnabled = true;

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = color;
		label = new Label(text, labelStyle);
	}

	/**
	 * Creates a new label using a different font and font size than default, and can enable markup so that the specific
	 * sections of text can be different colours
	 *
	 * @param fontLocation  the location of the font as a directory location
	 * @param fontSize      the size of the font
	 * @param text          the text to be contained in this label
	 * @param markupEnabled whether to colour the text according to the markup style noted in:
	 *                      {@link com.badlogic.gdx.graphics.g2d.BitmapFont.BitmapFontData}
	 */
	public LabelComponent(String fontLocation, int fontSize, String text, boolean markupEnabled) {
		BitmapFont font = Utils.getFont(fontLocation, fontSize);
		font.getData().markupEnabled = markupEnabled;

		Label.LabelStyle labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		label = new Label(text, labelStyle);
	}

	/**
	 * @return The text of the {@link Label}.
	 */
	public String getText() {
		return label.getText().toString();
	}

	/**
	 * Changes the text of the {@link Label}.
	 *
	 * @param str The new text.
	 */
	public void setText(String str) {
		label.setText(str);
	}

	@Override
	public Label getComponent() {
		return label;
	}

}
