package com.b3.gui.components;

import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import javax.swing.*;

/**
 * Represents a dialog box
 *
 * @author oxe410
 */
public class MessageBoxComponent extends GUIComponent {

	private Stage popupStage;
	private Dialog dialog;

	/**
	 * Creates a dialog box
	 *
	 * @param popupStage    The popup stage to display on
	 * @param text          The message text
	 * @param additionalBtn The additional button (e.g. OK). Provide an empty string to not display this button
	 */
	public MessageBoxComponent(Stage popupStage, String text, String additionalBtn) {
		// Get atlas, skin and font
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
		Skin skin = new Skin(atlas);
		BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 20);

		// Setup styles
		Window.WindowStyle windowStyle = new Window.WindowStyle(font, Color.BLACK, skin.getDrawable("window_03"));
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.font = font;
		textButtonStyle.up = skin.getDrawable("button_04");
		textButtonStyle.down = skin.getDrawable("button_03");

		skin.add("dialog", windowStyle, Window.WindowStyle.class);
		skin.add("default", labelStyle, Label.LabelStyle.class);
		skin.add("default", textButtonStyle, TextButton.TextButtonStyle.class);

		// Create dialog
		dialog = new Dialog("", skin, "dialog");
		dialog.pad(50, 50, 30, 50);
		dialog.text(text);
		dialog.setColor(Color.WHITE);
		if (!additionalBtn.isEmpty())
			dialog.button(additionalBtn);

		this.popupStage = popupStage;
	}

	/**
	 * Creates a dialog box from a table
	 *
	 * @param popupStage The popup stage
	 * @param content    The content represented as a {@link Table}
	 */
	public MessageBoxComponent(Stage popupStage, Table content) {
		// Get atlas, skin and font
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
		Skin skin = new Skin(atlas);
		BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 20);

		Window.WindowStyle windowStyle = new Window.WindowStyle(font, Color.WHITE, skin.getDrawable("window_03"));
		skin.add("dialog", windowStyle, Window.WindowStyle.class);

		dialog = new Dialog("", skin, "dialog");
		dialog.pad(50, 50, 30, 50);
		dialog.add(content);

		// Close button
		ButtonComponent closeBtn = new ButtonComponent(skin, font, "Close");
		closeBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				dialog.hide();
			}
		});
		dialog.row().align(Align.center);
		dialog.add(closeBtn.getComponent()).padTop(50).colspan(2);

		this.popupStage = popupStage;
	}

	/**
	 * Changes the text that will pop up.
	 *
	 * @param text The new text.
	 */
	public void setText(String text) {
		Label lbl = (Label) dialog.getContentTable().getChildren().get(0);

		if (lbl != null) {
			lbl.setText(text);
			lbl.getStyle().fontColor = Color.WHITE;
		}
	}

	/**
	 * Shows the current dialog
	 */
	public void show() {
//		dialog.show(popupStage).setPosition(10, Gdx.graphics.getHeight() / 2 - dialog.getHeight() / 2);
 		dialog.show(popupStage).setPosition(Gdx.graphics.getWidth() / 2 - dialog.getWidth() / 2, Gdx.graphics.getHeight() / 2 - dialog.getHeight() / 2);
	}

	/**
	 * Show default swing message box
	 *
	 * @param infoMessage The message
	 * @param titleBar    The title
	 */
	public static void show(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(null, infoMessage, titleBar, JOptionPane.INFORMATION_MESSAGE);
	}
	
	@Override
	public Actor getComponent() {
		return dialog;
	}
	
}
