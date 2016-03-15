package com.b3.gui;

import com.b3.gui.components.LabelComponent;
import com.b3.search.Pseudocode;
import com.b3.util.Tuple;
import com.b3.util.Utils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class PseudocodeVisualiser extends Table implements Observer {

	private static PseudocodeVisualiser instance;

	private BitmapFont font;
	private Pixmap pixmap;
	private Table pseudocodeTable;

	private PseudocodeVisualiser() {
		font = Utils.getFont("monaco.ttf", 15);
		font.getData().markupEnabled = true;
		pixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
		pixmap.setColor(0.35f, 0.35f, 0.35f, 0.8f);
		pixmap.fill();

		Table descriptionTable = new Table();
		pseudocodeTable = new Table();
		PseudocodeVisualiser.setBackgroundColor(pseudocodeTable, 0.2f, 0.2f, 0.2f, 0.6f);

		// Add description labels
		LabelComponent descLbl1 = new LabelComponent("aller/Aller_Bd.ttf", 18, getDescription(0), Color.BLACK);
		descriptionTable.add(descLbl1.getComponent());
		descriptionTable.row();

		LabelComponent descLbl2 = new LabelComponent("aller/Aller_Bd.ttf", 18, getDescription(1), Color.BLACK);
		descriptionTable.add(descLbl2.getComponent()).padTop(15);

		add(descriptionTable);
		row();
		add(pseudocodeTable).padTop(60);
	}

	private static void setBackgroundColor(Table table, float r, float g, float b, float a) {
		Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
		pm1.setColor(r, g, b, a);
		pm1.fill();
		table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
	}

	private String getDescription(int index) {
		ArrayList<String> desc = new ArrayList<String>() {{
			add("Click on 'Begin' to visualise the pseudocode.\n" +
					"Lines will be highlighted at each tick\n" +
					"according to the algorithm.");

			add("Click on 'Manual inspect' to\nmanually control the search.");
		}};

		return desc.get(index);
	}

	public static PseudocodeVisualiser getInstance() {
		if (instance == null)
			instance = new PseudocodeVisualiser();
		return instance;
	}

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
		for (Tuple<String, Tuple<Boolean, Integer>> line : pseudocode.getLines()) {
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
		}
	}
}
