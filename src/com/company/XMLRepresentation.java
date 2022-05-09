package com.company;

import java.util.ArrayList;
import java.util.List;

//клас, с който се репрезентира xml съдържанието под формата на на java обекти.
public class XMLRepresentation {
    //колекция от всички обекти, стоящи на най-горното ниво
    private List<XMLElement> listOfElements;

    public XMLRepresentation() {
        this.listOfElements = new ArrayList<>();
    }

    //добавяне на елемент в колекцията
    public void addChild(XMLElement xmlElement) {
        this.listOfElements.add(xmlElement);
    }

    public List<XMLElement> getListOfElements() {
        return listOfElements;
    }
}
