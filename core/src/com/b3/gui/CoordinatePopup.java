package com.b3.gui;

import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Font;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Scanner;

/**
 * Created by Nishanth on 06/03/2016.
 */
public class CoordinatePopup {

    private final BitmapFont font;
    private final Sprite background;
    private final Scanner reader;

    private SpriteBatch spriteBatch;
    private static String coordinate;

    public static Boolean visibility;

    public CoordinatePopup () {
        spriteBatch = new SpriteBatch();
        //load font from file
        font = Font.getFont(Config.getString(ConfigKey.FONT_FILE), 18);
        //load texture
        Texture tempTexture = new Texture("core/assets/world/popups/bottom_canvas.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        background = new Sprite(tempTexture);

        visibility = false;
        coordinate = null;

        reader = new Scanner(System.in);  // Reading from System.in
    }

    public void setVisibility (Boolean visibility) {
        this.visibility = visibility;
    }

    public static void setCoordinate (int x, int y) {
        String temp = "(" + x + ", " + y + ")";
        coordinate = temp;
    }

    public void render () {
        if (visibility) {
            spriteBatch.begin();
            spriteBatch.draw(background, (float) ((Gdx.graphics.getWidth() / 2) - 30), (float) -2.5, background.getWidth() - (8 - coordinate.length())*10, background.getHeight());
            font.draw(spriteBatch, coordinate, Gdx.graphics.getWidth() / 2, 25);
            spriteBatch.end();
        }
    }

    public void resize () {
        spriteBatch = new SpriteBatch();
    }
}
