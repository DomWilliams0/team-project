package com.b3.util;

import com.b3.TestConstants;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the {@link Config} class.
 * 
 * @author dxw405
 */
public class ConfigTest {

	@Before
	public void setUp() {
	}

	@Test
	public void testReference() {
		Config.loadConfig(TestConstants.REFERENCE_CONFIG);
		assertFalse(Config.getBoolean(ConfigKey.PHYSICS_RENDERING));
		assertEquals(Config.getFloat(ConfigKey.ENTITY_DIAMETER), 0.3f, TestConstants.EQ_THRESHOLD);
		assertEquals(Config.getInt(ConfigKey.CAMERA_DISTANCE_MAXIMUM).intValue(), 35);
	}

	@Test
	public void testUserConfig() {
		Config.loadConfig(TestConstants.REFERENCE_CONFIG, TestConstants.USER_CONFIG);
		assertFalse(Config.getBoolean(ConfigKey.CAMERA_RESTRICT));
		assertEquals(Config.getFloat(ConfigKey.CAMERA_ZOOM_SPEED), 20.25f, TestConstants.EQ_THRESHOLD);
		assertEquals(Config.getFloat(ConfigKey.CAMERA_MOVE_SPEED), 10.9f, TestConstants.EQ_THRESHOLD);
	}

	@Test
	public void testReferenceConfigFound() {
		try {
			Config.loadConfig("definitely-doesn't-exist-ref.yml");
		} catch (IllegalArgumentException e) {
			return;
		}

		fail("Reference config wasn't found but didn't throw an exception");
	}

	@Test
	public void testUserConfigNotFound() {
		try {
			System.err.println("--- Expecting error message about definitely-doesn't-exist-user.yml");
			Config.loadConfig(TestConstants.REFERENCE_CONFIG, "definitely-doesn't-exist-user.yml");
			System.err.println("--- End of error message");
		} catch (Exception e) {
			fail("User config wasn't found but did throw an exception");
		}
	}

}
