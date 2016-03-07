package com.b3.gui;

import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.sun.corba.se.impl.naming.cosnaming.NamingUtils;
import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.copy;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;

/**
 * A pop-up visual that explains what each node is
 * Including drawing the heurstic and explaining to the user where the costs come from.
 */

public class RenderTester {

    private ShapeRenderer shapeRenderer;

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
    private boolean popupShowing;
    private int counterAnimationFade;
    private boolean stickyCurrentNode;
    private Sprite endNodeDFSBFS;

    /**
     * Creates a new pop-up, with an empty canas
     * @param world the world that this pop-up will be shown on top of
     */
    public RenderTester(World world) {
        counterAnimationFade = 0;
        popupShowing = false;

        this.world = world;
        this.worldGraph = world.getWorldGraph();
        this.worldCamera = world.getWorldCamera();

        spriteBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        Texture tempTexture = new Texture("core/assets/world/popups/emptycanvas250x250.png");
        emptyCanvas = new Sprite(tempTexture);

        pageNo = 0;

        loadTextures();
    }

    /**
     * Load all of the textures that will be used in render tester - faster to load them now rather than when user clicks on it.
     */
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

        tempTexture = new Texture("core/assets/world/popups/endnode250x250DFSBFS.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        endNodeDFSBFS = new Sprite(tempTexture);

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
        for (int i = 0; i <= 9; i++) {
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

    /**
     * Using the current click of the user's mouse, this will draw the respective pop-up on the screen.
     * Will change the page if user clicks on the same node twice
     * Will reset the page counter if the user clicks on a different node.
     * @param currentNodeClickX the x position (on the nodes graph / worldGraph) that the user last clicked one
     * @param currentNodeClickY the y position (on the nodes graph / worldGraph) that the user last clicked one
     */
    public void render(int currentNodeClickX, int currentNodeClickY) {
        counterAnimationFade++;
        popupShowing = false;
        float scalingZoom = (float) (worldCamera.getActualZoom() / 4.5);

        //SPRITES
        spriteBatch.setProjectionMatrix(worldCamera.combined);
        spriteBatch.begin();

        if (stickyCurrentNode && world.getWorldGraph().getCurrentSearch().isPaused()) {
            currentNodeClickX = worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().getX();
            currentNodeClickY = worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().getY();
            world.setCurrentClick(currentNodeClickX, currentNodeClickY);
        }

        //----ALL RENDERS GO HERE---
        //DONE MULTI-PAGES if start node
        if (worldGraph.getCurrentSearch().getStart().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
            if (pageNo >= startNodeSprite.length) pageNo = 0; //reset to first page
            spriteBatch.draw(startNodeSprite[pageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
            popupShowing = true;
        } else
            //DONE MULTI_PAGES if end node
            if (worldGraph.getCurrentSearch().getEnd().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
                if (pageNo >= 3 && worldGraph.getCurrentSearch().getAlgorithm() == SearchAlgorithm.A_STAR) pageNo = 0; //reset to first page if neccessary
                if (pageNo >= 2 && worldGraph.getCurrentSearch().getAlgorithm() != SearchAlgorithm.A_STAR) pageNo = 0; //reset to first page if neccessary

                if (pageNo == 2) {
                    //calculate data needed
                    float x1;
                    float y1;
                    if (worldGraph.getCurrentSearch().getMostRecentlyExpanded() != null) {
                        x1 = (float) (worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().getX() + 0.5);
                        y1 = (float) (worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().getY() + 0.5);
                    } else {
                        x1 = (float) (worldGraph.getCurrentSearch().getStart().getPoint().getX() + 0.5);
                        y1 = (float) (worldGraph.getCurrentSearch().getStart().getPoint().getY() + 0.5);
                    }

                    float x2 = (float) (worldGraph.getCurrentSearch().getEnd().getPoint().getX() + 0.5); float y2 = (float) (worldGraph.getCurrentSearch().getEnd().getPoint().getY() + 0.5);
                    float xCoord = (int) (x1 + x2) / 2; float yCoord = (int) (y1 + y2) / 2;
                    float cost;
                    if (worldGraph.getCurrentSearch().getMostRecentlyExpanded() != null) {
                        cost = calculateEuclidian(worldGraph.getCurrentSearch().getMostRecentlyExpanded(), worldGraph.getCurrentSearch().getEnd());
                    } else {
                        cost = calculateEuclidian(worldGraph.getCurrentSearch().getStart(), worldGraph.getCurrentSearch().getEnd());
                    }

                    spriteBatch.end();
                    //draw lines
                    drawHeuristic(cost, currentNodeClickX, currentNodeClickY, scalingZoom);
                    spriteBatch.begin();
                    spriteBatch.setProjectionMatrix(worldCamera.combined);
                    //draw actual g(x)
                    drawStaticNumberOnScreen((int) cost, xCoord, yCoord, scalingZoom);

                    //draw y cost
                    xCoord = x1;
                    yCoord = (y1 + y2) / 2;
                    cost = Math.abs(y1 - y2);
                    drawStaticNumberOnScreen((int) cost, xCoord, yCoord, scalingZoom);

                    //draw x cost
                    xCoord = (x1 + x2) / 2;
                    yCoord = y2;
                    cost = Math.abs(x1 - x2);
                    drawStaticNumberOnScreen((int) cost, xCoord, yCoord, scalingZoom);

                    spriteBatch.end();
                    spriteBatch.begin();
                    spriteBatch.setProjectionMatrix(worldCamera.combined);
                } else {
                    if (worldGraph.getCurrentSearch().getAlgorithm() == SearchAlgorithm.A_STAR)
                        spriteBatch.draw(endNodeSprite[pageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                            (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                    else
                        if (pageNo == 0)
                            spriteBatch.draw(endNodeDFSBFS, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
                        else
                            spriteBatch.draw(endNodeSprite[pageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                    (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);

                }

                popupShowing = true;

            } else
                //DONE MULTi-PAGES, needs cost breakdown though if recently expanded - current node being explored
                if (worldGraph.getCurrentSearch().getMostRecentlyExpanded() != null)
                    if (worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().equals(new Point(currentNodeClickX, currentNodeClickY))) {
                        stickyCurrentNode = true;
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

                        //show normal pop-ups (first 2 pages)
                        if (pageNo != 3)
                            spriteBatch.draw(currentNodeSprite[convertedPageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);

                        //draw current g(x) function onto the screen (3rd page)
                        if (pageNo == 2 && world.getWorldGraph().getCurrentSearch().getAlgorithm() == SearchAlgorithm.A_STAR) {
                            drawNumberOnScreen((int) gxFunction, currentNodeClickX, currentNodeClickY + (scalingZoom / 11), scalingZoom);
                        }

                        //show how costs are calulated (4th page)
                        if (pageNo == 3) {
                            //if has been shown for a little while
                            float animate = 0;
                            if (counterAnimationFade < 200) {
                                //don't scale
                                animate = scalingZoom;
                            } else {
                                //do scale down (so all costs are shown)
                                animate = scalingZoom - (counterAnimationFade-200);
                                if (animate < 0) animate = 0;
                            }
                            spriteBatch.draw(currentNodeSprite[5], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, animate);
                            List<Node> currentPath = worldGraph.getCurrentSearch().getPath();
                            ArrayList<Integer> arrCostsCurrentSerach = getCostsAllNodes(currentPath);

                            for (int i = 0; i < currentPath.size(); i++) {
                                if (arrCostsCurrentSerach.size() > 0) {
                                    drawCostOnScreen(arrCostsCurrentSerach.get(0), currentPath.get(i).getPoint(), currentPath.get(i+1).getPoint(), scalingZoom);
                                    arrCostsCurrentSerach.remove(0);
                                }
                            }
                        }
                        popupShowing = true;
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

                                if (pageNo == 1 && worldGraph.getCurrentSearch().getAlgorithm() == SearchAlgorithm.A_STAR)
                                    drawEquationOnScreen((int)total, (int)gxFunction, (int)cost, currentNodeClickX, currentNodeClickY + (scalingZoom/50), scalingZoom);

                                popupShowing = true;
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

                                if (pageNo == 1 && world.getWorldGraph().getCurrentSearch().getAlgorithm() == SearchAlgorithm.A_STAR)
                                    drawNumberOnScreen((int) gxFunction, currentNodeClickX, (float) currentNodeClickY + (scalingZoom / 11), scalingZoom);

                                popupShowing = true;
                            } else
                                //MULTI PAGE DONE if already expanded
                                if (worldGraph.getCurrentSearch().getVisited() != null)
                                    if (worldGraph.getCurrentSearch().getVisited().contains(new Node(new Point(currentNodeClickX, currentNodeClickY)))) {
                                        if (pageNo >= fullyExploredSprite.length) pageNo = 0; //reset to first page if neccessary
                                        //TODO Put some info around the screen somewhere
                                        spriteBatch.draw(fullyExploredSprite[pageNo], (float) ((currentNodeClickX - scalingZoom / 2) + 0.5),
                                                (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);

                                        popupShowing = true;
                                    }
        //----ALL RENDERS GO HERE---

        spriteBatch.end();
        shapeRenderer.end();
    }

    /**
     * Calculated the Euclidian distance between first node and second node.
     * IE SQRT((x2-x1)^2 + (y2-y1)^2)
     * @param start first node
     * @param end second node
     * @return
     */
    private float calculateEuclidian(Node start, Node end) {
        Point p1 = start.getPoint();
        Point p2 = end.getPoint();

        int x = p1.getX() - p2.getX();
        int y = p1.getY() - p2.getY();

        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Given a search path (a list of nodes), it will find the costs between each and every pair of adjacent nodes in the list.
     * @param path the path to generate the cost list for
     * @return ArrayList of Integer containing a list of costs corrosponding to the edge costs between the path given, in the correct (IE not reversed) order
     */
    private ArrayList<Integer> getCostsAllNodes(List<Node> path) {
        ArrayList<Integer> arrTempCosts = new ArrayList<Integer>();
        if (path.isEmpty()) return arrTempCosts;

        for (int i = path.size()-1; i > 0; i--) {
            Node pointOne = path.get(i);
            Node pointTwo = path.get(i-1);

            float costBetweenOneTwo = pointOne.getEdgeCost(pointTwo);
            arrTempCosts.add((int) costBetweenOneTwo);
        }

        reverse(arrTempCosts);

        return arrTempCosts;
    }

    /**
     * Draws the heuristic, including how it was worked out. IE a division triangle with cost numbers
     * @param gcost the actual cost of the heuristic, rounded to the nearest integer.
     * @param currentNodeClickX the x position (on the nodes graph / worldGraph) that the user last clicked one
     * @param currentNodeClickY the y position (on the nodes graph / worldGraph) that the user last clicked one
     * @param scalingZoom
     */
    private void drawHeuristic(float gcost, int currentNodeClickX, int currentNodeClickY, float scalingZoom) {

        float x1;
        float y1;
        if (worldGraph.getCurrentSearch().getMostRecentlyExpanded() != null) {
            x1 = (float) (worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().getX() + 0.5);
            y1 = (float) (worldGraph.getCurrentSearch().getMostRecentlyExpanded().getPoint().getY() + 0.5);
        } else {
            x1 = (float) (worldGraph.getCurrentSearch().getStart().getPoint().getX() + 0.5);
            y1 = (float) (worldGraph.getCurrentSearch().getStart().getPoint().getY() + 0.5);
        }
        float x2 = (float) (worldGraph.getCurrentSearch().getEnd().getPoint().getX() + 0.5);
        float y2 = (float) (worldGraph.getCurrentSearch().getEnd().getPoint().getY() + 0.5);

        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.SKY);

        shapeRenderer.rectLine(x1, y1, x2, y2, (float) 0.25);

        shapeRenderer.end();

        drawDottedLine((float) 0.3, x1, y1, x1, y2);
        drawDottedLine((float) 0.3, x2, y2, x1, y2);
    }

    /**
     * Draws a dotted line from one place to another using a ShapeRenderer
     * @param dotDist the distance between each dot. 0.3 is recommended value.
     * @param x1 x coordination of beginning of dotted line
     * @param y1 y coordination of beginning of dotted line
     * @param x2 x coordination of end of dotted line
     * @param y2 y coordination of end of dotted line
     */
    private void drawDottedLine(float dotDist, float x1, float y1, float x2, float y2) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);

        Vector2 vec2 = new Vector2(x2, y2).sub(new Vector2(x1, y1));
        float length = vec2.len();
        for(float i = 0; i < length; i += dotDist) {
            vec2.clamp(length - i, length - i);
            shapeRenderer.circle(x1 + vec2.x, y1 + vec2.y, (float) 0.1, 10);
        }

        shapeRenderer.end();
    }

    /**
     * Draws an equation in the following format:
     * total = firstNo + secondNo
     * onto the screen.
     * @param total the total value that will be displaed left of all other numbers
     * @param firstNo the number in between = and +
     * @param secondNo the number most to the right
     * @param currentNodeClickX the x coordinate of a Node on the WorldGraph
     * @param currentNodeClickY the y coordinate of a Node on the WorldGraph
     * @param scalingZoom the current amount of zoom that the camera has.
     *
     */
    private void drawEquationOnScreen(int total, int firstNo, int secondNo, float currentNodeClickX, float currentNodeClickY, float scalingZoom) {
        drawNumberOnScreen(total, currentNodeClickX-(scalingZoom/10), currentNodeClickY, scalingZoom);
        spriteBatch.draw(equals, (float) ((currentNodeClickX - scalingZoom / 2) + 0.5), (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        drawNumberOnScreen(firstNo, currentNodeClickX+(scalingZoom/10), currentNodeClickY, scalingZoom);
        spriteBatch.draw(plus, (float) ((currentNodeClickX - scalingZoom / 3.5) + 0.5) , (float) (currentNodeClickY + 0.5), scalingZoom, scalingZoom);
        drawNumberOnScreen(secondNo, currentNodeClickX+3*(scalingZoom/10), currentNodeClickY, scalingZoom);
    }

    /**
     * Draws an cost in the following format:
     * cost
     * onto the screen, inbetween
     * @param cost the value of g(x) to thus node. that will be displaed left of all other numbers
     * @param one the first node
     * @param two the second node
     * @param scalingZoom the current amount of zoom that the camera has.
     *
     */
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

    /**
     * Draws an number onto the screen at the x and y position, with the current z position. Will not move when z value changes
     * @param number the number to be shown on the screen
     * @param currentNodeClickX the x coordinate that the number should be displayed at
     * @param currentNodeClickY the Y coordinate that the number should be displayed at
     * @param scalingZoom the current amount of zoom that the camera has.
     */
    private void drawStaticNumberOnScreen(int number, float currentNodeClickX, float currentNodeClickY, float scalingZoom) {
        //if single digits
        if (number > 100 || number < 0) {
            number = 99;
            System.err.println("Currently, drawNumberOnScreen only works with < 100 numbers; using this instead: " + number);
        }

        if (number < 10) {
            try {
                spriteBatch.draw(numbers[number], (float) (currentNodeClickX - scalingZoom / 2 + 0.5), (float) (currentNodeClickY - scalingZoom / 2 + 0.25), scalingZoom, scalingZoom);
            } catch (NullPointerException e) {
                System.err.println("Error in drawStaticNumberOnScreen in " + number + ". E" + e);
            }
        } else {
            //if not
            int firstNo = number / 10;
            int secondNo = number % 10;
            float x1 = (float) (currentNodeClickX - scalingZoom / 2 + 0.5);
            float x2 = x1 + (scalingZoom / 20);

            try {
                spriteBatch.draw(numbers[firstNo], x1, (float) (currentNodeClickY - scalingZoom / 2 + 0.25), scalingZoom, scalingZoom);
                spriteBatch.draw(numbers[secondNo], x2, (float) (currentNodeClickY - scalingZoom / 2 + 0.25), scalingZoom, scalingZoom);
            } catch (NullPointerException e) {
                System.err.println("Error in second drawStaticNumberOnScreen in " + number + ". E" + e);
            }
        }
    }

    /**
     * Draws an number onto the screen at the x and y position, with the current z position. WILL move when z value changes (to prevent overlap)
     * @param number the number to be shown on the screen
     * @param currentNodeClickX the x coordinate that the number should be displayed at
     * @param currentNodeClickY the Y coordinate that the number should be displayed at
     * @param scalingZoom the current amount of zoom that the camera has.
     */
    private void drawNumberOnScreen(int number, float currentNodeClickX, float currentNodeClickY, float scalingZoom) {
        //if single digits
        if (number > 100 || number < 0) {
            number = 99;
            System.err.println("Currently, drawNumberOnScreen only works with < 100 numbers; using this instead: " + number);
        }

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

    /**
     * Checks if any pop-up is currently showing
     * @return true iff pop-up is showing; else false.
     */
    public boolean getPopupShowing() {
        return popupShowing;
    }

    /**
     * Change the page number if the pop-up is showing
     */
    public void flipPageRight() {
        pageNo++;
    }

    /**
     * Change the page number back to the first (aka 0)
     */
    public void resetPage() {
        stickyCurrentNode = false;
        pageNo = 0;
    }

    public void resetCounterAnimation() {
        counterAnimationFade = 0;
    }
}
