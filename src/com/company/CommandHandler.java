package com.company;

import com.company.exceptions.*;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

//клас, който обработва подадените конзолни програми
public class CommandHandler {
    //инстанция на обекта, който работи с файлове
    private FileManager fileManager;

    public CommandHandler() {
        this.fileManager = new FileManager();
    }

    //метод който приема командите със switch-case, валидира ги в заивисмост от командата и вика конкретните методи за манипулация
    public void manageCommands()
            throws IOException, FileNotClosedException, FileNotOpenedException, AttributeNotFoundException,
            IdNotFoundException, ChildNotFoundException, CannotAddChildException, ElementNotFoundException {
        Scanner scanner = new Scanner(System.in);
        boolean toBreak = false;
        boolean isFileOpened = false;
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
                    if (length != 2) {
                        illegalComand();
                        break;
                    } else {
                        open(splittedCommand[1]);
                        isFileOpened = true;
                    }
                    break;
                case "close":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    close();
                    isFileOpened = false;
                    break;
                case "save":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    save();
                    break;
                case "help":
                    help();
                    break;
                case "saveas":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 2) {
                        illegalComand();
                        break;
                    } else {
                        saveAs(splittedCommand[1]);
                    }
                    break;
                case "exit":
                    toBreak = true;
                    exit();
                    break;
                case "print":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    System.out.println();
                    System.out.println(XMLHandler.convertXMLObjectsToString(fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "select":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 3) {
                        illegalComand();
                        break;
                    }
                    System.out.println(XMLHandler.getAttributeValueByElementId(splittedCommand[1], splittedCommand[2],
                            fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "set":
                    checkIsFileOpened(isFileOpened);
                    if (length != 4) {
                        illegalComand();
                        break;
                    }
                    XMLHandler.setAttributeValueById(splittedCommand[1], splittedCommand[2], splittedCommand[3], fileManager.getXmlRepresentation());
                    System.out.println("The element attribute was set successfully!");
                    System.out.println();
                    break;
                case "children":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 2) {
                        illegalComand();
                        break;
                    }
                    System.out.println(XMLHandler.getAttributesOfChildren(splittedCommand[1], fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "child":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 3) {
                        illegalComand();
                        break;
                    }
                    System.out.println(XMLHandler.getNthChildOfElement(splittedCommand[1], Integer.parseInt(splittedCommand[2]), fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "text":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 2) {
                        illegalComand();
                        break;
                    }
                    System.out.println(XMLHandler.getTextOfElement(splittedCommand[1], fileManager.getXmlRepresentation()));
                    System.out.println();
                    break;
                case "delete":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 3) {
                        illegalComand();
                        break;
                    }
                    XMLHandler.deleteElementAttributeByKey(splittedCommand[1], splittedCommand[2], fileManager.getXmlRepresentation());
                    System.out.println("Element deleted successfully");
                    System.out.println();
                    break;
                case "newchild":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 3) {
                        illegalComand();
                        break;
                    }
                    XMLHandler.addElementToElement(splittedCommand[1], splittedCommand[2], fileManager.getXmlRepresentation());
                    System.out.println("Element added successfully");
                    System.out.println();
                    break;
                case "xpath":
                    if (!checkIsFileOpened(isFileOpened)) {
                        break;
                    }
                    if (length != 2) {
                        illegalComand();
                        break;
                    }
                    String command2 = splittedCommand[1];
                    xPathHandler(command2);
                    break;
                default:
                    illegalComand();
                    break;
            }
        }
    }

    //отваряне на файл ако друг не е вече отворен
    private void open(String path) throws IOException, FileNotClosedException, FileNotOpenedException {
        if (!fileManager.isFileOpened()) {
            fileManager.openFile(path);
            System.out.println("Successfully opened " + StringHandler.trimFileName(path));
        } else {
            throw new FileNotClosedException("Another file is already opened!");
        }
    }

    //затваряне на файл ако има отворен такъв
    private void close() throws FileNotOpenedException {
        if (fileManager.isFileOpened()) {
            String path = fileManager.getPath();
            fileManager.closeFile();
            System.out.println("Successfully closed " + StringHandler.trimFileName(path));
        } else {
            throw new FileNotOpenedException("No file is currently opened");
        }
    }

    //записване в текущия файл
    private void save() throws IOException, FileNotOpenedException {
        if (fileManager.isFileOpened()) {
            fileManager.saveFile(XMLHandler.convertXMLObjectsToString(fileManager.getXmlRepresentation()));
            System.out.println("Successfully saved " + StringHandler.trimFileName(fileManager.getPath()));
        } else {
            throw new FileNotOpenedException("No file is currently opened");
        }
    }

    //записване в указан файл
    private void saveAs(String path) throws IOException, FileNotOpenedException {
        if (fileManager.isFileOpened()) {
            fileManager.saveFileAs(XMLHandler.convertXMLObjectsToString(fileManager.getXmlRepresentation()), path);
            System.out.println("Successfully saved " + StringHandler.trimFileName(fileManager.getPath()));
        } else {
            throw new FileNotOpenedException("No file is currently opened");
        }
    }

    //меню
    private void help() {
        System.out.println("The following commands are supported:");
        System.out.println("open <file> opens <file>");
        System.out.println("close         closes currently opened file");
        System.out.println("save          saves the currently open file");
        System.out.println("saveas <file>   saves the currently open file in <file>");
        System.out.println("help          prints this information");
        System.out.println("exit          exists the program");
        System.out.println("print         prints the xml with modified ids");
        System.out.println("select <id> <key>  prints the attributes of an element provided the id and the attribute name");
        System.out.println("set <id> <key> <value> setting a value to an element attribute");
        System.out.println("children <id>  outputs list of the attributes of children elements of element with given id");
        System.out.println("child <id> <n>  prints the n-th element of an element with given id");
        System.out.println("text <id>       print the whole element with given id");
        System.out.println("delete <id> <key> deletes an attribute of an element with given id");
        System.out.println("newchild <id> <name> adds new child with given name to element with given attribute");
        System.out.println("xpath <nameOfParent>/<nameofChild>  prints a list of every child element with the given name in the parent element");
        System.out.println("xpath <nameOfParent>/<nameofChild>[n] prints the nth child with given name of a given element");
        System.out.println("xpath <name>(@id)  prints a list of the ids of all elements witht he given name");
        System.out.println("xpath <nameOfParent>(<nameOfChildChecking>=<value>/<nameOfChildToOutput>  prints all children of an element with the given name where a child value corresponds to the given");
    }

    //излизане от програмата
    private void exit() {
        System.out.println("Exiting the program...");
    }

    //метод който указва, че подадената команда е грешна
    private void illegalComand() {
        System.out.println("The command you entered is illegal");
        System.out.println();
        help();
    }

    //в случай че подадената команда е xpath, се вика този метод, понеже логиката му е малко по-сложна.
    //валидира подадената команда в зависимост от коя xpath команда е извикана
    private void xPathHandler(String command) throws ElementNotFoundException {
        String[] splittedXPath = command.split("/");
        if (splittedXPath.length == 1) {
            String[] elements = splittedXPath[0].split("\\(@");
            if (elements.length != 2) {
                illegalComand();
                return;
            }
            String parent = elements[0];
            String id = elements[1].substring(0, elements.length);
            if (!id.equals("id")) {
                illegalComand();
                return;
            }
            List<String> list = XMLHandler.xPathGetAllIdsOfElement(parent, fileManager.getXmlRepresentation());
            StringHandler.printIdStringList(list);
        } else {
            String parent = splittedXPath[0];
            String child = splittedXPath[1];
            String[] checkSplit = parent.split("\\(");
            if (checkSplit.length == 2) {
                String currentParent = checkSplit[0];
                String command2 = checkSplit[1];
                String[] commandSplit = command2.split("=");
                String currentChild = commandSplit[0];
                String value = commandSplit[1].substring(0, commandSplit[1].length() - 1);
                List<XMLElement> list = XMLHandler.xPathFindElementsByNameAndValueOfChild(currentChild, currentParent, value, child, fileManager.getXmlRepresentation());
                StringHandler.printElementList(list);
                return;
            }
            String[] checkSplit2 = child.split("\\[");
            if (checkSplit2.length == 1) {
                List<XMLElement> list = XMLHandler.xPathElementList(child, parent, fileManager.getXmlRepresentation());
                if (list.size() == 0) {
                    throw new ElementNotFoundException("The are no elements of this type!");
                }
                StringHandler.printElementList(list);
                return;
            } else {
                String realChild = checkSplit2[0];
                String index = checkSplit2[1].substring(0, checkSplit2.length - 1);
                XMLElement element = XMLHandler.xPathElement(parent, realChild, fileManager.getXmlRepresentation(), Integer.parseInt(index));
                StringHandler.printSingleElement(element);
            }
        }
    }

    //проверява дали вече друг файл не е бил отворен
    private boolean checkIsFileOpened(boolean isFileOpened) {
        if (!isFileOpened) {
            System.out.println("Please first open a file");
            return false;
        }
        return true;
    }
}
