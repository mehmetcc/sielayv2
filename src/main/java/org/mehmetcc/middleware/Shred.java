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
        System.out.println("Running shred.");
        var result = context.readResource(Path.of(SOURCE_FILE_LOCATION))
                .flatMap(source -> validate(path).map(path -> new PathAndContents(path, source)))
                .flatMap(this::overwrite)
                .flatMap(this::delete);
        System.out.println("shred finished.");
        return result;
    }

    private Optional<Path> validate(final Path path) {
        if (context.exists(path)) return Optional.of(path);
        else return Optional.empty();
    }

    private Optional<PathAndContents> overwrite(final PathAndContents result) {
        var check = true;
        for (int i = 0; i < NUMBER_OF_OVERWRITES && check; i++) { // I really dislike writing loops with curly braces
            System.out.printf("%s Overwriting.%n", i + 1);
            check = context.write(path, result.contents());
        }
        return check(result, check);
    }

    private Optional<PathAndContents> check(final PathAndContents result, final Boolean check) {
        if (check) return Optional.of(result);
        else {
            System.err.println("Overwrite failed.");
            return Optional.empty();
        }
    }

    private Optional<Boolean> delete(final PathAndContents result) {
        return Optional.of(context.delete(result.path()));
    }

    private record PathAndContents(Path path, String contents) {
    }
}
