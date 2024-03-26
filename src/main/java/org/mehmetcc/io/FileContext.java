package org.mehmetcc.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class FileContext {
    private static final String FILE_CANT_BE_READ = "File can't be read.";

    private static final String FILE_CANT_BE_WRITTEN = "File can't be written.";

    private static final String FILE_CANT_BE_DELETED = "File can't be deleted.";

    private static final String FILE_DOES_NOT_EXISTS = "File does not exists";

    public Optional<String> readResource(final Path path) {
        try (final InputStreamReader isr = new InputStreamReader(getResourceStream(path.toString()));
             final BufferedReader br = new BufferedReader(isr)) {
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return Optional.of(builder.toString());
        } catch (IOException e) {
            System.err.println(FILE_CANT_BE_READ);
            return Optional.empty();
        }
    }


    public Optional<String> read(final Path path) {
        try {
            return Optional.of(Files.readString(path));
        } catch (IOException e) {
            System.err.println(FILE_CANT_BE_READ);
            return Optional.empty();
        }
    }

    public Boolean write(final Path path, final String contents) {
        try {
            Files.writeString(path, contents);
            return true;
        } catch (IOException e) {
            System.err.println(FILE_CANT_BE_WRITTEN);
            return false;
        }
    }

    public Boolean exists(final Path path) {
        if (Files.notExists(path)) {
            System.err.println(FILE_DOES_NOT_EXISTS);
            return false;
        }

        return true;
    }

    public Boolean delete(final Path path) {
        try {
            Files.delete(path);
            return true;
        } catch (IOException e) {
            System.err.println(FILE_CANT_BE_DELETED);
            return false;
        }
    }

    private InputStream getResourceStream(final String fileName) {
        InputStream ioStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(fileName);

        if (ioStream == null) {
            throw new IllegalArgumentException(fileName + " is not found");
        }
        return ioStream;
    }
}
