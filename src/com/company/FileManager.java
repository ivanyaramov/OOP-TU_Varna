package com.company;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileManager {
    private File file;
    private String path;

    public void openFile(String path) throws IOException {
    File file = new File(path);
    if(!file.exists()){
        file.createNewFile();
    }
    this.path = path;
    this.file = file;
    }

    public boolean isFileOpened(){
        if(this.file == null){
            return false;
        }
        return true;
    }

    public void closeFile(){
        this.file = null;
    }

    public void saveFile(String content) throws IOException {
       saveFileAs(content, this.path);
    }

    public void saveFileAs(String content, String path) throws IOException {
        FileWriter myWriter = new FileWriter(path);
        myWriter.write(content);
        myWriter.close();
    }

    public String getPath() {
        return path;
    }


}
