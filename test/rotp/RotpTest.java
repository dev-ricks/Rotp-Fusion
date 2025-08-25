package rotp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RotpTest {

    @BeforeEach
    void resetStaticFields() {
        Rotp.actualAlloc = -1;
        Rotp.exeFileName = "test.exe";
        Rotp.jarFileName = "test.jar";
    }

    @Test
    void restart_UsesExeFile_IfExists() throws IOException {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File exeFile = new File(tempDir, "test.exe");
        if (!exeFile.exists()) {
            assertTrue(exeFile.createNewFile());
        }
        exeFile.deleteOnExit();

        Rotp.startupDir = tempDir.getAbsolutePath();

        String[] command = Rotp.getCommandStringTokens(tempDir);
        assertEquals(exeFile.getAbsolutePath(), command[0]);
    }

    @Test
    void restart_ExeFileCommand_ShouldIncludeExeFilePath() throws Exception {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        String tempDirPath = tempDir.getAbsolutePath() + File.separator;
        File exeFile = new File(tempDir, "test.exe");
        if (!exeFile.exists()) {
            assertTrue(exeFile.createNewFile());
        }
        exeFile.deleteOnExit();

        Rotp.startupDir = tempDir.getAbsolutePath();
        Rotp.exeFileName = "test.exe";
        Rotp.jarFileName = "test.jar";
        Rotp.actualAlloc = -1;

        // Mock Runtime to intercept exec call
        Runtime mockRuntime = mock(Runtime.class);
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class, CALLS_REAL_METHODS)) {
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);

            Rotp.restart();
            verify(mockRuntime).exec(
                    argThat((String[] cmd) -> cmd.length > 0 && exeFile.getAbsolutePath().equals(cmd[0])), any(),
                    any());
        }
    }

    @Test
    void restart_FallsBackToJar_IfExeDoesNotExist() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File exeFile = new File(tempDir, "test.exe");
        if (exeFile.exists()) {
            assertTrue(exeFile.delete());
        }

        Rotp.startupDir = tempDir.getAbsolutePath();

        String[] command = Rotp.getCommandStringTokens(tempDir);
        assertEquals("java", command[0]);
        assertEquals("-jar", command[1]);
        assertEquals("test.jar", command[2]);
    }

    @Test
    void restart_UsesMemoryAllocation_IfSpecified() {
        Rotp.actualAlloc = 512;
        File tempDir = new File(System.getProperty("java.io.tmpdir"));
        File exeFile = new File(tempDir, "test.exe");
        if (exeFile.exists()) {
            assertTrue(exeFile.delete());
        }

        Rotp.startupDir = tempDir.getAbsolutePath();

        String[] command = Rotp.getCommandStringTokens(tempDir);
        assertEquals("java", command[0]);
        assertEquals("-Xmx512m", command[1]);
        assertEquals("-jar", command[2]);
        assertEquals("test.jar", command[3]);
    }

    @Test
    void restart_HandlesIOException_ThenPrintsStacktrace() throws Exception {
        // Redirect System.err to capture output
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));
        // Mock Runtime.getRuntime().exec to throw IOException
        Runtime mockRuntime = mock(Runtime.class);
        when(mockRuntime.exec(any(String[].class), isNull(), any(File.class))).thenThrow(
                new IOException("Simulated IO error"));
        try (MockedStatic<Runtime> runtimeMock = mockStatic(Runtime.class, CALLS_REAL_METHODS)) {
            runtimeMock.when(Runtime::getRuntime).thenReturn(mockRuntime);
            // Call the method under test
            Rotp.restart();
        } finally {
            System.setErr(originalErr);
        }
        // Assert that the error message and stack trace were printed
        String errOutput = errContent.toString();
        assertTrue(errOutput.contains("Error attempting restart"));
        assertTrue(errOutput.contains("Simulated IO error"));
        assertTrue(errOutput.contains("java.io.IOException"));
    }
}
