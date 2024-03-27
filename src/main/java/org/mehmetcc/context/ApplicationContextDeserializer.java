package org.mehmetcc.context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ApplicationContextDeserializer {
    private static final String DESERIALIZATION_FAILURE = "Error while deserializing ApplicationContext.";

    private static final String NO_APPLICATION_CONTEXT_EXISTS = "No ApplicationContext exists. You should run fill-data first.";

    public Optional<ApplicationContext> deserialize() {
        var path = Path.of(ApplicationContextConstants.APPLICATION_CONTEXT_LOCATION);
        return check(path).flatMap(this::read);
    }

    private Optional<Path> check(final Path path) {
        if (Files.exists(path)) return Optional.of(path);
        else {
            System.err.println(NO_APPLICATION_CONTEXT_EXISTS);
            return Optional.empty();
        }
    }

    private Optional<ApplicationContext> read(final Path path) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ApplicationContext context = mapper.readValue(path.toFile(), ApplicationContext.class);
            return Optional.of(context);
        } catch (IOException e) {
            System.err.println(DESERIALIZATION_FAILURE);
            return Optional.empty();
        }
    }
}

