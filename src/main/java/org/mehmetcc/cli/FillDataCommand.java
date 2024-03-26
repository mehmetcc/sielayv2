package org.mehmetcc.cli;

import org.mehmetcc.context.ApplicationContextSerializer;
import org.mehmetcc.io.FileContext;
import org.mehmetcc.middleware.FillData;
import org.mehmetcc.middleware.MiddlewareConstants;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "fill-data", description = "Fill given path with predetemined data.", mixinStandardHelpOptions = true)
public class FillDataCommand implements Runnable {
    @CommandLine.Parameters(index = "0")
    private Path path;

    @CommandLine.Option(names = {"-S", "--seperator"}, description = "Seperator to split the file.", defaultValue = MiddlewareConstants.DEFAULT_SEPERATOR)
    private String seperator;

    @Override
    public void run() {
        new FillData(path, new FileContext(), new ApplicationContextSerializer(), seperator).execute();
    }
}
