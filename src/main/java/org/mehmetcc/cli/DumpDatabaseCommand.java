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
    @CommandLine.Parameters(
            description = "Source file.",
            arity = "0..1")
    private Path path;

    @CommandLine.Option(names = {"-S", "--seperator"},
            description = "Seperator to split the file.",
            defaultValue = MiddlewareConstants.DEFAULT_SEPERATOR,
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    private String seperator;

    @Override
    public void run() {
        new DumpDatabase(path, new FileContext(), new DatabaseContext(), new ApplicationContextDeserializer(), seperator).execute();
    }
}
