package com.b3.gui.help;

import com.b3.search.WorldGraph;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Legend extends Table {

	public Legend(Skin skin) {
		super(skin);
		this.setSkin(skin);
		add("Legend:");
		row();
		Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGB565);
		TextureRegionDrawable backgroundTexture = null;

		//Visited
		pm.setColor(WorldGraph.VISITED_COLOUR);
		pm.fill();
		backgroundTexture = new TextureRegionDrawable(new TextureRegion(new Texture(new PixmapTextureData(pm, null, false, false))));
		Table vis = new Table(skin);
		vis.setBackground(backgroundTexture);
		vis.add("Visited set").left();
		add(vis).left();
		row();

		//Just Expanded
		pm.setColor(WorldGraph.JUST_EXPANDED_COLOUR);
		pm.fill();
		backgroundTexture = new TextureRegionDrawable(new TextureRegion(new Texture(new PixmapTextureData(pm, null, false, false))));
		Table jex = new Table(skin);
		jex.setBackground(backgroundTexture);
		jex.add("Just Expanded").left();
		add(jex).left();
		row();

		//Just Expanded
		pm.setColor(WorldGraph.LAST_FRONTIER_COLOUR);
		pm.fill();
		backgroundTexture = new TextureRegionDrawable(new TextureRegion(new Texture(new PixmapTextureData(pm, null, false, false))));
		Table nf = new Table(skin);
		nf.setBackground(backgroundTexture);
		nf.add("New Frontier Node").left();
		add(nf).left();
		row();

		//Just Expanded
		pm.setColor(WorldGraph.FRONTIER_COLOUR);
		pm.fill();
		backgroundTexture = new TextureRegionDrawable(new TextureRegion(new Texture(new PixmapTextureData(pm, null, false, false))));
		Table fr = new Table(skin);
		fr.setBackground(backgroundTexture);
		fr.add("Frontier Nodes").left();
		add(fr).left();
		row();


		left();
	}

}
