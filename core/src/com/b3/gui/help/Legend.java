package com.b3.gui.help;

import com.b3.gui.components.LabelComponent;
import com.b3.search.WorldGraphRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Provides an explanation of what colours in the simulation mean
 * i.e. the colours used to display the nodes or highlight in the {@link com.b3.gui.VisScrollPane}s.
 * This {@link Legend} cannot be altered besides changing the colours in {@link WorldGraphRenderer}
 * <p>
 * @author lxd417
 */
public class Legend extends Table {
	
	private Pixmap pm;
	private Skin skin;

	/**
	 * Create a new {@link Legend}, using a given {@link Skin}
	 *
	 * @param skin The {@link Skin} with which to render this {@link Legend}.
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
	 * Initialise the components of this {@link Legend}
	 * Populate it with data.
	 */
	private void initComponents() {
		//Encapsulate the title in a label and add it
		LabelComponent lbl = new LabelComponent("aller/Aller_Rg.ttf", 20, "Legend:", Color.WHITE);
		add(lbl.getComponent());
		row();

		//Visited
		addLegend("Visited set", WorldGraphRenderer.VISITED_COLOUR);

		//Just Expanded
		addLegend("Just Expanded", WorldGraphRenderer.JUST_EXPANDED_COLOUR);

		//New frontier
		addLegend("New Frontier Node", WorldGraphRenderer.LAST_FRONTIER_COLOUR);

		//Frontier
		addLegend("Frontier Nodes", WorldGraphRenderer.FRONTIER_COLOUR);

		pm.dispose();
	}

	/**
	 * Add text to this {@link Table}, highlighted in a given {@link Color}.
	 * Also adds a {@link Table#row()} to this table.
	 *
	 * @param text The text to display
	 * @param c    The colour in which to highlight the text
	 */
	private void addLegend(String text, Color c) {
		//set the colour
		pm.setColor(c);
		pm.fill();
		//setup the colour in a drawable
		TextureRegionDrawable trd = new TextureRegionDrawable(new TextureRegion(new Texture(new PixmapTextureData(pm, null, false, false))));

		//setup the wrapping table
		Table t = new Table(skin);
		//apply the colour
		t.setBackground(trd);

		//setup text in wrapping label
		LabelComponent lbl = new LabelComponent("aller/Aller_Rg.ttf", 16, text, Color.BLACK);

		//add the components
		t.add(lbl.getComponent()).left();
		add(t).left();
		row();
	}

}
