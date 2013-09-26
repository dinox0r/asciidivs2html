package com.test.ascii2divs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AsciiImage {
    public static final char DEFAULT_HORIZONTAL_WALL_CHAR = '-';
    public static final char DEFAULT_VERTICAL_WALL_CHAR = 'I';    
    public static final char EMPTY_SPACE = ' ';
    
    private int w;
    private int h;
    private char horizontalWall;
    private char verticalWall;
    private List<String> lines;

    AsciiImage() {
        this.w = 0;
        this.h = 0;
        this.horizontalWall = DEFAULT_HORIZONTAL_WALL_CHAR;
        this.verticalWall = DEFAULT_VERTICAL_WALL_CHAR;
        this.lines = new ArrayList<String>();
    }
    
    public void addLine(String line) { 
        if (line.length() > w) { 
            w = line.length();
        }
        lines.add(line);
        h++;
    }

    public int width() {
        return w;
    }
    
    public int height() {
        return h;
    }

    public char getHorizontalWall() {
        return horizontalWall;
    }

    public void setHorizontalWall(char horizontalWall) {
        this.horizontalWall = horizontalWall;
    }

    public char getVerticalWall() {
        return verticalWall;
    }

    public void setVerticalWall(char verticalWall) {
        this.verticalWall = verticalWall;
    }    
    
    public void loadAsciiImageFile(String asciiImageFilePath) throws IOException, FileNotFoundException {
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(asciiImageFilePath));
        
        String line;
        while ((line = reader.readLine()) != null) {
            addLine(line);
        }
        reader.close();
    }    
    
    public char at(int x, int y) {
        if (x < 0) {
            return 0;
        }
        if (y < 0) {
            return 0;
        }
        if (y > h) {
            return 0;
        }
        
        String line = lines.get(y);
        int len = line.length();
        if (x >= len) {
            return 0;
        }
        
        return line.charAt(x);
    }
}
