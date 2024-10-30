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
    //_________________________________________________________
     // Test for ls with no arguments
    @Test
    void testLsShowFiles() throws IOException {
        File file1 = new File("file1.txt");
        File file2 = new File("file2.txt");
        file1.createNewFile();
        file2.createNewFile();

        terminalCommands.ls(new String[0]);  // Test with no args

        // Clean up
        file1.delete();
        file2.delete();
    }

    // Test ls with -a argument (show hidden files)
    @Test
    void testLsShowHiddenFiles() throws IOException {
        File file1 = new File(".hiddenFile.txt");
        file1.createNewFile();

        terminalCommands.ls(new String[]{"-a"});  // Expect to see hidden files

        // Clean up
        file1.delete();
    }

    // Test ls with -r argument (reverse order)
    @Test
    void testLsReverseOrder() throws IOException {
        File file1 = new File("fileA.txt");
        File file2 = new File("fileB.txt");
        file1.createNewFile();
        file2.createNewFile();

        terminalCommands.ls(new String[]{"-r"});  // Expect reverse order

        // Clean up
        file1.delete();
        file2.delete();
    }

    // Test ls with both -a and -r arguments
    @Test
    void testLsShowHiddenAndReverseOrder() throws IOException {
        File file1 = new File(".hiddenFile.txt");
        File file2 = new File("fileB.txt");
        file1.createNewFile();
        file2.createNewFile();

        terminalCommands.ls(new String[]{"-a", "-r"});  // Expect hidden and reverse order

        // Clean up
        file1.delete();
        file2.delete();
    }

    // Test for pwd (prints current directory)
    @Test
    void testPwd() {
        terminalCommands.pwd();
    }

    // Test cd to home directory
    @Test
    void testCdToHome() {
        terminalCommands.cd(new String[0]);
        assertEquals(System.getProperty("user.home"), terminalCommands.currentDir.toString());
    }

    // Test cd to an existing directory
    @Test
    void testCdToDirectory() {
        File dir = new File("testDir");
        dir.mkdir();
        terminalCommands.cd(new String[]{"testDir"});
        assertEquals(dir.getAbsolutePath(), terminalCommands.currentDir.toString());

        // Clean up
        dir.delete();
    }

    // Test cd to a non-existent directory
    @Test
    void testCdToNonExistentDirectory() {
        terminalCommands.cd(new String[]{"nonExistentDir"});
        // Expected output: should display an error message and not change the directory
        assertNotEquals("nonExistentDir", terminalCommands.currentDir.toString());
    }

    // Test cd with too many arguments
    @Test
    void testCdTooManyArguments() {
        terminalCommands.cd(new String[]{"dir1", "dir2"});
        // Expected output: should display an error message for too many arguments
    }


    
}
