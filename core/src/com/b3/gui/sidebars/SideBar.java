package com.b3.gui.sidebars;

import com.b3.gui.GuiUtils;
import com.b3.gui.TabbedPane;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.sidebars.tabs.CompareModeSettingsTab;
import com.b3.gui.sidebars.tabs.LearningModeSettingsTab;
import com.b3.gui.sidebars.tabs.PracticeModeSettingsTab;
import com.b3.gui.sidebars.tabs.Tab;
import com.b3.mode.ModeType;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Easy peazy basis of a sidebar.
 * Ensures consistent styles.
 */
public class SideBar extends Table {

	protected Stage stage;
	protected World world;

	protected TabbedPane tabbedPane;
	protected ButtonComponent triggerBtn;
	protected Map<String, Tab> tabs;

	protected Skin skin;
	protected BitmapFont font;
	protected String background;

	protected float preferredWidth;
	protected boolean left;
	protected boolean isOpen;

	/**
	 * Creates a new sidebar
	 *
	 * @param stage          the stage that will contain this sidebar, used for resizing.
	 * @param world          the world that this sidebar is linked to
	 * @param left           if true then on left, otherwise right
	 * @param background     the background image that this will use for
	 * @param preferredWidth the preferred width of this sidebar
	 */
	SideBar(Stage stage,
	        World world,
	        boolean left,
	        String background,
	        float preferredWidth) {

		this.stage = stage;
		this.preferredWidth = preferredWidth;
		this.world = world;
		this.tabs = new LinkedHashMap<>();
		this.left = left;
		this.background = background;
		isOpen = false;

		// Setup the skin
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
		this.skin = new Skin(atlas);
		this.font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);
		//this.skin.add("default", font, BitmapFont.class);

