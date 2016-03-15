package com.b3.entity.ai;

import com.b3.search.SearchTicker;

/**
 * @author dxw405
 */
public interface BehaviourWithPathFind {
	/**
	 * @return True if the final goal has been reached _since the last tick_,
	 * i.e. for the first time
	 */
	boolean hasArrivedForTheFirstTime();

	/**
	 * @return The current search ticker
	 */
	SearchTicker getSearchTicker();
}
