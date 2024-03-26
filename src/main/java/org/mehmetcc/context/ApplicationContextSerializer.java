package org.mehmetcc.context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Optional;

public class ApplicationContextSerializer {
    public Optional<Boolean> serialize(final ApplicationContext context) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(ApplicationContextConstants.APPLICATION_CONTEXT_LOCATION), context);
            System.out.printf("ApplicationContext saved under %s%n", ApplicationContextConstants.APPLICATION_CONTEXT_LOCATION);
            return Optional.of(true);
        } catch (Exception e) {
            System.err.println("Error while serializing ApplicationContext.");
            return Optional.empty();
        }
    }
}
