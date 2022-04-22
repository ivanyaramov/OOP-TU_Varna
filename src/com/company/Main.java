package com.company;

import com.company.exceptions.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
	// write your code here
String path = "output.xml";
FileManager fileManager = new FileManager();
fileManager.openFile(path);
String content = fileManager.readFile();
XMLRepresentation xmlRepresentation = XMLHandler.convertStringToXMLObjects(content);
XMLHandler.setUnDublicatingIdsToElements(xmlRepresentation);
        System.out.println(XMLHandler.convertXMLObjectsToString(xmlRepresentation));
//        XMLHandler.setAttributeValueById("0_1", "boms", "444", xmlRepresentation);
//        System.out.println(XMLHandler.convertXMLObjectsToString(xmlRepresentation));
//        System.out.println(XMLHandler.getAttributesOfChildren("10_1", xmlRepresentation));
//        System.out.println(XMLHandler.getTextOfElement("3", xmlRepresentation));
//        XMLHandler.deleteElementAttributeByKey("10_1", "id", xmlRepresentation);
//        XMLHandler.addElementToElement("6", "addnato", xmlRepresentation);
//        XMLHandler.addElementToElement("8", "addnato", xmlRepresentation);
        List<XMLElement> list = XMLHandler.xPathElementList("address", "per5son", xmlRepresentation);

        for(XMLElement el: list){
            StringBuilder sb = new StringBuilder();
            StringHandler.addElementsToStringBuilder(el, sb, 3);
            System.out.println(1);
            System.out.println(sb.toString());
        }
//        System.out.println(XMLHandler.convertXMLObjectsToString(xmlRepresentation));
//        XMLElement xmlElement = XMLHandler.xPathElement("person","address", xmlRepresentation, 0);
//        StringBuilder sb = new StringBuilder();
//        StringHandler.addElementsToStringBuilder(xmlElement, sb, 0);
//        System.out.println(sb);
    }
}
