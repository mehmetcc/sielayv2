package org.mehmetcc.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.UUID;

public class DatabaseContext {
    public void fill(final List<String> content) {
        createDirectory();

        var databasePath = "databases/" + UUID.randomUUID();
        executeStatement(content, databasePath);
    }

    public void fill(final List<String> content, Path path) {
        executeStatement(content, path.toString());
    }

    private void executeStatement(final List<String> content, final String databasePath) {
        String url = "jdbc:sqlite:" + databasePath + ".sqlite";

        // This can be divided into sub-functions, but I did not want to mess with control
        // of managed resources
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement()) {
            System.out.printf("A new database under %s has been created.%n", databasePath);

            statement.setQueryTimeout(30); // in seconds
            statement.execute("create table lines (id integer, line string)");

            for (int i = 0; i < content.size(); i++) {
                statement.executeUpdate(
                        "insert into lines values(%d, '%s')".formatted(i, trimString(content.get(i))));
            }

        } catch (SQLException e) {
            System.err.println("Failed to fill in the database.");
            System.err.println(e.getMessage());
        }
    }

    private String trimString(final String str) {
        return str.replace("'", "\"");
    }

    private void createDirectory() {
        try {
            Files.createDirectories(Paths.get("databases"));
        } catch (IOException e) {
            System.err.println("Error happened while creating databases subdirectory");
        }
    }
}
