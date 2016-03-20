package com.b3.gui.sidebars.tabs;

import com.b3.gui.sidebars.SideBar;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.Map;

/**
 * Describes a tab represented as a {@link Table} object
 *
 * @author oxe410
 */
public abstract class Tab {

	protected Table tab;


	/**
	 * @param skin           The libGDX skin
	 * @param font           The font to apply
	 * @param preferredWidth The tab width
	 * @param parent         The {@link SideBar} which contains this tab
	 * @param data           Additional data
	 */
	public Tab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
		tab = new Table();
	}


	/**
	 * @return The inner tab representation
	 */
	public Table getTab() {
		return tab;
	}
}
