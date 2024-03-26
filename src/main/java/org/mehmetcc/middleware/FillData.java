package org.mehmetcc.middleware;

import org.mehmetcc.context.ApplicationContext;
import org.mehmetcc.context.ApplicationContextSerializer;
import org.mehmetcc.io.FileContext;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

public class FillData {
    private static final String SOURCE_FILE_LOCATION = "queen_of_hearts.txt";

    private final Path path;

    private final FileContext context;

    private final ApplicationContextSerializer serializer;

    private final String seperator;

    public FillData(final Path path, final FileContext context, final ApplicationContextSerializer serializer, final String seperator) {
        this.path = path;
        this.context = context;
        this.serializer = serializer;
        this.seperator = seperator;
    }

    public Optional<Boolean> execute() {
        return context.readResource(Path.of(SOURCE_FILE_LOCATION))
                .flatMap(this::validate)
                .flatMap(source -> write(path, source))
                .flatMap(this::serialize);
    }

    private Optional<String> validate(final String contents) {
        if (contents.lines().count() < 10) {
            System.err.println("Input should be at least 10 lines");
            return Optional.empty();
        }

        return Optional.of(contents);
    }

    private Optional<Boolean> serialize(final String contents) {
        serializer.serialize(new ApplicationContext(contents, seperator));
        return Optional.of(true);
    }

    private Optional<String> write(final Path path, final String contents) {
        var processed = process(contents);
        if (context.write(path, processed))
            return Optional.of(processed);
        return Optional.empty();
    }

    private String process(final String str) {
        return str.replaceAll("\r", "")
                .lines()
                .map(line -> "%s %s".formatted(line, seperator))
                .collect(Collectors.joining("\n"));
    }
}
