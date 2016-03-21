package com.b3.search;

import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Tuple;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Observable pseudocode, which links what a user sees on the screen to what is happening in the code
 * Contains the lines of pseudocode
 * Highlights the lines
 *
 * @author oxe410 nbg481
 */
public class Pseudocode extends Observable {

	private SearchTicker searchTicker;
	private ArrayList<String> arrayList;
	private SearchAlgorithm algorithm;
	private List<Tuple<String, Tuple<Boolean, Integer>>> lines;
	private int currentLine;
	private String firstFrontier;

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
	public Pseudocode(SearchAlgorithm algorithm, SearchTicker searchTicker) {
		this.algorithm = algorithm;
		this.searchTicker = searchTicker;

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
					add(new Tuple<>("[LIGHT_GRAY]visited[].add([PINK]n[])\n", new Tuple<>(false, 1)));
					add(new Tuple<>("if [PINK]n[] is target:", new Tuple<>(false, 1)));
					add(new Tuple<>("return constructed path\n", new Tuple<>(false, 2)));
					add(new Tuple<>("for each node [SCARLET]m[] that is adjacent to [PINK]n[]:", new Tuple<>(false, 1)));
					add(new Tuple<>("if [SCARLET]m[] not in [LIGHT_GRAY]visited[] and not in [GREEN]frontier[]:", new Tuple<>(false, 2)));
					add(new Tuple<>("[GREEN]frontier[].add([SCARLET]m[])", new Tuple<>(false, 3)));
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
					add(new Tuple<>("for each node [SCARLET]m[] that is adjacent to [PINK]n[]:", new Tuple<>(false, 1)));
					add(new Tuple<>("tentative_g <- g([PINK]n[]) + edgeCost([PINK]n[], [FIREBRICK]m[])", new Tuple<>(false, 2)));
					add(new Tuple<>("if tentative_g <= g([SCARLET]m[]):", new Tuple<>(false, 2)));
					add(new Tuple<>("cameFrom.put([SCARLET]m[], [PINK]n[])", new Tuple<>(false, 3)));
					add(new Tuple<>("if [SCARLET]m[] not in [LIGHT_GRAY]visited[] and not in [GREEN]frontier[]:", new Tuple<>(false, 3)));
					add(new Tuple<>("frontier.add([SCARLET]m[])", new Tuple<>(false, 4)));
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
		else
			currentLine = 0;

		setChanged();
		notifyObservers();

		updateLines(i);
	}

	/**
	 * Updates the lines depending on the algorithm and works out the link between the constants and variables
	 *
	 * @param i the line to evaluate for variables
     */
	private void updateLines(int i) {

		System.out.println("CURRENT LINE " + i);

		if (arrayList == null) {
			arrayList = new ArrayList<>(20);
			for (int j = 0; j < 20; j++) {
				arrayList.add(" ");
			}
		} else {
			if (arrayList.size() >= i)
				switch (algorithm) {
					case A_STAR:
						updateForAStar(i);
						break;
					case BREADTH_FIRST:
//						updateForBreathFirst(i);
						updateForDepthFirst(i);
						break;
					case DEPTH_FIRST:
						updateForDepthFirst(i);
						break;
				}
		}
	}

	/**
	 * TODO
     */
	private String updateForDepthFirst(int i) {
		String tempText = "";
		switch (i) {
			case 1: {
				if (searchTicker.getFrontier() != null)
					if (searchTicker.getFrontier().size() >0)
						firstFrontier = searchTicker.getFrontier().peek().toString();

			}
			case 2: {
				arrayList.set(1, firstFrontier);
			}
			break;
			case 3: {

				arrayList.set(2, firstFrontier);
			}
			break;
			case 4: {
				arrayList.set(3, firstFrontier);
			}
			break;
			case 6: {
				if (searchTicker.getMostRecentlyExpanded().getNeighbours() != null)
					tempText = searchTicker.getMostRecentlyExpanded().getNeighbours().toString();
				arrayList.set(5, tempText);
			}
			break;
			case 7: {
				if (searchTicker.getCurrentNeighbour() != null)
					tempText = searchTicker.getCurrentNeighbour().toString();
				arrayList.set(6, tempText);

				if (searchTicker.getMostRecentlyExpanded().getNeighbours() != null)
					arrayList.set(5, tempText + " : " + searchTicker.getMostRecentlyExpanded().getNeighbours().toString());
			}
			break;
			case 8: {
				if (searchTicker.getCurrentNeighbour() != null)
					tempText = searchTicker.getCurrentNeighbour().toString();
				arrayList.set(7, tempText);
			}
			break;
		}

		for (int j = i+1; j < arrayList.size(); j++)
			arrayList.set(j, "");

		return tempText;
	}

