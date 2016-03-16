package com.b3.mode;

import com.b3.MainGame;
import com.b3.gui.PopupDescription;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.gui.sidebars.SideBarPracticeMode;
import com.b3.input.InputHandler;
import com.b3.input.KeyboardController;
import com.b3.search.Point;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.b3.world.WorldGUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A base game mode
 *
 * @author dxw405
 */
public abstract class Mode extends ScreenAdapter {

	private final ModeType modeType;
	private Point currentSelection;

	protected World world;
	protected WorldCamera camera;

	protected MainGame game;
	protected KeyboardController keyboardController;

	protected Stage sideBarStage;
	protected SideBarNodes sideBarNodes;

	public Mode(ModeType modeType, MainGame mainGame, String worldPath, float startingCameraRotation) {

		game = mainGame;
		InputHandler inputHandler = game.getInputHandler();

		this.modeType = modeType;

		// load world
		world = new World(worldPath);

		// position camera
		Vector2 cameraPos = new Vector2(world.getTileSize().scl(0.5f));
		camera = new WorldCamera(1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(cameraPos.x, cameraPos.y, Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM));
		camera.near = 1f;
		camera.far = 300f;
		camera.lookAt(cameraPos.x, cameraPos.y, 0);
		camera.rotate(startingCameraRotation, 1, 0, 0);
		camera.update();

		camera.setWorld(world);
		world.initEngine(camera);
		initialise();

		// initialise sidebars and back button
		initSidebar();

		// init input handlers
		keyboardController = new KeyboardController();
		inputHandler.addProcessor(keyboardController);
		inputHandler.addProcessor(sideBarStage);
		registerFurtherInputProcessors(inputHandler);

		this.currentSelection = new Point(0, 0);
		
		world.getWorldGUI().showIntroPopup();
	}

	protected abstract void registerFurtherInputProcessors(InputHandler inputHandler);

	protected abstract void tick();

	protected abstract void initialise();

	public void finishInitialisation() {
	}
	
	protected void renderBeforeWorld() {
	}

	protected void initSidebar() {
		sideBarStage = new Stage(new ScreenViewport());

		if (modeType != ModeType.COMPARE) {
			sideBarNodes = modeType == ModeType.PRACTICE ?
					new SideBarPracticeMode(sideBarStage, world, game, 460) :
					new SideBarNodes(sideBarStage, world);

			sideBarNodes.setStepthrough(true);
			sideBarStage.addActor(sideBarNodes);
		}

	}

	@Override
	public void render(float delta) {
		// delta time
		float rawDeltaTime = Gdx.graphics.getRawDeltaTime();
		Utils.TRUE_DELTA_TIME = rawDeltaTime;
		Utils.DELTA_TIME = rawDeltaTime * Config.getFloat(ConfigKey.GAME_SPEED);

		// clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// camera movement
		camera.move(keyboardController);
		camera.update();

		renderBeforeWorld();
		
		// world rendering
		world.render();
		

		// sidebar rendering
		sideBarStage.act(Utils.TRUE_DELTA_TIME);
		if (sideBarNodes != null) {
			sideBarNodes.render();
			WorldGUI worldGUI = world.getWorldGUI();
			if (!worldGUI.getPseudoCode()) {
				sideBarNodes.resetPseudoCode();
				worldGUI.setPseudoCode(true);
			}
			if (worldGUI.getCurrentClick() != null) sideBarNodes.highlightNode(worldGUI.getCurrentClick(), true);
				PopupDescription popupDescription = worldGUI.getPopupDescription();
			if (sideBarNodes.hasNewClick()) {
				worldGUI.setCurrentClick(sideBarNodes.getNewClick().getX(), sideBarNodes.getNewClick().getY());
				//check if need to change page
				// Check if node page no. should be incremented or reset to beginning (as clicked on different node)
				if (currentSelection.x == sideBarNodes.getNewClick().getX() && currentSelection.y == sideBarNodes.getNewClick().getY()) {
					//old node so change page number
					if (popupDescription.getPopupShowing())
						//if popup showing
						popupDescription.resetCounterAnimation();
					popupDescription.flipPageRight();
				} else {
					//new node so reset page number
					if (popupDescription.getPopupShowing())
						//if popup showing
						popupDescription.resetPage();
				}

				currentSelection = new Point(sideBarNodes.getNewClick().getX(), sideBarNodes.getNewClick().getY());
			}
		}
		sideBarStage.draw();

		tick();

		if (keyboardController.shouldExit())
			Gdx.app.exit();
	}

	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();

		sideBarStage.getViewport().update(width, height, true);

		if (sideBarNodes != null)
			sideBarNodes.resize(width, height);

		world.getWorldGUI().getCoordinatePopup().resize();
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		world.dispose();
	}
}
