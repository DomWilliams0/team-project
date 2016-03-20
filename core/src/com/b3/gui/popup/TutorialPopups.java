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
 * Pop-ups that will be shown when the user is in tutorial mode. These pop-ups tell the user what to do to progress in
 * the tutorial
 *
 * @author nbg481
 */
public class TutorialPopups {

	private final Sprite backgroundTexture;
	private final BitmapFont font;
	private final String[] tutorialText;
	private Point currentPos;
	private int stepCounter;
	private Node currentEndNode;
	private int currentPage;

	/**
	 * Setup the tutorial popups, loading all the textures and setting up the array of text to be shown to the user
	 */
	public TutorialPopups() {
		Texture tempTexture = new Texture("gui/tutorial/bg.png");
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
				'\n' + "[LIGHT_GRAY]clicking the next button[]" +
				'\n' + "hover over the coordinates in the sidebar +" + '\n' + "to highlight them on the world";
		tutorialText[7] = "[LIGHT_GRAY] Click[] on the [PINK]current node[] to see more information";
		tutorialText[8] = "[LIGHT_GRAY]Click[] on the [PINK]current node[] again to change the page"
				+ '\n' + "until all the previous costs are shown ";
		tutorialText[9] = "[LIGHT_GRAY]Click[] on the [BLUE]end node[] and change the page until"
				+ '\n' + "the heuristic is shown";
		tutorialText[10] = "Open the left hand menu"
				+ '\n' + "and [LIGHT_GRAY]enable the pseudocode checkbox[]"
				+ '\n' + "then [LIGHT_GRAY]on the right hand side[] you can"
				+ '\n' + "watch how the search works behind the scenes";
		tutorialText[11] = "And that's it! You now know how to use this program"
				+ '\n' + "but if you ever get stuck you can click on a help tab at the top"
				+ '\n' + "You can go back to the main menu using the left side menu";

		currentPos = new Point(1, 1);

		stepCounter = 0;
	}

	/**
	 * Renders the currently dispalying pop-up on screen
	 */
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

	/**
	 * Checks that the current task that the user has to do has been completed
	 * @param world the tutorial world that the user is in
	 * @param sideBar the left hand sidebar (for use when checking if has opened + button presses)
	 * @param sideBarNodes the right hand sidebar (for use when checking if has opened + button presses)
     */
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
				if (world.getWorldGUI().getCurrentClick().equals(world.getWorldGraph().getCurrentSearch().getMostRecentlyExpanded().getPoint())) {
					stepCounter++;
				}
				break;
			case 8:
				if (currentPage == 3) {
					stepCounter++;
				}
				break;
			case 9:
				if (world.getWorldGUI().getCurrentClick().equals(world.getWorldGraph().getCurrentSearch().getEnd().getPoint()) & currentPage == 2) {
					stepCounter++;
				}
				break;
			case 10:
				if (world.getWorldGraph().getCurrentSearch().isInspectingSearch()) {
					stepCounter++;
				}
				break;
			default:
				break;
		}
	}

	/**
	 * @param currentPos the current position of the mouse on a node in the world
     */
	public void setCurrentPos(Point currentPos) {
		this.currentPos = currentPos;
	}

	/**
	 * @return the task that the user is currently on
     */
	public int getCounter() {
		return stepCounter;
	}

	/**
	 * @param currentPage the current page that the user is on in any of the {@link com.b3.gui.PopupDescription}
     */
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

}
