package com.company;

import java.util.ArrayDeque;
import java.util.Map;

public class XMLHandler {
    public static XMLRepresentation convertStringToXMLObjects(String content){
        StartIndexKeeper startIndexKeeper = new StartIndexKeeper(0);
        ArrayDeque<XMLElement> stack = new ArrayDeque<>();
        XMLRepresentation xmlRepresentation = new XMLRepresentation();
        while(startIndexKeeper.getStartIndex()<content.length()-1){
            String saveHere = "";
          String wholeTag =  StringHandler.findTag(content, startIndexKeeper);
          boolean isClosingTag = StringHandler.isClosingTag(wholeTag);
          if(isClosingTag){
              XMLElement lastEl = stack.pop();
              if(stack.isEmpty()){
                  xmlRepresentation.addChild(lastEl);
              }
              continue;
          }
          String tag = StringHandler.trimTagName(wholeTag);
            Map<String, String> attributes = StringHandler.getAttributesOfTag(wholeTag);
         XMLElement element = new XMLElement(attributes, tag);
         if(stack.size()!=0){
             stack.poll().addChild(element);
         }

          boolean isClosed = StringHandler.isTagClosed(wholeTag);
          if(isClosed){
              continue;
          }

              boolean followedByNewTag = StringHandler.followedByNewTag(content, startIndexKeeper);
              if(followedByNewTag){

              }
              else{
                  String value = StringHandler.findTagValue(content, startIndexKeeper);
                  element.setValue(value);
              }
              stack.push(element);

            System.out.println();
        }
      return xmlRepresentation;
    }
}
