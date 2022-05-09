package com.company;

//класа се ползва, за да може да се подава текущия индекс (до който е стигнала програмата докато парсира xml-a) по референция
public class StartIndexKeeper {
    private Integer startIndex;

    public StartIndexKeeper(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }
}
