package com.b3.mode;

import com.b3.MainGame;
import com.b3.gui.PopupDescription;
import com.b3.gui.components.MenuComponent;
import com.b3.gui.components.MenuItemComponent;
import com.b3.gui.components.MessageBoxComponent;
import com.b3.gui.help.HelpBox;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.input.InputHandler;
import com.b3.input.KeyboardController;
import com.b3.input.SoundController;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.b3.world.WorldGUI;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A base game mode
 *
 * @author dxw405 oxe410
 */
public abstract class Mode extends ScreenAdapter {

	private Point currentSelection;

	protected final World world;
	protected final WorldCamera camera;

	protected final MainGame game;
	protected final KeyboardController keyboardController;

	protected Stage menuStage;
	protected MenuComponent menu;

	protected Stage sideBarStage;
	protected SideBarNodes sideBarNodes;

	/**
	 * @param mainGame     The game instance
	 * @param worldPath    The path of the world to load
	 * @param startingFOV  The FOV to start at
	 * @param startingZoom The zoom level to start at
	 * @param startingX    The X coordinate to start at. <code>Null</code> for world centre
	 * @param startingY    The Y coordinate to start at. <code>Null</code> for world centre
	 */
	Mode(MainGame mainGame, String worldPath, float startingFOV, float startingZoom, Float startingX, Float startingY) {

		game = mainGame;
		InputHandler inputHandler = game.getInputHandler();

		SearchTicker.setInspectSearch(false);

		// load world
		world = new World(worldPath);

		// position camera
		Vector2 centre = world.getTileSize().scl(0.5f);
		if (startingY == null)
			startingY = centre.y;
		if (startingX == null)
			startingX = centre.x;

		world.initCamera(startingFOV, startingX, startingY, startingZoom);
		camera = world.getWorldCamera();
		initialise();

		// initialise sidebars and back button
		initSidebar();

		// initialise menu
		initMenu();

		// init input handlers
		keyboardController = new KeyboardController();
		inputHandler.addProcessor(keyboardController);
		inputHandler.addProcessor(menuStage);
		inputHandler.addProcessor(sideBarStage);
		registerFurtherInputProcessors(inputHandler);

		this.currentSelection = new Point(0, 0);
	}

	/**
	 * Allows other {@link InputHandler} to watch over this world, and respond to inputs
	 *
	 * @param inputHandler the extra {@link InputHandler} to watch over this world
	 */
	protected abstract void registerFurtherInputProcessors(InputHandler inputHandler);

	/**
	 * Ticked once per frame. All rendering and stage acting goes in here
	 */
	protected abstract void tick();

	/**
	 * Instantiation that takes place _in_ the Mode constructor, after the world
	 * has been loaded
	 */
	protected abstract void initialise();

	/**
	 * Further instantiation that takes place _after_ the constructor, when the
	 * mode has been fully loaded
	 */
	public void finishInitialisation() {
	}

	/**
	 * Initialise the sidebars, making sure that the correct tabs are shown in learning mode
	 */
	protected void initSidebar() {
		sideBarStage = new Stage(new ScreenViewport());
	}

	/**
	 * Creates the top toolbar with mode independent buttons, i.e. returning to the main menu
	 */
	private void initMenu() {
		menuStage = new Stage(new ScreenViewport());
		menu = new MenuComponent();

		// Get skin and font
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
		Skin skin = new Skin(atlas);
		BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);

		// Add items
		MenuItemComponent helpItem = new MenuItemComponent(skin, font, "Help");
		MenuItemComponent mainMenuItem = new MenuItemComponent(skin, font, "Main menu");
		MenuItemComponent exitItem = new MenuItemComponent(skin, font, "Exit");

		helpItem.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// help popup
				HelpBox helpBox = new HelpBox();

				MessageBoxComponent messageBoxComponent = new MessageBoxComponent(menuStage, helpBox);
				messageBoxComponent.show();
			}
		});
		mainMenuItem.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundController.stopSound(3);
				game.goToMainMenu();
			}
		});
		exitItem.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.app.exit();
			}
		});

		menu.addItem(helpItem);
		menu.addItem(mainMenuItem);
		menu.addItem(exitItem);

		menuStage.addActor(menu.getComponent());
	}

	/**
	 * Renders the world, sidebars, screen and keyboard controllers
	 *
	 * @param delta the difference in time since last tick
	 */
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

		// world rendering
		world.render();

		// menu rendering
		menu.render();
		menuStage.act();
		menuStage.draw();

		// sidebar rendering
		sideBarStage.act(Utils.TRUE_DELTA_TIME);
		if (sideBarNodes != null) {
			sideBarNodes.render();
			WorldGUI worldGUI = world.getWorldGUI();

			if (worldGUI.getCurrentClick() != null) sideBarNodes.highlightNode(worldGUI.getCurrentClick(), true);
			PopupDescription popupDescription = worldGUI.getPopupDescription();
			if (sideBarNodes.hasNewClick()) {
				worldGUI.setCurrentClick(sideBarNodes.getNewClick().getX(), sideBarNodes.getNewClick().getY());
				// check if need to change page
				// Check if node page no. should be incremented or reset to beginning (as clicked on different node)
				if (currentSelection.x == sideBarNodes.getNewClick().getX() && currentSelection.y == sideBarNodes.getNewClick().getY()) {
					// old node so change page number
					if (popupDescription.getPopupShowing())
						// if popup showing
						popupDescription.resetCounterAnimation();
					popupDescription.flipPageRight();
				} else {
					// new node so reset page number
					if (popupDescription.getPopupShowing())
						// if popup showing
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

	/**
	 * Reshuffle the sidebars to keep centered when the user resizes the window
	 *
	 * @param width  the new width of the window UI
	 * @param height the new height of the window UI
	 */
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();

		// Menu updating
		menuStage.getViewport().update(width, height, true);
		menu.resize(width, height);

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
