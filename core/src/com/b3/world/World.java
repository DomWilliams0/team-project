package com.b3.world;

import com.b3.DebugRenderer;
import com.b3.entity.Agent;
import com.b3.entity.ai.*;
import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.system.AISystem;
import com.b3.entity.system.PhysicsSystem;
import com.b3.entity.system.RenderSystem;
import com.b3.gui.CoordinatePopup;
import com.b3.gui.RenderTester;
import com.b3.gui.popup.PopupManager;
import com.b3.input.InputHandler;
import com.b3.mode.ModeType;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.PendingTeleport.TeleportType;
import com.b3.world.building.Building;
import com.b3.world.building.BuildingModelCache;
import com.b3.world.building.BuildingType;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.*;

import static com.b3.mode.ModeType.*;
import static com.b3.world.building.BuildingType.HOUSE;

public class World implements Disposable {

	private static short ENTITY_CULL_TAG = 10101;
	private CoordinatePopup coordinatePopup;

	private TiledMap map;
	private InputHandler inputHandler;

	private Vector2 tileSize, pixelSize;

	private ShapeRenderer shapeRenderer;
	private DebugRenderer debugRenderer;
	private TiledMapRenderer renderer;
	private Environment environment;

	private ModelBatch buildingBatch;
	private List<Building> buildings;
	private BuildingModelCache buildingCache;

	private ModelManager modelManager;
	private PopupManager popupManager;

	private Engine engine;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;
	private Body buildingBody; // all buildings will be fixtures on a single body

	private WorldGraph worldGraph;

	private Set<Entity> deadEntities;
	private List<PendingTeleport> pendingTeleports;
	private WorldCamera worldCamera;

	private float counterAnimation = -1;
	private int counterScaler = 0;
	private double pos = 1;

	private ModeType mode;
	//private boolean compareMode;
	//current node user has clicked on
	private int currentNodeClickX;
	private int currentNodeClickY;
	private boolean newClick = false;

	private RenderTester rt;
	private float animationNextDestination;

	private int yNextDestination;
	private int xNextDestination;

	private Point currentMousePos;

	private boolean pseudoCodeEnabled;

	private Sprite AStarTexture;
	private Sprite DFSTexture;
	private Sprite BFSTexture;


	public World() {

	}

