package com.b3.gui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * A small sidebar that contains a limited number of buttons
 *
 * @author oxe410
 */
public class MenuComponent extends Table {

    private final Sprite backgroundTexture;
    private SpriteBatch spriteBatch;

    private float height;

    /**
     * Creates a new menu component, of default height: 50 pixels
     */
    public MenuComponent() {
        this(50);
    }

    /**
     * Creates a new menu component, of a specific height
     * @param height the height in pixels of the meny component
     */
    public MenuComponent(float height) {
        this.height = height;
        backgroundTexture = new Sprite(new Texture("world/popups/emptycanvas250x250.png"));
        spriteBatch = new SpriteBatch();
        init();
    }

    /**
     * Renders the background of the menu component
     */
    public void render() {
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, (float) -19, Gdx.graphics.getHeight() - backgroundTexture.getHeight() + 75, 350, 100);
        spriteBatch.end();
    }

    /**
     * Initialises the components
     */
    private void init() {
        setPosition(0, Gdx.graphics.getHeight() - height);
        setSize(Gdx.graphics.getWidth(), height);
        align(Align.left);
    }

    /**
     * Add a component to the menu component, to the right of the current components in the menu component
     * @param item the {@link MenuItemComponent} that is to be added to the Menu Component
     */
    public void addItem(MenuItemComponent item) {
        add(item.getComponent()).spaceLeft(10).pad(5, 10, 5, 10);
    }

    /**
     * Resize the components to:
     * avoid stretching
     * maintain relative position
     * @param width the current width of the program
     * @param height the current height of the program
     */
    public void resize(int width, int height) {
        spriteBatch = new SpriteBatch();
        setPosition(0, height - this.height);
        setWidth(width);
        setHeight(this.height);
    }
}
