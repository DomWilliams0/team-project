package com.b3.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class Database {

    private Database() {}

    public static void init() {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:core/assets/b3.db");
            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT COUNT(*) as cnt FROM sqlite_master WHERE type='table' AND name='search';");
            rs.next();
            if (rs.getInt("cnt") == 0) {
                // Initialise tables
                String sql = new String(Files.readAllBytes(Paths.get("core/assets/b3.sql")));
                statement.executeUpdate(sql);
            }

            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

}
