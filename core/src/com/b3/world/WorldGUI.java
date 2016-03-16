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
import com.badlogic.gdx.math.Vector2;

import static com.b3.mode.ModeType.*;

public class WorldGUI {
	private World world;

	private PopupManager popupManager;
	private CoordinatePopup coordinatePopup;
	private ShapeRenderer shapeRenderer;

	private float counterAnimation = -1;
	private int counterScaler = 0;
	private double pos = 1;

	//current node user has clicked on
	private int currentNodeClickX;
	private int currentNodeClickY;

	private PopupDescription popupDescription;
	private float animationNextDestination;

	private int yNextDestination;
	private int xNextDestination;

	private Point currentMousePos;

	private boolean pseudoCodeEnabled;

	public WorldGUI(World world) {
		this.world = world;

		animationNextDestination = 0;
		xNextDestination = 0;
		yNextDestination = 0;

		shapeRenderer = new ShapeRenderer();
		coordinatePopup = new CoordinatePopup();

		popupDescription = new PopupDescription(world);
		popupManager = new PopupManager(MainGame.getCurrentMode());
	}
	
	public void showIntroPopup() {
		popupManager.showIntro();
	}

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
		ModeType mode = MainGame.getCurrentMode();

		renderGUI();

		//pop-ups on nodes
		if (mode == LEARNING || mode == TUTORIAL)
			popupDescription.render(currentNodeClickX, currentNodeClickY, world.getWorldGraph().getCurrentSearch());

		//pop-ups to show current coordinate
		coordinatePopup.render();

		//render big pop-ups
		popupManager.render();
	}


	/**
	 * Renders graph, building placement overlay and animations
	 */
	private void renderGUI() {
		float zoomScalar = getZoomScalar();
		WorldGraphRenderer worldGraphRenderer = world.getWorldGraph().getRenderer();
		WorldCamera worldCamera = world.getWorldCamera();

		int fovNumber = MainGame.getCurrentMode() == COMPARE ? 67 : 40; // Todo - Nish, what is this?
		if (worldCamera.getFOV() < fovNumber) {
			Vector2 cameraPos = world.getTileSize().scl(0.5f);
			worldCamera.setFieldOfViewY(worldCamera.getFOV() + 1);
			worldCamera.lookAt(cameraPos.x + (fovNumber - worldCamera.getFOV()), cameraPos.y + (fovNumber - worldCamera.getFOV()), 0);
			counterAnimation = 10;
		}

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


	private float getZoomScalar() {
		if (Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM) != 45)
			System.err.println("Set max zoom in userconfig to 45, zoom only works with this so far...");

		//TODO make it work for different max zooms
		WorldCamera worldCamera = world.getWorldCamera();
		float zoomScalar = worldCamera.getCurrentZoom();

		if (zoomScalar < 14 && zoomScalar > 1.5) {
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

	public void setCurrentClick(int x, int y) {
		if (x == currentNodeClickX && y == currentNodeClickY) return;
		this.currentNodeClickX = x;
		this.currentNodeClickY = y;
	}

	public Point getCurrentClick() {
		return new Point(currentNodeClickX, currentNodeClickY);
	}

	public void setNextDestination(int x, int y) {
		animationNextDestination = (float) 2.0;
		xNextDestination = x;
		yNextDestination = y;
		world.getWorldGraph().setNextDestination(x, y);
	}

	public void setCurrentMousePos(int screenX, int screenY) {
		currentMousePos = new Point(screenX, screenY);
	}

	//TODO
	public void setPseudoCode(boolean enabled) {
		pseudoCodeEnabled = enabled;
	}

	public boolean getPseudoCode() {
		return pseudoCodeEnabled;
	}

	public CoordinatePopup getCoordinatePopup() {
		return coordinatePopup;
	}

	public PopupDescription getPopupDescription() {
		return popupDescription;
	}
}
