package com.company;

import com.company.exceptions.*;

import java.util.*;

public class XMLHandler {
    public static XMLRepresentation convertStringToXMLObjects(String content) {
        StartIndexKeeper startIndexKeeper = new StartIndexKeeper(0);
        ArrayDeque<XMLElement> stack = new ArrayDeque<>();
        XMLRepresentation xmlRepresentation = new XMLRepresentation();
        while (startIndexKeeper.getStartIndex() < content.length() - 1) {
            String wholeTag = StringHandler.findTag(content, startIndexKeeper);
            boolean isClosingTag = StringHandler.isClosingTag(wholeTag);
            if (isClosingTag) {
                XMLElement lastEl = stack.pop();
                if (stack.isEmpty()) {
                    xmlRepresentation.addChild(lastEl);
                }
                continue;
            }
            String tag = StringHandler.trimTagName(wholeTag);
            Map<String, String> attributes = StringHandler.getAttributesOfTag(wholeTag);
            XMLElement element = new XMLElement(attributes, tag);
            if (!stack.isEmpty()) {
                stack.peek().addChild(element);
            }

            boolean isClosed = StringHandler.isTagClosed(wholeTag);
            if (isClosed) {
                if (stack.isEmpty()) {
                    xmlRepresentation.addChild(element);
                }
                continue;
            }

            boolean followedByNewTag = StringHandler.followedByNewTag(content, startIndexKeeper);
            if (followedByNewTag) {

            } else {
                String value = StringHandler.findTagValue(content, startIndexKeeper);
                element.setValue(value);
            }
            stack.push(element);

        }
        return xmlRepresentation;
    }

