package com.b3.search;

import com.b3.search.util.takeable.Takeable;
import com.b3.util.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchSnapshotTracker {

    private int chunk;
    private List<Tuple<Takeable<Node>, Set<Node>>> snapshots;

    public SearchSnapshotTracker() {
        chunk = 10;
        snapshots = new ArrayList<>();
    }

    public void addSnapshot(Tuple<Takeable<Node>, Set<Node>> snapshot) {
        if (snapshots.size() >= chunk) {
            // Flush and save into DB
            return;
        }

        snapshots.add(snapshot);
    }

    public List<Tuple<Takeable<Node>, Set<Node>>> getCurrentSnapshots() {
        return snapshots;
    }

    public List<Tuple<Takeable<Node>, Set<Node>>> getAllSnapshots() {
        // Get from DB

        return snapshots;
    }
}
