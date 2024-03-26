package org.mehmetcc.middleware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mehmetcc.context.ApplicationContext;
import org.mehmetcc.context.ApplicationContextDeserializer;
import org.mehmetcc.io.DatabaseContext;
import org.mehmetcc.io.FileContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DumpDatabaseTest {
    @Mock
    private FileContext fileContext;

    @Mock
    private DatabaseContext databaseContext;

    @Mock
    private ApplicationContextDeserializer deserializer;

    @Mock
    private Path path;

    private String seperator;

    private DumpDatabase dumpDatabase;

    private

    @BeforeEach
    void setup() {
        seperator = ":::";
        dumpDatabase = new DumpDatabase(path, fileContext, databaseContext, deserializer, seperator);
    }

    @Test
    void whenNoPathGivenButSuccessfullyDeserializeApplicationContext_shouldDeserializeFromApplicationContextDeserializerWriteToDatabase() {
        // Data Prep.
        dumpDatabase = new DumpDatabase(null, fileContext, databaseContext, deserializer, seperator);
        var applicationContext = new ApplicationContext("i:\nam:\nspeed:\ni:\nam:\nspeed:\ni:\nam:\nspeed:\ni:\nam:\nspeed:", seperator);
        var processed = Arrays.asList(applicationContext.contents()
                .replaceAll("\n", "")
                .split(seperator));

        // Stubbing
        when(deserializer.deserialize()).thenReturn(Optional.of(applicationContext));
        doNothing().when(databaseContext).fill(processed);

        // Interaction
        var result = dumpDatabase.execute();

        // Verification
        verify(deserializer).deserialize();
        verify(databaseContext).fill(processed);

        // Assertions
        assertThat(result).isNotEmpty().hasValue(true);
    }

    @Test
    void whenNoPathGivenAndApplicationContextCantBeFound_shouldReturnEmpty() {
        // Data Prep.
        dumpDatabase = new DumpDatabase(null, fileContext, databaseContext, deserializer, seperator);

        // Stubbing
        when(deserializer.deserialize()).thenReturn(Optional.empty());

        // Interaction
        var result = dumpDatabase.execute();

        // Verification
        verify(deserializer).deserialize();

        // Assertions
        assertThat(result).isEmpty();
    }

    @Test
    void whenValidPathGiven_shouldWriteDatabaseAndReturnTrue() {
        // Data Prep.
        var str = "i\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed";
        var processed = Arrays.asList(str
                .replaceAll("\n", "")
                .split(seperator));

        // Stubbing
        when(fileContext.exists(path)).thenReturn(true);
        when(fileContext.read(path)).thenReturn(str.describeConstable());
        doNothing().when(databaseContext).fill(processed);

        // Interaction
        var result = dumpDatabase.execute();

        // Verification
        verify(fileContext).exists(path);
        verify(fileContext).read(path);
        verify(databaseContext).fill(processed);

        // Assertions
        assertThat(result).isNotEmpty().hasValue(true);
    }

    @Test
    void whenInvalidPathGiven_shouldReturnEmpty() {
        // Stubbing
        when(fileContext.exists(path)).thenReturn(false);

        // Interaction
        var result = dumpDatabase.execute();

        // Verification
        verify(fileContext).exists(path);

        // Assertions
        assertThat(result).isEmpty();

    }

    @Test
    void whenValidPathGivenFileCantBeRead_shouldReturnEmpty() {
        // Stubbing
        when(fileContext.exists(path)).thenReturn(true);
        when(fileContext.read(path)).thenReturn(Optional.empty());

        // Interaction
        var result = dumpDatabase.execute();

        // Verification
        verify(fileContext).exists(path);
        verify(fileContext).read(path);

        // Assertions
        assertThat(result).isEmpty();
    }
}