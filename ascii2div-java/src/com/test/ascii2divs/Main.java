package com.test.ascii2divs;

import java.io.File;
import java.io.IOException;

public class Main {
    private static void checkArguments(String... args) {
        if (args.length != 1) { 
            System.err.println("Usage: java Main <ascii-file-path>");
            System.exit(1);
        }
        
        checkFileArgument(args);
    }
    
    private static void checkFileArgument(String... args) {
        File file = new File(args[0]);
        if (!file.exists()) { 
            System.err.println("The file passed as parameter does not exists!");
            System.exit(1);            
        }        
    }
    
    public static void main(String... args) throws IOException { 
        checkArguments(args);

        AsciiImage asciiImage = new AsciiImage();
        asciiImage.loadAsciiImageFile(args[0]);
        
        AsciiToDivGenerator asciiToDivGenerator = new AsciiToDivGenerator(asciiImage);
        
        asciiToDivGenerator.solveAndPrintDivs();
    }
}
