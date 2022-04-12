package com.company;

import java.util.LinkedHashMap;
import java.util.Map;

public class StringHandler {
    public static String trimFileName(String path) {
        String[] arr = path.split("\\\\");
        return arr[arr.length - 1];
    }

    public static int findTag(String content, int startIndex, String saveHere) {
        int start = -1;
        int end = -1;
        for (int i = 0; i < content.length(); i++) {
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
        return end + 1;
    }

    public static boolean isTagClosed(String content) {
        return content.charAt(content.length() - 2) == '/';
    }

    public static void trimTagName(String content, String saveHere) {
        StringHandler.trimXMLChars(content, saveHere);
        String[] splitContent = saveHere.split(" ");
        saveHere = splitContent[0];
    }

    public static void trimXMLChars(String content, String saveHere) {
        if (StringHandler.isClosingTag(content)) {
            saveHere = content.substring(2, content.length() - 1);
            return;
        }
        if (StringHandler.isTagClosed(content)) {
            saveHere = content.substring(1, content.length() - 2);
        } else {
            saveHere = content.substring(1, content.length() - 1);
        }
    }

    public static boolean isClosingTag(String content) {
        return content.charAt(1) == '/';
    }

    public static boolean followedByNewTag(String content, int startIndex) {
        return content.charAt(startIndex) == '<';
    }

    public static Map<String, String> getAttributesOfTag(String content) {
        Map<String, String> map = new LinkedHashMap<>();
        String[] splitContent = content.split(" ");

        for (int i = 1; i < splitContent.length; i++) {
            String keyValue = splitContent[i];
            String[] keyValueSplit = keyValue.split("=");
            String key = keyValueSplit[0];
            String value = keyValueSplit[1];
            StringHandler.trimQuoteMarks(value);
            map.put(key, value);

        }
        if(map.size()>0) {
            return map;
        }
        return null;
    }

    public static void trimQuoteMarks(String content) {
        content = content.substring(1, content.length() - 1);
    }

    public static int findTagValue(String content, int startIndex, String saveHere){
        int end = -1;
        for(int i=startIndex;i< content.length();i++){
            char current = content.charAt(i);
            if(current == '<'){
                end = i;
            }
        }
        saveHere = content.substring(startIndex, end);
        return end;
    }

}
