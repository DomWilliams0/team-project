package com.b3.search;

import com.b3.search.util.takeable.Takeable;
import com.b3.util.Tuple;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchSnapshotTracker {

    private int chunk;
    private List<Tuple<Takeable<Node>, Set<Node>>> snapshots;

    public SearchSnapshotTracker(SearchTicker searchTicker) throws ClassNotFoundException {
        chunk = 10;
        snapshots = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:core/assets/b3.db");
            Statement statement = connection.createStatement();

            statement.executeUpdate("INSERT INTO search (algorithm, datetime) VALUES ('" + searchTicker.getAlgorithm().toString());

            ResultSet rs = statement.executeQuery("SELECT COUNT(*) as cnt FROM sqlite_master WHERE type='table' AND name='search';");
            rs.next();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
