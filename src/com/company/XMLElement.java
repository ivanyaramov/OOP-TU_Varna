package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XMLElement {
    private Map<String, String> attributes;
    private String value;
    private String name;
    private List<XMLElement> children;



    public XMLElement(Map<String, String> attributes, String name) {
        this.attributes = attributes;
        this.name = name;
        this.children = new ArrayList<>();
    }

    public XMLElement(String name) {
        this.attributes = new HashMap<>();
        this.name = name;
        this.children = new ArrayList<>();
    }


    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<XMLElement> getChildren() {
        return children;
    }

    public void setChildren(List<XMLElement> children) {
        this.children = children;
    }

    public void addChild(XMLElement xmlElement){
        children.add(xmlElement);
    }
}
