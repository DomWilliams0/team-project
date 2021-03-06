package com.b3.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Controls all of the sounds that will play during the lifetime of the program.
 *
 * @author nbg481
 */
public class SoundController {

	private static Sound[] sounds;
	private static long id;

	/**
	 * Loads the sounds controller and loads all the potential sounds in to memory, should be called once at program launch.
	 *
	 * @param soundList An Array of paths to the sounds files which will be loads in order, e.g. the first thing in this array has the soundsIndex of 1.
	 */
	public SoundController(String[] soundList) {
		sounds = new Sound[soundList.length];

		id = -1;

		for (int i = 0; i < soundList.length; i++) {
			sounds[i] = Gdx.audio.newSound(Gdx.files.internal(soundList[i]));
		}
	}

	/**
	 * Plays a specific sound
	 *
	 * @param soundIndex the number of the sounds to be played (ie the position it was in, when this object was created
	 * @return true if played successfully; false if cannot find sounds or cannot be played
	 */
	public static boolean playSounds(int soundIndex) {
		if (soundIndex >= sounds.length) return false;
		sounds[soundIndex].play(1.0f);
		return true;
	}

	/**
	 * Deletes all sounds safely
	 */
	public void dispose() {
		for (Sound sound : sounds) {
			sound.dispose();
		}
	}

	/**
	 * Plays a specific sounds at a specific pitch
	 *
	 * @param soundIndex the number of the sounds to be played (ie the position it was in, when this object was created
	 * @param pitch      the pitch of the sound to be played. From 0.5 -> 2, with 0 is normal pitch, 0.5 is lower and 2 is highest
	 * @return true if played successfully; false if cannot find sounds or cannot be played
	 */
	public static boolean playSounds(int soundIndex, float pitch) {
		if (id == -1) id = sounds[soundIndex].play(1.0f, pitch, 0);
		sounds[soundIndex].setLooping(id, true);
		sounds[soundIndex].setPitch(id, pitch);
		return true;
	}

	/**
	 * Stops soundIndex from playing, if already playing
	 *
	 * @param soundIndex the index of the sound to stop
	 */
	public static void stopSound(int soundIndex) {
		if (id != -1) {
			sounds[soundIndex].stop();
			id = -1;
		}
	}

}
