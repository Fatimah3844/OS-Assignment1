package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TerminalCommandsTest {

    private TerminalCommands terminalCommands;
    private final String testDirectory = "testDir";
    private final String testFile = "testFile.txt";

    @BeforeEach
    public void setUp() {
        terminalCommands = new TerminalCommands();
        new File(testDirectory).delete();
        new File(testFile).delete();
    }

    @Test
    void testMkdir() {
        terminalCommands.mkdir(new String[]{testDirectory});
        assertTrue(new File(testDirectory).exists(), "Directory should be created.");
    }

    @Test
    void testRmdir() {
        terminalCommands.mkdir(new String[]{testDirectory});
        terminalCommands.rmdir(new String[]{testDirectory});
        assertFalse(new File(testDirectory).exists(), "Directory should be removed.");
    }

    @Test
    void testRm() throws IOException {
        new File(testFile).createNewFile();
        terminalCommands.rm(new String[]{testFile});
        assertFalse(new File(testFile).exists(), "File should be deleted.");
    }

    @Test
    void testTouch() throws IOException {
        terminalCommands.touch(new String[]{testFile});
        assertTrue(new File(testFile).exists(), "File should be created.");
    }

    @Test
    void testCatShowFileContent() throws IOException {
        try (BufferedWriter br = new BufferedWriter(new FileWriter(testFile))) {
            br.write("Test cat command!");
            br.newLine();
            br.write("This is a test file.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        terminalCommands.cat(new String[]{testFile});

        String output = outputStream.toString().trim();
        assertTrue(output.contains("Test cat command!"), "File content should be displayed.");
        assertTrue(output.contains("This is a test file."), "File content should be displayed.");
    }

    @Test
    public void testCatOverwrite() throws IOException {
        new File(testFile).createNewFile();
        String simulatedInput = "The new content in the file.\n@c";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        terminalCommands.cat(new String[]{">", testFile});

        List<String> lines = Files.readAllLines(new File(testFile).toPath());
        assertEquals(1, lines.size(), "File should contain one line.");
        assertEquals("The new content in the file.", lines.get(0), "Content should match the input.");
    }

    @Test
    public void testCatAppendFile() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testFile))) {
            writer.write("Initial content.\n");
        }

        String simulatedInput = "Appended content.\n@c";
        ByteArrayInputStream in = new ByteArrayInputStream(simulatedInput.getBytes());
        System.setIn(in);

        terminalCommands.cat(new String[]{">>", testFile});

        List<String> lines = Files.readAllLines(new File(testFile).toPath());
        assertEquals(2, lines.size(), "File should contain two lines.");
        assertEquals("Initial content.", lines.get(0), "First line should match the initial content.");
        assertEquals("Appended content.", lines.get(1), "Second line should match the appended input.");
    }

    @Test
    void testMv() throws IOException {
        File sourceFile = new File(testFile);
        sourceFile.createNewFile();
        String destinationPath = "movedFile.txt";

        terminalCommands.mv(sourceFile.getAbsolutePath(), destinationPath);

        assertFalse(sourceFile.exists(), "Source file should be deleted.");
        assertTrue(new File(destinationPath).exists(), "Destination file should exist.");
    }

    @Test
    void testMvRename() throws IOException {
        File sourceFile = new File(testFile);
        sourceFile.createNewFile();
        String renamedFile = "renamedFile.txt";

        terminalCommands.mv(sourceFile.getAbsolutePath(), renamedFile);

        assertFalse(sourceFile.exists(), "Source file should be deleted.");
        assertTrue(new File(renamedFile).exists(), "Renamed file should exist.");
    }

    @AfterEach
    public void tearDown() {
        new File(testDirectory).delete();
        new File(testFile).delete();
        new File("movedFile.txt").delete();
        new File("renamedFile.txt").delete();
    }
}
