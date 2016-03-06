package com.b3.gui;

import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Nishanth on 02/03/2016.
 */
public class ErrorPopups {

    public static boolean shouldClose;
    public static boolean justOpen;

    private final OrthographicCamera camera;
    private final WorldCamera worldCamera;

    private SpriteBatch spriteBatch;
    private Sprite sprite;

    private int noOfTicksDisplay;
    private int maxTicks;

    private long startSeconds;

    public ErrorPopups(WorldCamera worldCamera, Sprite sprite) {
        justOpen = false;
        shouldClose = false;
        this.worldCamera = worldCamera;
        this.sprite = sprite;
        noOfTicksDisplay = 0;
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void showPopup(int noOfTicksDisplay) {

        justOpen = true;

        if (this.noOfTicksDisplay > 10) {
            System.err.println("ALREADY DISPLAYING POPUP");
        } else {
//            this.maxTicks = noOfTicksDisplay;
//            this.noOfTicksDisplay = noOfTicksDisplay;
            this.maxTicks = noOfTicksDisplay;
            this.noOfTicksDisplay = noOfTicksDisplay;
            this.startSeconds = System.currentTimeMillis();
        }
    }

    public void render() {

        if (shouldClose && noOfTicksDisplay > 100) { noOfTicksDisplay = 100; shouldClose = false; justOpen = false;}

        //if has not ran out of time
        if (noOfTicksDisplay != 0) {
            //decrement time left allowed on screen
//            noOfTicksDisplay--;
            long change = (System.currentTimeMillis() - startSeconds);

            noOfTicksDisplay = noOfTicksDisplay - ((int)change/10);

            startSeconds = System.currentTimeMillis();
            spriteBatch.begin();

            int width = Gdx.graphics.getWidth();
            int height = Gdx.graphics.getHeight();

            //zoom animation
            float imgWidth;
            float imgHeight;
            if (noOfTicksDisplay < 100) {
                justOpen = false;
                imgHeight = sprite.getHeight() / (100-noOfTicksDisplay);
                if (noOfTicksDisplay < 50) {
                    imgWidth = sprite.getWidth() / (50-noOfTicksDisplay);
                } else {
                    imgWidth = sprite.getWidth();
                }
            } else {
                //if first 50 ticks
                if (noOfTicksDisplay > (maxTicks - 25)) {
                    imgHeight = sprite.getHeight() / (25-(maxTicks-noOfTicksDisplay));
                    if (noOfTicksDisplay > (maxTicks - 12)) {
                        imgWidth = sprite.getWidth() / (12-(maxTicks-noOfTicksDisplay));
                    } else {
                        imgWidth = sprite.getWidth();
                    }
                } else {
                    imgWidth = sprite.getWidth();
                    imgHeight = sprite.getHeight();
                }
            }

            spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
            spriteBatch.draw(sprite, width / 2 - (imgWidth/2), height/2 - (imgHeight/2), imgWidth, imgHeight);
            spriteBatch.end();
        }
    }
}
