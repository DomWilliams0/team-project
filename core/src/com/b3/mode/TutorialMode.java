package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourMultiContinuousPathFind;
import com.b3.gui.help.HelpBox;
import com.b3.gui.popup.Popup;
import com.b3.gui.sidebars.SideBarIntensiveLearningMode;
import com.b3.gui.sidebars.tabs.PracticeModeSettingsTab;
import com.b3.input.InputHandler;
import com.b3.input.PracticeModeWorldSelectionHandler;
import com.b3.input.TutorialModeSelectionHandler;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TutorialMode extends Mode {

	private ArrayList<Integer> keysPressed;
	private Stage popupStage;
	private Sprite backgroundTexture;

	private int tutorialCounter;
	private BitmapFont font;
	private String[] tutorialText;
	private Point currentPos;
	private SideBarIntensiveLearningMode sideBar;

	private int stepCounter;
	private Node currentEndNode;
	private int currentPage;

	public TutorialMode(MainGame game) {
		// create world
		super(ModeType.TUTORIAL, game, "core/assets/world/world_smaller_test_tiym.tmx", 26f);

        world.getWorldGraph().setLearningModeNext(SearchAlgorithm.A_STAR);
		world.getWorldGraph().getCurrentSearch().pause(1);
		world.getWorldGraph().getCurrentSearch().setUpdated(true);

		loadTexturesTutorial();

		keysPressed = new ArrayList<Integer>();

		currentPos = new Point(1,1);

		stepCounter = 0;
	}

	private void loadTexturesTutorial() {
		Texture tempTexture = new Texture("core/assets/gui/tutorial/bg.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		backgroundTexture = new Sprite(tempTexture);

		font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 20);

		tutorialCounter = 0;

		tutorialText = new String[12];
		tutorialText[0] = "Zoom out fully with + and - or scrolling to see contrast mode"
				+ '\n' + "Use the arrow keys to move around the world"
				+ '\n' + "Complete these two tasks to continue";
		tutorialText[1] = "A node is represented by a black diamond"
				+ '\n' + "A low cost edge is represented by a black or white line,"
				+ '\n' + "a high cost one by a red line" + '\n'
				+ "The end node is represented by a blue diamond" + '\n'
				+ "The start node is the blue node with a orange circle around it,"
				+ '\n' + "click on this to continue";
		tutorialText[2] = "Open the left hand sidebar by clicking the arrow to the left";
		tutorialText[3] = "Press play to start A* search"
				+ '\n' + "You can adjust the search and game speed as well"
				+ '\n' + "Use the arrow keys to move around the world"
				+ '\n' + "Complete these two tasks to continue";
		tutorialText[1] = "A node is represented by a black diamond"
				+ '\n' + "A low cost edge is represented by a black or white line,"
				+ '\n' + "a high cost one by a red line" + '\n'
				+ "The end node is represented by a blue diamond" + '\n'
				+ "The start node is the blue node with a orange circle around it,"
				+ '\n' + "click on this to continue";
		tutorialText[2] = "Open the left hand sidebar by clicking the arrow to the left";
		tutorialText[3] = "Press play to start A* search"
				+ '\n' + "You can adjust the search and game speed as well";
		tutorialText[4] = "Now that the search has finished"
				+ '\n' + "pause the search and close the sidebar on the left";
		tutorialText[5] = "Open the right hand sidebar by clicking the arrow to the right";
		tutorialText[6] = "Step through the search a couple of steps by clicking the next button"
				+ '\n' + "and watch the data structure update"
				+ '\n' + "hover over the coordinates in the sidebar to highlight them on the world";
		tutorialText[7] = "Click on the current node (pink) to see more information";
		tutorialText[8] = "Click on the current node again (pink) to change the page"
				+ '\n' + "until all the previous costs are shown ";
		tutorialText[9] = "Click on the end node and change the page until"
				+ '\n' + "the heuristic is shown";
		tutorialText[10] = "Open the right hand menu"
				+ '\n' + "and click on the pseudocode tab"
				+ '\n' + "then click activate and watch how the search works behind the scenes";
		tutorialText[11] = "And that's it! You now know how to use this program"
				+ '\n' + "but if you ever get stuck you can click on a help tab at the top"
				+ '\n' + "You can go back to the main menu using the left hand side settings menu";
	}

	@Override
	protected void registerFurtherInputProcessors(InputHandler inputHandler) {
		// world clicking
		inputHandler.addProcessor(new TutorialModeSelectionHandler(world, this));
	}

	/**
	 * Sets up the sidebars (one with options on the left; one with nodes and step-by-step buttons on right; and help box on top)
	 */
	@Override
	protected void initSidebar() {
		super.initSidebar();

		sideBar = new SideBarIntensiveLearningMode(sideBarStage, world);
		sideBar.setController(game);
		sideBarStage.addActor(sideBar);
	}

	/**
	 * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
	 *
	 * @param delta Delta time since last frame
	 */
	@Override
	protected void tick(float delta) {
		if (!world.getPseudoCode()) {
			sideBarNodes.resetPseudoCode();
			world.setPseudoCode(true);
		}
		sideBar.render();

		drawPopup(backgroundTexture, tutorialText[tutorialCounter]);
		
		checkTaskCompleted();

	}

	private void checkTaskCompleted() {
		switch (tutorialCounter) {
			case 0:
				//+ or - pressed or scrolled and arrow keys pressed
				if (world.getWorldCamera().getCurrentZoom() > 10 && (world.getWorldCamera().getPosX() != 0 || world.getWorldCamera().getPosY() != 0)) {
					tutorialCounter++;
				}
				break;
			case 1:
				if (currentPos.equals(world.getWorldGraph().getCurrentSearch().getStart().getPoint())) {
					tutorialCounter++;
				}
				break;
			case 2:
				if (sideBar.isOpen()) {
					tutorialCounter++;
				}
				break;
			case 3:
				if (world.getWorldGraph().getCurrentSearch().isPathComplete()) {
					tutorialCounter++;
				}
				break;
			case 4:
				if (!sideBar.isOpen() & world.getWorldGraph().getCurrentSearch().isPaused()) {
					tutorialCounter++;
				}
				break;
			case 5:
				if (sideBarNodes.isOpen) {
					tutorialCounter++;
					currentEndNode = world.getWorldGraph().getCurrentSearch().getEnd();
				}
				break;
			case 6:
				if (!currentEndNode.equals(world.getWorldGraph().getCurrentSearch().getEnd()) && world.getWorldGraph().getCurrentSearch().getPath().size() > 5) {
					tutorialCounter++;
				}
				break;
			case 7:
				if (world.getCurrentClick().equals(world.getWorldGraph().getCurrentSearch().getMostRecentlyExpanded().getPoint())) {
					tutorialCounter++;
				}
				break;
			case 8:
				if (currentPage == 3) {
					tutorialCounter++;
				}
				break;
			case 9:
				if (world.getCurrentClick().equals(world.getWorldGraph().getCurrentSearch().getEnd().getPoint()) & currentPage == 2) {
					tutorialCounter++;
				}
				break;
			case 10:
				if (!sideBarNodes.getPseudocodeBegin()) {
					tutorialCounter++;
				}
				break;
			default:
				break;
		}
	}

	private void drawPopup(Sprite tutorial, String displayText) {
		float xGraphic = Gdx.graphics.getWidth() / 2 - tutorial.getWidth() / 2;
		float yGraphic = 0;

		SpriteBatch spriteBatch = new SpriteBatch();

		spriteBatch.begin();

		spriteBatch.draw(tutorial, xGraphic, yGraphic);
		font.draw(spriteBatch, displayText, xGraphic + 20, yGraphic + tutorial.getHeight() - 20);

		spriteBatch.end();
	}

	@Override
	protected void spawnInitialEntities() {
		WorldGraph worldGraph = world.getWorldGraph();
		Agent agent = world.spawnAgent(new Vector2(worldGraph.getMaxXValue() / 2, worldGraph.getMaxYValue() / 2));
		BehaviourMultiContinuousPathFind behaviour = new BehaviourMultiContinuousPathFind(
				agent, SearchAlgorithm.A_STAR, worldGraph, camera, world);
		agent.setBehaviour(behaviour);

		worldGraph.setCurrentSearch(agent, behaviour.getSearchTicker());
	}


	/**
	 * Updates the position of the sidebars and world and scale when the window has been resized
	 * Prevents stretching of elements
	 * Allows app window to be multi-sized and also work for multiple resolutions
	 *
	 * @param width  the current width of the window
	 * @param height the current height of the window
	 */
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		sideBar.resize(width, height);
	}

	public void setCurrentPos(Point currentPos) {
		this.currentPos = currentPos;
	}

	public boolean needPopups() {
		return (tutorialCounter >= 7);
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
