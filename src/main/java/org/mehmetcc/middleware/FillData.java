package org.mehmetcc.middleware;

import org.mehmetcc.io.FileContext;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public class FillData {
    private static final String SOURCE_FILE_LOCATION = "queen_of_hearts.txt";

    private final Path path;

    private final FileContext context;

    private final String seperator;

    public FillData(final Path path, final FileContext context, final String seperator) {
        this.path = path;
        this.context = context;
        this.seperator = seperator;
    }

    public Optional<Boolean> execute() {
        return context.readResource(Path.of(SOURCE_FILE_LOCATION))
                .flatMap(source -> write(path, source));
    }

    private Optional<Boolean> write(final Path path, final String contents) {
        return Optional.of(context.write(path, process(contents)));
    }

    private String process(final String str) {
        return str.replaceAll("\r", "")
                .lines()
                .map(line -> "%s %s".formatted(line, seperator))
                .collect(Collectors.joining("\n"));
    }
}
