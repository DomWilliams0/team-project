package com.b3.search;

/**
 * @author nbg481
 */
public class PointTimer {

	private final int x;
	private final int y;
	private int timer;

	public PointTimer(int x, int y, int timer) {
		this.x = x;
		this.y = y;
		this.timer = timer;
	}

	public Point getPoint() {
		return new Point(x, y);
	}

	public int getTimer() {
		return timer;
	}

	public void decrementTimer() {
		timer--;
	}

	public void decrementTimer(int change) {
		timer = timer - change;
	}

	public Boolean finishedTiming() {
		return (timer == 0);
	}

}
