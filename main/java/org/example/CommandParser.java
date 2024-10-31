package org.example;
import java.util.Scanner;
import java.io.*;

public class CommandParser {
    private TerminalCommands terminalCommands;

    public CommandParser() {
        this.terminalCommands = new TerminalCommands();
    }

    private static File currentDir = new File(System.getProperty("user.dir"));

    private void runCommand(String command) {
        try {
            // Split the command by '|' to identify and handle any piping
            String[] pipeCommands = command.split("\\|");
            Process process = null;

            for (int i = 0; i < pipeCommands.length; i++) {
                
                String[] cmdParts = pipeCommands[i].trim().split(" ");
                String cmd = cmdParts[0];

                // Check if the command is a terminal command
                if (cmd.equals("mkdir")) {
                    terminalCommands.mkdir(cmdParts);
                    return; 
                } 
                else if (cmd.equals("rmdir")) {
                    terminalCommands.rmdir(cmdParts);
                    return; 
                }
                else if (cmd.equals("rm")) {
                    terminalCommands.rm(cmdParts);
                    return; 
                }
                else if (cmd.equals("touch")) {
                    terminalCommands.touch(cmdParts);
                    return; 
                }
                else if (cmd.equals("cat")) {
                    terminalCommands.cat(cmdParts);
                    return; 
                }
                else if (cmd.equals("mv")) {
                    if (cmdParts.length < 3) {
                        System.out.println("Usage: mv <src> <dst>");
                        return;
                    }
                    terminalCommands.mv(cmdParts[1], cmdParts[2]);
                    return; 
                }
                else if (cmd.equals("ls")) {
                    terminalCommands.ls(cmdParts);
                    return; 
                } 
                else if (cmd.equals("cd")) {
                    terminalCommands.cd(cmdParts);
                    return; 
                }
                else if (cmd.equals("pwd")) {
                    terminalCommands.pwd();
                    return; 
                }

                // Initialize ProcessBuilder with command and directory
                ProcessBuilder pb = new ProcessBuilder(cmdParts);
                pb.directory(currentDir);

                // Check for redirection '>' or '>>' in the command
                if (pipeCommands[i].contains(">")) {
                    String[] redirectParts = pipeCommands[i].split(">");
                    cmdParts = redirectParts[0].trim().split(" ");
                    String outputFile = redirectParts[1].trim();

                    // Set up ProcessBuilder with the updated command parts
                    pb = new ProcessBuilder(cmdParts);
                    pb.directory(currentDir);

                    // Determine whether to append or overwrite based on '>>'
                    if (pipeCommands[i].contains(">>")) {
                        pb.redirectOutput(ProcessBuilder.Redirect.appendTo(new File(outputFile)));
                    } else {
                        pb.redirectOutput(new File(outputFile));
                    }
                }

                // Start the process
                process = pb.start();

                // If this is not the last command, set up the input stream for piping
                if (i < pipeCommands.length - 1) {
                    Process nextProcess = new ProcessBuilder(pipeCommands[i + 1].trim().split(" "))
                            .redirectInput(process.getInputStream())
                            .directory(currentDir)
                            .start();
                    process.waitFor(); // Wait for the current process to finish
                    process = nextProcess; // Move to the next process in the pipeline
                }
                else {
                    process.waitFor(); // Wait for the last command to complete
                    // If no redirection is specified, display the output in the console
                    if (!pipeCommands[i].contains(">")) {
                        displayOutput(process);
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println("Error executing command: " + e.getMessage()); // Display error message if execution fails
        }
    }
    private static void displayOutput(Process process) throws IOException {
            // BufferedReader to read the process's standard output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Print each line from the output
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
     }
    
        private static void displayHelp() {
        System.out.println("Available Commands:");
        System.out.println("pwd          : Print current directory");
        System.out.println("cd <dir>     : Change directory");
        System.out.println("ls           : List files in the current directory");
        System.out.println("mkdir <dir>  : Create new directory");
        System.out.println("rmdir <dir>  : Remove directory");
        System.out.println("touch <file> : Create empty file");
        System.out.println("mv <src> <dst>: Move or rename a file");
        System.out.println("rm <file>    : Remove a file");
        System.out.println("cat <file>   : Display file content");
        System.out.println("> <file>     : Redirect output to a file");
        System.out.println(">> <file>    : Append output to a file");
        System.out.println("|            : Pipe commands");
        System.out.println("exit         : Exit the CLI");
    }

}
