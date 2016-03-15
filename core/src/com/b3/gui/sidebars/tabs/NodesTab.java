package com.b3.gui.sidebars.tabs;

import com.b3.gui.VisNodes;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.sidebars.SideBar;
import com.b3.mode.ModeType;
import com.b3.search.SearchTicker;
import com.b3.world.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Map;

public class NodesTab implements Tab {

	private Table nodesTab;
	private VisNodes ui;
	private ButtonComponent nextBtn;

	public NodesTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {

		// Extract data
		World world = (World) data.get("world");
		Stage stage = (Stage) data.get("stage");

		nodesTab = new Table();
		nodesTab.setFillParent(true);
		//nodesTab.pad(10);

		// Create the data table which will display the nodes
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
		skin.add("default", labelStyle);
		ui = new VisNodes(stage, skin, world);

		nextBtn = new ButtonComponent(skin, font, "Next step");
		nextBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
				ticker.tick(true);
			}
		});

		nextBtn.getComponent().setVisible(world.getMode() != ModeType.PRACTICE);

		//put the nodes ui onto this
		nodesTab.add(ui).maxWidth(preferredWidth).top().pad(20);
		nodesTab.row();
		nodesTab.add(nextBtn.getComponent());
	}

	public VisNodes getUI() {
		return ui;
	}

	public ButtonComponent getNextBtn() {
		return nextBtn;
	}

	@Override
	public Table getTab() {
		return nodesTab;
	}
}
