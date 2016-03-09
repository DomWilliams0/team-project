package com.b3.gui.help;

import com.b3.gui.components.ButtonComponent;
import com.b3.mode.Mode;
import com.b3.mode.ModeType;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Provides help for the user based on the mode of the simulation.
 * Gives instructions on how the mode can be used and interacted with
 * And a legend to explain meaning of colours.
 *
 * Created by Ben, worked on mostly by Lewis
 */
public class HelpBox extends Table {

    private Stage stage;
    private ModeType mode;
    private ButtonComponent triggerBtn;
    private boolean isOpen;
    private float preferredHeight;
	private final String padding = "   ";

    public HelpBox(Stage stage, ModeType mode) {
        this(stage, mode, 400);
    }

    public HelpBox(Stage stage, ModeType mode, float preferredHeight) {
        this.stage = stage;
        this.mode = mode;
        this.isOpen = false;
        this.preferredHeight = preferredHeight;

        initComponents();
		bottom();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void setBackgroundColor(float r, float g, float b, float a) {
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(r, g, b, a);
        pm1.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
    }

    private void initComponents() {
		// ===============
		// === STYLING ===
		// ===============

        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("core/assets/gui/ui-blue.atlas"));
        Skin skin = new Skin(atlas);
		BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);
		skin.add("default", font, BitmapFont.class);
		Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);
		skin.add("default", style);

		this.setSkin(skin);

		// =================
		// === HELP TEXT ===
		// =================

		switch(mode) {
			case LEARNING : 	setupLM(); break;
			case COMPARE : 		setupCM(); break;
			case TRY_YOURSELF :	setupTY(); break;
		}

		row();
		row();
		row();

		// ==============
		// === LEGEND ===
		// ==============

		add(new Legend(this.getSkin())).left();

        // ======================
        // === TRIGGER BUTTON ===
        // ======================

        triggerBtn = new ButtonComponent(skin, font, "HELP");
        triggerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
				isOpen = !isOpen;
                resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        });

        background(skin.getDrawable("window_03"));
        this.stage.addActor(triggerBtn.getComponent());
    }

    /**
     * Resize this menu
     * Should be called whenever the window is resized.
     *
     * @param width Window width
     * @param height Window height
     */
    public void resize(int width, int height) {
		setX(0);
		setSize(width, preferredHeight);

        TextButton _triggerBtn = triggerBtn.getComponent();

		triggerBtn.getComponent().setX((width / 2) - 31);

		if (isOpen) {
			setY(height - preferredHeight);
			_triggerBtn.setText("CLOSE");
			_triggerBtn.setY(height - preferredHeight - 34);
		} else {
			setY(Gdx.graphics.getHeight() + preferredHeight);
			_triggerBtn.setText("HELP");
			_triggerBtn.setY(height - 34);
		}
	}

	private void setupLM() {
		add("Interacting with the world:");
		add(padding);
		add("Visualisation sidebar (right):");
		add(padding);
		add("Settings sidebar (left):");

		row();

		add("Click nodes to view details and highlight\nit in the Visualisation sidebar.\n" +
				"Click again to view more details about it.\n" +
				"Hover over a node to display its coordinates.\n" +
				"Right click a node to set\nit as the next destination.");
		add(padding);
		add("Click nodes to display details over it in the world.\n" +
				"Hover over a node to highlight it briefly in the world.\n" +
				"You can step through the algorithm\nwith the Next Step button when paused.");
		add(padding);
		add("");

	}

	private void setupCM() {

	}

	private void setupTY() {

	}

}
