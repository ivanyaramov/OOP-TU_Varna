package com.company;

public class StringHandler {
    public static String trimFileName(String path){
        String[] arr = path.split("\\\\");
        return arr[arr.length-1];
    }
}
