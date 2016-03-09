package com.b3.gui;

import com.b3.search.Pseudocode;
import com.b3.util.Tuple;
import com.b3.util.Utils;
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

    public static PseudocodeVisualiser getInstance() {
        if (instance == null)
            instance = new PseudocodeVisualiser();
        return instance;
    }

    public static PseudocodeVisualiser getInstance(Skin skin) {
        if (instance == null) {
            instance = new PseudocodeVisualiser();
        }

        instance.setSkin(skin);
        instance.font = Utils.getFont("monaco.ttf", 14);
        instance.font.getData().markupEnabled = true;
        instance.pixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
        instance.pixmap.setColor(Color.LIME);
        instance.pixmap.fill();

        return instance;
    }

    @Override
    public void update(Observable o, Object arg) {
        Pseudocode pseudocode = (Pseudocode)o;

        clear();
        for (Tuple<String, Tuple<Boolean, Integer>> line : pseudocode.getLines()) {
            // Set label
            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = font;
            labelStyle.background = line.getSecond().getFirst() ?
                    new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) :
                    null;

            // Add line to table
            Label actor = new Label(line.getFirst(), labelStyle);
            add(actor).align(Align.left).padLeft(line.getSecond().getSecond() * 20);
            row().align(Align.left).fill();
        }
    }
}
