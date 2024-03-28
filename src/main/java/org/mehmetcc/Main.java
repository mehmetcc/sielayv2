package org.mehmetcc;

import org.mehmetcc.cli.RootCommand;
import picocli.CommandLine;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        var cli = new CommandLine(RootCommand.class);
        while (true) {
            var tmp = scanner.nextLine().split(" ");
            cli.execute(tmp);
        }
    }
}