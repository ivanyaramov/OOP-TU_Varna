package com.company;

import java.util.ArrayList;
import java.util.List;

public class XMLRepresentation {
    private List<XMLElement> listOfElements;

    public XMLRepresentation() {
        this.listOfElements = new ArrayList<>();
    }

    public void addChild(XMLElement xmlElement){
        this.listOfElements.add(xmlElement);
    }
}
