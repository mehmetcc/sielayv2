package org.mehmetcc.middleware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mehmetcc.io.FileContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FillDataTest {
    @Mock
    private Path path;

    @Mock
    private FileContext context;

    private String seperator;

    private FillData fillData;

    private Path sourcePath;

    @BeforeEach
    void setup() {
        seperator = ":::";
        sourcePath = Path.of("queen_of_hearts.txt");
        fillData = new FillData(path, context, seperator);
    }

    @Test
    void whenContextIsAbleToReadFromResources_shouldWriteToNewFile() {
        // Data Prep.
        var str = "i\nam\nspeed";
        var expected = str.replaceAll("\r", "")
                .lines()
                .map(line -> "%s %s".formatted(line, ":::"))
                .collect(Collectors.joining("\n"));

        // Stubbing
        when(context.readResource(sourcePath)).thenReturn(str.describeConstable());
        when(context.write(path, expected)).thenReturn(true);

        // Interaction
        var result = fillData.execute();

        // Verification
        verify(context).readResource(sourcePath);
        verify(context).write(path, expected);

        // Assertions
        assertThat(result).isNotEmpty().hasValue(true);
    }

    @Test
    void whenResourceCantBeRead_shouldReturnEmpty() {
        // Stubbing
        when(context.readResource(sourcePath)).thenReturn(Optional.empty());

        // Interaction
        var result = fillData.execute();

        // Verification
        verify(context).readResource(sourcePath);

        // Assertions
        assertThat(result).isEmpty();
    }

    @Test
    void whenWriteOperationFails_shouldReturnFalse() {
        // Data Prep.
        var str = "heheeee";
        var processed = "heheeee :::";

        // Stubbing
        when(context.readResource(sourcePath)).thenReturn(str.describeConstable());
        when(context.write(path, processed)).thenReturn(false);

        // Interaction
        var result = fillData.execute();

        // Verification
        verify(context).readResource(sourcePath);
        verify(context).write(path, processed);

        // Assertions
        assertThat(result).isNotEmpty().hasValue(false);
    }
}