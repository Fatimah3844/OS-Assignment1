import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    private String commandName;
    private String[] commandArgs;

    // remove extra space and split each command parts

  
    public boolean parse(String command) {
        command = command.trim();
        if (command.isEmpty()) {
            return false;
        }

        String[] commandParts = command.split("\\s+");

        // Use ArrayList to store non-empty arguments
        ArrayList<String> argList = new ArrayList<>();
        for (int i = 1; i < commandParts.length; i++) {
            if (!commandParts[i].isEmpty()) {
                argList.add(commandParts[i]);
            }
        }

        commandName = commandParts[0];
        commandArgs = argList.toArray(new String[0]);
        return true;
    }


    public String getCommandName() {
        return commandName;
    }
// get copy of argument in string
  
    public String[] getArgs() {
        return Arrays.copyOf(commandArgs, commandArgs.length);
    }

    // append command parts and return it as string
    public String getNormalizedCommand() {
        StringBuilder fullCommand = new StringBuilder(commandName);
        for (String arg : commandArgs) {
            fullCommand.append(" ").append(arg);
        }
        return fullCommand.toString();
    }
}
