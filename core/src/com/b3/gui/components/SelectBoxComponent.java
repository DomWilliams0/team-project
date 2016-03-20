package com.b3.gui.components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * Represents a dropdown menu.
 *
 * @author oxe410
 */
public class SelectBoxComponent extends GUIComponent {

	private Array items;
	private SelectBox selectBox;

	/**
	 * Creates a selectbox component
	 *
	 * @param skin  The selectbox libGDX skin
	 * @param font  The font to apply
	 * @param items The items to visualise
	 */
	public SelectBoxComponent(Skin skin, BitmapFont font, Array items) {
		this.items = items;

		SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
		skin.add("default", font, BitmapFont.class);
		selectBoxStyle.font = skin.getFont("default");
		selectBoxStyle.fontColor = Color.BLACK;
		selectBoxStyle.background = skin.getDrawable("selectbox_01");
		selectBoxStyle.listStyle = new List.ListStyle();
		selectBoxStyle.listStyle.font = skin.getFont("default");
		selectBoxStyle.listStyle.fontColorSelected = Color.DARK_GRAY;
		selectBoxStyle.listStyle.fontColorUnselected = Color.GRAY;
		selectBoxStyle.listStyle.selection = skin.getDrawable("button_01");
		selectBoxStyle.listStyle.background = skin.getDrawable("selectbox_01");
		selectBoxStyle.scrollStyle = new ScrollPane.ScrollPaneStyle();
		skin.add("default", selectBoxStyle);

		selectBox = new SelectBox(skin);
		setItems(items);
	}

	/**
	 * @return An {@link Array} of the selectable items in the drop down list.
	 */
	public Array getItems() {
		return items;
	}

	/**
	 * Sets the selectable items in the drop down list.
	 *
	 * @param items The new items to appear in the dropdown list.
	 */
	public void setItems(Array items) {
		selectBox.setItems(items);
	}

	/**
	 * @return The item currently selected by the user from the dropdown list.
	 */
	public Object getSelected() {
		return selectBox.getSelected();
	}

	/**
	 * Sets the currently selected item in the dropdown list.
	 * If it is not an item in the list the first item will be selected.
	 *
	 * @param selected The new item.
	 */
	public void setSelected(Object selected) {
		selectBox.setSelected(selected);
	}

	/**
	 * @return The inner component
	 */
	@Override
	public Actor getComponent() {
		return selectBox;
	}
}
