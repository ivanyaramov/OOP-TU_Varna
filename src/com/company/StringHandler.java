package com.company;

import com.company.exceptions.IdNotFoundException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StringHandler {
    public static String trimFileName(String path) {
        String[] arr = path.split("\\\\");
        return arr[arr.length - 1];
    }

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

    public static boolean isTagClosed(String content) {
        return content.charAt(content.length() - 2) == '/';
    }

    public static String trimTagName(String content) {
       String saveHere = StringHandler.trimXMLChars(content);
        String[] splitContent = saveHere.split(" ");
        saveHere = splitContent[0];
        return saveHere;
    }

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

    public static boolean isClosingTag(String content) {
        return content.charAt(1) == '/';
    }

    public static boolean followedByNewTag(String content, StartIndexKeeper startIndexKeeper) {
        return content.charAt(startIndexKeeper.getStartIndex()) == '<';
    }

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
        if(map.size()>0) {
            return map;
        }
        return null;
    }

    public static String trimQuoteMarks(String content) {
        return content.substring(1, content.length() - 1);
    }

    public static String findTagValue(String content, StartIndexKeeper startIndexKeeper){
        int end = -1;
        int startIndex = startIndexKeeper.getStartIndex();
        for(int i=startIndex;i< content.length();i++){
            char current = content.charAt(i);
            if(current == '<'){
                end = i;
                break;
            }
        }
        startIndexKeeper.setStartIndex(end);
        return content.substring(startIndex, end);
    }

    public static void addElementsToStringBuilder(XMLElement xmlElement, StringBuilder sb, int level){
        StringHandler.addSpaces(level, sb);
        sb.append("<");
        sb.append(xmlElement.getName());
        boolean hasAttributes = xmlElement.getAttributes() != null;
        if(hasAttributes){
            addAttributesToStringBuilder(xmlElement.getAttributes(), sb);
        }
        if(xmlElement.getValue() == null && xmlElement.getChildren().size() == 0){
            sb.append("/>");
            sb.append(System.lineSeparator());
            return;
        }
        sb.append(">");
        if(xmlElement.getValue()!= null){
            sb.append(xmlElement.getValue());
            StringHandler.addClosingTagToStringBuilder(sb, xmlElement.getName());
        }
        else{
            sb.append(System.lineSeparator());
            for(int i=0; i<xmlElement.getChildren().size(); i++){
                StringHandler.addElementsToStringBuilder(xmlElement.getChildren().get(i), sb, level + 1);
            }
            StringHandler.addSpaces(level, sb);
            StringHandler.addClosingTagToStringBuilder(sb, xmlElement.getName());
        }

    }

    private static void addAttributesToStringBuilder(Map<String, String> map, StringBuilder sb){
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            sb.append(" ");
            sb.append(stringStringEntry.getKey());
            sb.append("=\"");
            sb.append(stringStringEntry.getValue());
            sb.append("\"");
        }
    }

    private static void addSpaces(int level, StringBuilder sb){
        for(int i=0; i<level; i++){
            sb.append("   ");
        }
    }

    private static void addClosingTagToStringBuilder(StringBuilder sb, String name){
        sb.append("</");
        sb.append(name);
        sb.append(">");
        sb.append(System.lineSeparator());
    }

    public static void addAttributesOfElementToStringBuilder(Map<String, String> map, StringBuilder sb){
        for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
            sb.append(String.format("%s = %s%n",stringStringEntry.getKey(), stringStringEntry.getValue()));
        }

    }



}
