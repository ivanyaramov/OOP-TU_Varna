package com.company;

import com.company.exceptions.*;

import java.io.IOException;
import java.util.Scanner;

public class CommandHandler {
    private FileManager fileManager;

    public CommandHandler() {
        this.fileManager = new FileManager();
    }

    private void manageCommands() throws IOException, FileNotClosedException, FileNotOpenedException, AttributeNotFoundException, IdNotFoundException, ChildNotFoundException, CannotAddChildException {
        Scanner scanner = new Scanner(System.in);
        boolean toBreak = false;
        while (!toBreak) {
            System.out.println("Please enter a command:");
            String fullCommand = scanner.nextLine();
            String[] splittedCommand = fullCommand.split(" ");
            int length = splittedCommand.length;
            if (length < 1) {
                illegalComand();
                continue;
            }
            String command = splittedCommand[0];
            switch (command) {
                case "open":
                    if (length < 2) {
                        illegalComand();
                    } else {
                        open(splittedCommand[1]);
                    }
                    break;
                case "close":
                    close();
                    break;
                case "save":
                    save();
                    break;
                case "saveas":
                    if (length < 2) {
                        illegalComand();
                    } else {
                        saveAs(splittedCommand[1]);
                    }
                    break;
                case "exit":
                    toBreak = true;
                    break;
                case "print":
                    System.out.println();
                    XMLHandler.convertXMLObjectsToString(fileManager.getXmlRepresentation());
                    System.out.println();
                    break;
                case "select":
                    if (length < 3) {
                        illegalComand();
                    }
                    XMLHandler.getAttributeValueByElementId(splittedCommand[1], splittedCommand[2], fileManager.getXmlRepresentation());
                    System.out.println();
                    break;
                case "set":
                    if (length < 4) {
                        illegalComand();
                    }
                    XMLHandler.setAttributeValueById(splittedCommand[1], splittedCommand[2],splittedCommand[3], fileManager.getXmlRepresentation());
                    System.out.println("The element attribute was set successfully!");
                    System.out.println();
                    break;
                case "children":
                    if (length < 2) {
                        illegalComand();
                    }
                    System.out.println(XMLHandler.getAttributesOfChildren(splittedCommand[1], fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "child":
                    if (length < 3) {
                        illegalComand();
                    }
                    System.out.println(XMLHandler.getNthChildOfElement(splittedCommand[1], Integer.parseInt(splittedCommand[2]), fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "text":
                    if (length < 2) {
                        illegalComand();
                    }
                    System.out.println(XMLHandler.getTextOfElement(splittedCommand[1], fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "delete":
                    if (length < 3) {
                        illegalComand();
                    }
                    XMLHandler.deleteElementAttributeByKey(splittedCommand[1], splittedCommand[2], fileManager.getXmlRepresentation());
                    System.out.println("Element deleted successfully");
                    System.out.println();
                    break;
                case "newchild":
                    if (length < 3) {
                        illegalComand();
                    }
                    XMLHandler.addElementToElement(splittedCommand[1], splittedCommand[2], fileManager.getXmlRepresentation());
                    System.out.println("Element added successfully");
                    System.out.println();
                    break;
                default:
                    illegalComand();
                    break;
            }
        }
    }

    private void open(String path) throws IOException, FileNotClosedException, FileNotOpenedException {
        if (!fileManager.isFileOpened()) {
            fileManager.openFile(path);
            System.out.println("Successfully opened " + StringHandler.trimFileName(path));
        } else {
            throw new FileNotClosedException("Another file is already opened!");
        }
    }

    private void close() throws FileNotOpenedException {
        if (fileManager.isFileOpened()) {
            String path = fileManager.getPath();
            fileManager.closeFile();
            System.out.println("Successfully closed " + StringHandler.trimFileName(path));
        } else {
            throw new FileNotOpenedException("No file is currently opened");
        }
    }

    private void save() throws IOException, FileNotOpenedException {
        if (fileManager.isFileOpened()) {
            fileManager.saveFile("aaa");
            System.out.println("Successfully saved " + StringHandler.trimFileName(fileManager.getPath()));
        } else {
            throw new FileNotOpenedException("No file is currently opened");
        }
    }

    private void saveAs(String path) throws IOException, FileNotOpenedException {
        if (fileManager.isFileOpened()) {
            fileManager.saveFileAs("aaa", path);
            System.out.println("Successfully saved " + StringHandler.trimFileName(fileManager.getPath()));
        } else {
            throw new FileNotOpenedException("No file is currently opened");
        }
    }

    private void help() {
        System.out.println("The following commands are supported:");
        System.out.println("open <file> opens <file>");
        System.out.println("close         closes currently opened file");
        System.out.println("save          saves the currently open file");
        System.out.println("saveas <file>   saves the currently open file in <file>");
        System.out.println("help          prints this information");
        System.out.println("exit          exists the program");
    }

    private void exit() {
        System.out.println("Exiting the program...");
    }

    private void illegalComand() {
        System.out.println("The command you entered is illegal");
        System.out.println();
        help();
    }


}
