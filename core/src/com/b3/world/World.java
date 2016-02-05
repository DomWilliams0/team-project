package com.b3.world;

import com.b3.Utils;
import com.b3.entity.component.PhysicsComponent;
import com.b3.entity.component.RenderComponent;
import com.b3.entity.system.RenderSystem;
import com.b3.event.EventGenerator;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.List;

public class World implements Disposable {

	private Vector2 tileSize, pixelSize;

	private TiledMapRenderer renderer;
	private Environment environment;

	private ModelBatch buildingBatch;
	private List<Building> buildings;
	private BuildingModelCache buildingCache;

	private Engine engine;
	private com.badlogic.gdx.physics.box2d.World physicsWorld;

	private EventGenerator eventGenerator;
	private WorldObserver worldObserver;

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

		createDefaultBuildings();

		// entities
		engine = new Engine();
		physicsWorld = new com.badlogic.gdx.physics.box2d.World(Vector2.Zero, true);
	}

	public void initEngine(PerspectiveCamera camera) {
//		engine.addSystem(new MovementSystem());
		engine.addSystem(new RenderSystem(camera));

		// debug: test entity
		addAgent(new Vector2(50, 50));
	}

	public void initEventGenerator() {
		// Uncomment for event generator (not working atm)
		//worldObserver = new WorldObserver();
		//eventGenerator = new EventGenerator(this);
		//eventGenerator.addObserver(worldObserver);
		//new Thread(eventGenerator).start();
	}

	public Engine getEngine() {
		return engine;
	}

	private void createDefaultBuildings() {
		Vector3 dim = new Vector3(2, 2, 0);
		float space = 4f;

		for (int x = 0; x < tileSize.x / space; x++)
			for (int y = 0; y < tileSize.y / space; y++)
				addBuilding(new Vector2(x * space * dim.x, y * space * dim.y), new Vector3(dim.x, dim.y, Utils.randomRange(5, 12)));
	}

	public Entity addAgent(Vector2 tilePos) {
		Entity e = new Entity();
//		e.add(new PositionComponent(tilePos.x, tilePos.y));
//		e.add(new VelocityComponent(10, 10)); // debug: test movement
		e.add(new RenderComponent(Color.BLUE, 2f));

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		e.add(new PhysicsComponent(physicsWorld, bodyDef, tilePos));

		engine.addEntity(e);
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
	public Building addBuilding(Vector2 pos, Vector3 dimensions) {
		ModelInstance instance = buildingCache.createBuilding(pos, dimensions);
		Gdx.app.debug("World", String.format("Added a building at (%2f, %2f) of dimensions (%2f, %2f, %2f)", pos.x, pos.y, dimensions.x, dimensions.y, dimensions.z));

		Building building = new Building(pos, dimensions, instance);
		buildings.add(building);

		return building;
	}

	// todo remove buildings too

	public void render(WorldCamera camera) {
		// tick physics
		physicsWorld.step(Gdx.graphics.getRawDeltaTime(), 6, 4);

		// render world
		camera.positionMapRenderer(renderer);
		renderer.render();

		// render entities
		engine.update(Gdx.graphics.getRawDeltaTime());

		// render buildings
		buildingBatch.begin(camera);
		buildings.stream()
				.filter(building -> building.isVisible(camera))
				.forEach(building -> buildingBatch.render(building.getModelInstance(), environment));
		buildingBatch.end();
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
