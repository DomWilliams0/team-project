package com.b3.world;

import com.b3.DebugRenderer;
import com.b3.Utils;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourPathFollow;
import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.system.AISystem;
import com.b3.entity.system.PhysicsSystem;
import com.b3.entity.system.RenderSystem;
import com.b3.event.EventGenerator;
import com.b3.event.EventType;
import com.b3.searching.Buildings;
import com.b3.searching.Node;
import com.b3.searching.Point;
import com.b3.searching.WorldGraph;
import com.b3.searching.optional.Maybe;
import com.b3.searching.SearchTicker;
import com.b3.searching.optional.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Disposable;

import java.util.*;
import java.util.stream.Collectors;

public class World implements Disposable {

	private static short ENTITY_CULL_TAG = 10101;

	private Vector2 tileSize, pixelSize;

	private DebugRenderer debugRenderer;
	private TiledMapRenderer renderer;
	private Environment environment;

	private ModelBatch buildingBatch;
	private List<Building> buildings;
	private BuildingModelCache buildingCache;

	private Engine engine;

	private com.badlogic.gdx.physics.box2d.World physicsWorld;
	private Body buildingBody; // all buildings will be fixtures on a single body

	private EventGenerator eventGenerator;
	private WorldObserver worldObserver;
	private WorldQueryService queryService;
	private WorldGraph<Point> worldGraph;

	private Set<Entity> deadEntities;
	private WorldCamera worldCamera;
	private float counter = 5;
	private float counter2 = 0;

	private SearchTicker searchTicker;

	public World(String fileName) {
		TiledMap map = new TmxMapLoader().load(fileName);
		tileSize = new Vector2(
				(int) map.getProperties().get("width"),
				(int) map.getProperties().get("height")
		);
		pixelSize = new Vector2(tileSize).scl(Utils.WORLD_SCALE);
		renderer = new OrthogonalTiledMapRenderer(map, 1f / Utils.TILESET_RESOLUTION);

		// buildings and lighting
		buildingBatch = new ModelBatch();
		buildings = new ArrayList<>();
		buildingCache = new BuildingModelCache();

		// todo shadows
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		// entities
		engine = new Engine();
		deadEntities = new HashSet<>();
		physicsWorld = new com.badlogic.gdx.physics.box2d.World(Vector2.Zero, true);
		BodyDef buildingBodyDef = new BodyDef();
		buildingBodyDef.type = BodyDef.BodyType.StaticBody;
		buildingBody = physicsWorld.createBody(buildingBodyDef);
		initEntityBoundaries(1f, Config.getFloat(ConfigKey.ENTITY_CULL_DISTANCE));

		// Query service
		queryService = new WorldQueryService(this);

		debugRenderer = new DebugRenderer(physicsWorld);

		worldGraph = new WorldGraph<>(100,100,this);

		int[] arr = {4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
				4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
				4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
				4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
				4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
				4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,};
		worldGraph.randomTheGraph(arr);
		worldGraph.addBuilding(43, 50, 4);
		worldGraph.addBuilding(45, 50, 4);
		worldGraph.addBuilding(47, 50, 4);
		worldGraph.addBuilding(49, 50, 4);
		worldGraph.addBuilding(51, 50, 4);
		worldGraph.addBuilding(53, 50, 4);

		worldGraph.addCostNode(47, 49, 50);
		worldGraph.addCostNode(51, 45, 50);

		worldGraph.addCostNode(10, 10, 10);
		worldGraph.addCostNode(12, 10, 20);
		worldGraph.addCostNode(14, 10, 30);
		worldGraph.addCostNode(16, 10, 40);
		worldGraph.addCostNode(18, 10, 50);
		worldGraph.addCostNode(20, 10, 70);
		worldGraph.addCostNode(47, 54, 100);

		createBuildings();

		// Search ticker
		searchTicker = new SearchTicker(10);

		Node<Point> src = worldGraph.getNode(new Point(40, 55));
		Node<Point> trgt = worldGraph.getNode(new Point(55, 45));

		searchTicker.reset(SearchAlgorithm.A_STAR, src, trgt);

		//		MOVED TO RENDER SO HAVE TIME FOR OPENING ANIMATIONS
		//		testPathFollowing();

	}

	public void testPathFollowing() {
		//EventType eventType = EventType.FIRE;

		// Get target and source buildings
		/*Building targetBuilding = (Building)evt.getMessage();
		targetBuilding.setEvent(eventType);
		Building sourceBuilding = world.getQueryService().getRandomBuildingByType(getBuildingTypeFromEvent(eventType));*/

		//Vector2 src = sourceBuilding.getTilePosition();
		Point p1 = new Point(40, 55);

		//Vector2 trgt = targetBuilding.getTilePosition();
		Point p2 = new Point(55, 45);

		System.out.println(p1);
		System.out.println(p2);

		// Perform search
		Maybe<List<Node<Point>>> optPath = worldGraph.findPathFromASTAR(p1, p2);

		if (optPath.isNothing()) {
			System.out.println("Nooooo");
			return;
		}

		System.out.println(optPath.fromMaybe());

		List<Node<Point>> path = optPath.fromMaybe();
		List<Vector2> points = path.stream().map(pointNode -> new Vector2(pointNode.getContent().getX(), pointNode.getContent().getY())).collect(Collectors
				.toList());

		spawnAgentWithPath(points.get(0), points);
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
			Float height = Float.parseFloat((String) props.get("building-height"));
			Float x = (Float) props.get("x") / Utils.TILESET_RESOLUTION;
			Float y = (Float) props.get("y") / Utils.TILESET_RESOLUTION;

			addBuilding(new Vector2(x, y), new Vector3(width, length, height), BuildingType.HOUSE);
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
		shape.setAsBox(offset + tileSize.x / 2f, thickness, new Vector2(tileSize.x / 2f, thickness / 2f - offset), 0f); // bottom
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

				deadEntities.add(entity);
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
		engine.addSystem(new AISystem());

		worldCamera = camera;

		// debug: test entities
		Integer debugCount = Config.getInt(ConfigKey.ENTITY_SPAWN_COUNT);
		for (int i = 0; i < debugCount; i++)
			spawnAgent(new Vector2(Utils.RANDOM.nextInt((int) tileSize.x), Utils.RANDOM.nextInt((int) tileSize.y)));
	}

