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
     Path currentDir;

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
        else{
            for(String dir : args){
                File file = new File(dir);//directory to be created
                if(!file.isAbsolute())//if path is relative
                {
                    Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();//retrieve current working directory
                    Path relativePath=Paths.get(dir);//create path from relative directory name
                    Path newPath=currentDir.resolve(relativePath);
                    file=new File(newPath.toString());
                }
                if(!file.exists())
                {
                    if (file.mkdirs()) {
                        System.out.println("Directory created sucessfully: " + file.getAbsolutePath());
                    } else {
                        System.out.println("Failed to create directory: " + file.getAbsolutePath());
                    }
                }
                else{
                    System.out.println("This folder name already exists!");
                }
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
            File file = new File(dir); // Directory to be removed
            if (!file.isAbsolute()) { // If path is relative
                Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath(); // Retrieve current working directory
                Path relativePath = Paths.get(dir); // Create path from relative directory name
                Path newPath = currentDir.resolve(relativePath);
                file = new File(newPath.toString());
            }

            if (file.exists()) {
                if (file.isDirectory()) {
                    // delete files and subdirectories
                    deleteDirectory(file);
                    System.out.println("Directory deleted successfully: " + file.getAbsolutePath());
                } else {
                    System.out.println("The specified path is not a directory: " + file.getAbsolutePath());
                }
            } else { // Folder does not exist
                System.out.println("Directory does not exist: " + file.getAbsolutePath());
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
        else{
            String fileDelete=args[0];
            File file=new File(fileDelete);
            //get the absolute path for the file
            if(!file.isAbsolute()){
                Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                Path relativePath = Paths.get(fileDelete);
                Path newPath = currentDir.resolve(relativePath);
                file = new File(newPath.toString());
            }
            if(file.exists()&&!file.isDirectory()){
                if(file.delete()){
                    System.out.println("This File is deleted successfully: "+file.getAbsolutePath());

                }
                else{
                    System.out.println("Failed to delete this file: "+file.getAbsolutePath());
                }
            }
            else{
                System.out.println("File does not exist!");
            }
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
        else {
            for (String newFile : args) {
                File file = new File(newFile);
                //get the absolute path for the file
                if (!file.isAbsolute()) {
                    Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                    Path relativePath = Paths.get(newFile);
                    Path newPath = currentDir.resolve(relativePath);
                    file = new File(newPath.toString());
                }
                try {
                    if (file.createNewFile()) { //create new file if it not exist
                        System.out.println("File created sucessfully: " + file.getAbsolutePath());
                    } else {//update exist file
                        file.setLastModified(System.currentTimeMillis());
                        System.out.println("File updated sucessfully: " + file.getAbsolutePath());
                    }
                } catch (IOException e) {
                    System.out.println("Error while creating or updating the file!");
                }
            }

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
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, firstArg.equals(">>")))) {
                Scanner in = new Scanner(System.in);
                System.out.println("Write in the file then add @c to close the file");
                String inputString;
                while (!(inputString = in.nextLine()).equalsIgnoreCase("@c")) {
                    writer.write(inputString);
                    writer.newLine();
                }
                System.out.println("Content written to file: " + filename);
            } catch (IOException e) {
                System.out.println("Error while writing to the file: " + e.getMessage());
            }
        } else {
            // Handle displaying the file content
            File file = new File(firstArg);
            if (!file.isAbsolute()) {
                Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                file = new File(currentDir.resolve(file.toPath()).toString());
            }

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
            String sourcePathStr = source;
            String destinationPathStr = destination;
            File sourceFile = new File(sourcePathStr);
            File destinationFile = new File(destinationPathStr);

            // to absolute paths if the source is relative
            if (!sourceFile.isAbsolute()) {
                Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                sourceFile = new File(currentDir.resolve(sourceFile.toPath()).toString());
            }

            // resolve the destination path
            if (!destinationFile.isAbsolute()) {
                Path currentDir = Paths.get(System.getProperty("user.dir")).toAbsolutePath();
                destinationFile = new File(currentDir.resolve(destinationFile.toPath()).toString());
            }


            if (!sourceFile.exists()) {
                System.out.println("Source file does not exist: " + sourceFile.getAbsolutePath());
                return;
            }

            // if the destination is a folder, append the source file's name
            if (destinationFile.isDirectory()) {
                destinationFile = new File(destinationFile, sourceFile.getName());
            }

            //  move or rename the source to the destination
            boolean success = sourceFile.renameTo(destinationFile);
            if (success) {
                System.out.println("Move/Rename the file successfully: " + sourceFile.getAbsolutePath() + " to " + destinationFile.getAbsolutePath());
            } else {
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
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        File[] contents = currentDir.toFile().listFiles();
        // make flags to mark args of command
        boolean showHidden = false;
        boolean reverseOrder = false;

        // this command can take different options -a , -r

        if (args.length == 1) {
            if (args[0].equals("-a")) { // will show all files including hidden ones
                showHidden = true;
            } else if (args[0].equals("-r")) { // will show or lists files in reverse order excluding hidden ones
                reverseOrder = true;
            } else {
                System.out.println("ls: invalid argument (currently supports only -r or -a)");
                return;
            }
        } else if (args.length == 2) { // if 2 options provided , it wii mark flags
            if ((args[0].equals("-a") && args[1].equals("-r")) || (args[0].equals("-r") && args[1].equals("-a"))) {
                showHidden = true;
                reverseOrder = true;
            } else {
                System.out.println("ls: invalid combination of arguments");
                return;
            }
        } else if (args.length > 2) {
            System.out.println("ls: too many arguments");
            return;
        }


        List<File> fileList = new ArrayList<>();
        final boolean finalShowHidden = showHidden;
        final boolean finalReverseOrder = reverseOrder;

        //  Filter files based on the hidden files setting
        for (File file : contents) {
            if (showHidden || !file.getName().startsWith(".")) {
                fileList.add(file);
            }
        }

    // Sort the list based on the reverse order setting
        if (reverseOrder) {
            fileList.sort(Comparator.reverseOrder());
        } else {
            fileList.sort(Comparator.naturalOrder());
        }

    // fileList contains the filtered and sorted files
        for (File file : fileList) {
            System.out.println(file.getName());  // Print each file name
        }


    }
  /*
  * pwd print current path
  *
  * */

     public void pwd(){
         Path currentDir = Paths.get(System.getProperty("user.dir"));
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











}
