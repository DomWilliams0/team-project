package com.b3.entity.ai;

public interface BehaviourWithPathFind {
	/**
	 * @return True if the final goal has been reached _since the last tick_,
	 * i.e. for the first time
	 */
	boolean hasArrivedForTheFirstTime();
}
