package org.example;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class CommandParser {
    private TerminalCommands terminalCommands;

    public CommandParser() {
        this.terminalCommands = new TerminalCommands();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Welcome to the CLI. Type 'exit' to quit.");
        terminalCommands.help();

        while (true) {
            System.out.print("> "); // Prompt for input
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break; // Exit the loop if user types 'exit'
            }

            // Handle output redirection and piping
            String[] tokens = input.split(" ");
            String command = tokens[0];
            String[] args = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, args, 0, tokens.length - 1);

            if (input.contains("|")) {
                String[] pipeCommands = input.split("\\|");
                for (String pipeCommand : pipeCommands) {
                    // Process each command in the pipe
                    processCommand(pipeCommand.trim());
                }
            } else {
                processCommand(input);
            }
        }
        scanner.close();
    }

    private void processCommand(String commandLine) {
        String[] tokens = commandLine.split(" ");
        String command = tokens[0];
        String[] args = new String[tokens.length - 1];
        System.arraycopy(tokens, 1, args, 0, tokens.length - 1);

        if (args.length > 0 && (args[args.length - 1].equals(">") || args[args.length - 1].equals(">>"))) {
            String redirectFile = args[args.length - 2];
            if (args[args.length - 1].equals(">")) {
                // Redirect output to a file
                redirectOutput(redirectFile, false);
            } else {
                // Append output to a file
                redirectOutput(redirectFile, true);
            }
            // Remove the redirection part from args
            args = Arrays.copyOf(args, args.length - 2);
        }

        switch (command.toLowerCase()) {
            case "mkdir":
                terminalCommands.mkdir(args);
                break;
            case "rmdir":
                terminalCommands.rmdir(args);
                break;
            case "rm":
                terminalCommands.rm(args);
                break;
            case "touch":
                terminalCommands.touch(args);
                break;
            case "cat":
                terminalCommands.cat(args);
                break;
            case "mv":
                if (args.length == 2) {
                    terminalCommands.mv(args[0], args[1]);
                } else {
                    System.out.println("Invalid usage of mv command.");
                }
                break;
            case "ls":
                terminalCommands.ls(args);
                break;
            case "cd":
                terminalCommands.cd(args);
                break;
            case "pwd":
                terminalCommands.pwd();
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }

    private void redirectOutput(String fileName, boolean append) {
        try {
            PrintStream out = new PrintStream(new FileOutputStream(fileName, append));
            System.setOut(out);
            System.setErr(out);
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
