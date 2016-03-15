package com.b3.gui.popup;

import com.b3.mode.ModeType;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class PopupManager {

	private final Popup buildingErrorPopup;
	private final Popup pseudocodeError;
	private final Popup behaviourError;
	private Popup introPopup;
	private Popup popup;

	public PopupManager(WorldCamera worldCamera, ModeType mode) {
		//load error textures
		Texture tempTexture = new Texture("core/assets/world/popups/errorBuildings.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		buildingErrorPopup = new Popup(new Sprite(tempTexture));

		tempTexture = new Texture("core/assets/world/popups/errorCode.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		pseudocodeError = new Popup(new Sprite(tempTexture));

		tempTexture = new Texture("core/assets/world/popups/errorSearch.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		behaviourError = new Popup(new Sprite(tempTexture));

		switch (mode) {
			case COMPARE:
				tempTexture = new Texture("core/assets/world/popups/Intro/C.png");
				break;
			case PRACTICE:
				tempTexture = new Texture("core/assets/world/popups/Intro/TY.png");
				break;
			case LEARNING:
				tempTexture = new Texture("core/assets/world/popups/Intro/ILM.png");
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

	public void showIntro() {
		if (introPopup != null)
			introPopup.showPopup(2000);
	}

	public void showBuildingError() {
		buildingErrorPopup.showPopup(750);
	}

	public void showPseudocodeError() {
		pseudocodeError.showPopup(750);
	}

	public void showBehaviourError() {
		behaviourError.showPopup(400);
	}

	public void render() {
		if (introPopup != null)
			introPopup.render();
		buildingErrorPopup.render();
		behaviourError.render();
		pseudocodeError.render();
	}

	public int length() {
		return 4;
	}

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
