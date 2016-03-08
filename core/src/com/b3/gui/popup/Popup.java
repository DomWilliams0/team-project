package com.b3.gui.popup;

import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Creates a pop-up and displays it to the user for x amount of time (or next click)
 * Displayed in middle of the GUI (width / 2; height / 2)
 * Created by Nishanth on 02/03/2016.
 */
public class Popup {

    public static boolean shouldClose;
    public static boolean justOpen;

    private final OrthographicCamera camera;
    private final WorldCamera worldCamera;

    private SpriteBatch spriteBatch;
    private Sprite sprite;

    private int noOfTicksDisplay;
    private int maxTicks;

    private long startSeconds;

    /**
     * Creates a new pop-up in a closed positons (make this initally, and then call showPopup, takes time to load texture from file)
     * Note that render(); needs to be put in a current render method.
     * @param worldCamera the camera that this pop-up will be linked to
     * @param sprite the texture / image file that this pop-up will display.
     */
    public Popup(WorldCamera worldCamera, Sprite sprite) {
        justOpen = false;
        shouldClose = false;
        this.worldCamera = worldCamera;
        this.sprite = sprite;
        noOfTicksDisplay = 0;
        spriteBatch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Will show the pop-up for x amount of time
     * @param noOfTicksDisplay number of ticks that this display will be shown for, will not be affected by how many calls to render is happening, as it linked to seconds.
     */
    public void showPopup(int noOfTicksDisplay) {

        justOpen = true;

        if (this.noOfTicksDisplay > 10) {
            System.out.println("ALREADY DISPLAYING POPUP");
        } else {
            this.maxTicks = noOfTicksDisplay;
            this.noOfTicksDisplay = noOfTicksDisplay;
            this.startSeconds = System.currentTimeMillis();
        }
    }

    /**
     * Renders the pop-up if it should be displayed.
     * Calculates the time it should be open in via timeInMilliseconds so number of calls to this method will not matter
     * More calls to this method means smoother animation.
     */
    public void render() {

        if (shouldClose && noOfTicksDisplay > 50) { noOfTicksDisplay = 50; shouldClose = false; justOpen = false;}

        //if has not ran out of time
        if (noOfTicksDisplay != 0) {
            //decrement time left allowed on screen
            long change = (System.currentTimeMillis() - startSeconds);

            noOfTicksDisplay = noOfTicksDisplay - ((int)change/10);

            startSeconds = System.currentTimeMillis();
            spriteBatch.begin();

            int width = Gdx.graphics.getWidth();
            int height = Gdx.graphics.getHeight();

            //zoom animation
            float imgWidth;
            float imgHeight;
            if (noOfTicksDisplay < 50) {
                justOpen = false;
                imgHeight = sprite.getHeight() / (50-noOfTicksDisplay);
                if (noOfTicksDisplay < 25) {
                    imgWidth = sprite.getWidth() / (25-noOfTicksDisplay);
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
