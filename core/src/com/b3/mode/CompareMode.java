package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourPathFind;
import com.b3.gui.help.HelpBox;
import com.b3.gui.sidebars.SideBarCompareMode;
import com.b3.input.InputHandler;
import com.b3.input.WorldSelectionHandler;
import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.badlogic.gdx.math.Vector2;

public class CompareMode extends Mode {

	private SideBarCompareMode sideBar;
	private HelpBox helpBox;

	public CompareMode(MainGame game) {
		super(ModeType.COMPARE, game, "core/assets/world/world-compare.tmx", 29.7f);
	}

	@Override
	protected void initSidebar() {
		super.initSidebar();
		sideBar = new SideBarCompareMode(sideBarStage, world);
		sideBar.setController(game);
		sideBarStage.addActor(sideBar);

		helpBox = new HelpBox(sideBarStage, world.getMode());
		sideBarStage.addActor(helpBox);
	}

	@Override
	protected void registerFurtherInputProcessors(InputHandler inputHandler) {
		// world clicking
		inputHandler.addProcessor(new WorldSelectionHandler(world));
	}

	@Override
	protected void tick(float delta) {
	}

	@Override
	protected void spawnInitialEntities() {

		SearchAlgorithm[] algorithms = {SearchAlgorithm.A_STAR, SearchAlgorithm.DEPTH_FIRST, SearchAlgorithm.BREADTH_FIRST};
		for (int i = 0; i < 3; i++) {
			int xOffset = i * 18;
			spawnAgent(
					new Vector2(12 + xOffset, 1),
					new Vector2(2 + xOffset, 29),
					algorithms[i]
			);
		}
	}

	private void spawnAgent(Vector2 startPos, Vector2 goalPos, SearchAlgorithm algorithm) {
		Agent agent = world.spawnAgent(startPos);
		BehaviourPathFind behaviour = new BehaviourPathFind(
				agent, startPos, goalPos, algorithm,
				world);
		agent.setBehaviour(behaviour);

		SearchTicker ticker = behaviour.getSearchTicker();
		world.getWorldGraph().setCurrentSearch(agent, ticker);
		// todo add to list of rendered tickers
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		sideBar.resize(width, height);
	}
}
