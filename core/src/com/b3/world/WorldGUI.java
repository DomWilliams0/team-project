package com.b3.world;

import com.b3.MainGame;
import com.b3.gui.CoordinatePopup;
import com.b3.gui.PopupDescription;
import com.b3.gui.popup.PopupManager;
import com.b3.mode.ModeType;
import com.b3.search.Point;
import com.b3.search.WorldGraphRenderer;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import static com.b3.mode.ModeType.*;

/**
 * Deals with rendering a layer of interaction ontop of the graph - pop-ups, errors and heuristics
 *
 * @author nbg481
 */
public class WorldGUI {
	private final World world;

	private final PopupManager popupManager;
	private final CoordinatePopup coordinatePopup;
	private final ShapeRenderer shapeRenderer;

	private float counterAnimation;
	private int counterScaler;
	private double pos;

	//current node user has clicked on
	private int currentNodeClickX;
	private int currentNodeClickY;

	private final PopupDescription popupDescription;
	private float animationNextDestination;

	private int yNextDestination;
	private int xNextDestination;

	private Point currentMousePos;

	private boolean shownOnce;

	private Runnable prePopopRender;

	/**
	 * Creates a new WorldGUI, fundamentally linked to a specific world
	 *
	 * @param world the world that this GUI will be overlayed ontop of
	 */
	public WorldGUI(World world) {
		this.world = world;

		animationNextDestination = 0;
		xNextDestination = 0;
		yNextDestination = 0;
		counterAnimation = 10;
		counterScaler = 0;
		pos = 1;
		shownOnce = false;

		shapeRenderer = new ShapeRenderer();
		coordinatePopup = new CoordinatePopup();

		popupDescription = new PopupDescription(world);
		popupManager = new PopupManager(MainGame.getCurrentMode());
		prePopopRender = null;
	}

	/**
	 * Shows the intro pop-up on first launching the mode
	 */
	public void showIntroPopup() {
		popupManager.showIntro();
	}

	/**
	 * @return the pop-up manager for this {@link World} and WorldGUI ONLY
	 */
	public PopupManager getPopupManager() {
		return popupManager;
	}

	/**
	 * Clearing up all entities marked as dead (which can't be done while ticking the world)
	 * Rendering the world
	 * Ticking entity behaviours and physics
	 * Rendering entities
	 * Rendering the search graph
	 * Rendering buildings
	 * Rendering physics/collisions (if configured)
	 */
	public void render() {
		renderGUI();
	}

	/**
	 * renders any pop-ups (errors, pop-ups)
	 */
	public void renderPopups() {
		ModeType mode = MainGame.getCurrentMode();

		if (prePopopRender != null)
			prePopopRender.run();

		checkForInitialPopup();

		//pop-ups on nodes
		if (mode == LEARNING || mode == TUTORIAL)
			popupDescription.render(currentNodeClickX, currentNodeClickY, world.getWorldGraph().getCurrentSearch());

		//pop-ups to show current coordinate
		coordinatePopup.render();

		//render big pop-ups
		popupManager.render();
	}

	/**
	 * If the initial animation has finished and the intro pop-up has not been shown yet, then
	 * show it
	 */
	private void checkForInitialPopup() {
		if (world.getWorldGraph().getRenderer().getAnimationFinished() && !shownOnce) {
			world.getWorldGUI().getPopupManager().showIntro();
			shownOnce = true;
		}
	}

