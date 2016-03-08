package com.b3.gui.popup;

import com.b3.mode.Mode;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class PopupManager {
	
	private final Popup buildingErrorPopup;
	private final Popup pseudocodeError;
	private final Popup behaviourError;
	private final Popup introPopup;
	
	public PopupManager(WorldCamera worldCamera, Mode mode) {
		//load error textures
		Texture tempTexture = new Texture("core/assets/world/popups/errorBuildings.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		buildingErrorPopup = new Popup(worldCamera, new Sprite(tempTexture));
		
		tempTexture = new Texture("core/assets/world/popups/errorCode.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		pseudocodeError = new Popup(worldCamera, new Sprite(tempTexture));
		
		tempTexture = new Texture("core/assets/world/popups/errorSearch.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		behaviourError = new Popup(worldCamera, new Sprite(tempTexture));
		
		switch (mode) {
			case COMPARE: tempTexture = new Texture("core/assets/world/popups/Intro/C.png");
				break;
			case TRY_YOURSELF: tempTexture = new Texture("core/assets/world/popups/Intro/TY.png");
				break;
			case LEARNING: tempTexture = new Texture("core/assets/world/popups/Intro/ILM.png");
				break;
			default: tempTexture = null; break;
		}
		
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		introPopup = new Popup(worldCamera, new Sprite(tempTexture));
	}
	
	public void showIntro() {
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
		introPopup.render();
		buildingErrorPopup.render();
		behaviourError.render();
		pseudocodeError.render();
	}
	
}
