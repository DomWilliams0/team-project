package com.b3.gui.sidebars.tabs;

import com.b3.gui.PseudocodeVisualiser;
import com.b3.gui.components.ButtonComponent;
import com.b3.search.SearchTicker;
import com.b3.world.World;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.Map;

public class PseudocodeTab implements Tab {

	private Table pseudocodeTable;
	private ButtonComponent inspectSearchBtn;
	private ButtonComponent manualAutoBtn;
	private ButtonComponent nextBtn;

	public PseudocodeTab(Skin skin, BitmapFont font, Map<String, Object> data) {
		pseudocodeTable = new Table();
		pseudocodeTable.setFillParent(true);
		//pseudocodeTable.pad(20);

		// Extract data
		// ------------
		World world = (World) data.get("world");

		// Pseudocode visualiser
		// ---------------------
		PseudocodeVisualiser pseudocodeVisualiser = PseudocodeVisualiser.getInstance(skin);
		pseudocodeTable.add(pseudocodeVisualiser).spaceBottom(30).row();

		// Next button
		// -----------
		nextBtn = new ButtonComponent(skin, font, "Next");
		nextBtn.getComponent().setVisible(false);
		nextBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
				ticker.setInspectSearch(true);
				ticker.tick(true);
			}
		});

		// Manual/Automatic inspection
		// ---------------------------
		manualAutoBtn = new ButtonComponent(skin, font, "Manual inspect");
		manualAutoBtn.setData(true);
		manualAutoBtn.getComponent().setVisible(false);
		manualAutoBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if ((Boolean) manualAutoBtn.getData()) {
					// Currently automatic -> manual
					SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
					ticker.pause(1);
					ticker.setUpdated(true);
					ticker.setInspectSearch(true);

					nextBtn.getComponent().setVisible(true);
					manualAutoBtn.setData(false);
					manualAutoBtn.setText("Automatic inspect");
				} else {
					// Currently manual -> automatic
					SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
					ticker.resume(1);

					nextBtn.getComponent().setVisible(false);
					manualAutoBtn.setData(true);
					manualAutoBtn.setText("Manual inspect");
				}
			}
		});

		// Inspect search button (start/stop)
		// ----------------------------------
		inspectSearchBtn = new ButtonComponent(skin, font, "Begin");
		SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
		ticker.addObserver(inspectSearchBtn);

		inspectSearchBtn.setUpdateListener(observable -> {
			boolean started = (Boolean) inspectSearchBtn.getData();

			if (started) {
				nextBtn.getComponent().setVisible(ticker.isPaused());
				manualAutoBtn.setData(!ticker.isPaused());
				manualAutoBtn.setText(ticker.isPaused() ? "Automatic inspect" : "Manual inspect");
			}
			return null;
		});

		inspectSearchBtn.setData(false);
		inspectSearchBtn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean currentlyStarted = (Boolean) inspectSearchBtn.getData();

				SearchTicker ticker = world.getWorldGraph().getCurrentSearch();

				ticker.setInspectSearch(!currentlyStarted);
				ticker.resume(1);

				manualAutoBtn.getComponent().setVisible(!currentlyStarted);
				inspectSearchBtn.setData(!currentlyStarted);
				inspectSearchBtn.setText(currentlyStarted ? "Begin" : "Stop");

				// Clear pseudocode information
				if (currentlyStarted) {
					nextBtn.getComponent().setVisible(false);
					manualAutoBtn.setData(true);
					manualAutoBtn.setText("Manual inspect");
					world.getWorldGUI().setPseudoCode(false);
					ticker.clearPseudocodeInfo();
				}
			}
		});

		pseudocodeTable.add(inspectSearchBtn.getComponent()).spaceBottom(10).row();
		pseudocodeTable.add(manualAutoBtn.getComponent()).spaceBottom(10).row();
		pseudocodeTable.add(nextBtn.getComponent());
	}

	public ButtonComponent getInspectSearchBtn() {
		return inspectSearchBtn;
	}

	public ButtonComponent getManualAutoBtn() {
		return manualAutoBtn;
	}

	public ButtonComponent getNextBtn() {
		return nextBtn;
	}

	@Override
	public Table getTab() {
		return pseudocodeTable;
	}

}