    public static String convertXMLObjectsToString(XMLRepresentation xmlRepresentation) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < xmlRepresentation.getListOfElements().size(); i++) {
            StringHandler.addElementsToStringBuilder(xmlRepresentation.getListOfElements().get(i), sb, 0);
        }
        return sb.toString();
    }

    public static void setUnDublicatingIdsToElements(XMLRepresentation xmlRepresentation) {
        Set<String> duplicatedElements = new HashSet<>();
        Set<String> allIds = new HashSet<>();
        findDuplicatingIds(xmlRepresentation, duplicatedElements, allIds);
        setIdsOfElementsInRepresentation(duplicatedElements, xmlRepresentation, allIds);
    }

    public static void findDuplicatingIds(XMLRepresentation xmlRepresentation, Set<String> duplicatedElements, Set<String> allElements) {
        for (int i = 0; i < xmlRepresentation.getListOfElements().size(); i++) {
            XMLHandler.findDuplicatingIdsInChildElements(xmlRepresentation.getListOfElements().get(i), duplicatedElements, allElements);
        }
    }

    public static void findDuplicatingIdsInChildElements(XMLElement xmlElement, Set<String> duplicatedElements, Set<String> allElements) {

        if (xmlElement.getAttributes() != null) {
            if (xmlElement.getAttributes().containsKey("id")) {

                String value = xmlElement.getAttributes().get("id");
                String realValue = value.split("_")[0];
                if (!allElements.add(realValue)) {
                    duplicatedElements.add(realValue);
                }


            }

        }
        for (int i = 0; i < xmlElement.getChildren().size(); i++) {
            XMLHandler.findDuplicatingIdsInChildElements(xmlElement.getChildren().get(i), duplicatedElements, allElements);
        }
    }

    public static void setIdsOfElementsInRepresentation(Set<String> duplicatedElements, XMLRepresentation xmlRepresentation, Set<String> allIds) {
        Map<String, String> duplicatedElementsCounter = new HashMap<>();
        for (int i = 0; i < xmlRepresentation.getListOfElements().size(); i++) {
            XMLHandler.setIdsOfElements(duplicatedElements, allIds, xmlRepresentation.getListOfElements().get(i), duplicatedElementsCounter);
        }
    }

    public static void setIdsOfElements(Set<String> duplicatedElements, Set<String> allIds, XMLElement xmlElement, Map<String, String> duplicatedElementsCounter) {
        if (xmlElement.getAttributes() != null) {
            Map<String, String> map = xmlElement.getAttributes();
            if (!map.containsKey("id")) {
                XMLHandler.setUntakenID(map, allIds, xmlElement);
            } else {
                String wholeId = map.get("id");
                String id = wholeId.split("_")[0];
                if (duplicatedElements.contains(id)) {
                    if (duplicatedElementsCounter.containsKey(id)) {
                        String current = duplicatedElementsCounter.get(id);
                        int currentInt = Integer.parseInt(current);
                        currentInt++;
                        duplicatedElementsCounter.put(id, String.valueOf(currentInt));
                    } else {
                        duplicatedElementsCounter.put(id, "1");
                    }
                    String newId = id + "_" + duplicatedElementsCounter.get(id);
                    map.put("id", newId);
                }
            }

        } else {
            Map<String, String> map = new HashMap<>();
            XMLHandler.setUntakenID(map, allIds, xmlElement);
        }
        for (int i = 0; i < xmlElement.getChildren().size(); i++) {
            XMLHandler.setIdsOfElements(duplicatedElements, allIds, xmlElement.getChildren().get(i), duplicatedElementsCounter);
        }
    }

    private static void setUntakenID(Map<String, String> map, Set<String> allIds, XMLElement xmlElement) {
        String id = "1";
        while (allIds.contains(id)) {
            int idAsNumber = Integer.parseInt(id);
            idAsNumber++;
            id = String.valueOf(idAsNumber);
        }
        map.put("id", id);
        xmlElement.setAttributes(map);
        allIds.add(id);
    }

    public static void setAttributeValueById(String id, String key, String value, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        XMLElement element = XMLHandler.findElementById(id, xmlRepresentation);
        element.getAttributes().put(key, value);


    }

    public static String getAttributeValueByElementId(String id, String key, XMLRepresentation xmlRepresentation) throws IdNotFoundException, AttributeNotFoundException {
        XMLElement element = XMLHandler.findElementById(id, xmlRepresentation);
        if (!element.getAttributes().containsKey(key)) {
            throw new AttributeNotFoundException("There was no attribute with key " + key + " in the element");
        }
        return element.getAttributes().get(key);
    }

    public static XMLElement findElementById(String id, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        for (XMLElement el : xmlRepresentation.getListOfElements()) {
            XMLElement newEl = XMLHandler.findElementByIdInElement(el, id);
            if (newEl != null) {
                return newEl;
            }
        }
        throw new IdNotFoundException("There is no element with id " + id);
    }

    public static XMLElement findElementByIdInElement(XMLElement xmlElement, String id) {
        if (xmlElement.getAttributes().get("id").equals(id)) {
            return xmlElement;
        }
        for (XMLElement el : xmlElement.getChildren()) {
            XMLElement newEl = XMLHandler.findElementByIdInElement(el, id);
            if (newEl != null) {
                return newEl;
            }
        }
        return null;
    }

    public static void findElementsByNameInElement(String parentName, String expectedParentName, XMLElement xmlElement, String name, List<XMLElement> list) {
        if (expectedParentName != null) {
            if (xmlElement.getName().equals(name) && parentName.equals(expectedParentName)) {
                list.add(xmlElement);
            }
        } else if (xmlElement.getName().equals(name)) {
            list.add(xmlElement);
        }
        for (XMLElement current : xmlElement.getChildren()) {
            XMLHandler.findElementsByNameInElement(xmlElement.getName(), expectedParentName, current, name, list);
        }
    }

    public static List<XMLElement> findElementsByName(String expectedParentName, String name, XMLRepresentation xmlRepresentation) {
        List<XMLElement> list = new ArrayList<>();
        for (XMLElement el : xmlRepresentation.getListOfElements()) {
            XMLHandler.findElementsByNameInElement("", expectedParentName, el, name, list);
        }
        return list;
    }

    public static List<XMLElement> findElementsByChildrenValue(String parentName, String childName, String value, String get, XMLRepresentation xmlRepresentation) {
        List<XMLElement> list = new ArrayList<>();
        for (XMLElement el : xmlRepresentation.getListOfElements()) {
            XMLHandler.findElementsByChildrenValueInElements(parentName, childName, value, get, list, el);
        }
        return list;
    }

    public static void findElementsByChildrenValueInElements(String parentName, String childName, String value, String get, List<XMLElement> list, XMLElement xmlElement) {
        boolean toAdd = false;
        if (xmlElement.getName().equals(parentName)) {
            for (XMLElement current : xmlElement.getChildren()) {
                if (current.getName().equals(childName) && current.getValue().equals(value)) {
                    toAdd = true;
                    break;
                }
            }

            for (XMLElement current : xmlElement.getChildren()) {
                if (current.getName().equals(get) && toAdd) {
                    list.add(current);

                }
            }
        }

        for (XMLElement current : xmlElement.getChildren()) {
            XMLHandler.findElementsByChildrenValueInElements(parentName, childName, value, get, list, current);
        }
    }


    public static String getAttributesOfChildren(String id, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        StringBuilder sb = new StringBuilder();
        XMLElement element = XMLHandler.findElementById(id, xmlRepresentation);
        if (element.getChildren().size() != 0) {
            for (XMLElement current : element.getChildren()) {
                XMLHandler.getAttributesOfChildrenAndCurrent(current, sb);
            }
        }
        return sb.toString();
    }

    public static void getAttributesOfChildrenAndCurrent(XMLElement element, StringBuilder sb) {
        if (element.getAttributes() != null) {
            StringHandler.addAttributesOfElementToStringBuilder(element.getAttributes(), sb);
        }
        if (element.getChildren().size() != 0) {
            for (XMLElement current : element.getChildren()) {
                XMLHandler.getAttributesOfChildrenAndCurrent(current, sb);
            }
        }
    }

    public static String getNthChildOfElement(String id, int n, XMLRepresentation xmlRepresentation) throws IdNotFoundException, ChildNotFoundException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        if (xmlElement.getChildren().size() < n - 1) {
            throw new ChildNotFoundException("There is no child " + n + " in the element with id " + id);
        }
        StringBuilder sb = new StringBuilder();
        StringHandler.addElementsToStringBuilder(xmlElement.getChildren().get(n - 1), sb, 0);
        return sb.toString();
    }

    public static String getTextOfElement(String id, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        StringBuilder sb = new StringBuilder();
        StringHandler.addElementsToStringBuilder(xmlElement, sb, 0);
        return sb.toString();
    }

    public static void deleteElementAttributeByKey(String id, String key, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        if (xmlElement.getAttributes() != null) {
            xmlElement.getAttributes().remove(key);
        }
    }

    public static void addElementToElement(String id, String name, XMLRepresentation xmlRepresentation) throws IdNotFoundException, CannotAddChildException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        if (xmlElement.getValue() != null) {
            throw new CannotAddChildException("You can not add child to this element! It has a value.");
        }
        XMLElement newElement = new XMLElement(name);
        xmlElement.getChildren().add(newElement);
        XMLHandler.setUnDublicatingIdsToElements(xmlRepresentation);
    }

    public static List<XMLElement> xPathElementList(String elementName, String expectedParentName, XMLRepresentation xmlRepresentation) {
        return XMLHandler.findElementsByName(expectedParentName, elementName, xmlRepresentation);
    }

    public static XMLElement xPathElement(String expectedParentName, String elementName, XMLRepresentation xmlRepresentation, int number) throws ElementNotFoundException {
        List<XMLElement> list = XMLHandler.findElementsByName(expectedParentName, elementName, xmlRepresentation);
        if (number > list.size() - 1) {
            throw new ElementNotFoundException("There are no " + number + " elements of this type ");
        }
        return list.get(number);
    }

    public static List<String> xPathGetAllIdsOfElement(String elementName, XMLRepresentation xmlRepresentation) throws ElementNotFoundException {
        List<XMLElement> xmlElements = XMLHandler.findElementsByName(null, elementName, xmlRepresentation);
        List<String> listOfIds = new ArrayList<>();
        for (XMLElement xmlElement : xmlElements) {
            listOfIds.add(xmlElement.getAttributes().get("id"));
        }
        if (listOfIds.size() == 0) {
            throw new ElementNotFoundException("No elements with name " + elementName + " were found");
        }
        return listOfIds;
    }

    public static List<XMLElement> xPathFindElementsByNameAndValueOfChild(String elementName, String parentName, String value, String get, XMLRepresentation xmlRepresentation) throws ElementNotFoundException {
        List<XMLElement> list = XMLHandler.findElementsByChildrenValue(parentName, elementName, value, get, xmlRepresentation);
        if (list.size() == 0) {
            throw new ElementNotFoundException("No elements with such children values were found or no children with such name exist.");
        }
        return list;
    }


}
