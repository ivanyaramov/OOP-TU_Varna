package com.company;

import java.io.File;
import java.io.IOException;

public class FileManager {
    private File file;

    public void openFile(String path) throws IOException {
    File file = new File(path);
    if(!file.exists()){
        file.createNewFile();
    }
    else{
        System.out.println("exists");
    }
    }

}
