package com.b3.gui;

import com.b3.MainGame;
import com.b3.gui.components.LabelComponent;
import com.b3.gui.sidebars.tabs.NodesTab;
import com.b3.mode.ModeType;
import com.b3.search.Pseudocode;
import com.b3.util.Tuple;
import com.b3.util.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Represents the pseudocode visualisation box
 *
 * @author oxe410 nbg481
 */
public class PseudocodeVisualiser extends Table implements Observer {

	private static PseudocodeVisualiser instance;

	private BitmapFont font;
	private Pixmap pixmap;
	private Table pseudocodeTable;
	private NodesTab nodesTab;

	/**
	 * Creates the default {@link PseudocodeVisualiser} object
	 */
	private PseudocodeVisualiser() {
		font = Utils.getFont("monaco.ttf", 15);
		font.getData().markupEnabled = true;
		pixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
		pixmap.setColor(0.35f, 0.35f, 0.35f, 0.8f);
		pixmap.fill();

		Table descriptionTable = new Table();
		pseudocodeTable = new Table();
		GuiUtils.setBackgroundColor(pseudocodeTable, 0.2f, 0.2f, 0.2f, 0.6f);

		// Add description labels
		LabelComponent descLbl1 = new LabelComponent("aller/Aller_Bd.ttf", 18, getDescription(0), Color.BLACK);
		descriptionTable.add(descLbl1.getComponent()).padTop(20);
		descriptionTable.row();

		add(descriptionTable);
		row();
		add(pseudocodeTable).padTop(25);
	}

	/**
	 * Returns the description to display on top of the pseudocode
	 *
	 * @param index Defines which description to choose
	 * @return See above
	 */
	private String getDescription(int index) {
		ArrayList<String> desc = new ArrayList<String>() {{
			add("Pseudocode visualisation");
		}};

		return desc.get(index);
	}

	/**
	 * @return The default {@link PseudocodeVisualiser} object
	 */
	public static PseudocodeVisualiser getInstance() {
		if (instance == null)
			instance = new PseudocodeVisualiser();
		return instance;
	}

	/**
	 * @param skin The libGDX skin
	 * @return The default {@link PseudocodeVisualiser} object
	 */
	public static PseudocodeVisualiser getInstance(Skin skin) {
		if (instance == null) {
			instance = new PseudocodeVisualiser();
		}

		instance.setSkin(skin);
		return instance;
	}

	@Override
	public void update(Observable o, Object arg) {
		Pseudocode pseudocode = (Pseudocode) o;

		pseudocodeTable.clear();
		List<Tuple<String, Tuple<Boolean, Integer>>> lines = pseudocode.getLines();
		for (int i = 0; i < lines.size(); i++) {
			Tuple<String, Tuple<Boolean, Integer>> line = lines.get(i);
			// Set label
			Label.LabelStyle labelStyle = new Label.LabelStyle();
			labelStyle.font = font;
			labelStyle.background = line.getSecond().getFirst() ?
					new TextureRegionDrawable(new TextureRegion(new Texture(pixmap))) :
					null;

			// Add line to table
			Label label = new Label(line.getFirst(), labelStyle);
			pseudocodeTable.add(label).align(Align.left).padLeft(line.getSecond().getSecond() * 20);
			pseudocodeTable.row().align(Align.left).fill();

			final int lineForListener = i;
			label.addListener(new ClickListener() {

				/**
				 * Updates the pseudocode line to show the current value of all the variables in the string
				 */
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
					if (lineForListener != 4 && lineForListener != 0 && MainGame.getCurrentMode() == ModeType.LEARNING) {
						Tuple<String, String> replacement = pseudocode.getImportantInfo(lineForListener);
						String currentText = label.getText().toString();
						String newText = parseAndChange(currentText, replacement.getFirst(), replacement.getSecond());
						if (replacement.getFirst().length() > 11 && !replacement.getSecond().equals("-")) {
							nodesTab.setSize(replacement.getFirst().length() * 10);
						}
						label.setText(newText);
					}
					return super.touchDown(event, x, y, pointer, button);
				}
			});
		}
	}

	/**
	 * Parses the text and replaces all instances of itemToReplace with toReplaceWith in currentText
	 *
	 * @param currentText   the current text in the label
	 * @param toReplaceWith the substring to insert into the current text
	 * @param itemToReplace the substring to remove fromm the current text and insert toReplaceWith into
	 * @return The processed string
	 */
	private String parseAndChange(String currentText, String toReplaceWith, String itemToReplace) {
		if (toReplaceWith.equals("") || toReplaceWith.equals(" "))
			return currentText;

		if (toReplaceWith.equals("+"))
			return currentText;

		if (itemToReplace.equals("-"))
			return toReplaceWith;

		// Update conflicts for A*
		String tempchange = currentText.replace(itemToReplace, toReplaceWith);
		tempchange = tempchange.replace("frotier", "frontier");
		tempchange = tempchange.replace("fro" + toReplaceWith + "tier", "frontier");
		tempchange = tempchange.replace("ca" + toReplaceWith + "eFro" + toReplaceWith, "cameFrom");
		tempchange = tempchange.replace("return", "return");
		// Update conflicts for DFS

		return tempchange;
	}

	public void setNodesTab(NodesTab nodesTab) {
		this.nodesTab = nodesTab;
	}

}