	/**
	 * Creates the event generator thread and starts it
	 */
	public void initEventGenerator() {
		// Uncomment for event generator (not working atm)
		worldObserver = new WorldObserver(this);
		eventGenerator = new EventGenerator(this);
		eventGenerator.addObserver(worldObserver);
		new Thread(eventGenerator).start();
	}

	public Engine getEngine() {
		return engine;
	}

	public WorldGraph getWorldGraph() {
		return worldGraph;
	}

	public WorldQueryService getQueryService() {
		return queryService;
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

	private void createBuildings() {
		List<BuildingType> types = Collections.unmodifiableList(Arrays.asList(BuildingType.values()));
		BuildingType buildingType = types.get(Utils.RANDOM.nextInt(types.size()));

		Vector3 twoSize = new Vector3(2, 1, 10);
		Vector3 threeSize = new Vector3(1, 2, 10);
		Vector3 fourSize = new Vector3(2, 2, 10);

		for (int i = 0; i < worldGraph.getBuildings().size(); i++) {
			Buildings b = (Buildings) worldGraph.getBuildings().get(i);

			Vector2 v2 = new Vector2((float) b.getXValueofCoord() - (float) 0.5, (float) b.getYValueofCoord() - (float) 0.5);

			switch (b.getBuildingSize()) {
				case 2:
					addBuilding(v2, twoSize, buildingType);
					break;

				case 3:
					addBuilding(v2, threeSize, buildingType);
					break;

				case 4:
					addBuilding(v2, fourSize, buildingType);
					break;
			}
		}

		/*ArrayList<Integer> remaining = Utils.range(buildings.size());

		int rnIndex = Utils.RANDOM.nextInt(remaining.size());
		buildings.get(rnIndex).setType(BuildingType.POLICE_STATION);
		remaining.remove((Object)rnIndex);

		rnIndex = Utils.RANDOM.nextInt(remaining.size());
		buildings.get(rnIndex).setType(BuildingType.FIRE_STATION);
		remaining.remove((Object)rnIndex);

		rnIndex = Utils.RANDOM.nextInt(remaining.size());
		buildings.get(rnIndex).setType(BuildingType.RESTAURANT);
		remaining.remove((Object)rnIndex);*/
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
		//		dimensions = new Vector3(dimensions).scl(Utils.TILE_SIZE, Utils.TILE_SIZE, 1); // height isn't scaled
		//		pos = new Vector2(pos).scl(Utils.TILE_SIZE);

		ModelInstance instance = buildingCache.createBuilding(pos, dimensions);
		Gdx.app.debug("World", String.format("Added a building at (%2f, %2f) of dimensions (%2f, %2f, %2f)", pos.x, pos.y, dimensions.x, dimensions.y,
				dimensions.z));

		Building building = new Building(pos, dimensions, instance);
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
			buildingBody.createFixture(buildingDef);
			shape.dispose(); // todo reuse shape and fixture for all buildings
		}

		return building;
	}

	// todo remove buildings too

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
		deadEntities.forEach(e -> {

			PhysicsComponent phys = e.getComponent(PhysicsComponent.class);
			if (phys != null)
				physicsWorld.destroyBody(phys.getBody());

			e.removeAll();

			engine.removeEntity(e);
		});
		deadEntities.clear();

		// render tiled world
		worldCamera.positionMapRenderer(renderer);
		renderer.render();

		// render underlying graph
		if (counter > 1) {
			counter = (float) (counter - 0.25);
			counter2 = 0;
		}

		//TODO fix building animation crappyness / or just delete it
		worldGraph.render(worldCamera, searchTicker, counter);

		// tick entities and physics
		engine.update(Gdx.graphics.getRawDeltaTime());

		if (counter2 != -1 & counter2 != 10 & counter2 != 11) {
			counter2 = (float) (counter2 + 0.5);
			for (int i = 0; i < buildings.size(); i++) {
				Building temp = buildings.get(i);
				Vector3 v = temp.getDimensions();
				Vector3 newV3 = new Vector3(v.x, v.y, counter2);
				ModelInstance tempInstance = buildingCache.createBuilding(temp.getTilePosition(), newV3);
				buildings.set(i, new Building(temp.getTilePosition(), newV3, tempInstance));
			}
		}
		//if (counter2 == 10) { testPathFollowing(); counter2 = 11;}

		buildingBatch.begin(worldCamera);
		buildings.stream()
				.filter(building -> building.isVisible(worldCamera))
				.forEach(building -> buildingBatch.render(building.getModelInstance(), environment));
		buildingBatch.end();

		// physics debug rendering
		if (Config.getBoolean(ConfigKey.PHYSICS_RENDERING))
			debugRenderer.render(worldCamera);
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
}
