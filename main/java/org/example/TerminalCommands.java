package org.example;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class TerminalCommands {
    public Path currentDir = Paths.get(System.getProperty("user.dir"));

    public TerminalCommands() {
        currentDir = Path.of(System.getProperty("user.dir"));

    }
    /*
     * mkdir
     * make new directory or nested directory in absolute or relative path
     *
     * */
    public void mkdir(String [] args){
        if(args.length == 0){
            System.out.println("Need at least one argument!");
            return;
        }
        for (String dir : args) {
            Path newDirPath = currentDir.resolve(dir).normalize(); // Use currentDir to create path
            File newDir = newDirPath.toFile(); // Convert to File object

            if (!newDir.exists()) {
                if (newDir.mkdirs()) {
                    System.out.println("Directory created successfully: " + newDir.getAbsolutePath());
                } else {
                    System.out.println("Failed to create directory: " + newDir.getAbsolutePath());
                }
            } else {
                System.out.println("This folder name already exists!");
            }
        }
    }

    /*
     * rmdir
     * remove directory
     *
     * */

    public void rmdir(String[] args) {
        if (args.length == 0) {
            System.out.println("Need at least one argument!");
            return;
        } else if (args.length > 1) {
            System.out.println("Need a single argument!");
            return;
        }

        for (String dir : args) {
            Path dirPath = currentDir.resolve(dir).normalize(); // Use currentDir
            File dirFile = dirPath.toFile();

            if (dirFile.exists()) {
                if (dirFile.isDirectory()) {
                    deleteDirectory(dirFile);
                    System.out.println("Directory deleted successfully: " + dirFile.getAbsolutePath());
                } else {
                    System.out.println("The specified path is not a directory: " + dirFile.getAbsolutePath());
                }
            } else {
                System.out.println("Directory does not exist: " + dirFile.getAbsolutePath());
            }
        }
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file); // Recursively delete subdirectory
                }
                file.delete(); // Delete file or empty directory
            }
        }
        directory.delete(); // Finally delete the empty directory
    }
    /*
     * rm
     * delete file in the current directory
     *
     * */

    public void rm(String[] args){
        if(args.length!=1){
            System.out.println("Invalid input, Expected one argument!");
            return;
        }
        String fileDelete = args[0];
        Path filePath = currentDir.resolve(fileDelete).normalize(); // Use currentDir
        File file = filePath.toFile();

        if (file.exists() && !file.isDirectory()) {
            if (file.delete()) {
                System.out.println("This file is deleted successfully: " + file.getAbsolutePath());
            } else {
                System.out.println("Failed to delete this file: " + file.getAbsolutePath());
            }
        } else {
            System.out.println("File does not exist!");
        }
    }

    /*
     * touch
     * creat new file or update the time of existed file
     *
     * */
    public void touch(String[] args){
        if(args.length!=1){
            System.out.println("Invalid input, Expected one argument!");
            return;
        }
        String newFile = args[0];
        Path filePath = currentDir.resolve(newFile).normalize(); // Use currentDir
        File file = filePath.toFile();

        try {
            if (file.createNewFile()) {
                System.out.println("File created successfully: " + file.getAbsolutePath());
            } else {
                file.setLastModified(System.currentTimeMillis());
                System.out.println("File updated successfully: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Error while creating or updating the file!");
        }
    }


    /*
     * cat
     * handle > write to the file (overwrite)
     * handle >> edit the file (append)
     * Display file content or concatenate files
     *
     *
     * */
    public void cat(String[] args) {
        if (args.length == 0) {
            System.out.println("Invalid usage of cat command. Provide a filename or use '>' or '>>'.");
            return;
        }

        String firstArg = args[0];
        String filename = args.length > 1 ? args[1] : null;

        // Check if the first argument is a redirection operator
        if (firstArg.equals(">") || firstArg.equals(">>")) {
            if (filename == null) {
                System.out.println("Missing filename for redirection! Usage: > filename or >> filename");
                return;
            }

            // Handle write or append
            Path outputPath = currentDir.resolve(filename).normalize(); // Use currentDir
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath.toFile(), firstArg.equals(">>")))) {
                Scanner in = new Scanner(System.in);
                System.out.println("Write in the file then add @c to close the file");
                String inputString;
                while (!(inputString = in.nextLine()).equalsIgnoreCase("@c")) {
                    writer.write(inputString);
                    writer.newLine();
                }
                System.out.println("Content written to file: " + outputPath);
            } catch (IOException e) {
                System.out.println("Error while writing to the file: " + e.getMessage());
            }
        } else {
            // Handle displaying the file content
            Path filePath = currentDir.resolve(firstArg).normalize(); // Use currentDir
            File file = filePath.toFile();

            if (file.exists() && file.isFile()) {
                try {
                    Files.lines(file.toPath()).forEach(System.out::println);
                } catch (IOException e) {
                    System.out.println("Error reading file: " + file.getAbsolutePath());
                }
            } else {
                System.out.println("File does not exist: " + file.getAbsolutePath());
            }
        }
    }
    /*
     * mv
     * rename file directories
     * move files from one location to another within a file system.
     *
     * */

    public void mv(String source,String destination) {
        Path sourcePath = currentDir.resolve(source).normalize(); // Use currentDir
        Path destinationPath = currentDir.resolve(destination).normalize(); // Use currentDir
        File sourceFile = sourcePath.toFile();

        if (!sourceFile.exists()) {
            System.out.println("Source file does not exist: " + sourceFile.getAbsolutePath());
            return;
        }

        // If the destination is a directory, append the source file's name
        if (Files.isDirectory(destinationPath)) {
            destinationPath = destinationPath.resolve(sourceFile.getName());
        }

        try {
            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Moved/Renamed the file successfully: " + sourceFile.getAbsolutePath() + " to " + destinationPath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("Failed to move/rename: " + sourceFile.getAbsolutePath());
        }

    }
    /*
     * ls() lists contents (file & dires) of current directory
     *
     *
     *
     * */


    public void ls(String[] args) {
        File[] contents = currentDir.toFile().listFiles();
        boolean showHidden = false;
        boolean reverseOrder = false;
        boolean redirectOutput = false;
        boolean appendOutput = false;
        String outputFile = null;

        // Parse arguments
        int i = 0;
        while (i < args.length) {
            switch (args[i]) {
                case "-a":
                    showHidden = true;
                    break;
                case "-r":
                    reverseOrder = true;
                    break;
                case ">":
                    redirectOutput = true;
                    if (i + 1 < args.length) {
                        outputFile = args[i + 1];
                    } else {
                    System.out.println("ls: missing file name after '>'");
                    return;
                    }
                    i++; // Skip next argument (output file name)
                    break;
                case ">>":
                    appendOutput = true;
                    if (i + 1 < args.length) {
                        outputFile = args[i + 1];
                    } else {
                        System.out.println("ls: missing file name after '>>'");
                        return;
                    }
                    i++; // Skip next argument (output file name)
                    break;
                default:
                    System.out.println("ls: invalid argument or combination");
                    return;
            }
            i++;
        }

        // Gather the output to display or redirect
        StringBuilder output = new StringBuilder();
        if (contents != null) {
            // Sort in reverse order if needed
            if (reverseOrder) {
                Arrays.sort(contents, Collections.reverseOrder());
            } else {
                Arrays.sort(contents);
            }

            for (File file : contents) {
                // Skip hidden files unless -a is specified
                if (!showHidden && file.isHidden()) {
                    continue;
                }
                output.append(file.getName()).append("\n");
            }
        }

        // Handle output redirection if specified
        if (redirectOutput || appendOutput) {
            try (FileWriter writer = new FileWriter(outputFile, appendOutput)) {
                writer.write(output.toString());
            } catch (IOException e) {
                System.out.println("ls: error writing to file: " + e.getMessage());
            }
        } else {
            // Print output to console
            System.out.print(output.toString());
        }
    }
    /*
     * pwd print current path
     *
     * */

    public void pwd(){

        System.out.println(currentDir.normalize());// remove  redundant parts from path
    }

    /*
     * cd change the current directory
     *
     *
     *
     * */

    public void cd(String[] args) {
        if (args.length > 1) { // if args more than 1
            System.out.println("cd: too many arguments");
            return;
        }

        // If no arguments are passed, go to the home directory
        if (args.length == 0) {
            Path homeDir = Path.of(System.getProperty("user.home"));
            if (Files.isDirectory(homeDir)) {
                currentDir = homeDir;
                System.out.println("Changed directory to home: " + currentDir);
            } else {
                System.out.println("cd: cannot change to home directory: No such directory");
            }
            return;
        }

        // If one argument is passed
        String dir = args[0];
        try {
            Path newDir = currentDir.resolve(dir).normalize();
            if (Files.isDirectory(newDir)) {
                currentDir = newDir; // Update the class-level currentDir
                System.out.println("Changed directory to: " + currentDir);
            } else {
                System.out.println("cd: cannot change directory to '" + dir + "': No such directory");
            }
        } catch (InvalidPathException e) {
            System.out.println("cd: invalid path '" + dir + "'");
        }
    }

    /*
    * Help command
    * clear command
    *
    * */

    public void help() {
        System.out.println("Available Commands:");
        System.out.println("pwd             : Print the path of the current directory");
        System.out.println("cd <dir>        : Change directory");
        System.out.println("ls              : List files and directory in the current directory");
        System.out.println("ls -r           : Lists files and directories in reverse order");
        System.out.println("ls -a           : Lists all files and directories, including hidden ones.");
        System.out.println("mkdir <dir>     : Create new directory");
        System.out.println("rmdir <dir>     : Remove directory");
        System.out.println("touch <file>    : Create empty file");
        System.out.println("mv <src> <dst>  : Move or rename a file");
        System.out.println("rm <file>       : Remove a file");
        System.out.println("cat <file>      : Display the file content");
        System.out.println("cat > <file>    : Overwrite output to a file");
        System.out.println("cat >> <file>   : Append output to a file");
        System.out.println("|               : Pipe commands");
        System.out.println("exit            : Exit the CLI");
        System.out.println("clear           : Clear the commands");
    }

    public void clear(){
        for(int clear=0;clear<50000;clear++){
            System.out.println();
        }
    }










}