	public World(String fileName, ModeType mode, InputHandler inputHandler) {
		pseudoCodeEnabled = true;

		this.inputHandler = inputHandler;
		this.mode = mode;

		animationNextDestination = 0;
		xNextDestination = 0;
		yNextDestination = 0;

		map = new TmxMapLoader().load(fileName);
		tileSize = new Vector2(
				(int) map.getProperties().get("width"),
				(int) map.getProperties().get("height")
		);
		pixelSize = new Vector2(tileSize).scl(Utils.WORLD_SCALE);
		renderer = new OrthogonalTiledMapRenderer(map, 1f / Utils.TILESET_RESOLUTION);

		// buildings and lighting
		buildingBatch = new ModelBatch();
		buildings = new ArrayList<>();
		buildingCache = new BuildingModelCache(this);
		shapeRenderer = new ShapeRenderer();

		// todo shadows
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		// entities
		engine = new Engine();
		deadEntities = new HashSet<>();
		pendingTeleports = new ArrayList<>();
		physicsWorld = new com.badlogic.gdx.physics.box2d.World(Vector2.Zero, true);
		BodyDef buildingBodyDef = new BodyDef();
		buildingBodyDef.type = BodyDef.BodyType.StaticBody;
		buildingBody = physicsWorld.createBody(buildingBodyDef);
		initEntityBoundaries(1f, Config.getFloat(ConfigKey.ENTITY_CULL_DISTANCE));

		debugRenderer = new DebugRenderer(physicsWorld);

		worldGraph = new WorldGraph(this);
		worldGraph.initRenderer();

		// load map tiles
		loadBuildings(map);
		processMapTileTypes(map);

		// model manager
		modelManager = new ModelManager(environment, map);

		//GUI overlay
		coordinatePopup = new CoordinatePopup();

		Texture tempTexture = new Texture("core/assets/gui/ASTARTEXT.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		AStarTexture = new Sprite(tempTexture);

		tempTexture = new Texture("core/assets/gui/DFSTEXT.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		DFSTexture = new Sprite(tempTexture);

		tempTexture = new Texture("core/assets/gui/BFSTEXT.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		BFSTexture = new Sprite(tempTexture);
	}

	private void processMapTileTypes(TiledMap map) {
		processMapTileTypes(map, true);
	}

	private void processMapTileTypes(TiledMap map, boolean renderBoxes) {
		FixtureDef objectDef = null;
		if (renderBoxes) { // renderBoxes is needed so as to be compatible with the tests.
			objectDef = new FixtureDef();
			objectDef.shape = new PolygonShape();
		}

		for (MapLayer layer : map.getLayers()) {
			if (!layer.isVisible() || !(layer instanceof TiledMapTileLayer))
				continue;

			TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;

			// remove node even if unknown tile type is found
			boolean objectLayer = layer.getName().equals("objects");

			// remove nodes on invalid tiles
			// todo assign costs to edges
			for (int y = 0; y < tileLayer.getHeight(); y++) {
				for (int x = 0; x < tileLayer.getWidth(); x++) {
					TiledMapTileLayer.Cell cell = tileLayer.getCell(x, y);
					if (cell == null)
						continue;

					TileType type = TileType.getFromCell(cell);
					if (!objectLayer && type == TileType.UNKNOWN)
						continue;


					// add collision box for objects
					if (objectLayer && renderBoxes) {
						((PolygonShape) objectDef.shape).setAsBox(
								0.5f, 0.5f, new Vector2(x + 0.5f, y + 0.5f), 0f
						);
						buildingBody.createFixture(objectDef);
					}


					Node node = worldGraph.getNode(new Point(x, y));
					if (node == null)
						continue;

					// cost of 0
					if (!type.shouldHaveNode()) {
						worldGraph.removeNode(node);
					}
					// apply cost to edges if the tile types are the same, or this node's cost is more than its neighbour's
					else
						node.getEdges().keySet()
								.parallelStream()
								.filter(n -> {
									TileType t = TileType.getFromCell(tileLayer.getCell(n.getPoint().x, n.getPoint().y));
									return type.getCost() >= t.getCost() || t == type;
								})
								.forEach(n -> node.setEdgeCost(n, type.getCost()));
				}
			}
		}

		if (renderBoxes) {
			objectDef.shape.dispose();
		}
	}

	/**
	 * Loads buildings from the "buildings" layer in the given map
	 *
	 * @param map The map
	 */
	private void loadBuildings(TiledMap map) {
		MapLayer buildingsLayer = map.getLayers().get("buildings");
		if (buildingsLayer == null) {
			System.err.println("No buildings layer found in world");
			return;
		}

		for (MapObject object : buildingsLayer.getObjects()) {
			MapProperties props = object.getProperties();
			Float width = (Float) props.get("width") / Utils.TILESET_RESOLUTION;
			Float length = (Float) props.get("height") / Utils.TILESET_RESOLUTION;
			Float height = Float.parseFloat((String) props.get("building-height")) * 2;
			Float x = (Float) props.get("x") / Utils.TILESET_RESOLUTION;
			Float y = (Float) props.get("y") / Utils.TILESET_RESOLUTION;

			addBuilding(new Vector2(x, y), new Vector3(width, length, height), HOUSE);
		}
	}

	/**
	 * Creates an invisible border around the world, which kills entities when touched.
	 * <p>
	 * It does this by setting a ContactFilter and ContactListener on the physicsWorld;
	 * this will have to be refactored if any other collision processing is needed.
	 *
	 * @param thickness The thickness of each border
	 * @param offset    The gap between the world edge and the start of the border
	 */
	private void initEntityBoundaries(float thickness, float offset) {
		FixtureDef boundaryDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		boundaryDef.shape = shape;
		boundaryDef.isSensor = true;
		boundaryDef.filter.groupIndex = ENTITY_CULL_TAG;

		// todo this side doesn't seem quite right...
		shape.setAsBox(offset + tileSize.x / 2f, thickness, new Vector2(tileSize.x / 2f, -thickness / 2f - offset), 0f); // bottom
		buildingBody.createFixture(boundaryDef);

		shape.setAsBox(offset + tileSize.x / 2f, thickness, new Vector2(tileSize.x / 2f, tileSize.y + thickness / 2f + offset), 0f); // top
		buildingBody.createFixture(boundaryDef);

		shape.setAsBox(thickness, offset + tileSize.y / 2f, new Vector2(-thickness / 2f - offset, tileSize.y / 2f), 0f); // left
		buildingBody.createFixture(boundaryDef);

		shape.setAsBox(thickness, offset + tileSize.y / 2f, new Vector2(tileSize.x + thickness / 2f + offset, tileSize.y / 2f), 0f); // right
		buildingBody.createFixture(boundaryDef);

		physicsWorld.setContactListener(new ContactListener() {
			@Override
			public void beginContact(Contact contact) {
				// one must be a sensor
				if (!contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor())
					return;

				Fixture f = contact.getFixtureA().getFilterData().groupIndex == ENTITY_CULL_TAG ? contact.getFixtureB() : contact.getFixtureA();
				Entity entity = (Entity) f.getBody().getUserData();
				if (entity == null)
					return;

				// searching
				if (worldGraph.isAgentSearching(entity))
					return;

				// teleport to other side of the world
				Vector2 position = f.getBody().getPosition();
				TeleportType teleportType;
				if (position.x < 0)
					teleportType = TeleportType.TO_RIGHT;
				else if (position.x >= tileSize.x)
					teleportType = TeleportType.TO_LEFT;

				else if (position.y < 0)
					teleportType = TeleportType.TO_BOTTOM;
				else
					teleportType = TeleportType.TO_TOP;

				pendingTeleports.add(new PendingTeleport(f.getBody(), teleportType));
			}

			@Override
			public void endContact(Contact contact) {
			}

			@Override
			public void preSolve(Contact contact, Manifold manifold) {
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse contactImpulse) {
			}
		});
	}

	/**
	 * Initiates entity systems with the given camera
	 *
	 * @param camera The world camera
	 */
	public void initEngine(WorldCamera camera) {
		engine.addSystem(new PhysicsSystem(physicsWorld));
		engine.addSystem(new RenderSystem(camera));
		engine.addSystem(new AISystem(worldGraph));

		worldCamera = camera;
		worldCamera.setCurrentZoom(Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM) / 2);

		//set up these after the camera has been setup
		rt = new RenderTester(this);

		popupManager = new PopupManager(worldCamera, mode);
		popupManager.showIntro();
	}

	private Vector2 generateRandomTile() {
		int x, y;
		do {
			x = Utils.RANDOM.nextInt(worldGraph.getMaxXValue());
			y = Utils.RANDOM.nextInt(worldGraph.getMaxYValue());
		} while (!worldGraph.hasNode(new Point(x, y)));
		return new Vector2(x, y);
	}

	public ModelManager getModelManager() {
		return modelManager;
	}

	public PopupManager getPopupManager() {
		return popupManager;
	}

	public Engine getEngine() {
		return engine;
	}

	public WorldGraph getWorldGraph() {
		return worldGraph;
	}

	/**
	 * Used for debugging: creates a regular grid of buildings across the world
	 */
	private void createDefaultBuildings() {
		Vector3 dim = new Vector3(1, 1, 3);
		float space = 4f;

		// Get building type
		List<BuildingType> types = Collections.unmodifiableList(Arrays.asList(BuildingType.values()));
		BuildingType buildingType = types.get(Utils.RANDOM.nextInt(types.size()));

		for (int x = 0; x < tileSize.x / space; x++)
			for (int y = 0; y < tileSize.y / space; y++)
				addBuilding(new Vector2(x * space, y * space), new Vector3(dim.x, dim.y, Utils.randomRange(4f, 8f)), buildingType);

	}

	/**
	 * Spawns a new entity in the world, at the given tile position
	 *
	 * @param tilePos The tile position to spawn the entity at
	 * @return The newly created agent
	 */
	public Agent spawnAgent(Vector2 tilePos) {
		return new Agent(this, tilePos);
	}

	/**
	 * {@link World#spawnAgentWithPath(Vector2, List)}
	 */
	public Agent spawnAgentWithPath(Vector2 tilePos, Vector2... path) {
		return spawnAgentWithPath(tilePos, Arrays.asList(path));
	}

	/**
	 * Spawns an agent at the start point of the path, and sets its behaviour to follow the given path
	 *
	 * @param tilePos The tile position to spawn the entity at
	 * @param path    A list of tiles that make up the path
	 * @return The new agent
	 */
	public Agent spawnAgentWithPath(Vector2 tilePos, List<Vector2> path) {
		Agent agent = spawnAgent(tilePos);
		agent.setBehaviour(new BehaviourPathFollow(agent, path));
		return agent;
	}

	/**
	 * Spawns an agent at the given tile position, who will path find to the given goal tiles in sequence, using
	 * the given algorithm
	 *
	 * @param tilePos   The spawn position, and the start tile for path finding
	 * @param algorithm The algorithm to use
	 * @param visualise If this search should be visualised. There can only be one visualised search at a time
	 * @param endTiles  A list of tile position, which will be travelled to in turn
	 * @return The new agent
	 */
	private Agent spawnAgentWithMultiplePathFinding(Vector2 tilePos, SearchAlgorithm algorithm, boolean visualise, Vector2... endTiles) {
		if (endTiles.length == 0)
			throw new IllegalArgumentException("List of goals given must not be empty");

		Agent agent = spawnAgent(tilePos);
		BehaviourMultiPathFind behaviour = new BehaviourMultiPathFind(agent, tilePos, endTiles[0], algorithm, worldGraph, worldCamera, this);

		for (int i = 1, endTilesLength = endTiles.length; i < endTilesLength; i++)
			behaviour.addNextGoal(endTiles[i]);

		agent.setBehaviour(behaviour);
		if (visualise)
			worldGraph.setCurrentSearch(agent, behaviour.getSearchTicker());
		return agent;
	}

	/**
	 * Spawns an agent at the given tile position, who will path find to the given goal tile with the given algorithm
	 *
	 * @param tilePos   The spawn position, and the start tile for path finding
	 * @param endNode   The node to path find to
	 * @param algorithm The algorithm to use
	 * @param visualise If this search should be visualised. There can only be one visualised search at a time
	 * @return The new agent
	 */
	private Agent spawnAgentWithPathFinding(Vector2 tilePos, Vector2 endNode, SearchAlgorithm algorithm, boolean visualise) {
		Agent agent = spawnAgent(tilePos);
		BehaviourPathFind behaviour = new BehaviourPathFind(agent, tilePos, endNode, algorithm, this);
		agent.setBehaviour(behaviour);
		if (visualise)
			worldGraph.setCurrentSearch(agent, behaviour.getSearchTicker());
		return agent;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	/**
	 * Adds a building to the world at the given coordinates
	 *
	 * @param pos        Tile position
	 * @param dimensions Building dimensions, in tiles. z is height
	 * @param type       Building type
	 * @return The newly created building
	 */
	public Building addBuilding(Vector2 pos, Vector3 dimensions, BuildingType type) {

		for (int i = (int) pos.x; i < pos.x + dimensions.x; i++) {
			for (int j = (int) pos.y; j < pos.y + dimensions.y; j++) {
				if (!worldGraph.hasNode(new Point(i, j))) {
					return null;
				}
			}
		}

		Gdx.app.debug("World", String.format("Added a building at (%2f, %2f) of dimensions (%2f, %2f, %2f)", pos.x, pos.y, dimensions.x, dimensions.y,
				dimensions.z));

		Building building = new Building(pos, dimensions, buildingCache);
		building.setType(type);
		buildings.add(building);

		// physics
		if (Config.getBoolean(ConfigKey.BUILDING_COLLISIONS)) {
			FixtureDef buildingDef = new FixtureDef();
			PolygonShape shape = new PolygonShape();
			shape.setAsBox(
					dimensions.x / 2, dimensions.y / 2,
					new Vector2(pos.x + dimensions.x / 2, pos.y + dimensions.y / 2),
					0f
			);

			buildingDef.shape = shape;
			buildingDef.density = pos.x;
			buildingDef.friction = pos.y;

			buildingBody.createFixture(buildingDef);

			shape.dispose(); // todo reuse shape and fixture for all buildings
		}

		worldGraph.addBuilding(building);

		return building;
	}

	public void addBuilding(Building building) {
		buildings.add(building);
		worldGraph.addBuilding(building);
	}

	/**
	 * Updates and renders the world, by:
	 * Clearing up all entities marked as dead (which can't be done while ticking the world)
	 * Rendering the world
	 * Ticking entity behaviours and physics
	 * Rendering entities
	 * Rendering the search graph
	 * Rendering buildings
	 * Rendering physics/collisions (if configured)
	 */
	public void render() {
		// remove dead entities
		cleanseDeadEntities();

		// teleport entities
		processPendingTeleports();

		// render tiled world
		worldCamera.positionMapRenderer(renderer);
		renderer.render();

		renderGUI();

		// tick entities and physics
		engine.update(Utils.DELTA_TIME);

		buildingBatch.begin(worldCamera);
		buildings.stream()
				.filter(building -> building.isVisible(worldCamera))
				.forEach(building -> buildingBatch.render(building.getModelInstance(), environment));
		buildingBatch.end();

		// render models
		modelManager.render(worldCamera);

		//pop-ups on nodes
		if (mode == LEARNING) rt.render(currentNodeClickX, currentNodeClickY, worldGraph.getCurrentSearch());

		//pop-ups to show current coordinate
		coordinatePopup.render();

		//render big pop-ups
		popupManager.render();

		//if compare mode load text to show what it is all about
		if (mode == COMPARE) renderTextBelowWorld();

		// physics debug rendering
		if (Config.getBoolean(ConfigKey.PHYSICS_RENDERING))
			debugRenderer.render(worldCamera);
	}

	private void renderTextBelowWorld() {
		SpriteBatch spriteBatch = new SpriteBatch();
		spriteBatch.setProjectionMatrix(worldCamera.combined);
		spriteBatch.begin();

		spriteBatch.draw(AStarTexture, -3, (float) -7.5, AStarTexture.getWidth() / 13, AStarTexture.getHeight() / 13);
		spriteBatch.draw(DFSTexture, 15, (float) -7.5, AStarTexture.getWidth() / 13, AStarTexture.getHeight() / 13);
		spriteBatch.draw(BFSTexture, 33, (float) -7.5, AStarTexture.getWidth() / 13, AStarTexture.getHeight() / 13);

		spriteBatch.end();
	}

	/**
	 * Removes entities marked as dead since the last tick
	 */
	private void cleanseDeadEntities() {
		deadEntities.forEach(e -> {

			PhysicsComponent phys = e.getComponent(PhysicsComponent.class);
			if (phys != null)
				physicsWorld.destroyBody(phys.body);

			e.removeAll();

			engine.removeEntity(e);
		});
		deadEntities.clear();
	}

	/**
	 * Teleports entities who crossed the world's borders in the last frame to the other side
	 */
	private void processPendingTeleports() {
		pendingTeleports.forEach(pendingTeleport -> {
			Vector2 position = pendingTeleport.body.getPosition();
			switch (pendingTeleport.teleportType) {
				case TO_LEFT:
					position.x = 0;
					break;
				case TO_TOP:
					position.y = 0;
					break;
				case TO_RIGHT:
					position.x = tileSize.x;
					break;
				case TO_BOTTOM:
					position.y = tileSize.y;
					break;
			}

			pendingTeleport.body.setTransform(position, pendingTeleport.body.getAngle());
		});
		pendingTeleports.clear();
	}

	/**
	 * Renders graph, building placement overlay and animations
	 */
	private void renderGUI() {
		float zoomScalar = getZoomScalar();

		int fovNumber = mode == COMPARE ? 67 : 40; // Todo - Nish, what is this?
		if (worldCamera.getFOV() < fovNumber) {
			Vector2 cameraPos = getTileSize().scl(0.5f);
			worldCamera.setFieldOfViewY(worldCamera.getFOV() + 1);
			worldCamera.lookAt(cameraPos.x + (fovNumber - worldCamera.getFOV()), cameraPos.y + (fovNumber - worldCamera.getFOV()), 0);
			counterAnimation = 10;
		}

		if (counterAnimation > 1) {
			counterAnimation = (float) (counterAnimation - 0.25);
			if (Config.getBoolean(ConfigKey.SHOW_GRID))
				worldGraph.render(worldCamera, counterAnimation, zoomScalar);
		} else {
			if (Config.getBoolean(ConfigKey.SHOW_GRID))
				worldGraph.render(worldCamera, 1, zoomScalar);
		}

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setProjectionMatrix(worldCamera.combined);

		if (animationNextDestination != 0) {
			shapeRenderer.setColor(Color.BLUE);
			animationNextDestination = (float) (animationNextDestination - 0.2);
			shapeRenderer.ellipse((float) (xNextDestination - (animationNextDestination / 2) + 0.5), (float) (yNextDestination - (animationNextDestination / 2) + 0.5), animationNextDestination, animationNextDestination);
		}

		//render add building overlay if needed
		if (Config.getBoolean(ConfigKey.ADD_BUILDING_MODE) || Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE)) {
			boolean adding = Config.getBoolean(ConfigKey.ADD_BUILDING_MODE);
			float x = currentMousePos.getX();
			float y = currentMousePos.getY();

			if (adding && isValidBuildingPos(x, y))
				shapeRenderer.setColor(Color.LIGHT_GRAY);
			else
				shapeRenderer.setColor(Color.FIREBRICK);

			shapeRenderer.box(x, y, 0, 4f, 4f, 1f);
		}

		shapeRenderer.end();
	}

	public boolean isValidBuildingPos(float x, float y) {

		for (int i = (int) x; i < x + 4; i++) {
			for (int j = (int) y; j < y + 4; j++) {
				if (!worldGraph.hasNode(new Point(i, j)) ||
						new Point(i, j).equals(getWorldGraph().getCurrentSearch().getStart().getPoint()) ||
						new Point(i, j).equals(getWorldGraph().getCurrentSearch().getEnd().getPoint())) {
					return false;
				}
			}
		}

//		WorldGraph tempWG = new WorldGraph(this);
//
//		Building building = new Building(new Vector2(x,y), new Vector3(4,4,10), buildingCache);
//		building.setType(BuildingType.HOUSE);
//		tempWG.addBuilding(building);
//
//		tempWG.checkEveryEdge();

		return true;
	}


	private float getZoomScalar() {
		if (Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM) != 45)
			System.err.println("Set max zoom in userconfig to 45, zoom only works with this so far...");

		//TODO make it work for different max zooms
		float zoomScalar = worldCamera.getCurrentZoom();

		if (zoomScalar < 14 && zoomScalar > 1.5) {
			counterScaler++;
		} else {
			counterScaler = 0;
			if (zoomScalar >= 14)
				pos = -0.25;
			if (zoomScalar <= 1.5)
				pos = 0.25;
		}

		if (counterScaler > 5) {
			//too long in-between animations
			worldCamera.setCurrentZoom((float) (worldCamera.getActualZoom() + pos));
		}
		return zoomScalar;
	}

	/**
	 * Flatten buildings and models.
	 *
	 * @param flatten whether they should be flat or not.
	 */
	public void flattenBuildings(boolean flatten) {
		for (Building building : buildings) {
			building.setFlattened(flatten);
		}
	}

	@Override
	public void dispose() {
		buildingCache.dispose();
		engine.removeAllEntities();
		physicsWorld.dispose();
	}

	public Vector2 getTileSize() {
		return new Vector2(tileSize);
	}

	public Vector2 getPixelSize() {
		return new Vector2(pixelSize);
	}

	/**
	 * Checks if the given tile position is within the worlds bounds
	 *
	 * @param tilePos The tile position to check
	 * @return True if the tile is in bounds, otherwise false
	 */
	public boolean isInBounds(Vector2 tilePos) {
		return tilePos.x >= 0 && tilePos.x < tileSize.x
				&& tilePos.y >= 0 && tilePos.y < tileSize.y;
	}

	public com.badlogic.gdx.physics.box2d.World getPhysicsWorld() {
		return physicsWorld;
	}

	public WorldCamera getWorldCamera() {
		return worldCamera;
	}

	public void setCurrentClick(int x, int y) {
		if (x == currentNodeClickX && y == currentNodeClickY) return;
		this.currentNodeClickX = x;
		this.currentNodeClickY = y;
		newClick = true;
	}

	public boolean hasNewClick() {
		return newClick;
	}

	public Point getCurrentClick() {
		newClick = false;
		return new Point(currentNodeClickX, currentNodeClickY);
	}

	public void setNextDestination(int x, int y) {
		animationNextDestination = (float) 2.0;
		xNextDestination = x;
		yNextDestination = y;
		worldGraph.setNextDestination(x, y);
	}

	/*public Boolean getCompareMode() {
		return compareMode;
	}*/

	public ModeType getMode() {
		return mode;
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	public RenderTester getrt() {
		return rt;
	}

	public void setCurrentMousePos(int screenX, int screenY) {
		currentMousePos = new Point(screenX, screenY);
	}

	//TODO make it so don't have to click in bottom left corner
	public void removeBuilding(Vector2 positionDeletion) {
		for (int i = 0; i < buildings.size(); i++) {
			if (buildings.get(i).getTilePosition().equals(positionDeletion)) {
				buildings.remove(i);
				//has to be bottom left (assuming 4x4xZ dimensions)

				if (Config.getBoolean(ConfigKey.BUILDING_COLLISIONS)) {

					Array<Fixture> listBuildingsPhysics = buildingBody.getFixtureList();

					Fixture toBeDestroyed = null;
					for (int j = 0; j < listBuildingsPhysics.size; j++) {
						Fixture current = listBuildingsPhysics.get(j);

//						System.out.println(current.getDensity() + "|" + current.getFriction());

						if (current.getDensity() == positionDeletion.x && current.getFriction() == positionDeletion.y) {
							toBeDestroyed = current;
						}
					}
					if (toBeDestroyed != null)
						buildingBody.destroyFixture(toBeDestroyed);
				}

				worldGraph.removeBuilding(positionDeletion);

				processMapTileTypes(map);
				return;
			}
		}
		System.out.println("BAD");
	}

	public void setPseudoCode(boolean enabled) {
		pseudoCodeEnabled = enabled;
	}

	public boolean getPseudoCode() {
		return pseudoCodeEnabled;
	}

	public CoordinatePopup getCoordinatePopup() {
		return coordinatePopup;
	}

}
