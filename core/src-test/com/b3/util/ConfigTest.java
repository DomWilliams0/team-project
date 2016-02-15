package com.b3.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ConfigTest {

	private static final float EQ_THRESHOLD = 0.001f;

	private static final String REFERENCE_CONFIG = "src-test/resources/reference.yml";
	private static final String USER_CONFIG = "src-test/resources/userconfig.yml";


	@Before
	public void setUp() {
		
	}

	@Test
	public void testReference() {
		Config.loadConfig(REFERENCE_CONFIG);
		assertFalse(Config.getBoolean(ConfigKey.PHYSICS_RENDERING));
		assertEquals(Config.getFloat(ConfigKey.ENTITY_DIAMETER), 0.3f, EQ_THRESHOLD);
		assertEquals(Config.getInt(ConfigKey.CAMERA_DISTANCE_MAXIMUM).intValue(), 35);
	}

	@Test
	public void testUserConfig() {
		Config.loadConfig(REFERENCE_CONFIG, USER_CONFIG);
		assertFalse(Config.getBoolean(ConfigKey.CAMERA_RESTRICT));
		assertEquals(Config.getFloat(ConfigKey.CAMERA_ZOOM_SPEED), 20.25f, EQ_THRESHOLD);
		assertEquals(Config.getFloat(ConfigKey.CAMERA_MOVE_SPEED), 10.9f, EQ_THRESHOLD);
	}

}
