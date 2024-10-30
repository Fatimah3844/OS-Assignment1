package org.example;
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

        while (true) {
            System.out.print("> "); // Prompt for input
            input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                break; // Exit the loop if user types 'exit'
            }

            //split user input to array based on space
            String[] tokens = input.split(" ");

            //command is the first index
            String command = tokens[0];
            //rest of the array
            String[] args = new String[tokens.length - 1];
            //copy from token array to args array
            System.arraycopy(tokens, 1, args, 0, tokens.length - 1);

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
                default:
                    System.out.println("Unknown command: " + command);
                    break;
            }
        }
        scanner.close();
    }
}