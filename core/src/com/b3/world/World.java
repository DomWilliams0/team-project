package com.b3.world;

import com.b3.DebugRenderer;
import com.b3.Utils;
import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.component.RenderComponent;
import com.b3.entity.system.PhysicsSystem;
import com.b3.entity.system.RenderSystem;
import com.b3.event.EventGenerator;
import com.b3.util.Config;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.GdxAI;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import java.util.*;

public class World implements Disposable {

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

	public World(String fileName) {
		TiledMap map = new TmxMapLoader().load(fileName);
		tileSize = new Vector2(
				(int) map.getProperties().get("width"),
				(int) map.getProperties().get("height")
		);
		pixelSize = new Vector2(tileSize).scl(Utils.WORLD_SCALE);
		renderer = new OrthogonalTiledMapRenderer(map, 1 / Utils.WORLD_SCALE);

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
		physicsWorld = new com.badlogic.gdx.physics.box2d.World(Vector2.Zero, true);
		BodyDef buildingBodyDef = new BodyDef();
		buildingBodyDef.type = BodyDef.BodyType.StaticBody;
		buildingBody = physicsWorld.createBody(buildingBodyDef);

		// Query service
		queryService = new WorldQueryService(this);

		debugRenderer = new DebugRenderer(physicsWorld);

		createDefaultBuildings();

		// debug test agent
//		addAgentWithPath(false, Array.with(
//				new Vector2(25, 25),
//				new Vector2(45, 25),
//				new Vector2(45, 45),
//				new Vector2(25, 45),
//				new Vector2(25, 25)
//		));

	}

	public void initEngine(PerspectiveCamera camera) {
		engine.addSystem(new RenderSystem(camera));
		engine.addSystem(new PhysicsSystem());
	}

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

	private void createDefaultBuildings() {
		Vector3 dim = new Vector3(1, 1, 10);
		float space = 4f;

		// Get building type
		Random rn = new Random();
		List<BuildingType> types = Collections.unmodifiableList(Arrays.asList(BuildingType.values()));
		BuildingType buildingType = types.get(rn.nextInt(types.size()));

		for (int x = 0; x < tileSize.x / space; x++)
			for (int y = 0; y < tileSize.y / space; y++)
				addBuilding(new Vector2(x * space, y * space), dim, buildingType);

	}

	/**
	 * Spawns an agent at the given tile
	 *
	 * @param tilePos Tile position
	 * @return The new entity
	 */
	public Entity addAgent(Vector2 tilePos) {
		Entity e = new Entity();

		float diameter = 0.5f;
		float radius = diameter / 2f;
		e.add(new RenderComponent(Color.BLUE, radius));

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.linearDamping = 0.9f;
		e.add(new PhysicsComponent(physicsWorld, bodyDef, tilePos, radius));

		engine.addEntity(e);
		return e;
	}

	/**
	 * Spawns an agent at the start point of the path, and sets its behaviour to follow the given path
	 *
	 * @param arrive True if the agent should stop when he arrives at the final point
	 * @param points The path to follow
	 * @return The new entity
	 */
	public Entity addAgentWithPath(boolean arrive, Array<Vector2> points) {
		if (points.size < 2)
			throw new IllegalArgumentException("Given path is too short in addAgentWithPath");

		// move to centres of tiles
		for (int i = 0; i < points.size; i++)
			points.get(i).add(0.5f, -0.5f);


		LinePath<Vector2> path = new LinePath<>(points, true);

		Entity e = addAgent(path.getStartPoint());
		PhysicsComponent phys = e.getComponent(PhysicsComponent.class);

		FollowPath<Vector2, LinePath.LinePathParam> behaviour =
				new FollowPath<>(phys, path);
		behaviour.setArriveEnabled(arrive);
		behaviour.setPathOffset(1f);
		behaviour.setPredictionTime(1f);
		behaviour.setArrivalTolerance(1f);
		behaviour.setDecelerationRadius(0f);

		phys.setSteeringBehavior(behaviour);


		return e;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	/**
	 * @param pos        Tile position
	 * @param dimensions Building dimensions, in tiles. z is height
	 * @return The newly constructed building
	 */

	/**
	 * @param pos        Tile position
	 * @param dimensions Building dimensions, in tiles. z is height
	 * @param type       Building type
	 * @return The newly constructed building
	 */
	public Building addBuilding(Vector2 pos, Vector3 dimensions, BuildingType type) {
		dimensions = new Vector3(dimensions).scl(Utils.WORLD_SCALE, Utils.WORLD_SCALE, 1); // height isn't scaled
		pos = new Vector2(pos).scl(Utils.WORLD_SCALE);

		ModelInstance instance = buildingCache.createBuilding(pos, dimensions);
		Gdx.app.debug("World", String.format("Added a building at (%2f, %2f) of dimensions (%2f, %2f, %2f)", pos.x, pos.y, dimensions.x, dimensions.y, dimensions.z));

		Building building = new Building(pos, dimensions, instance);
		building.setType(type);
		buildings.add(building);

		// physics
		if (Config.get("building-collisions", Boolean.class)) {

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

	public void render(WorldCamera camera) {
		// tick physics
		float delta = Gdx.graphics.getRawDeltaTime();
		physicsWorld.step(delta, 6, 4);
		GdxAI.getTimepiece().update(delta);

		// render world
		camera.positionMapRenderer(renderer);
		renderer.render();

		// render entities
		engine.update(delta);

		// render buildings
		buildingBatch.begin(camera);
		buildings.stream()
				.filter(building -> building.isVisible(camera))
				.forEach(building -> buildingBatch.render(building.getModelInstance(), environment));
		buildingBatch.end();

		// physics debug rendering
		// debugRenderer.render(camera);
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
}
