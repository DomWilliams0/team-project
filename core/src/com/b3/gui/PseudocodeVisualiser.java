package com.b3.gui;

import com.b3.search.Pseudocode;
import com.b3.util.Font;
import com.b3.util.Tuple;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.Observable;
import java.util.Observer;

public class PseudocodeVisualiser extends Table implements Observer {

    private static PseudocodeVisualiser instance;

    private BitmapFont font;
    private Pixmap pixmap;

    private PseudocodeVisualiser() {}

    private PseudocodeVisualiser(Skin skin) {
        super(skin);

        //this.stage = stage;
        //this.skin = skin;
        this.font = Font.getFont("monaco.ttf", 14);

        this.pixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
        this.pixmap.setColor(Color.LIME);
        this.pixmap.fill();
    }

    public static PseudocodeVisualiser getInstance() {
        if (instance == null)
            instance = new PseudocodeVisualiser();
        return instance;
    }

    public static PseudocodeVisualiser getInstance(Skin skin) {
        if (instance == null)
            instance = new PseudocodeVisualiser(skin);
        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {
        Pseudocode pseudocode = (Pseudocode)o;

        clear();
        for (Tuple<String, Tuple<Boolean, Integer>> line : pseudocode.getLines()) {
            // Set label
            Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
            labelStyle.background = line.getSecond().getFirst() ?
                    new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) :
                    null;

            // Add line to table
            Label actor = new Label(line.getFirst(), labelStyle);
            add(actor).align(Align.left).padLeft(line.getSecond().getSecond() * 20);
            row();
        }
    }
}