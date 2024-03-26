package org.mehmetcc.context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ApplicationContextDeserializer {
    private static final String DESERIALIZATION_FAILURE = "Error while deserializing ApplicationContext.";

    private static final String NO_APPLICATION_STATE_EXISTS = "No ApplicationState exists.";

    public ApplicationContext deserialize() {
        var path = Path.of(ApplicationContextConstants.APPLICATION_CONTEXT_LOCATION);
        var result = check(path).flatMap(this::deserialize);

        if (result.isPresent()) return result.get();
        else {
            System.exit(0);
            return null;
        }
    }

    private Optional<Path> check(final Path path) {
        if (Files.notExists(path)) return Optional.of(path);
        else {
            System.err.println(NO_APPLICATION_STATE_EXISTS);
            return Optional.empty();
        }
    }

    private Optional<ApplicationContext> deserialize(final Path path) {
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

