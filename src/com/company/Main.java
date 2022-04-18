package com.company;

import com.company.exceptions.AttributeNotFoundException;
import com.company.exceptions.FileNotOpenedException;
import com.company.exceptions.IdNotFoundException;

import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, FileNotOpenedException, AttributeNotFoundException, IdNotFoundException {
	// write your code here
String path = "output.xml";
FileManager fileManager = new FileManager();
fileManager.openFile(path);
String content = fileManager.readFile();
XMLRepresentation xmlRepresentation = XMLHandler.convertStringToXMLObjects(content);
XMLHandler.setUnDublicatingIdsToElements(xmlRepresentation);
        System.out.println(XMLHandler.convertXMLObjectsToString(xmlRepresentation));
        XMLHandler.setAttributeValueById("0_1", "boms", "444", xmlRepresentation);
        System.out.println(XMLHandler.convertXMLObjectsToString(xmlRepresentation));
    }
}
