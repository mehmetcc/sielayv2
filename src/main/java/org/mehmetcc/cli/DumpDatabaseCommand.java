package org.mehmetcc.cli;

import org.mehmetcc.context.ApplicationContextDeserializer;
import org.mehmetcc.io.DatabaseContext;
import org.mehmetcc.io.FileContext;
import org.mehmetcc.middleware.DumpDatabase;
import org.mehmetcc.middleware.MiddlewareConstants;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "dump-db", description = "Dump binary data to an sqlite database.", mixinStandardHelpOptions = true)
public class DumpDatabaseCommand implements Runnable {
    @CommandLine.Parameters(index = "0", defaultValue = CommandLine.Parameters.NULL_VALUE)
    private Path path;

    @CommandLine.Option(names = {"-S", "--seperator"}, description = "Seperator to split the file.", defaultValue = MiddlewareConstants.DEFAULT_SEPERATOR)
    private String seperator;

    @Override
    public void run() {
        new DumpDatabase(path, new FileContext(), new DatabaseContext(), new ApplicationContextDeserializer(), seperator).execute();
    }
}
