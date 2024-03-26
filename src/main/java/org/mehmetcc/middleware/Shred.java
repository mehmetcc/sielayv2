package org.mehmetcc.middleware;

import org.mehmetcc.io.FileContext;

import java.nio.file.Path;
import java.util.Optional;

public class Shred {
    private static final Integer NUMBER_OF_OVERWRITES = 3;

    private static final String SOURCE_FILE_LOCATION = "nasreddin_hodja.txt";

    private final Path path;

    private final FileContext context;

    public Shred(final Path path, final FileContext context) {
        this.path = path;
        this.context = context;
    }

    public Optional<Boolean> execute() {
        return context.readResource(Path.of(SOURCE_FILE_LOCATION))
                .flatMap(result -> validate(path).map(path -> new PathAndContents(path, result)))
                .flatMap(this::overwrite)
                .flatMap(this::delete);
    }

    private Optional<Path> validate(final Path path) {
        if (context.exists(path)) return Optional.of(path);
        else return Optional.empty();
    }

    private Optional<PathAndContents> overwrite(final PathAndContents result) {
        var check = true;
        for (int i = 0; i < NUMBER_OF_OVERWRITES && check; i++)
            check = context.write(path, result.contents());
        return check ? Optional.of(result) : Optional.empty();
    }

    private Optional<Boolean> delete(final PathAndContents result) {
        return Optional.of(context.delete(result.path()));
    }

    private record PathAndContents(Path path, String contents) { }
}
