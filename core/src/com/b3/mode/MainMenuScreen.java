package com.b3.mode;

import com.b3.MainGame;
import com.b3.gui.components.ImageButtonComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Shows the main menu, allowing the user to choose a mode
 *
 * @author nbg481 dxw405
 */
public class MainMenuScreen extends ScreenAdapter {

	private final Table wrapper;

	private final OrthographicCamera camera;
	private final Stage mainMenuStage;
	private final SpriteBatch spriteBatch;
	private final MainGame controller;
	private final Sprite backgroundSprite;
	private ShapeRenderer shapeRenderer;
	private float aspectRatioY;

	/**
	 * Constructs the (static / final) main menu camera and the two buttons, and sets up events for each respective button.
	 *
	 * @param controller used to set up the world, contains directories to config files
	 */
	public MainMenuScreen(MainGame controller) {
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.mainMenuStage = new Stage(new ScreenViewport());
		this.controller = controller;
		this.spriteBatch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();
		this.wrapper = new Table();

		mainMenuStage.addActor(wrapper);
		controller.getInputHandler().addProcessor(mainMenuStage);

		// create gui
		wrapper.setWidth(Gdx.graphics.getWidth());
		wrapper.setHeight(Gdx.graphics.getHeight());

		int padding = 5;
		wrapper.add(new Image(new Texture("icon_final.png")))
				.padBottom(padding * 8)
				.row();

		ModeType[] modes = {ModeType.TUTORIAL, ModeType.LEARNING, ModeType.PRACTICE, ModeType.COMPARE};
		for (ModeType modeType : modes) {
			wrapper.add(createButton(modeType).getComponent()).center().pad(padding);
			wrapper.row();
		}

		backgroundSprite = new Sprite(new Texture("menu_bg.png"));

		aspectRatioY = backgroundSprite.getHeight() / backgroundSprite.getWidth();
	}

	/**
	 * Creates a button for the given mode, using the assets $mode_primary.png and $mode_mouseover.png
	 *
	 * @param modeType The mode
	 * @return The newly created button
	 */
	private ImageButtonComponent createButton(ModeType modeType) {
		String modeName = modeType.name().toLowerCase();
		ImageButtonComponent button = new ImageButtonComponent(modeName + "_mode_primary.png",  modeName + "_mode_pressed.png",modeName + "_mode_mouseover.png");
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				dispose();
				controller.goToMode(modeType);
			}
		});
		return button;
	}

	/**
	 * Render the buttons on the screen, and update the viewpoint with the new (if any) change to the width and height of the window (allows scaling and positioning of buttons properly)
	 *
	 * @param delta not used
	 */
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.925490196f, 0.941176471f, 0.941176471f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.viewportWidth = Gdx.graphics.getWidth();
		camera.viewportHeight = Gdx.graphics.getHeight();

		camera.update();

		renderBackground();

		mainMenuStage.act();
		mainMenuStage.draw();
	}

	/**
	 * Renders the background at the correct aspect ratio
	 */
	private void renderBackground() {
		spriteBatch.begin();

		float width = Gdx.graphics.getWidth();
		float height = width * aspectRatioY;

		if (height < Gdx.graphics.getHeight()) {
			height = Gdx.graphics.getHeight();
			width = height / aspectRatioY;
		}

		spriteBatch.draw(backgroundSprite, 0, 0, width, height);
		spriteBatch.end();
	}

	/**
	 * Called whenever the window is resized
	 * Keeps the two buttons centred by updating the table to fill the screen (and so the center of the table = the center of the screeN-
	 *
	 * @param width  the current width of the window
	 * @param height the current height of the window
	 */
	@Override
	public void resize(int width, int height) {
		shapeRenderer.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		wrapper.setSize(width, height);
		mainMenuStage.getViewport().update(width, height, true);
	}

	/**
	 * Cleans up stages when window is closed to allow for clean exit from program
	 */
	@Override
	public void dispose() {
		controller.getInputHandler().clear();
		mainMenuStage.dispose();
	}
}
