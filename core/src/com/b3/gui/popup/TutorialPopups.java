package com.b3.gui.popup;

import com.b3.gui.sidebars.SideBarIntensiveLearningMode;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Nishanth on 11/03/2016.
 */
public class TutorialPopups {

    private final Sprite backgroundTexture;
    private final BitmapFont font;
    private final String[] tutorialText;
    private Point currentPos;
    private int stepCounter;
    private Node currentEndNode;
    private int currentPage;

    public TutorialPopups() {
        Texture tempTexture = new Texture("core/assets/gui/tutorial/bg.png");
        tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        backgroundTexture = new Sprite(tempTexture);

        font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 20);

        tutorialText = new String[12];
        tutorialText[0] = "Zoom out fully with [RED]+[] and [RED]-[] or [RED]scrolling[] to see contrast mode"
                + '\n' + "Use the [RED]arrow keys[] to move around the world"
                + '\n' + "[LIGHT_GRAY]Complete these two tasks to continue[]";
        tutorialText[1] = "A node is represented by a [DARK_GRAY]black diamond[]"
                + '\n' + "A low cost edge is represented by a [DARK_GRAY]black or white line[],"
                + '\n' + "a high cost one by a [RED]red line[]" + '\n'
                + "The end node is represented by a [BLUE]blue diamond[]" + '\n'
                + "The start node is the [BLUE]blue node[] with a [ORANGE]orange circle[] around it,"
                + '\n' + "[LIGHT_GRAY]click on this to continue[]";
        tutorialText[2] = "Open the left hand sidebar by"
                + '\n' + "[LIGHT_GRAY]clicking the arrow to the left[]";
        tutorialText[3] = "[LIGHT_GRAY]Press play[] to start A* search"
                + '\n' + "You can adjust the search and game speed as well";
        tutorialText[4] = "Now that the search has finished"
                + '\n' + "[LIGHT_GRAY]pause the search[] and"
                + '\n' + "[LIGHT_GRAY]close the sidebar on the left[]";
        tutorialText[5] = "Open the right hand sidebar by "
                + '\n' + "[LIGHT_GRAY]clicking the arrow to the right[]";
        tutorialText[6] = "Step through the search a couple of steps by " +
                '\n' +  "[LIGHT_GRAY]clicking the next button[]" +
                '\n' + "hover over the coordinates in the sidebar +" + '\n' + "to highlight them on the world";
        tutorialText[7] = "[LIGHT_GRAY] Click[] on the [PINK]current node[] to see more information";
        tutorialText[8] = "[LIGHT_GRAY]Click[] on the [PINK]current node[] again to change the page"
                + '\n' + "until all the previous costs are shown ";
        tutorialText[9] = "[LIGHT_GRAY]Click[] on the [BLUE]end node[] and change the page until"
                + '\n' + "the heuristic is shown";
        tutorialText[10] = "Open the right hand menu"
                + '\n' + "and [LIGHT_GRAY]click on the pseudocode tab[]"
                + '\n' + "then [LIGHT_GRAY]click activate[] and"
                + '\n' + "watch how the search works behind the scenes";
        tutorialText[11] = "And that's it! You now know how to use this program"
                + '\n' + "but if you ever get stuck you can click on a help tab at the top"
                + '\n' + "You can go back to the main menu using the left side menu";

        currentPos = new Point(1, 1);

        stepCounter = 0;
    }

    public void render() {
        float xGraphic = Gdx.graphics.getWidth() / 2 - backgroundTexture.getWidth() / 2;
        float yGraphic = 0;

        font.getData().markupEnabled = true;

        SpriteBatch spriteBatch = new SpriteBatch();

        spriteBatch.begin();

        spriteBatch.draw(backgroundTexture, xGraphic, yGraphic);
        font.draw(spriteBatch, tutorialText[stepCounter], xGraphic + 20, yGraphic + backgroundTexture.getHeight() - 20);

        spriteBatch.end();
    }

    public void checkTaskCompleted(World world, SideBarIntensiveLearningMode sideBar, SideBarNodes sideBarNodes) {
        switch (stepCounter) {
            case 0:
                //+ or - pressed or scrolled and arrow keys pressed
                if (world.getWorldCamera().getCurrentZoom() > 10 && (world.getWorldCamera().getPosX() != 0 || world.getWorldCamera().getPosY() != 0)) {
                    stepCounter++;
                }
                break;
            case 1:
                if (currentPos.equals(world.getWorldGraph().getCurrentSearch().getStart().getPoint())) {
                    stepCounter++;
                }
                break;
            case 2:
                if (sideBar.isOpen()) {
                    stepCounter++;
                }
                break;
            case 3:
                if (world.getWorldGraph().getCurrentSearch().isPathComplete()) {
                    stepCounter++;
                }
                break;
            case 4:
                if (!sideBar.isOpen() & world.getWorldGraph().getCurrentSearch().isPaused()) {
                    stepCounter++;
                }
                break;
            case 5:
                if (sideBarNodes.isOpen()) {
                    stepCounter++;
                    currentEndNode = world.getWorldGraph().getCurrentSearch().getEnd();
                }
                break;
            case 6:
                if (!currentEndNode.equals(world.getWorldGraph().getCurrentSearch().getEnd()) && world.getWorldGraph().getCurrentSearch().getPath().size() > 5) {
                    stepCounter++;
                }
                break;
            case 7:
                if (world.getCurrentClick().equals(world.getWorldGraph().getCurrentSearch().getMostRecentlyExpanded().getPoint())) {
                    stepCounter++;
                }
                break;
            case 8:
                if (currentPage == 3) {
                    stepCounter++;
                }
                break;
            case 9:
                if (world.getCurrentClick().equals(world.getWorldGraph().getCurrentSearch().getEnd().getPoint()) & currentPage == 2) {
                    stepCounter++;
                }
                break;
            case 10:
                if (!sideBarNodes.getPseudocodeBegin()) {
                    stepCounter++;
                }
                break;
            default:
                break;
        }
    }

    public void setCurrentPos(Point currentPos) {
        this.currentPos = currentPos;
    }

    public int getCounter() {
        return stepCounter;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
}
