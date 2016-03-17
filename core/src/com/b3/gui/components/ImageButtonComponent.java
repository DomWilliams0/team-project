package com.b3.gui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.text.MessageFormat;

/**
 * Represents an image button component
 */
public class ImageButtonComponent extends Component {

	private ImageButton imageButton;
	private Object data;

	/**
	 * Creates an instance of an ImageButtonComponent
	 *
	 * @param imageUpPath   The path to the image for the UP state
	 * @param imageDownPath The path to the image for the DOWN state
	 * @param imageOverPath The path to the image for the OVER state
	 */
	public ImageButtonComponent(String imageUpPath, String imageDownPath, String imageOverPath) {
		Texture textureUp = new Texture(Gdx.files.internal(MessageFormat.format("gui/buttons/{0}", imageUpPath)));
		Texture textureDown = new Texture(Gdx.files.internal(MessageFormat.format("gui/buttons/{0}", imageDownPath)));
		Texture textureOver = new Texture(Gdx.files.internal(MessageFormat.format("gui/buttons/{0}", imageOverPath)));
		TextureRegion imageUp = new TextureRegion(textureUp);
		TextureRegion imageDown = new TextureRegion(textureDown);
		TextureRegion imageOver = new TextureRegion(textureOver);

		ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
		imageButtonStyle.imageUp = new TextureRegionDrawable(imageUp);
		imageButtonStyle.imageDown = new TextureRegionDrawable(imageDown);
		imageButtonStyle.imageOver = new TextureRegionDrawable(imageOver);

		imageButton = new ImageButton(imageButtonStyle);
	}

	/**
	 * @return The inner button representation
	 */
	public ImageButton getComponent() {
		return imageButton;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public void addListener(ChangeListener listener) {
		imageButton.addListener(listener);
	}


}
