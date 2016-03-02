package com.b3.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Nishanth on 02/03/2016.
 */
public class ErrorPopups{

    private final SpriteBatch spriteBatch;
    private final OrthographicCamera camera;
    private final WorldCamera worldCamera;
    private Sprite sprite;
    private int noOfTicksDisplay;

    public ErrorPopups(WorldCamera worldCamera, Sprite sprite) {
        this.worldCamera = worldCamera;
        this.sprite = sprite;
        noOfTicksDisplay = 0;
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void showPopup(int noOfTicksDisplay) {
        this.noOfTicksDisplay = noOfTicksDisplay;
    }

    public void render() {
        //if has not ran out of time
        if (noOfTicksDisplay != 0) {
            //decrement time left allowed on screen
            noOfTicksDisplay--;
            spriteBatch.begin();
            int width = Gdx.graphics.getWidth();
            int height = Gdx.graphics.getHeight();
            float imgWidth = sprite.getWidth();
            float imgHeight = sprite.getHeight();
            spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
            spriteBatch.draw(sprite, width / 2 - (imgWidth/2), height/2 - (imgHeight/2), imgWidth, imgHeight);
            spriteBatch.end();
        }
    }
}
