package com.b3.gui;

import com.b3.search.Node;
import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.b3.search.util.takeable.StackT;
import com.b3.search.util.takeable.Takeable;
import com.b3.world.World;
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
import com.badlogic.gdx.utils.Disposable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by lewis on 15/02/16.
 */
public class SideBarNodes extends Table implements Disposable {
    private Stage stage;
    private ButtonComponent triggerBtn;
    private VisNodes ui;
    private World world;
    private boolean isOpen;
    private float preferredWidth;

    public SideBarNodes(Stage stage) {
        this(stage, 230);
    }

    public SideBarNodes(Stage stage, float preferredWidth) {
        this.stage = stage;
        this.isOpen = false;
        this.preferredWidth = preferredWidth;

        setPosition(Gdx.graphics.getWidth(), 0);
        setSize(preferredWidth, Gdx.graphics.getHeight());

        initComponents();
    }

    public void setWorld(World world) {
        this.world = world;
    }

    private void setBackgroundColor(float r, float g, float b, float a) {
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(r, g, b, a);
        pm1.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
    }

    private void initComponents() {
        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("core/assets/gui/ui-blue.atlas"));
        Skin skin = new Skin(atlas);
//        skin.addRegions(new TextureAtlas(Gdx.files.internal("core/assets/gui/uiscrollskin.atlas")));
        BitmapFont font = new BitmapFont(Gdx.files.internal("core/assets/gui/default.fnt"),
                Gdx.files.internal("core/assets/gui/default.png"), false);
        skin.add("default", font, BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", style);

         ui = new VisNodes(stage, skin);

        // ======================
        // === TRIGGER BUTTON ===
        // ======================

        triggerBtn = new ButtonComponent(skin, font, "<");
        triggerBtn.getTextButton().setPosition(Gdx.graphics.getWidth() - triggerBtn.getTextButton().getWidth() + 20, Gdx.graphics.getHeight() / 2);
        triggerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton _triggerBtn = triggerBtn.getTextButton();

                if (!isOpen) {
                    setX(Gdx.graphics.getWidth()- preferredWidth);
                    setY(0);
                    _triggerBtn.setText(">");
                    _triggerBtn.setX(Gdx.graphics.getWidth() - preferredWidth - _triggerBtn.getWidth() + 20);

                    isOpen = true;
                }
                else {
                    setX(Gdx.graphics.getWidth());
                    _triggerBtn.setText("<");
                    _triggerBtn.setX(Gdx.graphics.getWidth() - _triggerBtn.getWidth() + 20);

                    isOpen = false;
                }

            }
        });

        add(ui).maxWidth(preferredWidth);
        background(skin.getDrawable("window_03"));
        this.stage.addActor(triggerBtn.getTextButton());

    }

    public void act() {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    public void render() {
        setHeight(Gdx.graphics.getHeight());
        triggerBtn.getTextButton().setY(Gdx.graphics.getHeight() / 2);
        //FOR TESTING
        SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
        if(currentSearch == null || currentSearch.getFrontier() == null || currentSearch.getVisited() == null) {
            //todo should probably handle this occurrence inside ui.render
            ui.render(new StackT<>(), new HashSet<>(), SearchAlgorithm.BREADTH_FIRST);
        } else {
            ui.render(currentSearch.getFrontier(), currentSearch.getVisited(), currentSearch.getAlgorithm());
        }
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }


}
