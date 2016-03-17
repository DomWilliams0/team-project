package com.b3.gui;

import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Displays a coordinate picker on the bottom of the screen showing the current node that the user is hovering over
 *
 * @author nbg481
 */
public class CoordinatePopup {

	private final BitmapFont font;
	private final Sprite background;

	private SpriteBatch spriteBatch;
	private static String coordinate;

	public static Boolean visibility;

	/**
	 * Sets up a new coordinate picker displayer
	 */
	public CoordinatePopup() {
		spriteBatch = new SpriteBatch();
		//load font from file
		font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 18);
		//load texture
		Texture tempTexture = new Texture("world/popups/bottom_canvas.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		background = new Sprite(tempTexture);

		visibility = false;
		coordinate = null;
	}

	/**
	 * Sets the visibility of the coordinate picker
	 * @param visibility if true then can see pop-up, if false then cannot
     */
	public void setVisibility(Boolean visibility) {
		CoordinatePopup.visibility = visibility;
	}

	/**
	 * sets the coordinate to display as (x, y)
	 * @param x the x coordinate the user's mouse is hovering on
	 * @param y the y coordinate the user's mouse is hovering on
     */
	public static void setCoordinate(int x, int y) {
		coordinate = "(" + x + ", " + y + ")";
	}

	/**
	 * Renders the coordinate picker
	 */
	public void render() {
		if (visibility) {
			spriteBatch.begin();
			spriteBatch.draw(background, (float) ((Gdx.graphics.getWidth() / 2) - 30), (float) -2.5, background.getWidth() - (8 - coordinate.length()) * 10, background.getHeight());
			font.draw(spriteBatch, coordinate, Gdx.graphics.getWidth() / 2, 25);
			spriteBatch.end();
		}
	}

	/**
	 * Resises the coordinate picker, to be called whenever the viewport is resized.
	 */
	public void resize() {
		spriteBatch = new SpriteBatch();
	}
}
