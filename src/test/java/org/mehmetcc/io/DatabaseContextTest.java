package org.mehmetcc.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseContextTest {
    @TempDir
    File temporaryDirectory;

    DatabaseContext context;

    @BeforeEach
    void setup() {
        context = new DatabaseContext();
    }

    @Test
    void shouldCreateDatabase() {
        var content = List.of("a", "b", "c");
        var path = temporaryDirectory.toPath().resolve(Path.of("database"));
        var database = path.resolveSibling(path.getFileName() + ".sqlite");
        context.fill(content, path);

        assertTrue(Files.exists(database));
    }

}