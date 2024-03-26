package org.mehmetcc.cli;

import org.mehmetcc.command.Shred;
import org.mehmetcc.io.FileContext;
import picocli.CommandLine;

import java.nio.file.Path;

@CommandLine.Command(name = "shred", description = "Overwrite file contents 3 times before deleting it.", mixinStandardHelpOptions = true)
public class ShredCommand implements Runnable {
    //@CommandLine.Option(required = true, names = {"-P", "--path"}, description = "Path to file to be overwritten and deleted.")
    @CommandLine.Parameters(index = "0")
    private Path path;


    @Override
    public void run() {
        new Shred(path, new FileContext()).execute();
    }
}
