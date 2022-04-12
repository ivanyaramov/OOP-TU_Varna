package com.company;

import com.company.exceptions.FileNotOpenedException;

import java.io.*;

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

    public String readFile() throws FileNotOpenedException, IOException {
        if(isFileOpened()){
            StringBuilder resultStringBuilder = new StringBuilder();
            InputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = br.readLine()) != null) {
                    resultStringBuilder.append(line).append("\n");
                }

            return resultStringBuilder.toString();
        }
        else{
            throw new FileNotOpenedException("File  must be opened before reading");
        }
    }

    public String getPath() {
        return path;
    }


}
