package com.company;

import com.company.exceptions.IdNotFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//клас, който служи за обработване и манипулиране на текста от файла (спомага за превръщането на xml съдържанието в java обекти)
public class StringHandler {
    //връща само името на файла, като отрязва директорията му
    public static String trimFileName(String path) {
        String[] arr = path.split("\\\\");
        return arr[arr.length - 1];
    }

    //връща следващия поред елемент в цялостния xml (startIndexKeeper указва от къде да почне да чете)
    public static String findTag(String content, StartIndexKeeper startIndexKeeper) {
        String saveHere = "";
        int start = -1;
        int end = -1;
        int startIndex = startIndexKeeper.getStartIndex();
        for (int i = startIndex; i < content.length(); i++) {
            char current = content.charAt(i);
            if (current == '<') {
                start = i;
            }
            if (current == '>') {
                end = i;
                break;
            }
        }
        saveHere = content.substring(start, end + 1);
        startIndexKeeper.setStartIndex(end + 1);
        return saveHere;
    }

    //проверява дали елементът няма стойност
    public static boolean isTagClosed(String content) {
        return content.charAt(content.length() - 2) == '/';
    }

    //връща само името на елемента, като отрязва безполезните символи
    public static String trimTagName(String content) {
        String saveHere = StringHandler.trimXMLChars(content);
        String[] splitContent = saveHere.split(" ");
        saveHere = splitContent[0];
        return saveHere;
    }

    //маха безполезните символи, идващи от файла в зависимост от това тагът от кой тип е
    public static String trimXMLChars(String content) {
        String saveHere = "";
        if (StringHandler.isClosingTag(content)) {
            saveHere = content.substring(2, content.length() - 1);
            return saveHere;
        }
        if (StringHandler.isTagClosed(content)) {
            saveHere = content.substring(1, content.length() - 2);
        } else {
            saveHere = content.substring(1, content.length() - 1);
        }
        return saveHere;
    }

    //проверява дали тагът е затварящ
    public static boolean isClosingTag(String content) {
        return content.charAt(1) == '/';
    }

    //проверява дали в текущия таг следва вложено дете или стойност
    public static boolean followedByNewTag(String content, StartIndexKeeper startIndexKeeper) {
        return content.charAt(startIndexKeeper.getStartIndex()) == '<';
    }

    //връща карта от атрибутите на текущия таг
    public static Map<String, String> getAttributesOfTag(String content) {
        Map<String, String> map = new LinkedHashMap<>();
        content = StringHandler.trimXMLChars(content);
        String[] splitContent = content.split(" ");

        for (int i = 1; i < splitContent.length; i++) {
            String keyValue = splitContent[i];
            String[] keyValueSplit = keyValue.split("=");
            String key = keyValueSplit[0];
            String value = keyValueSplit[1];
            value = StringHandler.trimQuoteMarks(value);
            map.put(key, value);

        }
        if (map.size() > 0) {
            return map;
        }
        return null;
    }

    //премахва кавички
    public static String trimQuoteMarks(String content) {
        return content.substring(1, content.length() - 1);
    }

    //връща стойноста на елемента
    public static String findTagValue(String content, StartIndexKeeper startIndexKeeper) {
        int end = -1;
        int startIndex = startIndexKeeper.getStartIndex();
        for (int i = startIndex; i < content.length(); i++) {
            char current = content.charAt(i);
            if (current == '<') {
                end = i;
                break;
            }
        }
        startIndexKeeper.setStartIndex(end);
        return content.substring(startIndex, end);
    }

    //добавя подаден елемент като текст в подадения StringBuilder
    public static void addElementsToStringBuilder(XMLElement xmlElement, StringBuilder sb, int level) {
        StringHandler.addSpaces(level, sb);
        sb.append("<");
        sb.append(xmlElement.getName());
        boolean hasAttributes = xmlElement.getAttributes() != null;
        if (hasAttributes) {
            addAttributesToStringBuilder(xmlElement.getAttributes(), sb);
        }
        if (xmlElement.getValue() == null && xmlElement.getChildren().size() == 0) {
            sb.append("/>");
            sb.append(System.lineSeparator());
            return;
        }
        sb.append(">");
        if (xmlElement.getValue() != null) {
            sb.append(xmlElement.getValue());
            StringHandler.addClosingTagToStringBuilder(sb, xmlElement.getName());
        } else {
            sb.append(System.lineSeparator());
            for (int i = 0; i < xmlElement.getChildren().size(); i++) {
                StringHandler.addElementsToStringBuilder(xmlElement.getChildren().get(i), sb, level + 1);
            }
            StringHandler.addSpaces(level, sb);
            StringHandler.addClosingTagToStringBuilder(sb, xmlElement.getName());
        }

    }

    //добавя атрибути към StringBuilder
    private static void addAttributesToStringBuilder(Map<String, String> map, StringBuilder sb) {
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            sb.append(" ");
            sb.append(stringStringEntry.getKey());
            sb.append("=\"");
            sb.append(stringStringEntry.getValue());
            sb.append("\"");
        }
    }

    //добавя отстояние от началото на реда за красиво изобразявнае
    private static void addSpaces(int level, StringBuilder sb) {
        for (int i = 0; i < level; i++) {
            sb.append("    ");
        }
    }

    //добавяне на затварящ таг, като му се подаде името
    private static void addClosingTagToStringBuilder(StringBuilder sb, String name) {
        sb.append("</");
        sb.append(name);
        sb.append(">");
        sb.append(System.lineSeparator());
    }

    //добавяне на атрбитуи към StringBuilder
    public static void addAttributesOfElementToStringBuilder(Map<String, String> map, StringBuilder sb) {
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            sb.append(String.format("%s = %s%n", stringStringEntry.getKey(), stringStringEntry.getValue()));
        }

    }

    //принтиране на подаден списък с елементи
    public static void printElementList(List<XMLElement> list) {
        for (XMLElement el : list) {
            StringBuilder sb = new StringBuilder();
            StringHandler.addElementsToStringBuilder(el, sb, 0);
            System.out.println(sb);
            System.out.println();
        }
    }

    //добавяне на подаден списък от стрингове (използва се като се принтират ид-тата)
    public static void printIdStringList(List<String> list) {
        for (String current : list) {
            System.out.println("id=" + current);
        }
    }

    //принтиране на един елемент
    public static void printSingleElement(XMLElement element) {
        StringBuilder sb = new StringBuilder();
        StringHandler.addElementsToStringBuilder(element, sb, 0);
        System.out.println(sb);
        System.out.println();
    }


}
