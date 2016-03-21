package com.b3.gui.help;

import com.b3.MainGame;
import com.b3.gui.components.LabelComponent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Provides help for the user based on the {@link com.b3.mode.ModeType} of the simulation.
 * Gives instructions on how the mode can be used and interacted with
 * And a {@link Legend} to explain meaning of colours.
 * <p>
 * @author lxd417
 */
public class HelpBox extends Table {

	private float preferredHeight;
	private Table worldT;
	private Table worldNodesT;
	private Table sidebarsT;

	private final String controls = "Move around the world using the arrow keys.\n" +
			"Zoom in and out using the mouse wheel or +/-\n" +
			"Zoom out far to enter contrast view.\n";
	private final String visbarhelp = "In the visualisation sidebar,\n" +
			"Click nodes to display details over it in the world.\n" +
			"Hover over a node to highlight it briefly in the world.\n" +
			"Press Next Step to stepthrough the algorithm while paused.";
	private final String setbarhelp = "The settings sidebar allows you to edit settings\n" +
			"such as simulation speed and search speed.\n" +
			"You can also play/pause the search here.";

	/**
	 * Create a new {@link HelpBox}.
	 * Bases its text on the current {@link com.b3.mode.ModeType} in {@link MainGame#getCurrentMode()}
	 */
	public HelpBox() {
		switch (MainGame.getCurrentMode()) {
			case LEARNING:
				preferredHeight = 390;
				break;
			case PRACTICE:
				preferredHeight = 340;
				break;
			case COMPARE:
				preferredHeight = 300;
				break;
		}

		initComponents();
		bottom();
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	private void initComponents() {
		// ===============
		// === STYLING ===
		// ===============

		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("gui/ui-blue.atlas"));
		Skin skin = new Skin(atlas);
		BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);
		skin.add("default", font, BitmapFont.class);
		Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);
		skin.add("default", style);

		this.setSkin(skin);

		// ==========================
		// === HELP TEXT & LEGEND ===
		// ==========================

		switch (MainGame.getCurrentMode()) {
			case LEARNING:
				setupLM();
				break;
			case COMPARE:
				setupCM();
				break;
			case PRACTICE:
				setupPM();
				break;
		}

		fillThis();

		row();
	}

	/**
	 * Place the help tables onto this help box
	 * Formatted in the desired way
	 */
	private void fillThis() {
		//top row for sidebar info then interacting with the nodes in the world
		add(sidebarsT);
		String padding = "   ";
		add(padding);
		add(worldNodesT);

		row();

		//bottom row for legend & controls for the world
		add(new Legend(getSkin())).left();
		add(padding);
		add(worldT);
	}

	/**
	 * Resize this menu
	 * Should be called whenever the window is resized.
	 *
	 * @param width  Window width
	 * @param height Window height
	 */
	public void resize(int width, int height) {
		setX(0);
		setSize(width, preferredHeight);
	}

	/**
	 * Setup the help box for {@link com.b3.mode.LearningMode}.
	 */
	private void setupLM() {
		addHelp(this, "This mode is to learn about a specific search algorithm in depth, in a small, focused world.", true)
				.colspan(5);

		row();

		worldT = new Table(getSkin());
		addHelp(worldT, "Interacting with the world:", true);
		worldT.row().row();
		addHelp(worldT, controls +
				"Open left sidebar for settings,\n" +
				"Open right sidebar for data collections."
		);

		worldNodesT = new Table(getSkin());
		addHelp(worldNodesT, "Interacting with the world nodes:", true);
		worldNodesT.row().row();
		addHelp(worldNodesT, "Click nodes to view details and highlight\n" +
				"it in the Visualisation sidebar.\n" +
				"Click again to view more details about it.\n" +
				"Hover over a node to display its coordinates.\n" +
				"Right click a node to set\n" +
				"it as the next destination."
		);

		sidebarsT = new Table(getSkin());
		addHelp(sidebarsT, "The sidebars:", true);
		sidebarsT.row().row();
		addHelp(sidebarsT, visbarhelp + "\n" +
				setbarhelp
		);

	}

	/**
	 * Setup the help box for {@link com.b3.mode.CompareMode}.
	 */
	private void setupCM() {
		addHelp(this, "This mode is to compare algorithms side-by-side in a large, lively world.", true)
				.colspan(5);

		row();

		worldT = new Table(getSkin());
		addHelp(worldT, "Interacting with the world:", true);
		worldT.row().row();
		addHelp(worldT, controls +
				"Open left sidebar for settings"
		);

		worldNodesT = new Table(getSkin());
		addHelp(worldNodesT, "Interacting with the world nodes:", true);
		worldNodesT.row().row();
		addHelp(worldNodesT,
				"Hover over a node to display its coordinates."
		);

		sidebarsT = new Table(getSkin());
		addHelp(sidebarsT, "The sidebar:", true);
		sidebarsT.row().row();
		addHelp(sidebarsT, setbarhelp);
	}

	/**
	 * Setup the help box for {@link com.b3.mode.PracticeMode}.
	 */
	private void setupPM() {
		addHelp(this, "This mode is to practice your knowledge in a small, focused world", true)
				.colspan(5);

		row();

		worldT = new Table(getSkin());
		addHelp(worldT, "Interacting with the world:", true);
		worldT.row().row();
		addHelp(worldT, controls +
				"Open right sidebar for data collections"
		);

		worldNodesT = new Table(getSkin());
		addHelp(worldNodesT, "Interacting with the world nodes:", true);
		worldNodesT.row().row();
		addHelp(worldNodesT, "Click a node to expand it, or add it to the frontier.\n" +
				"Hover over a node to display its coordinates.\n" +
				"Right click a node to set\n" +
				"it as the next destination."
		);

		sidebarsT = new Table(getSkin());
		addHelp(sidebarsT, "The sidebar:", true);
		sidebarsT.row().row();
		addHelp(sidebarsT, visbarhelp + "\n" +
				"You can also edit game speed and next\n" +
				"search algorithm in the sidebar."
		);

	}

	/**
	 * Adds some help text to the given table, in a label component formatted in the appropriate way
	 *
	 * @param t       The table to add the text to
	 * @param s       The string to put in
	 * @param isTitle Whether this is a title
	 * @return The cell created in the table
	 */
	private Cell addHelp(Table t, String s, boolean isTitle) {
		Color c = isTitle ? Color.WHITE : Color.CYAN;
		int size = isTitle ? 20 : 16;
		LabelComponent lbl = new LabelComponent("aller/Aller_Rg.ttf", size, s, c);
		return t.add(lbl.getComponent());
	}

	/**
	 * Adds some help text to the given table, in a label component formatted in the appropriate way
	 * Assumes the text is not a title as defined by {@link #addHelp(Table, String, boolean)}.
	 *
	 * @param t       The table to add the text to
	 * @param s       The string to put in
	 * @return The cell created in the table
	 */
	private Cell addHelp(Table t, String s) {
		return addHelp(t, s, false);
	}
	
}
