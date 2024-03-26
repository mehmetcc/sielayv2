package org.mehmetcc;

import org.mehmetcc.cli.Root;
import picocli.CommandLine;

public class Main {

  public static void main(String[] args) {
    new CommandLine(Root.class).execute(args);
  }
}