package com.company;

import com.company.exceptions.FileNotOpenedException;

import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, FileNotOpenedException {
	// write your code here
String path = "output.xml";
FileManager fileManager = new FileManager();
fileManager.openFile(path);
String content = fileManager.readFile();
XMLRepresentation xmlRepresentation = XMLHandler.convertStringToXMLObjects(content);
        System.out.println();
    }
}
