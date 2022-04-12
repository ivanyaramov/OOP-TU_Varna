package com.company;

import java.util.List;
import java.util.Map;

public class XMLElement {
    Map<String, String> attributes;
    String value;
    String name;
    List<XMLElement> children;

    public XMLElement(Map<String, String> attributes, String value, String name) {
        this.attributes = attributes;
        this.value = value;
        this.name = name;
    }

    public XMLElement(Map<String, String> attributes, String name, List<XMLElement> children) {
        this.attributes = attributes;
        this.name = name;
        this.children = children;
    }

    public XMLElement(Map<String, String> attributes, String name) {
        this.attributes = attributes;
        this.name = name;
    }
}
