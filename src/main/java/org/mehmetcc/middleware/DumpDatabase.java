package org.mehmetcc.middleware;

import org.mehmetcc.context.ApplicationContextDeserializer;
import org.mehmetcc.io.DatabaseContext;
import org.mehmetcc.io.FileContext;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

public class DumpDatabase {
    private final FileContext fileContext;

    private final DatabaseContext databaseContext;

    private final ApplicationContextDeserializer deserializer;

    private final Path path;

    private final String seperator;

    public DumpDatabase(final Path path,
                        final FileContext fileContext,
                        final DatabaseContext databaseContext,
                        final ApplicationContextDeserializer deserializer,
                        final String seperator) {
        this.fileContext = fileContext;
        this.databaseContext = databaseContext;
        this.deserializer = deserializer;
        this.path = path;
        this.seperator = seperator;
    }

    public Optional<Boolean> execute() {
        if (Objects.isNull(path))
            return deserializer.deserialize()
                    .flatMap(result -> writeDatabase(result.contents(), result.seperator()));
        else return validate(path)
                .flatMap(fileContext::read)
                .flatMap(contents -> writeDatabase(contents, seperator));
    }

    private Optional<Path> validate(final Path path) {
        if (fileContext.exists(path)) return Optional.of(path);
        else return Optional.empty();
    }

    private Optional<Boolean> writeDatabase(final String contents, final String seperator) {
        databaseContext.fill(lines(contents, seperator));
        return Optional.of(true);
    }

    private List<String> lines(final String contents, final String seperator) {
        try {
            return Arrays.asList(contents
                    .replaceAll("\n", "")
                    .split(seperator));
        } catch (PatternSyntaxException e) {
            System.err.println("Invalid escape character. Use a different seperator in the future.");
            return List.of(contents);
        }
    }
}
