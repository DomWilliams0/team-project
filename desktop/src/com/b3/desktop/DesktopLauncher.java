package com.b3.desktop;

import com.b3.MainGame;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * This is the main class that is run for desktop applications.
 */
public class DesktopLauncher {
	
	/**
	 * The main method.
	 * Launches the actual application {@link MainGame} from {@code core}.
	 * @param arg Ignored.
	 */
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;
		config.title = "Cop Chase";
		new LwjglApplication(new MainGame(), config);
	}
	
}
