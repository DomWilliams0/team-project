package com.b3.gui.help;

import com.b3.gui.components.ButtonComponent;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class HelpBox extends Table {

    private Stage stage;
    private World world;
    private ButtonComponent triggerBtn;
    private boolean isOpen;
    private float preferredHeight;

    public HelpBox(Stage stage, World world) {
        this(stage, world, 400);
    }

    public HelpBox(Stage stage, World world, float preferredHeight) {
        this.stage = stage;
        this.world = world;
        this.isOpen = false;
        this.preferredHeight = preferredHeight;

        initComponents();
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
        BitmapFont font = new BitmapFont(Gdx.files.internal("core/assets/gui/default.fnt"),
                Gdx.files.internal("core/assets/gui/default.png"), false);

		// =================
		// === HELP TEXT ===
		// =================

		// TODO - Implement.

		// ==============
		// === LEGEND ===
		// ==============



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

}
