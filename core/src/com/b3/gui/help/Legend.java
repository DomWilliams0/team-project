package com.b3.gui.help;

import com.b3.gui.components.LabelComponent;
import com.b3.search.WorldGraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Provides an explanation of what colours in the software mean
 * i.e. the colours used to display the nodes or highlight in the sidebar.
 * This legend cannot be altered besides changing the colours in WorldGraph
 *
 * Created by Ben, worked on mostly by Lewis.
 */
public class Legend extends Table {
	private Pixmap pm;
	private TextureRegionDrawable trd;
	private Skin skin;

	/**
	 * Create a new legend, using a given skin
	 * @param skin The skin with which to render this legend.
	 */
	public Legend(Skin skin) {
		super(skin);
		this.skin = skin;
		pm = new Pixmap(1, 1, Pixmap.Format.RGB565);

		//setup this and its parts
		left();
		initComponents();
	}

	/**
	 * Initialise the components of this legend
	 * Populate it with data.
	 */
	private void initComponents() {
		LabelComponent lbl = new LabelComponent("aller/Aller_Rg.ttf", 20, "Legend:", new Color(0xa0a0ffff));
		add(lbl.getComponent());
		row();

		//Visited
		addLegend("Visited set", WorldGraph.VISITED_COLOUR);

		//Just Expanded
		addLegend("Just Expanded", WorldGraph.JUST_EXPANDED_COLOUR);

		//New frontier
		addLegend("New Frontier Node", WorldGraph.LAST_FRONTIER_COLOUR);

		//Frontier
		addLegend("Frontier Nodes", WorldGraph.FRONTIER_COLOUR);

		pm.dispose();
	}

	private void addLegend(String text, Color c) {
		pm.setColor(c);
		pm.fill();
		trd = new TextureRegionDrawable(new TextureRegion(new Texture(new PixmapTextureData(pm, null, false, false))));
		Table t = new Table(skin);
		t.setBackground(trd);
		LabelComponent lbl = new LabelComponent("aller/Aller_Rg.ttf", 16, text, Color.BLACK);
		t.add(lbl.getComponent()).left();
		add(t).left();
		row();
	}

}