	/**
	 * TODO
     */
	private String updateForBreathFirst(int i) {
		return null;
	}

	/**
	 * Updates the varibale-constant updating system
	 *
	 * @param i the line to evaluate
	 * @return the string that the line was set to
     */
	private String updateForAStar(int i) {
		String tempText = "";
		switch (i) {
			case 1: {
				if (searchTicker.getFrontier().peek() != null)
					tempText = searchTicker.getFrontier().peek().toString();
				else
					tempText = searchTicker.getStart().toString();
				arrayList.set(i, tempText);
			}
			break;
			case 2: {
				if (searchTicker.getMostRecentlyExpanded() != null)
					tempText = searchTicker.getMostRecentlyExpanded().toString();
				else
					tempText = searchTicker.getStart().toString();
				arrayList.set(i, tempText);
			}
			break;
			case 4: {
				if (searchTicker.getMostRecentlyExpanded() != null)
					tempText = searchTicker.getMostRecentlyExpanded().toString();
				else
					tempText = searchTicker.getStart().toString();
				arrayList.set(3, tempText);
			}
			break;
			case 6: {
				if (searchTicker.getMostRecentlyExpanded().getNeighbours() != null)
					tempText = searchTicker.getMostRecentlyExpanded().getNeighbours().toString();
				else if (searchTicker.getCurrentNeighbour() != null)
					tempText = searchTicker.getCurrentNeighbour().toString();
				else
					if (searchTicker.getCurrentNeighbours() != null)
						tempText = searchTicker.getMostRecentlyExpanded().getNeighbours().toArray()[0].toString();
				arrayList.set(5, tempText);
			}
			break;
			case 7: {
				tempText = searchTicker.getTentative_gString().toString();
				arrayList.set(6, tempText);
			}
			break;
			case 8: {
				if (searchTicker.getCurrentNeighbour() != null)
					tempText = searchTicker.getCurrentNeighbour().toString();
				else
				if (searchTicker.getCurrentNeighbours() != null)
					tempText = searchTicker.getMostRecentlyExpanded().getNeighbours().toArray()[0].toString();
				arrayList.set(5, tempText + ": " + searchTicker.getMostRecentlyExpanded().getNeighbours().toString());
				arrayList.set(7, tempText);
			}
			break;
			case 9: {
				if (searchTicker.getCurrentNeighbour() != null)
					tempText = searchTicker.getCurrentNeighbour().toString();
				else
				if (searchTicker.getCurrentNeighbours() != null)
					tempText = searchTicker.getMostRecentlyExpanded().getNeighbours().toArray()[0].toString();
				arrayList.set(5, tempText + ": " + searchTicker.getMostRecentlyExpanded().getNeighbours().toString());
				arrayList.set(8, tempText);
			}
			break;
			case 10: {
				if (searchTicker.getCurrentNeighbour() != null)
					tempText = searchTicker.getCurrentNeighbour().toString();
				arrayList.set(9, tempText);
			}
			break;
			case 11: {
				if (searchTicker.getCurrentNeighbour() != null)
					tempText = searchTicker.getCurrentNeighbour().toString();
				arrayList.set(10, tempText);
			}
			break;
		}

		for (int j = i+1; j < arrayList.size(); j++)
			arrayList.set(j, "");

		return tempText;
	}

	/**
	 * Gets the variables and their value
	 *
	 * @param i the line to evaluate
	 * @return a {@link Tuple} of two strings, the first is the constants and the second the variable
     */
	public Tuple<String, String> getImportantInfo(int i) {
		if (arrayList == null) {
			return null;
		}
		if (arrayList.size() <= i) {
			return null;
		}

		String replacement = "";
		if (algorithm == SearchAlgorithm.A_STAR) {
			if (i <= 4)
				replacement = "n";
			else if (i == 5)
				replacement = "m";
			else if (i >= 7)
				replacement = "m";
			else
				replacement = "-";
			if (i == 4 || i == 0)
				replacement = "+";
		} else if (algorithm == SearchAlgorithm.DEPTH_FIRST || algorithm == SearchAlgorithm.BREADTH_FIRST) {
			if (i <= 4)
				replacement = "n";
			else if (i == 5)
				replacement = "m";
			else if (i >= 7)
				replacement = "m";
			else
				replacement = "m";
			if (i == 4 || i == 0)
				replacement = "+";
		}

		return new Tuple<String, String>(arrayList.get(i), replacement);
	}

}
