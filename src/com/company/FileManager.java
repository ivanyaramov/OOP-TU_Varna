package com.company;

import com.company.exceptions.FileNotOpenedException;

import java.io.*;

//клас, който се използва за работа със файлове
public class FileManager {
    //текущия отворен файл
    private File file;
    //целия път към текущия файл
    private String path;
    //репрезентацията на текущия файл под формата на java обекти
    private XMLRepresentation xmlRepresentation;

    //отваряне на файл и ако не съществува се създава такъв
    //след като се отвори съдържанието му се парсира под формата на обекти
    public void openFile(String path) throws IOException, FileNotOpenedException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        this.path = path;
        this.file = file;
        xmlRepresentation = XMLHandler.convertStringToXMLObjects(readFile());
        XMLHandler.setUnDublicatingIdsToElements(xmlRepresentation);
    }

    //проверява дали вече няма отворен файл
    public boolean isFileOpened() {
        if (this.file == null) {
            return false;
        }
        return true;
    }

    //затвяря файла
    public void closeFile() {
        this.file = null;
    }

    //записва съдържанието в отворения файл
    public void saveFile(String content) throws IOException {
        saveFileAs(content, this.path);
    }

    //записва съдържанието в подаден файл
    public void saveFileAs(String content, String path) throws IOException {
        FileWriter myWriter = new FileWriter(path);
        myWriter.write(content);
        myWriter.close();
    }

    //чете съдържанието на файла и връща текст
    public String readFile() throws FileNotOpenedException, IOException {
        if (isFileOpened()) {
            StringBuilder resultStringBuilder = new StringBuilder();
            InputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
            String toReturn = resultStringBuilder.toString();
            toReturn = toReturn.replace("\n", "");
            toReturn = toReturn.replace("  ", "");
            return toReturn;
        } else {
            throw new FileNotOpenedException("File  must be opened before reading");
        }
    }

    public String getPath() {
        return path;
    }

    public XMLRepresentation getXmlRepresentation() {
        return xmlRepresentation;
    }
}
