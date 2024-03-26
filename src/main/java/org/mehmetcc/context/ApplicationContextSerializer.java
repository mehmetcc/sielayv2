package org.mehmetcc.context;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class ApplicationContextSerializer {
    public void serialize(final ApplicationContext context) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File(ApplicationContextConstants.APPLICATION_CONTEXT_LOCATION), context);
            System.out.printf("ApplicationContext saved under %s%n", ApplicationContextConstants.APPLICATION_CONTEXT_LOCATION);
        } catch (Exception e) {
            System.err.println("Error while serializing ApplicationContext. Gracefully terminating");
            System.exit(0);
        }
    }
}
