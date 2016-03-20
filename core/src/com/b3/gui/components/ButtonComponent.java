package com.b3.gui.components;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;

/**
 * Represents a button component
 *
 * @author oxe410
 */
public class ButtonComponent extends GUIComponent implements Observer {

	private TextButton textButton;
	private Object data;
	private Consumer<Observable> callback;

	/**
	 * Creates an instance of a ButtonComponent
	 *
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
	 * Changes the text of the {@link TextButton}.
	 *
	 * @param text The new text.
	 */
	public void setText(String text) {
		textButton.setText(text);
	}

	/**
	 * @return The text on the {@link TextButton}.
	 */
	public CharSequence getText() {
		return textButton.getText();
	}

	/**
	 * @return the data of the current button, as the Object that was set originally
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data of any type that should be contained in this button component
	 */
	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public TextButton getComponent() {
		return textButton;
	}

	public void setCallback(Consumer<Observable> callback) {
		this.callback = callback;
	}

	/**
	 * This method is called whenever the observed object is changed. An
	 * application calls an <tt>Observable</tt> object's
	 * <code>notifyObservers</code> method to have all the object's
	 * observers notified of the change.
	 *
	 * @param o   the observable object.
	 * @param arg an argument passed to the <code>notifyObservers</code>
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (callback != null)
			callback.accept(o);
	}
}
