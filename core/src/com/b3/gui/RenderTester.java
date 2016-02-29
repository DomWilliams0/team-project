package com.b3.gui;


import com.b3.gui.components.ButtonComponent;
import com.b3.search.Node;
import com.b3.search.Point;
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
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.reverse;
import static java.util.Collections.sort;

public class RenderTester {

    private WorldCamera textCamera;
    private WorldGraph worldGraph;
    private WorldCamera worldCamera;
    private World world;
    private Sprite emptyCanvas;
    private SpriteBatch spriteBatch;

    private BitmapFont fontButton;
    private BitmapFont fontAStar;

    private Sprite[] currentNodeSprite;
    private Sprite[] startNodeSprite;
    private Sprite[] fullyExploredSprite;
    private Sprite[] endNodeSprite;
    private Sprite[] lastFrontierSprite;
    private Sprite[] olderFrontierSprite;
    private Sprite[] numbers;

    private Stage stage;
    private int pageNo;
    private Sprite plus;
    private Sprite equals;

    public RenderTester(World world) {
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

        fontButton = generator.generateFont(parameter);
        parameter.size = 30;
        fontAStar = generator.generateFont(parameter);

        generator.dispose();

        pageNo = 0;

        loadTextures();

        setupButton();
    }

    private void setupButton() {
        stage = new Stage(new ScreenViewport());
        world.getInputHandler().addProcessor(stage);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
        Skin skin = new Skin(atlas);

        ButtonComponent playPause = new ButtonComponent(skin, fontButton, "Show More");
        playPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pageNo = pageNo + 1;
            }
        });

        stage.addActor(playPause.getComponent());
    }

    private void loadTextures() {
        //Load current node sprites (2 pages + 1 (dfs, bfs or A*)) + costs A*
        currentNodeSprite = new Sprite[6];
        Texture tempTexture = new Texture("core/assets/world/popups/currentnode250x250.JPG.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        currentNodeSprite[0] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/currentnode250x250.JPG_2.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        currentNodeSprite[1] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/currentnode250x250.JPG_3-DFS.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        currentNodeSprite[2] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/currentnode250x250.JPG_3-BFS.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        currentNodeSprite[3] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/currentnode250x250.JPG_3-A_STAR.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        currentNodeSprite[4] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/currentnode250x250.JPG_3-A_STAR2.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        currentNodeSprite[5] = new Sprite(tempTexture);

        //Load start node sprites (2 pages)
        startNodeSprite = new Sprite[2];
        tempTexture = new Texture("core/assets/world/popups/startnode250x250.JPG.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        startNodeSprite[0] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/startnode250x250.JPG_2.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        startNodeSprite[1] = new Sprite(tempTexture);

        //Load end node sprites (2 pages)
        endNodeSprite = new Sprite[2];
        tempTexture = new Texture("core/assets/world/popups/endnode250x250.JPG.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        endNodeSprite[0] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/endnode250x250.JPG_2.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        endNodeSprite[1] = new Sprite(tempTexture);

        //Aqua nodes - just added to frontire
        lastFrontierSprite = new Sprite[4];
        tempTexture = new Texture("core/assets/world/popups/lastF250x250.JPG.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lastFrontierSprite[0] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/lastF250x250.JPG-DFS.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lastFrontierSprite[1] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/lastF250x250.JPG-BFS.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lastFrontierSprite[2] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/lastF250x250.JPG-ASTAR.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        lastFrontierSprite[3] = new Sprite(tempTexture);

        //green nodes - in frontire but not expanded yet
        olderFrontierSprite = new Sprite[4];
        tempTexture = new Texture("core/assets/world/popups/oldF250x250.JPG.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        olderFrontierSprite[0] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/oldF250x250.JPG-DFS.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        olderFrontierSprite[1] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/oldF250x250.JPG-BFS.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        olderFrontierSprite[2] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/oldF250x250.JPG-ASTAR.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        olderFrontierSprite[3] = new Sprite(tempTexture);

        //fully explored (grey colours; two pages)
        fullyExploredSprite = new Sprite[2];
        tempTexture = new Texture("core/assets/world/popups/fullyExplored250x250.JPG.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fullyExploredSprite[0] = new Sprite(tempTexture);
        tempTexture = new Texture("core/assets/world/popups/fullyExplored250x250.JPG_2.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        fullyExploredSprite[1] = new Sprite(tempTexture);

        numbers = new Sprite[10];
        //Load Numbers
        for (int i = 0; i < 9; i++) {
            tempTexture = new Texture("core/assets/world/popups/Numbers/"+i+".png");
            tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            numbers[i] = new Sprite(tempTexture);
        }

        tempTexture = new Texture("core/assets/world/popups/Numbers/plus.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        plus = new Sprite(tempTexture);

        tempTexture = new Texture("core/assets/world/popups/Numbers/equals.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        equals = new Sprite(tempTexture);

    }

    public void render(int currentNodeClickX, int currentNodeClickY) {
        stage.draw();
        stage.act();
        stage.getViewport().update((int) worldCamera.viewportWidth, (int) worldCamera.viewportHeight, true);

        float scalingZoom = (float) (worldCamera.getActualZoom() / 4.5);

        //SPRITES
        spriteBatch.setProjectionMatrix(worldCamera.combined);
        spriteBatch.begin();

        //----ALL RENDERS GO HERE---
        //DONE MULTI-PAGES if start node
        if (worldGraph.getCurrentSearch().getStart().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
            if (pageNo >= startNodeSprite.length) pageNo = 0; //reset to first page
            spriteBatch.draw(startNodeSprite[pageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        } else
            //DONE MULTI_PAGES if end node
            if (worldGraph.getCurrentSearch().getEnd().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
                if (pageNo >= endNodeSprite.length) pageNo = 0; //reset to first page if neccessary

                drawHeuristic();

                spriteBatch.draw(endNodeSprite[pageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                        (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
            } else
                //DONE MULTi-PAGES, needs cost breakdown though if recently expanded
                if (worldGraph.getCurrentSearch().getMostRecentlyExpanded() != null)
                    if (worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
                        //TODO Put some info around the screen somewhere (use cost function below somewhere)
                        if (pageNo >= 4) pageNo = 0; //reset to first page if neccessary
                        float gxFunction = worldGraph.getCurrentSearch().getG(worldGraph.getCurrentSearch().getMostRecentlyExpanded());

                        int convertedPageNo = pageNo;
                        if (convertedPageNo == 2)
                            switch (world.getWorldGraph().getCurrentSearch().getAlgorithm()) {
                                case A_STAR: convertedPageNo = 4;
                                    break;
                                case BREADTH_FIRST: convertedPageNo = 3;
                                    break;
                                case DEPTH_FIRST: convertedPageNo = 2;
                                    break;
                            }

                        if (pageNo != 3)
                            spriteBatch.draw(currentNodeSprite[convertedPageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);

                        //draw current g(x) function onto the screen.
                        if (pageNo == 2) {
                            drawNumberOnScreen((int) gxFunction, currentNodeClickX, currentNodeClickY + (scalingZoom / 17), scalingZoom);
                        }
                        if (pageNo == 3) {
                            spriteBatch.draw(currentNodeSprite[5], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                            List<Node> currentPath = worldGraph.getCurrentSearch().getPath();
                            ArrayList<Integer> arrCostsCurrentSerach = getCostsAllNodes(currentPath);
                            for (int i = 0; i < currentPath.size(); i++) {
                                if (arrCostsCurrentSerach.size() > 0) {
                                    drawCostOnScreen(arrCostsCurrentSerach.get(0), currentPath.get(i).getPoint(), currentPath.get(i+1).getPoint(), scalingZoom);
                                    arrCostsCurrentSerach.remove(0);
                                }
                            }
                        }

                    } else
                        //DONE MULTI PAGE if JUST added to stack / queue
                        if (worldGraph.getCurrentSearch().getLastFrontier() != null)
                            if (worldGraph.getCurrentSearch().getLastFrontier().contains(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                if (pageNo >= 2) pageNo = 0; //reset to first page if neccessary

                                int convertedPageNo = pageNo;
                                if (convertedPageNo == 1)
                                    switch (world.getWorldGraph().getCurrentSearch().getAlgorithm()) {
                                        case A_STAR: convertedPageNo = 3;
                                            break;
                                        case BREADTH_FIRST: convertedPageNo = 2;
                                            break;
                                        case DEPTH_FIRST: convertedPageNo = 1;
                                            break;
                                    }

                                spriteBatch.draw(lastFrontierSprite[convertedPageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                        (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);

                                float gxFunction = worldGraph.getCurrentSearch().getG(worldGraph.getCurrentSearch().getMostRecentlyExpanded());
                                float cost = worldGraph.getCurrentSearch().getMostRecentlyExpanded().getEdgeCost(new Node(new Point(currentNodeClickX, currentNodeClickY)));
                                float total = cost + gxFunction;

                                if (pageNo == 1)
                                    drawEquationOnScreen((int)total, (int)gxFunction, (int)cost, currentNodeClickX, currentNodeClickY + (scalingZoom/50), scalingZoom);

                            } else if (worldGraph.getCurrentSearch().getFrontier().contains(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                //DONE MULTIPAGE if the old frontier
                                if (pageNo >= 2) pageNo = 0; //reset to first page if neccessary

                                int convertedPageNo = pageNo;
                                if (convertedPageNo == 1)
                                    switch (world.getWorldGraph().getCurrentSearch().getAlgorithm()) {
                                        case A_STAR: convertedPageNo = 3;
                                            break;
                                        case BREADTH_FIRST: convertedPageNo = 2;
                                            break;
                                        case DEPTH_FIRST: convertedPageNo = 1;
                                            break;
                                    }

                                spriteBatch.draw(olderFrontierSprite[convertedPageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                        (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);

                                float gxFunction = -1;

                                Object[] arrFront = worldGraph.getCurrentSearch().getFrontier().toArray();
                                for (int i = 0; i < arrFront.length; i++) {
                                    Node node = (Node) arrFront[i];
                                    if (node.equals(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                        gxFunction = worldGraph.getCurrentSearch().getG(node);
                                    }
                                }

                                if (pageNo == 1)
                                    drawNumberOnScreen((int) gxFunction, currentNodeClickX, (float) currentNodeClickY + (scalingZoom / 11), scalingZoom);
                            } else
                                //MULTI PAGE DONE if already expanded
                                if (worldGraph.getCurrentSearch().getVisited() != null)
                                    if (worldGraph.getCurrentSearch().getVisited().contains(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                        if (pageNo >= fullyExploredSprite.length) pageNo = 0; //reset to first page if neccessary
                                        //TODO Put some info around the screen somewhere
                                        spriteBatch.draw(fullyExploredSprite[pageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                                (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                                    }
        //----ALL RENDERS GO HERE---

        spriteBatch.end();
    }

    private ArrayList<Integer> getCostsAllNodes(List<Node> path) {
        ArrayList<Integer> arrTempCosts = new ArrayList<Integer>();
        if (path.isEmpty()) return arrTempCosts;

        for (int i = path.size()-1; i > 0; i--) {
            Node pointOne = path.get(i);
            Node pointTwo = path.get(i-1);

            float costBetweenOneTwo = pointOne.getEdgeCost(pointTwo);

            arrTempCosts.add((int) costBetweenOneTwo);
        }
        return arrTempCosts;
    }

    private void drawHeuristic() {

    }

    private void drawEquationOnScreen(int total, int firstNo, int secondNo, float currentNodeClickX, float currentNodeClickY, float scalingZoom) {
        drawNumberOnScreen(total, currentNodeClickX-(scalingZoom/10), currentNodeClickY, scalingZoom);
        spriteBatch.draw(equals, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        drawNumberOnScreen(firstNo, currentNodeClickX+(scalingZoom/10), currentNodeClickY, scalingZoom);
        spriteBatch.draw(plus, (float) ((currentNodeClickX - scalingZoom / 3.5) + 0.5) , (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        drawNumberOnScreen(secondNo, currentNodeClickX+3*(scalingZoom/10), currentNodeClickY, scalingZoom);
    }

    private void drawCostOnScreen(int cost, Point one, Point two, float scalingZoom) {
        //change in x
        if (one.getY() == two.getY()) {
            float x = (float) (one.getX() + two.getX()) / 2;
            float y = one.getY();
            drawStaticNumberOnScreen(cost, x, y, scalingZoom);
        } else
        //change in y
        if (one.getX() == two.getX()) {
            float x = one.getX();
            float y = (float) (one.getY() + two.getY()) / 2;
            drawStaticNumberOnScreen(cost, x, y, scalingZoom);
        }
    }

    private void drawStaticNumberOnScreen(int number, float currentNodeClickX, float currentNodeClickY, float scalingZoom) {
        //if single digits
        if (number > 100 || number < 0) {
            number = 99;
            System.err.println("Currently, drawNumberOnScreen only works with < 100 numbers; using this instead: " + number);
        }

//        System.out.println("Keep this here, bug somewhere and I don't know where so this is the number it's trying to print just in case it does crash: go onto intensive learning mode and click on pop-ups / show more and try to get numbers to show and try and break it " + number);

        if (number < 10) {
            spriteBatch.draw(numbers[number], (float) (currentNodeClickX - scalingZoom / 2 + 0.5 ), (float) (currentNodeClickY - scalingZoom / 2 + 0.25), scalingZoom, scalingZoom);
        } else {
            //if not
            int firstNo = number / 10;
            int secondNo = number % 10;
            float x1 = (float) (currentNodeClickX - scalingZoom / 2 + 0.5);
            float x2 = x1 + (scalingZoom / 20);

            spriteBatch.draw(numbers[firstNo], x1, (float) (currentNodeClickY - scalingZoom / 2 + 0.25), scalingZoom, scalingZoom);
            spriteBatch.draw(numbers[secondNo], x2, (float) (currentNodeClickY - scalingZoom / 2 + 0.25), scalingZoom, scalingZoom);
        }
    }

    private void drawNumberOnScreen(int number, float currentNodeClickX, float currentNodeClickY, float scalingZoom) {
        //if single digits
        if (number > 100 || number < 0) {
            number = 99;
            System.err.println("Currently, drawNumberOnScreen only works with < 100 numbers; using this instead: " + number);
        }

//        System.out.println("Keep this here, bug somewhere and I don't know where so this is the number it's trying to print just in case it does crash: " + number);

        if (number < 10) {
            spriteBatch.draw(numbers[number], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        } else {
        //if not
            int firstNo = number / 10;
            int secondNo = number % 10;
            float x1 = (float) ((currentNodeClickX - scalingZoom / 2) + 0.5);
            float x2 = (float) ((currentNodeClickX - scalingZoom / 2) + 0.5) + (scalingZoom / 20);

            spriteBatch.draw(numbers[firstNo], x1, (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
            spriteBatch.draw(numbers[secondNo], x2, (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        }
    }
}
