package org.mehmetcc.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mehmetcc.io.FileContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShredTest {
    @Mock
    private Path path;

    @Mock
    private FileContext context;

    @InjectMocks
    private Shred shred;

    private Path sourceFilePath;

    private String sourceFileContents;

    @BeforeEach
    void setup() {
        sourceFilePath = Path.of("nasreddin_hodja.txt");
        sourceFileContents = "Ben bir content'im";
    }

    @Test
    void whenContextIsAbleToReadSourceFileAndFileToBeShredded_thenVerifyOverwritingActionHappenedThreeTimesAndFileIsDeleted() {
        // Stubbing
        when(context.exists(any())).thenReturn(true);
        when(context.readResource(any())).thenReturn(sourceFileContents.describeConstable());
        when(context.exists(path)).thenReturn(true);
        when(context.write(path, sourceFileContents)).thenReturn(true);
        when(context.delete(path)).thenReturn(true);

        // Interaction
        var result = shred.execute();

        // Verification
        verify(context).exists(any());
        verify(context).readResource(any());
        verify(context, atLeast(3)).write(path, sourceFileContents);
        verify(context).delete(path);

        // Assertions
        assertThat(result).isPresent()
                .isNotEmpty()
                .hasValue(true);
    }

    @Test
    void whenSourceFileCantBeRead_thenReturnEmptyOptional() {
        // Stubbing
        when(context.readResource(sourceFilePath)).thenReturn(Optional.empty());

        // Interaction
        var result = shred.execute();

        // Verification
        verify(context).readResource(sourceFilePath);

        // Assertions
        assertTrue(result.isEmpty());
    }

    @Test
    void whenTargetFileCantBeValidated_thenReturnEmptyOptional() {
        // Stubbing
        when(context.exists(any())).thenReturn(true);
        when(context.readResource(any())).thenReturn(sourceFileContents.describeConstable());
        when(context.exists(path)).thenReturn(false);

        // Interaction
        var result = shred.execute();

        // Verification
        verify(context, atMost(2)).exists(any());
        verify(context).readResource(any());

        // Assertions
        assertTrue(result.isEmpty());
    }

    @Test
    void whenTargetFileCantBeWritten_thenReturnEmptyOptional() {
        // Stubbing
        when(context.exists(any())).thenReturn(true);
        when(context.readResource(any())).thenReturn(sourceFileContents.describeConstable());
        when(context.exists(path)).thenReturn(true);
        when(context.write(path, sourceFileContents)).thenReturn(false);

        // Interaction
        var result = shred.execute();

        // Verification
        verify(context).exists(any());
        verify(context).readResource(any());
        verify(context, atMost(1)).write(path, sourceFileContents);

        // Assertions
        assertTrue(result.isEmpty());
    }

    @Test
    void whenTargetFileCantBeDeleted_thenReturnFalse() {
        // Stubbing
        when(context.exists(any())).thenReturn(true);
        when(context.readResource(any())).thenReturn(sourceFileContents.describeConstable());
        when(context.exists(path)).thenReturn(true);
        when(context.write(path, sourceFileContents)).thenReturn(true);
        when(context.delete(path)).thenReturn(false);

        // Interaction
        var result = shred.execute();

        // Verification
        verify(context).exists(any());
        verify(context).readResource(any());
        verify(context, atLeast(3)).write(path, sourceFileContents);
        verify(context).delete(path);

        // Assertions
        assertThat(result).isPresent()
                .isNotEmpty()
                .hasValue(false);
    }
}