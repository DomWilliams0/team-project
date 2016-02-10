package com.b3.world;

import com.b3.DebugRenderer;
import com.b3.Utils;
import com.b3.entity.Agent;
import com.b3.entity.ai.PathFollowingBehaviour;
import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.system.PhysicsSystem;
import com.b3.entity.system.RenderSystem;
import com.b3.event.EventGenerator;
import com.b3.util.Config;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.*;

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

	private Set<Entity> deadEntities;
	private WorldCamera worldCamera;

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
		initEntityBoundaries(1f, 5f);

		// Query service
		queryService = new WorldQueryService(this);

		debugRenderer = new DebugRenderer(physicsWorld);

		createDefaultBuildings();
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
		engine.addSystem(new RenderSystem(camera));
		engine.addSystem(new PhysicsSystem());

		worldCamera = camera;

		// debug: test entities
		Integer debugCount = Config.getInt("debug-entity-count");
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

	public WorldQueryService getQueryService() {
		return queryService;
	}

	/**
	 * Used for debugging: creates a regular grid of buildings across the world
	 */
	private void createDefaultBuildings() {
		Vector3 dim = new Vector3(1, 1, 10);
		float space = 4f;

		// Get building type
		List<BuildingType> types = Collections.unmodifiableList(Arrays.asList(BuildingType.values()));
		BuildingType buildingType = types.get(Utils.RANDOM.nextInt(types.size()));

		for (int x = 0; x < tileSize.x / space; x++)
			for (int y = 0; y < tileSize.y / space; y++)
				addBuilding(new Vector2(x * space, y * space), dim, buildingType);

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
	 * Spawns an agent at the start point of the path, and sets its behaviour to follow the given path
	 *
	 * @param arrive True if the agent should stop when he arrives at the final point
	 * @param points The path to follow
	 * @return The new agent
	 */
	public Agent spawnAgentWithPath(boolean arrive, Array<Vector2> points) {
		return new Agent(this, Vector2.Zero, new PathFollowingBehaviour(points, arrive));
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
		Gdx.app.debug("World", String.format("Added a building at (%2f, %2f) of dimensions (%2f, %2f, %2f)", pos.x, pos.y, dimensions.x, dimensions.y, dimensions.z));

		Building building = new Building(pos, dimensions, instance);
		building.setType(type);
		buildings.add(building);

		// physics
		if (Config.getBoolean("building-collisions")) {
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
	 * Clearing up all entities marked as dead
	 * Updating physics
	 * Rendering the world
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

		// tick physics
		float delta = Gdx.graphics.getRawDeltaTime();
		physicsWorld.step(delta, 6, 4);
		GdxAI.getTimepiece().update(delta);

		// render world
		worldCamera.positionMapRenderer(renderer);
		renderer.render();

		// render entities
		engine.update(delta);

		// render buildings
		buildingBatch.begin(worldCamera);
		buildings.stream()
				.filter(building -> building.isVisible(worldCamera))
				.forEach(building -> buildingBatch.render(building.getModelInstance(), environment));
		buildingBatch.end();

		// physics debug rendering
		if (Config.getBoolean("debug-physics-rendering"))
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