		// Set position and size
		setPosition(left ? -preferredWidth : Gdx.graphics.getWidth(), 0);
		setSize(preferredWidth, Gdx.graphics.getHeight());
	}


	/**
	 * Factory method for creating a sidebar for a specific mode
	 *
	 * @param mode  The mode to create a sidebar for
	 * @param world The current world
	 * @param stage The stage that owns this sidebar
	 * @return The newly created sidebar
	 */
	public static SideBar createModeSidebar(ModeType mode, World world, Stage stage) {

		String background;
		float width;
		Class<? extends Tab> tabClass;

		switch (mode) {
			case LEARNING:
				background = "window_03";
				width = 320f;
				tabClass = LearningModeSettingsTab.class;
				break;
			case PRACTICE:
				background = "window_02";
				width = 460f;
				tabClass = PracticeModeSettingsTab.class;
				break;
			case COMPARE:
				background = "window_03";
				width = 230f;
				tabClass = CompareModeSettingsTab.class;
				break;
			default:
				throw new IllegalArgumentException("There is no sidebar for mode " + mode);
		}

		SideBar sideBar = new SideBar(stage, world, true, background, width);
		Map<String, Object> data = new HashMap<String, Object>() {{
			put("world", world);
		}};

		Tab tab = null;

		try {
			Constructor<? extends Tab> constructor = tabClass.getConstructor(Skin.class, BitmapFont.class,
					float.class, SideBar.class, Map.class);

			tab = constructor.newInstance(sideBar.skin, sideBar.font, width, sideBar, data);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
			System.err.println("Could not create sidebar for mode " + mode);
			e.printStackTrace();
		}

		if (tab != null)
			sideBar.tabs.put("Settings", tab);

		sideBar.initComponents();

		return sideBar;
	}


	/**
	 * Create the basic components of the sidebar.
	 * E.g.
	 * <ol>
	 * <li>The background</li>
	 * <li>{@link #tabs}, if there are any.</li>
	 * <li>The button to open and close the sidebar.</li>
	 * </ol>
	 */
	protected void initComponents() {
		// Set a default background colour
		GuiUtils.setBackgroundColor(this, 0.56f, 0.69f, 0.83f, 1);

		// ===================
		// === TABBED PANE ===
		// ===================

		TabbedPane.TabbedPaneStyle tabbedPaneStyle = new TabbedPane.TabbedPaneStyle();
		skin.add("default", font, BitmapFont.class);
		tabbedPaneStyle.font = font;
		tabbedPaneStyle.bodyBackground = skin.getDrawable("knob_06");
		tabbedPaneStyle.titleButtonSelected = skin.getDrawable("button_02");
		tabbedPaneStyle.titleButtonUnselected = skin.getDrawable("button_01");
		skin.add("default", tabbedPaneStyle);
		tabbedPane = new TabbedPane(skin);

		// ============
		// === TABS ===
		// ============

		if (tabs != null) {
			for (String tabName : tabs.keySet()) {
				tabbedPane.addTab(tabName, tabs.get(tabName).getTab());
			}
		}

		// ======================
		// === TRIGGER BUTTON ===
		// ======================

		triggerBtn = new ButtonComponent(skin, font, left ? ">" : "<");
		triggerBtn.getComponent().setPosition(left ? -20 : Gdx.graphics.getWidth() - triggerBtn.getComponent().getWidth() + 20, Gdx.graphics.getHeight() / 2);
		triggerBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (!isOpen) {
					open();
				} else {
					close();
				}

			}
		});

		add(tabbedPane).maxWidth(preferredWidth);
		background(skin.getDrawable(background));
		this.stage.addActor(triggerBtn.getComponent());
	}

	/**
	 * Opens the sidebar and shows its content.
	 */
	public void open() {
		TextButton _triggerBtn = triggerBtn.getComponent();

		if (left) {
			setX(0);
			setY(0);
			_triggerBtn.setText("<");
			_triggerBtn.setX(preferredWidth - 20);
		} else {
			setX(Gdx.graphics.getWidth() - preferredWidth);
			setY(0);
			_triggerBtn.setText(">");
			_triggerBtn.setX(Gdx.graphics.getWidth() - preferredWidth - _triggerBtn.getWidth() + 20);
		}

		isOpen = true;
	}

	/**
	 * Closes the sidebar and hides the content.
	 */
	public void close() {
		TextButton _triggerBtn = triggerBtn.getComponent();

		if (left) {
			setX(-preferredWidth);
			_triggerBtn.setText(">");
			_triggerBtn.setX(-20);
		} else {
			setX(Gdx.graphics.getWidth());
			_triggerBtn.setText("<");
			_triggerBtn.setX(Gdx.graphics.getWidth() - _triggerBtn.getWidth() + 20);
		}

		isOpen = false;
	}

	/**
	 * Toggles the sidebar {@link #open() open} or {@link #close() closed}.
	 */
	public void toggle() {
		if (isOpen) {
			close();
		} else {
			open();
		}
	}

	/**
	 * @return the preferred width of this sidebar, if space allows it will take up this amount of space max
	 */
	public float getPreferredWidth() {
		return preferredWidth;
	}

	/**
	 * @return the {@link World} that this sidebar is linked to
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * Sets the world for this sidebar
	 *
	 * @param world the {@link World} to use with this sidebar
	 */
	public void setWorld(World world) {
		this.world = world;
	}

	public ButtonComponent getTriggerBtn() {
		return triggerBtn;
	}

	/**
	 * Whether the sidebar is open.
	 *
	 * @return <code>true</code> if the sidebar is open;
	 * <code>false</code> otherwise.
	 */
	public boolean isOpen() {
		return isOpen;
	}

	/**
	 * Adds a tab to the sidebar
	 *
	 * @param tab the {@link Table} to add to the pane
	 */
	public void addTab(Table tab) {
		tabbedPane.addTab(tab.getName(), tab);
	}

	/**
	 * Should be called whenever the window resizes.
	 *
	 * @param width  The new width of the window.
	 * @param height The new height of the window.
	 */
	public void resize(int width, int height) {
		setHeight(height);
		triggerBtn.getComponent().setY(height / 2);

		TextButton _triggerBtn = triggerBtn.getComponent();
		if (isOpen) {
			if (left) {
				setX(0);
				setY(0);
				_triggerBtn.setX(preferredWidth - 20);
			} else {
				setX(width - preferredWidth);
				setY(0);
				_triggerBtn.setX(width - preferredWidth - _triggerBtn.getWidth() + 20);
			}
		} else {
			if (left) {
				setX(-preferredWidth);
				_triggerBtn.setX(-20);
			} else {
				setX(width);
				_triggerBtn.setX(width - _triggerBtn.getWidth() + 20);
			}
		}
	}

	public void render() {
	}

}
