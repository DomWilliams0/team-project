package com.b3.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * Created by Nishanth on 03/03/2016.
 */
public class SoundController {

    private static Sound[] sounds;

    /**
     * Loads the sounds controller and loads all the potential sounds in to memory, should be called once at program launch.
     * @param soundList An Array of paths to the sounds files which will be loads in order, e.g. the first thing in this array has the soundsIndex of 1.
     */
    public SoundController(String[] soundList) {
        sounds = new Sound[soundList.length];

        for (int i = 0; i < soundList.length; i++) {
            sounds[i] = Gdx.audio.newSound(Gdx.files.internal(soundList[i]));
        }
    }

    /**
     * Plays a specific sound
     * @param soundIndex the number of the sounds to be played (ie the position it was in, when this object was created
     * @return true if played successfully; false if cannot find sounds or cannot be played
     */
    public static boolean playSounds (int soundIndex) {
        if (soundIndex >= sounds.length) return false;
        sounds[soundIndex].play(1.0f);
        return true;
    }

    /**
     * Deletes all sounds safely
     */
    public void dispose() {
        for (int i = 0; i < sounds.length; i++) {
            sounds[i].dispose();
        }
    }

}
