package com.company;

import com.company.exceptions.*;

import java.util.*;

//клас който манипулира информацията от xml файла
public class XMLHandler {
    //метод, който превръща низа, прочетен от файла, в java обекти
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

    //метод, който парсира обратно обектите към текст
    public static String convertXMLObjectsToString(XMLRepresentation xmlRepresentation) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < xmlRepresentation.getListOfElements().size(); i++) {
            StringHandler.addElementsToStringBuilder(xmlRepresentation.getListOfElements().get(i), sb, 0);
        }
        return sb.toString();
    }

    //слага недублиращи ид-та на елементите, които имат дублиращи ид-та и слага ид-та на тези, които нямат
    public static void setUnDublicatingIdsToElements(XMLRepresentation xmlRepresentation) {
        Set<String> duplicatedElements = new HashSet<>();
        Set<String> allIds = new HashSet<>();
        findDuplicatingIds(xmlRepresentation, duplicatedElements, allIds);
        setIdsOfElementsInRepresentation(duplicatedElements, xmlRepresentation, allIds);
    }

    //намира дубликиращите се ид-та в цялата структура
    public static void findDuplicatingIds(XMLRepresentation xmlRepresentation, Set<String> duplicatedElements, Set<String> allElements) {
        for (int i = 0; i < xmlRepresentation.getListOfElements().size(); i++) {
            XMLHandler.findDuplicatingIdsInChildElements(xmlRepresentation.getListOfElements().get(i), duplicatedElements, allElements);
        }
    }

    //добавя ид-то на подадения елемент в подадената колекция, ако той се дублира и обхожда неговите деца
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

    //слага не-дубликиращи ид-та във всички елементи на репрезентацията
    public static void setIdsOfElementsInRepresentation(Set<String> duplicatedElements, XMLRepresentation xmlRepresentation, Set<String> allIds) {
        Map<String, String> duplicatedElementsCounter = new HashMap<>();
        for (int i = 0; i < xmlRepresentation.getListOfElements().size(); i++) {
            XMLHandler.setIdsOfElements(duplicatedElements, allIds, xmlRepresentation.getListOfElements().get(i), duplicatedElementsCounter);
        }
    }

    //метод, съдържащ логиката на това да се слагат уникално ид на подадения елемент, като се гледа дали се съдържа в
    //колекцията с дубликиращи се ид-та, а самото ид се избира като се гледа да не се съдържа в колекцията с всички текущи ид-та
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

    //метод който избира уникално ид, като инкрементира от стойност 1, докато не намери такова каквото не се съдържа в колекцията с всички ид-та
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

    //слагане на нов атрибут/ модифициране на стар атрибут по ключ на подаден елемент
    public static void setAttributeValueById(String id, String key, String value, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        XMLElement element = XMLHandler.findElementById(id, xmlRepresentation);
        element.getAttributes().put(key, value);


    }

    //връща стойността на атрибут, като е зададен ключа му и елемента
    public static String getAttributeValueByElementId(String id, String key, XMLRepresentation xmlRepresentation) throws IdNotFoundException, AttributeNotFoundException {
        XMLElement element = XMLHandler.findElementById(id, xmlRepresentation);
        if (!element.getAttributes().containsKey(key)) {
            throw new AttributeNotFoundException("There was no attribute with key " + key + " in the element");
        }
        return element.getAttributes().get(key);
    }

    //търсене на елемент по ид в репрезентацията
    public static XMLElement findElementById(String id, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        for (XMLElement el : xmlRepresentation.getListOfElements()) {
            XMLElement newEl = XMLHandler.findElementByIdInElement(el, id);
            if (newEl != null) {
                return newEl;
            }
        }
        throw new IdNotFoundException("There is no element with id " + id);
    }

    //намиране на елемент по ид в подаден елемент и децата му
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

    //намиране на елемент по име в подаден елемент
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

    //намиране на елемент по име в цялата репрезентация
    public static List<XMLElement> findElementsByName(String expectedParentName, String name, XMLRepresentation xmlRepresentation) {
        List<XMLElement> list = new ArrayList<>();
        for (XMLElement el : xmlRepresentation.getListOfElements()) {
            XMLHandler.findElementsByNameInElement("", expectedParentName, el, name, list);
        }
        return list;
    }

    //намрине на елемент, ако някое от децата му има подадената стойност (в цялата репрезентация)
    public static List<XMLElement> findElementsByChildrenValue(String parentName, String childName, String value, String get, XMLRepresentation xmlRepresentation) {
        List<XMLElement> list = new ArrayList<>();
        for (XMLElement el : xmlRepresentation.getListOfElements()) {
            XMLHandler.findElementsByChildrenValueInElements(parentName, childName, value, get, list, el);
        }
        return list;
    }

    //намиране на елемент по стойност на някое от децата му има подадената стойност
    //ако отговаря на това условие се добавя в списък
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

    //намиране на всички атрибути на децата на елемент в цялата, като му е подадено ид-то му
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

    //намиране на всички атрибути на децата на подаден елемент и се добавят в StringBuilder
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

    //намиране на N-то дете на елемент, като му е подадено ид-то
    public static String getNthChildOfElement(String id, int n, XMLRepresentation xmlRepresentation) throws IdNotFoundException, ChildNotFoundException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        if (xmlElement.getChildren().size() < n - 1) {
            throw new ChildNotFoundException("There is no child " + n + " in the element with id " + id);
        }
        StringBuilder sb = new StringBuilder();
        StringHandler.addElementsToStringBuilder(xmlElement.getChildren().get(n - 1), sb, 0);
        return sb.toString();
    }

    //намиране на текста на елемент, като му е подадено ид-то (обхваща и текста на децата му)
    public static String getTextOfElement(String id, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        StringBuilder sb = new StringBuilder();
        StringHandler.addElementsToStringBuilder(xmlElement, sb, 0);
        return sb.toString();
    }

    //трие атрибут на елемент като е подаден ключа на атрибута и ид-то на елемента
    public static void deleteElementAttributeByKey(String id, String key, XMLRepresentation xmlRepresentation) throws IdNotFoundException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        if (xmlElement.getAttributes() != null) {
            xmlElement.getAttributes().remove(key);
        }
    }

    //добавяне на нов елемент с подадено име към съществуващ елемент с подадено ид
    //ид-тата се проверяват и се слагат наново, за да е сигурно че няма дубликати
    public static void addElementToElement(String id, String name, XMLRepresentation xmlRepresentation) throws IdNotFoundException, CannotAddChildException {
        XMLElement xmlElement = XMLHandler.findElementById(id, xmlRepresentation);
        if (xmlElement.getValue() != null) {
            throw new CannotAddChildException("You can not add child to this element! It has a value.");
        }
        XMLElement newElement = new XMLElement(name);
        xmlElement.getChildren().add(newElement);
        XMLHandler.setUnDublicatingIdsToElements(xmlRepresentation);
    }

    //намира всички елементи по подадено име, в бащин елемент чието име също е подадено
    public static List<XMLElement> xPathElementList(String elementName, String expectedParentName, XMLRepresentation xmlRepresentation) {
        return XMLHandler.findElementsByName(expectedParentName, elementName, xmlRepresentation);
    }

    //като горната функция, само че връща само 1 елемент с индекс подадения
    public static XMLElement xPathElement(String expectedParentName, String elementName, XMLRepresentation xmlRepresentation, int number) throws ElementNotFoundException {
        List<XMLElement> list = XMLHandler.findElementsByName(expectedParentName, elementName, xmlRepresentation);
        if (number > list.size() - 1) {
            throw new ElementNotFoundException("There are no " + number + " elements of this type ");
        }
        return list.get(number);
    }

    //връща списък с всички ид-та на децата на елемент, чието име е подадено
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

    //връща всички елементи с подаденото име, които отговарят на подаденото име и съдържат в себе си дете с подаденото име и подадената стойност
    public static List<XMLElement> xPathFindElementsByNameAndValueOfChild(String elementName, String parentName, String value, String get, XMLRepresentation xmlRepresentation) throws ElementNotFoundException {
        List<XMLElement> list = XMLHandler.findElementsByChildrenValue(parentName, elementName, value, get, xmlRepresentation);
        if (list.size() == 0) {
            throw new ElementNotFoundException("No elements with such children values were found or no children with such name exist.");
        }
        return list;
    }


}
