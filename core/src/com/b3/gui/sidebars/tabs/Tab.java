package com.b3.gui.sidebars.tabs;

import com.b3.gui.components.GUIComponent;
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
	protected SideBar parent;

	/**
	 * @param skin           The libGDX skin
	 * @param font           The font to apply
	 * @param preferredWidth The tab width
	 * @param parent         The {@link SideBar} which contains this tab
	 * @param data           Additional data
	 */
	public Tab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
		tab = new Table();
		tab.setFillParent(true);
		tab.pad(20);

		this.parent = parent;
	}

	/**
	 * @return The inner tab representation
	 */
	public Table getTab() {
		return tab;
	}

	public SideBar getParent() {
		return parent;
	}

	public void addComponent(GUIComponent component, int alignment, float preferredWidth, float spaceTop, float spaceRight, float spaceBottom, float spaceLeft) {
		tab.add(component.getComponent())
				.align(alignment)
				.maxWidth(preferredWidth)
				.space(spaceTop, spaceLeft, spaceBottom, spaceRight);
		tab.row();
	}
}
