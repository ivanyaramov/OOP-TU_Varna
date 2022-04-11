package com.company;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
String path = "C:\\Temp\\file.xml";
        System.out.println(StringHandler.trimFileName(path));
    }
}
