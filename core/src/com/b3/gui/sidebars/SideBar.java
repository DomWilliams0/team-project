package com.b3.gui.sidebars;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.ArrayList;

public abstract class SideBar extends Table {
    public abstract float getPreferredWidth();
    public abstract void resize(int width, int height);
    public abstract void render();
    protected abstract void initComponents();
    protected void initComponents(ArrayList<Table> additionalTabs) {}
}
