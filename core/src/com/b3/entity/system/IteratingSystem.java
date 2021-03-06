package com.b3.entity.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

/**
 * A simple EntitySystem that iterates over each entity and calls processEntity() for each entity every time the EntitySystem is
 * updated. This is really just a convenience class as most systems iterate over a list of entities.
 *
 * @author Stefan Bachmann
 *         Modified by Dom Williams
 *         dxw405
 */
public abstract class IteratingSystem extends EntitySystem {
	private Family family;
	private ImmutableArray<Entity> entities;

	/**
	 * Instantiates a system that will iterate over the entities described by the Family.
	 *
	 * @param family The family of entities iterated over in this System
	 */
	public IteratingSystem(Family family) {
		this(family, 0);
	}

	/**
	 * Instantiates a system that will iterate over the entities described by the Family, with a specific priority.
	 *
	 * @param family   The family of entities iterated over in this System
	 * @param priority The priority to execute this system with (lower means higher priority)
	 */
	public IteratingSystem(Family family, int priority) {
		super(priority);

		this.family = family;
	}

	/**
	 * Called when this {@link System} is added to the given {@link Engine}
	 */
	@Override
	public void addedToEngine(Engine engine) {
		entities = engine.getEntitiesFor(family);
	}

	/**
	 * Called when this {@link System} is remove from the given {@link Engine}
	 */
	@Override
	public void removedFromEngine(Engine engine) {
		entities = null;
	}

	/**
	 * Called every frame before {@link IteratingSystem#processEntity(Entity, float)} is called
	 * on every entity
	 */
	public void beginProcessing() {

	}

	/**
	 * Called every frame after {@link IteratingSystem#processEntity(Entity, float)} is called
	 * on every entity
	 */
	public void endProcessing() {

	}

	/**
	 * Called every tick by the parent {@link Engine}
	 *
	 * @param deltaTime The delta time between the last and current frame
	 */
	@Override
	public void update(float deltaTime) {
		beginProcessing();
		for (int i = 0; i < entities.size(); ++i) {
			processEntity(entities.get(i), deltaTime);
		}
		endProcessing();
	}

	/**
	 * @return {@link ImmutableArray} of entities processed by the system
	 */
	public ImmutableArray<Entity> getEntities() {
		return entities;
	}

	/**
	 * @return The {@link Family} used when the system was created
	 */
	public Family getFamily() {
		return family;
	}

	/**
	 * This method is called on every entity on every update call of the EntitySystem. Override this to implement your system's
	 * specific processing.
	 *
	 * @param entity    The current {@link Entity} being processed
	 * @param deltaTime The delta time between the last and current frame
	 */
	protected abstract void processEntity(Entity entity, float deltaTime);
}
