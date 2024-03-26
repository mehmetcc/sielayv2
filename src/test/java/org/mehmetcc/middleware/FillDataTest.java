package org.mehmetcc.middleware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mehmetcc.context.ApplicationContextSerializer;
import org.mehmetcc.io.FileContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FillDataTest {
    @Mock
    private Path path;

    @Mock
    private FileContext context;

    @Mock
    private ApplicationContextSerializer serializer;

    private String seperator;

    private FillData fillData;

    private Path sourcePath;

    @BeforeEach
    void setup() {
        seperator = ":::";
        sourcePath = Path.of("queen_of_hearts.txt");
        fillData = new FillData(path, context, serializer, seperator);
    }

    @Test
    void whenContextIsAbleToReadFromResources_shouldWriteToNewFile() {
        // Data Prep.
        var str = "i\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed";

        var expected = str.replaceAll("\r", "")
                .lines()
                .map(line -> "%s %s".formatted(line, ":::"))
                .collect(Collectors.joining("\n"));


        // Stubbing
        when(context.readResource(sourcePath)).thenReturn(str.describeConstable());
        when(context.write(path, expected)).thenReturn(true);
        when(serializer.serialize(any())).thenReturn(Optional.of(true));

        // Interaction
        var result = fillData.execute();

        // Verification
        verify(context).readResource(sourcePath);
        verify(context).write(path, expected);
        verify(serializer).serialize(any());

        // Assertions
        assertThat(result).isNotEmpty().hasValue(true);
    }

    @Test
    void whenSerializationFails_shouldReturnEmpty() {
        var str = "i\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed";

        var expected = str.replaceAll("\r", "")
                .lines()
                .map(line -> "%s %s".formatted(line, ":::"))
                .collect(Collectors.joining("\n"));


        // Stubbing
        when(context.readResource(sourcePath)).thenReturn(str.describeConstable());
        when(context.write(path, expected)).thenReturn(true);
        when(serializer.serialize(any())).thenReturn(Optional.empty());

        // Interaction
        var result = fillData.execute();

        // Verification
        verify(context).readResource(sourcePath);
        verify(context).write(path, expected);
        verify(serializer).serialize(any());

        // Assertions
        assertThat(result).isEmpty();
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
        var str = "i\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed\ni\nam\nspeed";
        var processed = str.replaceAll("\r", "")
                .lines()
                .map(line -> "%s %s".formatted(line, ":::"))
                .collect(Collectors.joining("\n"));

        // Stubbing
        when(context.readResource(sourcePath)).thenReturn(str.describeConstable());
        when(context.write(path, processed)).thenReturn(false);

        // Interaction
        var result = fillData.execute();

        // Verification
        verify(context).readResource(sourcePath);
        verify(context).write(path, processed);

        // Assertions
        assertThat(result).isEmpty();
    }

    @Test
    void whenInputIsLessThen10Lines_shouldReturnEmptyOptional() {
        // Data Prep.
        var str = "i\nam\nspeed";

        // Stubbing
        when(context.readResource(sourcePath)).thenReturn(str.describeConstable());

        // Interaction
        var result = fillData.execute();

        // Verification
        verify(context).readResource(sourcePath);

        // Assertions
        assertThat(result).isEmpty();
    }
}