package com.b3.gui.sidebars.tabs;

import com.b3.MainGame;
import com.b3.gui.PseudocodeVisualiser;
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

/**
 * Represents a tab that holds the information about the nodes
 *
 * @author oxe410
 */
public class NodesTab extends Tab {

	private Table pseudocodeTable;
	private VisNodes ui;
	private ButtonComponent nextBtn;
	private int size;

	/**
	 * Create a {@link NodesTab} object
	 *
	 * @param skin           The libGDX skin
	 * @param font           The font to apply
	 * @param preferredWidth The width of the widget
	 * @param data           Additional data
	 */
	public NodesTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
		super(skin, font, preferredWidth, parent, data);

		// Extract data
		World world = (World) data.get("world");
		Stage stage = (Stage) data.get("stage");

		// Create the data table which will display the nodes
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
		skin.add("default", labelStyle);
		ui = new VisNodes(stage, skin, world);

		// Pseudocode
		pseudocodeTable = new Table();

		PseudocodeVisualiser pseudocodeVisualiser = PseudocodeVisualiser.getInstance(skin);
		pseudocodeVisualiser.setNodesTab(this);
		pseudocodeTable.add(pseudocodeVisualiser).row();

		// Next button
		nextBtn = new ButtonComponent(skin, font, "Next step");
		nextBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
				size = 0;
				ticker.tick(true);
			}
		});

		nextBtn.getComponent().setVisible(MainGame.getCurrentMode() != ModeType.PRACTICE);

		// put the nodes ui onto this
		tab.add(ui).maxWidth(preferredWidth);
		tab.row();
		tab.add(nextBtn.getComponent());
		tab.row();
	}

	/**
	 * @return The node visualisation/UI object
	 */
	public VisNodes getUI() {
		return ui;
	}

	public ButtonComponent getNextBtn() {
		return nextBtn;
	}

	public void setPseudocodeVisible(boolean enabled) {
		if (enabled && !pseudocodeTable.hasParent()) {
			tab.add(pseudocodeTable);
		} else if (!enabled && pseudocodeTable.isDescendantOf(tab)) {
			tab.removeActor(pseudocodeTable);
		}
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getSize() {
		return size;
	}
}
