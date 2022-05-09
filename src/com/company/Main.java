package com.company;

import com.company.exceptions.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    //метод, от който се стартира програмата
    public static void main(String[] args) throws Exception {
        CommandHandler commandHandler = new CommandHandler();
        commandHandler.manageCommands();

    }
}
