package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourPathFind;
import com.b3.gui.help.HelpBox;
import com.b3.gui.sidebars.SideBarCompareMode;
import com.b3.input.InputHandler;
import com.b3.input.WorldSelectionHandler;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class CompareMode extends Mode {

	private SideBarCompareMode sideBar;
	private HelpBox helpBox;
	private List<Agent> agents;

	public CompareMode(MainGame game) {
		super(ModeType.COMPARE, game, "core/assets/world/world-compare.tmx", 29.7f);
		agents = new ArrayList<>(3);
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
		WorldGraph graph = world.getWorldGraph();

		boolean allArrived = graph.getAllSearchAgents()
				.stream()
				.allMatch(a -> {
					BehaviourPathFind behaviour = (BehaviourPathFind) a.getBehaviour();
					return behaviour.hasArrived();
				});


		if (!allArrived)
			return;

		// todo user selects with mouse
		// todo correspond to search agent's segment

		graph.clearAllSearches();
		for (Agent agent : agents) {
			BehaviourPathFind oldBehaviour = (BehaviourPathFind) agent.getBehaviour();
			Point oldEnd = oldBehaviour.getSearchTicker().getEnd().getPoint();
			Vector2 oldEndVector = new Vector2(oldEnd.x, oldEnd.y);
			Point oldStart = oldBehaviour.getSearchTicker().getStart().getPoint();
			Vector2 oldStartVector = new Vector2(oldStart.x, oldStart.y);

			BehaviourPathFind newBehaviour = new BehaviourPathFind(agent,
					oldEndVector,
					oldStartVector,
					oldBehaviour.getSearchTicker().getAlgorithm(),
					world);
			agent.setBehaviour(newBehaviour);

			graph.setCurrentSearch(agent, newBehaviour.getSearchTicker());
		}

	}

	@Override
	protected void initialise() {
	}

	@Override
	public void finishInitialisation() {

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
		agents.add(agent);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		sideBar.resize(width, height);
	}
}
