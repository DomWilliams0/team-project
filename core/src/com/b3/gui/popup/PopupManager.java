package com.b3.gui.popup;

import com.b3.mode.ModeType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Manages all the currently showing pop-ups in the world
 *
 * @author nbg481 bxd428
 */
public class PopupManager {

	private final Popup buildingErrorPopup;
	private final Popup pseudocodeError;
	private final Popup behaviourError;
	private Popup introPopup;

	/**
	 * Load all the correct images from the files ready to be shown, depending on the mode
	 *
	 * @param mode the current mode the user is in
	 */
	public PopupManager(ModeType mode) {
		// load error textures
		Texture tempTexture = new Texture("world/popups/error/errorBuildings.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		buildingErrorPopup = new Popup(new Sprite(tempTexture));

		tempTexture = new Texture("world/popups/error/errorCode.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		pseudocodeError = new Popup(new Sprite(tempTexture));

		tempTexture = new Texture("world/popups/error/errorSearch.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		behaviourError = new Popup(new Sprite(tempTexture));

		switch (mode) {
			case COMPARE:
				tempTexture = new Texture("world/popups/intro/C.png");
				break;
			case PRACTICE:
				tempTexture = new Texture("world/popups/intro/TY.png");
				break;
			case LEARNING:
				tempTexture = new Texture("world/popups/intro/ILM.png");
				break;
			default:
				tempTexture = null;
				break;
		}

		if (tempTexture != null) {
			tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
			introPopup = new Popup(new Sprite(tempTexture));
		}
	}

	/**
	 * Shows the introduction popup depending on the mode the user has chosen
	 */
	public void showIntro() {
		if (introPopup != null)
			introPopup.showPopup(2000);
	}

	/**
	 * shows the error, when a user tries to add /remove a building when the search is in progress
	 */
	public void showBuildingError() {
		buildingErrorPopup.showPopup(750);
	}

	/**
	 * Shows an error when the user tries to activate pseducode mode when search is not in progress
	 */
	public void showPseudocodeError() {
		pseudocodeError.showPopup(750);
	}

	/**
	 * Shows an error when a path cannot be found to the final position
	 */
	public void showBehaviourError() {
		behaviourError.showPopup(400);
	}

	/**
	 * renders any active pop-ups
	 */
	public void render() {
		if (introPopup != null)
			introPopup.render();
		buildingErrorPopup.render();
		behaviourError.render();
		pseudocodeError.render();
	}

	/**
	 * @return the number of pop-ups that this manager is managing
	 */
	public int length() {
		return 4;
	}

	/**
	 * @param i the index. Has to be less than the length
	 * @return the pop-up of the index given
	 */
	public Popup getPopup(int i) {
		switch (i) {
			case 0:
				return introPopup;
			case 1:
				return buildingErrorPopup;
			case 2:
				return behaviourError;
			case 3:
				return pseudocodeError;
		}
		return null;
	}

}
