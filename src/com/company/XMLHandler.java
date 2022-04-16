package com.company;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Set;

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

    public static String convertXMLObjectsToString(XMLRepresentation xmlRepresentation){
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< xmlRepresentation.getListOfElements().size(); i++){
        StringHandler.addElementsToStringBuilder(xmlRepresentation.getListOfElements().get(i), sb, 0);
        }
        return sb.toString();
    }

    public static Set<String> findDuplicatingIds(XMLRepresentation xmlRepresentation){
        for(int i=0; i< xmlRepresentation.getListOfElements().size(); i++){

        }

    }

    public static void findDuplicatingIdsInChildElements(XMLElement xmlElement){

    }


}
