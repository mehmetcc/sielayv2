package org.mehmetcc.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "sielay",
        version = "2.0",
        subcommands = {ShredCommand.class},
        mixinStandardHelpOptions = true)
public class Root implements Runnable {
    @Override
    public void run() {

    }
}
