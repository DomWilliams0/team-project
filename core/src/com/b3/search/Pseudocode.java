package com.b3.search;

import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Observable pseudocode, which links what a user sees on the screen to what is happening in the code
 * Contains the lines of pseudocode
 * Highlights the lines
 */
public class Pseudocode extends Observable {

	private SearchAlgorithm algorithm;
	private List<Tuple<String, Tuple<Boolean, Integer>>> lines;
	private int currentLine;

	/**
	 * Constructs a new pseudocode
	 */
	public Pseudocode() {
		lines = new ArrayList<>();
		currentLine = 0;
	}

	/**
	 * Constructs a new pseudocode. Changes pseudocode depending on {@param algorithm}
	 * @param algorithm the algorithm to tailor this pseudocode to
     */
	public Pseudocode(SearchAlgorithm algorithm) {
		this.algorithm = algorithm;
		currentLine = 0;
		initLines();
	}

	/**
	 * Initialise the correct lines of pseudocode depending on the {@link SearchAlgorithm}
	 */
	private void initLines() {
		switch (algorithm) {
			case BREADTH_FIRST:
			case DEPTH_FIRST:
				lines = new ArrayList<Tuple<String, Tuple<Boolean, Integer>>>() {{
					add(new Tuple<>("while [GREEN]frontier[] is not empty:", new Tuple<>(false, 0)));
					add(new Tuple<>("[PINK]n[] <- [GREEN]frontier[].take()\n", new Tuple<>(false, 1)));
					add(new Tuple<>("[LIGHT_GRAY]visited[].add(n)\n", new Tuple<>(false, 1)));
					add(new Tuple<>("if [PINK]n[] is target:", new Tuple<>(false, 1)));
					add(new Tuple<>("return constructed path\n", new Tuple<>(false, 2)));
					add(new Tuple<>("for each node [FIREBRICK]m[] that is adjacent to [PINK]n[]:", new Tuple<>(false, 1)));
					add(new Tuple<>("if [FIREBRICK]m[] not in [LIGHT_GRAY]visited[] and not in [GREEN]frontier[]:", new Tuple<>(false, 2)));
					add(new Tuple<>("[GREEN]frontier[].add(m)", new Tuple<>(false, 3)));
				}};
				break;

			case A_STAR:
			case DIJKSTRA:
				lines = new ArrayList<Tuple<String, Tuple<Boolean, Integer>>>() {{
					add(new Tuple<>("while [GREEN]frontier[] is not empty:", new Tuple<>(false, 0)));
					add(new Tuple<>("[PINK]n[] = [GREEN]frontier[].take()\n", new Tuple<>(false, 1)));
					add(new Tuple<>("[LIGHT_GRAY]visited[].add([PINK]n[])\n", new Tuple<>(false, 1)));
					add(new Tuple<>("if [PINK]n[] is target:", new Tuple<>(false, 1)));
					add(new Tuple<>("return constructed path\n", new Tuple<>(false, 2)));
					add(new Tuple<>("for each node [FIREBRICK]m[] that is adjacent to [PINK]n[]:", new Tuple<>(false, 1)));
					add(new Tuple<>("tentative_g <- g([PINK]n[]) + edgeCost([PINK]n[], [FIREBRICK]m[])", new Tuple<>(false, 2)));
					add(new Tuple<>("if tentative_g <= g([FIREBRICK]m[]):", new Tuple<>(false, 2)));
					add(new Tuple<>("cameFrom.put([FIREBRICK]m[], [PINK]n[])", new Tuple<>(false, 3)));
					add(new Tuple<>("if [FIREBRICK]m[] not in [LIGHT_GRAY]visited[] and not in [GREEN]frontier[]:", new Tuple<>(false, 3)));
					add(new Tuple<>("frontier.add([FIREBRICK]m[])", new Tuple<>(false, 4)));
				}};
				break;
		}
	}

	/**
	 * @return the {@link SearchAlgorithm} that this pseudocode visualisation is using
     */
	public SearchAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Changes the pseudocode to match the new {@code algorithm}
	 * @param algorithm the algorithm to re-initialise this Pseuocode with
     */
	public void setAlgorithm(SearchAlgorithm algorithm) {
		this.algorithm = algorithm;
		initLines();

		setChanged();
		notifyObservers();
	}

	/**
	 * @return a {@link Tuple} of all the lines of pseudocode
     */
	public List<Tuple<String, Tuple<Boolean, Integer>>> getLines() {
		return lines;
	}

	/**
	 * @return the index of teh current line of pseudocode that is currently ighlighted
     */
	public int getCurrentLine() {
		return currentLine;
	}

	/**
	 * @param currentLine the line that will be highlighted
     */
	public void setCurrentLine(int currentLine) {
		this.currentLine = currentLine;
	}

	/**
	 * De-highlights the pseudocode and then highlights the current line of pseudocode
	 * @param i the line to highlight
     */
	public void highlight(int i) {
		// Un-highlight
		lines = lines
				.stream()
				.map(tuple -> new Tuple<>(tuple.getFirst(), new Tuple<>(false, tuple.getSecond().getSecond())))
				.collect(Collectors.toList());

		if (i - 1 >= 0) {
			Tuple<String, Tuple<Boolean, Integer>> tuple = lines.get(i - 1);
			lines.set(i - 1, new Tuple<>(tuple.getFirst(), new Tuple<>(true, tuple.getSecond().getSecond())));

			currentLine = i;
		}

		setChanged();
		notifyObservers();
	}

}
