package com.b3.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class SelectBoxComponent {

    private Array items;
    private SelectBox selectBox;

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

    public SelectBox getSelectBox() {
        return selectBox;
    }

    public Array getItems() {
        return items;
    }

    public void setItems(Array items) {
        selectBox.setItems(items);
    }

    public Object getSelected() {
        return selectBox.getSelected();
    }

    public void setSelected(Object selected) {
        selectBox.setSelected(selected);
    }

    public void addListener(ChangeListener listener) {
        selectBox.addListener(listener);
    }
}
