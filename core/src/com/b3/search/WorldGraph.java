package com.b3.search;

import com.b3.entity.Agent;
import com.b3.search.util.SearchAlgorithm;
import com.b3.world.World;
import com.b3.world.building.Building;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The rendered graph, that holds all running searches
 *
 * @author nbg481 dxw405 oxe410
 */
public class WorldGraph extends Graph {
	
	private final WorldGraphRenderer renderer;

	private SearchTicker latestSearchTicker;
	private Agent latestSearchAgent;
	private final Map<Agent, SearchTicker> searchTickers;

	private Vector2 wantedNextDestination;

	//search algorithm wanted for learning mode only (Leave as null if compare mode)
	private SearchAlgorithm learningModeNext = null;

	/**
	 * Constructs a new world graph with the following x and y dimensions.
	 * Graph has all successors, no missing edges nor non-default edge costs
	 *
	 * @param width  the width of the graph
	 * @param height the height of the graph
	 */
	public WorldGraph(int width, int height) {
		super(width, height);
		this.renderer = new WorldGraphRenderer(this);
		this.searchTickers = new LinkedHashMap<>();
		this.wantedNextDestination = null;
	}


	public WorldGraph(World world) {
		this((int) world.getTileSize().x, (int) world.getTileSize().y);
	}

	/**
	 * This allows tests to be run without initialising Gdx's graphics
	 * It must be called before any calls to render
	 */
	public void initRenderer() {
		renderer.initRenderer();
	}

	public WorldGraphRenderer getRenderer() {
		return renderer;
	}

	/**
	 * Snips the edges to accommodate for the given building
	 */
	public void addBuilding(Building building) {
		Vector2 tPos = building.getTilePosition();
		Vector3 dPos = building.getDimensions();
		int baseX = Math.round(tPos.x);
		int baseY = Math.round(tPos.y);
		int upToX = Math.round(dPos.x);
		int upToY = Math.round(dPos.y);

		snipEdges(baseX, upToX, baseY, upToY);
	}

	/**
	 * Sets the current search to display and the agent responsible.
	 *
	 * @param agent         The agent who will use the path generated.
	 * @param currentSearch The {@link SearchTicker} doing the path generation.
	 */
	public void setCurrentSearch(Agent agent, SearchTicker currentSearch) {
		latestSearchAgent = agent;
		latestSearchTicker = currentSearch;
		searchTickers.put(agent, currentSearch);
	}

	/**
	 * @return The {@link SearchTicker} that is currently being displayed.
	 */
	public SearchTicker getCurrentSearch() {
		return latestSearchTicker;
	}

	/**
	 * @return A set of all agents who have a corresponding SearchTicker
	 * Note that they are not all necessarily in progress
	 */
	public Set<Agent> getAllSearchAgents() {
		return searchTickers.keySet();
	}

	/**
	 * @return A collection of all current search tickers
	 * Note that they are not all necessarily in progress
	 */
	public Collection<SearchTicker> getAllSearches() {
		return searchTickers.values();
	}

	/**
	 * @return The agent that will follow {@link #getCurrentSearch()}.
	 */
	public Agent getCurrentSearchAgent() {
		return latestSearchAgent;
	}

	/**
	 * @param entity check this entity to see if it is currently searching
	 * @return true if the entity supplied is currently partwaythough a search
	 */
	public boolean isAgentSearching(Entity entity) {
		return entity instanceof Agent && searchTickers.containsKey(entity);
	}

	/**
	 * Clears all the current searches, resetting their respective search tickers
	 */
	public void clearAllSearches() {
		searchTickers.forEach((a, s) -> s.reset(true));
		searchTickers.clear();
	}

	/**
	 * Removes the link between the current searching agent and the search ticker.
	 * Resets the search ticker back to default
	 *
	 * @param searchingAgent the {@link Agent} that the current search is working on
	 */
	public void clearSearch(Agent searchingAgent) {
		SearchTicker searchTicker = searchTickers.remove(searchingAgent);
		if (searchTicker != null)
			searchTicker.reset(true);
	}

	/**
	 * Whether there is a search currently being displayed.
	 *
	 * @return <code>true</code> if there is a search being displayed;
	 * <code>false</code> otherwise.
	 */
	public boolean hasSearchInProgress() {
		return !searchTickers.isEmpty();
	}

	/**
	 * Sets the next destination for the {@link SearchTicker}.
	 *
	 * @param x The x coordinate to request.
	 * @param y The y coordinate to request.
	 */
	public void setNextDestination(int x, int y) {
		wantedNextDestination = new Vector2(x, y);
	}

	/**
	 * @return The next destination the {@link SearchTicker} will try reach, or null if not set.
	 */
	public Vector2 getNextDestination() {
		return wantedNextDestination;
	}

	/**
	 * Forgets about the current next destination
	 */
	public void clearNextDestination() {
		wantedNextDestination = null;
	}

	/**
	 * @return the {@link SearchAlgorithm} that the next search will use (after current agent gets to his destination)
	 */
	public SearchAlgorithm getLearningModeNext() {
		return learningModeNext;
	}

	/**
	 * Set the next {@link SearchAlgorithm} the search will use (after current agent gets to his destination)
	 *
	 * @param learningModeNext the {@link SearchAlgorithm} the next search will use
	 */
	public void setLearningModeNext(SearchAlgorithm learningModeNext) {
		this.learningModeNext = learningModeNext;
	}

	/**
	 * Removed a building from the WorldGraph,
	 * the {@link Node Nodes} and edges that were covered will be restored.
	 *
	 * @param deletionTile The bottom left hand corner of the building to delete.
	 */
	public void removeBuilding(Vector2 deletionTile) {
		addNodesInSquare((int) deletionTile.x, (int) deletionTile.y, 4);
	}

}
