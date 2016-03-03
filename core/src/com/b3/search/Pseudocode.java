package com.b3.search;

import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.stream.Collectors;

public class Pseudocode extends Observable {

    private SearchAlgorithm algorithm;
    private List<Tuple<String, Tuple<Boolean, Integer>>> lines;
    private int currentLine;

    public Pseudocode() {
        lines = new ArrayList<>();
        currentLine = 0;
    }

    public Pseudocode(SearchAlgorithm algorithm) {
        this.algorithm = algorithm;
        currentLine = 0;
        initLines();
    }

    private void initLines() {
        lines = new ArrayList<Tuple<String, Tuple<Boolean, Integer>>>() {{
            add(new Tuple<>("while frontier is not empty:",                 new Tuple<>(false, 0)));
            add(new Tuple<>("current = frontier.take()\n",                  new Tuple<>(false, 1)));
            add(new Tuple<>("visited.add(current)\n",                       new Tuple<>(false, 2)));
            add(new Tuple<>("if current is target:",                        new Tuple<>(false, 2)));
            add(new Tuple<>("return constructed path\n",                    new Tuple<>(false, 3)));
            add(new Tuple<>("for each node n that is adjacent to current:", new Tuple<>(false, 2)));
            add(new Tuple<>("if n not in visited and not in frontier:",     new Tuple<>(false, 3)));
            add(new Tuple<>("frontier.add(s)\n",                            new Tuple<>(false, 4)));
        }};

        switch (algorithm) {
            case BREADTH_FIRST:
            case DEPTH_FIRST:
                lines = new ArrayList<Tuple<String, Tuple<Boolean, Integer>>>() {{
                    add(new Tuple<>("while frontier is not empty:",                 new Tuple<>(false, 0)));
                    add(new Tuple<>("current <- frontier.take()\n",                  new Tuple<>(false, 1)));
                    add(new Tuple<>("visited.add(current)\n",                       new Tuple<>(false, 1)));
                    add(new Tuple<>("if current is target:",                        new Tuple<>(false, 1)));
                    add(new Tuple<>("return constructed path\n",                    new Tuple<>(false, 2)));
                    add(new Tuple<>("for each node n that is adjacent to current:", new Tuple<>(false, 1)));
                    add(new Tuple<>("if n not in visited and not in frontier:",     new Tuple<>(false, 2)));
                    add(new Tuple<>("frontier.add(n)\n",                            new Tuple<>(false, 3)));
                }};
                break;

            case A_STAR:
                lines = new ArrayList<Tuple<String, Tuple<Boolean, Integer>>>() {{
                    add(new Tuple<>("while frontier is not empty:",                 new Tuple<>(false, 0)));
                    add(new Tuple<>("current = frontier.take()\n",                  new Tuple<>(false, 1)));
                    add(new Tuple<>("visited.add(current)\n",                       new Tuple<>(false, 1)));
                    add(new Tuple<>("if current is target:",                        new Tuple<>(false, 1)));
                    add(new Tuple<>("return constructed path\n",                    new Tuple<>(false, 2)));
                    add(new Tuple<>("for each node n that is adjacent to current:", new Tuple<>(false, 1)));
                    add(new Tuple<>("tentative_g <- costSoFarFunction(current) + edgeCostFunction(current, n)",     new Tuple<>(false, 2)));
                    add(new Tuple<>("if tentative_g <= costSoFarFunction(n):",                            new Tuple<>(false, 2)));
                    add(new Tuple<>("cameFrom.put(n, current)",                            new Tuple<>(false, 3)));
                    add(new Tuple<>("if n not in visited and not in frontier:",                            new Tuple<>(false, 3)));
                    add(new Tuple<>("frontier.add(n)\n",                            new Tuple<>(false, 4)));
                }};

                break;

            case DIJKSTRA:

                break;
        }
    }

    public SearchAlgorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(SearchAlgorithm algorithm) {
        this.algorithm = algorithm;
        initLines();

        setChanged();
        notifyObservers();
    }

    public List<Tuple<String, Tuple<Boolean, Integer>>> getLines() {
        return lines;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(int currentLine) {
        this.currentLine = currentLine;
    }

    public void highlight(int i) {
        // Un-highlight
        lines = lines
                .stream()
                .map(tuple -> new Tuple<>(tuple.getFirst(), new Tuple<>(false, tuple.getSecond().getSecond())))
                .collect(Collectors.toList());

        Tuple<String, Tuple<Boolean, Integer>> tuple = lines.get(i - 1);
        lines.set(i - 1, new Tuple<>(tuple.getFirst(), new Tuple<>(true, tuple.getSecond().getSecond())));

        currentLine = i;

        setChanged();
        notifyObservers();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}