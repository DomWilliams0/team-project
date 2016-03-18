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

public class MenuComponent extends Table {

    private final Sprite backgroundTexture;
    private SpriteBatch spriteBatch;

    private float height;

    public MenuComponent() {
        this(50);
    }

    public MenuComponent(float height) {
        this.height = height;
        backgroundTexture = new Sprite(new Texture("world/popups/emptycanvas250x250.png"));
        spriteBatch = new SpriteBatch();
        init();
    }

    /**
     * Set the background colour of this menu
     * @param r Red colour component
     * @param g Green colour component
     * @param b Blue colour component
     * @param a Alpha component
     */
    private void setBackgroundColor(float r, float g, float b, float a) {
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(r, g, b, a);
        pm1.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
    }

    public void render() {
        spriteBatch.begin();
        spriteBatch.draw(backgroundTexture, (float) -19, Gdx.graphics.getHeight() - backgroundTexture.getHeight() + 75, 350, 100);
        spriteBatch.end();
    }

    private void init() {
//        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

        setPosition(0, Gdx.graphics.getHeight() - height);
        setSize(Gdx.graphics.getWidth(), height);
        align(Align.left);
    }

    public void addItem(MenuItemComponent item) {
        add(item.getComponent()).spaceLeft(10).pad(5, 10, 5, 10);
    }

    public void resize(int width, int height) {
        spriteBatch = new SpriteBatch();
        setPosition(0, height - this.height);
        setWidth(width);
        setHeight(this.height);
    }
}