	/**
	 * Renders graph, building placement overlay and animations
	 */
	private void renderGUI() {
		float zoomScalar = getZoomScalar();
		WorldGraphRenderer worldGraphRenderer = world.getWorldGraph().getRenderer();
		WorldCamera worldCamera = world.getWorldCamera();

		if (counterAnimation > 1) {
			counterAnimation = (float) (counterAnimation - 0.25);
			if (Config.getBoolean(ConfigKey.SHOW_GRID))
				worldGraphRenderer.render(worldCamera, counterAnimation, zoomScalar);
		} else {
			if (Config.getBoolean(ConfigKey.SHOW_GRID))
				worldGraphRenderer.render(worldCamera, 1, zoomScalar);
		}

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(worldCamera.combined);

		if (animationNextDestination != 0) {
			shapeRenderer.setColor(Color.BLUE);
			animationNextDestination = (float) (animationNextDestination - 0.2);
			shapeRenderer.ellipse((float) (xNextDestination - (animationNextDestination / 2) + 0.5), (float) (yNextDestination - (animationNextDestination / 2) + 0.5), animationNextDestination, animationNextDestination);
		}

		//render add building overlay if needed
		if (Config.getBoolean(ConfigKey.ADD_BUILDING_MODE) || Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE)) {
			boolean adding = Config.getBoolean(ConfigKey.ADD_BUILDING_MODE);
			float x = currentMousePos.getX();
			float y = currentMousePos.getY();

			if (adding && world.isValidBuildingPos(x, y))
				shapeRenderer.setColor(Color.LIGHT_GRAY);
			else
				shapeRenderer.setColor(Color.FIREBRICK);

			shapeRenderer.box(x, y, 0, 4f, 4f, 1f);
		}

		shapeRenderer.end();
	}

	/**
	 * @return the amount that node's should be scaled, and background blackened, depending on the amount of zoom of the
	 * camera.
	 */
	private float getZoomScalar() {
		//TODO Dom remove config
		if (Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM) != 45)
			System.err.println("Set max zoom in userconfig to 45, zoom only works with this so far...");

		WorldCamera worldCamera = world.getWorldCamera();
		float zoomScalar = worldCamera.getCurrentZoom();

		if (zoomScalar < 14 && zoomScalar > 1) {
			counterScaler++;
		} else {
			counterScaler = 0;
			if (zoomScalar >= 14)
				pos = -0.25;
			if (zoomScalar <= 1.5)
				pos = 0.25;
		}

		if (counterScaler > 5) {
			//too long in-between animations
			worldCamera.setCurrentZoom((float) (worldCamera.getActualZoom() + pos));
		}
		return zoomScalar;
	}

	/**
	 * Sets the currently clicked node
	 *
	 * @param x the x coordinate of the current click
	 * @param y the y coordinate of the current click
	 */
	public void setCurrentClick(int x, int y) {
		if (x == currentNodeClickX && y == currentNodeClickY) return;
		this.currentNodeClickX = x;
		this.currentNodeClickY = y;
	}

	/**
	 * @return the currently clicked node as a type {@link Point}
	 */
	public Point getCurrentClick() {
		return new Point(currentNodeClickX, currentNodeClickY);
	}

	/**
	 * Updates the next destination of the search
	 *
	 * @param x the x coordinate of the next search
	 * @param y the y coordinate of the next search
	 */
	public void setNextDestination(int x, int y) {
		animationNextDestination = (float) 2.0;
		xNextDestination = x;
		yNextDestination = y;
		world.getWorldGraph().setNextDestination(x, y);
	}

	/**
	 * Updates the current mouse position for use when adding buildings
	 *
	 * @param screenX the x position to add building
	 * @param screenY the y position to add building
	 */
	public void setCurrentMousePos(int screenX, int screenY) {
		currentMousePos = new Point(screenX, screenY);
	}

	/**
	 * Should only be called to be used when resizing, or mouse clicks / movements - this should not be changed indirectly
	 *
	 * @return the {@link CoordinatePopup} that is linked to this world
	 */
	public CoordinatePopup getCoordinatePopup() {
		return coordinatePopup;
	}

	/**
	 * Should only be called to be used when resizing, or mouse clicks / movements - this should not be changed indirectly
	 *
	 * @return the {@link PopupDescription} that is linked to this world
	 */
	public PopupDescription getPopupDescription() {
		return popupDescription;
	}

	/**
	 * Sets the function that is run before any popup rendering
	 *
	 * @param prePopopRender The new function to run
	 */
	public void setPrePopopRenderer(Runnable prePopopRender) {
		this.prePopopRender = prePopopRender;
	}
}
