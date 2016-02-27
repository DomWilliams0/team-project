package com.b3.gui;


import com.b3.gui.components.ButtonComponent;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RenderTester {

    private WorldCamera textCamera;
    private WorldGraph worldGraph;
    private WorldCamera worldCamera;
    private World world;
    private Sprite emptyCanvas;
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    private Sprite currentNodeSprite;
    private Sprite startNodeSprite;
    private Sprite fullyExploredSprite;
    private Sprite endNodeSprite;
    private Sprite lastFrontierSprite;
    private Sprite olderFrontierSprite;

    private Stage stage;

    public RenderTester (World world) {
        this.world = world;
        this.worldGraph = world.getWorldGraph();
        this.worldCamera = world.getWorldCamera();
        
        spriteBatch = new SpriteBatch();

        Texture tempTexture = new Texture("core/assets/world/popups/emptycanvas250x250.png");
        emptyCanvas = new Sprite(tempTexture);

        Vector2 cameraPos = new Vector2(world.getTileSize().scl(0.5f));
        textCamera = new WorldCamera(50, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        textCamera.position.set(cameraPos.x, cameraPos.y, worldCamera.getActualZoom() + 250);
        textCamera.near = 1f;
        textCamera.far = 300f;
        textCamera.lookAt(cameraPos.x, cameraPos.y, 10);
        textCamera.update();

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("core/assets/gui/default_bold.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 15;
        parameter.characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.!'()>?:";
        font = generator.generateFont(parameter);
        generator.dispose();

        loadTextures();

        setupButton();
    }

    private void setupButton() {
        stage = new Stage(new ScreenViewport());
        world.getInputHandler().addProcessor(stage);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
        Skin skin = new Skin(atlas);

        ButtonComponent playPause = new ButtonComponent(skin, font, "Show More");
        playPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("CLICKED");
                TextButton btnplaypause = playPause.getComponent();
                String text = btnplaypause.getText().toString();
                if(text.equals("Show More")) {
                    btnplaypause.setText("Show Less");
                } else if(text.equals("Show Less")){
                    btnplaypause.setText("Show More");
                }
            }
        });

        stage.addActor(playPause.getComponent());
    }

    private void loadTextures() {
        Texture tempTexture = new Texture("core/assets/world/popups/currentnode250x250.JPG.png");
        currentNodeSprite = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/startnode250x250.JPG.png");
        startNodeSprite = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/endnode250x250.JPG.png");
        endNodeSprite= new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/lastF250x250.JPG.png");
        lastFrontierSprite= new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/oldF250x250.JPG.png");
        olderFrontierSprite= new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/fullyExplored250x250.JPG.png");
        fullyExploredSprite= new Sprite(tempTexture);
    }


    public void render(int currentNodeClickX, int currentNodeClickY) {
        stage.draw();
        stage.act();
        stage.getViewport().update((int) worldCamera.viewportWidth,(int) worldCamera.viewportHeight, true);

//      stage.getViewport().setScreenSize(1000,1000); Doesn't work
//      stage.getViewport().update(5000,5000); Scales everything, need to just scale button

        float scalingZoom = (float) (worldCamera.getActualZoom() / 4.5);

        //SPRITES
        spriteBatch.setProjectionMatrix(worldCamera.combined);
        spriteBatch.begin();

        //----ALL RENDERS GO HERE---
        //if start node
        if (worldGraph.getCurrentSearch().getStart().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
            spriteBatch.draw(startNodeSprite, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                    (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        } else
            //if end node
            if (worldGraph.getCurrentSearch().getEnd().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
                spriteBatch.draw(endNodeSprite, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                        (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
            } else
                //if recently expanded
                if (worldGraph.getCurrentSearch().getMostRecentlyExpanded() != null)
                    if (worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
                        //TODO Put some info around the screen somewhere (use cost function below somewhere)

                        System.out.println(worldGraph.getCurrentSearch().getG(worldGraph.getCurrentSearch().getMostRecentlyExpanded()));

                        spriteBatch.draw(currentNodeSprite, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                    } else
                        //if JUST added to stack / queue
                        if (worldGraph.getCurrentSearch().getLastFrontier() != null)
                            if (worldGraph.getCurrentSearch().getLastFrontier().contains(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                //TODO Put some info around the screen somewhere and put costs somewhere (use cost variable below)
                                Node expanded = worldGraph.getCurrentSearch().getMostRecentlyExpanded();
                                float cost = expanded.getEdgeCost(new Node(new Point(currentNodeClickX, currentNodeClickY)));
                                spriteBatch.draw(lastFrontierSprite, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                        (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                            } else if (worldGraph.getCurrentSearch().getFrontier().contains(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                //TODO Put some info around the screen somewhere
                                spriteBatch.draw(olderFrontierSprite, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                        (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                            } else
                                //if already expanded
                                if (worldGraph.getCurrentSearch().getVisited() != null)
                                    if (worldGraph.getCurrentSearch().getVisited().contains(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                        //TODO Put some info around the screen somewhere
                                        spriteBatch.draw(fullyExploredSprite, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                                (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                                    }
        //----ALL RENDERS GO HERE---
        spriteBatch.end();

//        //FONTS
//        font.getData().setScale((float) 0.5);
//        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//
//        float zoomScaler = 22 - worldCamera.getActualZoom();
//
//        spriteBatch.setProjectionMatrix(textCamera.combined);
//        spriteBatch.begin();
//
//        //----ALL FONTS GO HERE---
//        if (worldGraph.getCurrentSearch().getStart().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
//            font.draw(spriteBatch,"Hello, my name is Nishanth and this is A* search", (float) (currentNodeClickX-19) - (worldCamera.getPosX() * (12)) + (zoomScaler) / 10,  (currentNodeClickY+60) - (worldCamera.getPosY() * 12), 55, Align.center, true);
//        }
//        //----ALL FONTS GO HERE---
//
//        spriteBatch.end();
    }
}
