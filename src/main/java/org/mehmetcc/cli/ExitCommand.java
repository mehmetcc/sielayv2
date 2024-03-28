package org.mehmetcc.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "exit", description = "Exit the program.", mixinStandardHelpOptions = true)
public class ExitCommand implements Runnable {
    @Override
    public void run() {
        System.exit(0);
    }
}
