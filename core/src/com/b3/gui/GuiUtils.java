package com.b3.gui;

import com.b3.gui.components.CheckBoxComponent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.function.Consumer;

/**
 * Static utils for GUI
 *
 * @author oxe410 dxw405
 */
public class GuiUtils {

	private GuiUtils() {
		// no instantiation4u
	}

	/**
	 * Utility to create a checkbox
	 *
	 * @param skin      The libGDX skin
	 * @param font      The font to apply
	 * @param table     The parent tab
	 * @param label     The label next to the checkbox
	 * @param configKey The associated configuration key
	 */
	public static void createCheckbox(Skin skin, BitmapFont font, Table table, String label, ConfigKey configKey, float preferredWidth) {
		createCheckbox(skin, font, table, label, configKey, null, preferredWidth);
	}

	/**
	 * Utility to create a checkbox
	 *
	 * @param skin      The libGDX skin
	 * @param font      The font to apply
	 * @param table     The parent tab
	 * @param label     The label next to the checkbox
	 * @param configKey The associated configuration key
	 */
	public static void createCheckbox(Skin skin, BitmapFont font, Table table, String label, ConfigKey configKey,
	                                  Consumer<Boolean> checkedListener, float preferredWidth) {
		CheckBoxComponent checkBox = new CheckBoxComponent(skin, font, label);
		checkBox.getComponent().setChecked(Config.getBoolean(configKey));
		checkBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean checked = checkBox.getComponent().isChecked();
				Config.set(configKey, checked);
				if (checkedListener != null)
					checkedListener.accept(checked);
			}
		});

		table.add(checkBox.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceBottom(10);
		table.row();
	}

}
