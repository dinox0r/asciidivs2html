package com.test.ascii2divs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsciiToDivGenerator {
    private char[][] visited;
    private AsciiImage img;
   
    public AsciiToDivGenerator(AsciiImage img) {
        this.img = img;
        
        // Primitive type arrays are automagically initialized with 0
        this.visited = new char[img.width()][img.height()];
    }

    private void resetVisitedArray() {
        for (char[] scanLine : visited) {
            Arrays.fill(scanLine, (char) ' ');        }
    }
    
    private void floodFill(int x, int y, Div bestRect) { 
        if (x < 0 || x >= img.width() || y < 0 || y >= img.height()) {
            return;
        }
    
        if (visited[x][y] != ' ') {
            // Element already visited
            return;
        }
    
        final char VERTICAL_WALL = img.getVerticalWall();
        final char HORIZONTAL_WALL = img.getHorizontalWall();
    
        boolean recurseLeft = true;
        boolean recurseRight = true;
        boolean recurseTop = true;
        boolean recurseBottom = true;
    
        if (img.at(x + 1, y) == VERTICAL_WALL) {
            if (x + 1 > bestRect.getXf()) {
                bestRect.setXf(x + 1);
            }
            recurseRight = false;
        }
        if (img.at(x + 1, y) == HORIZONTAL_WALL) {
            recurseRight = false;
        }

        if (img.at(x - 1, y) == VERTICAL_WALL) {
            if (x - 1 < bestRect.getX0()) {
                bestRect.setX0(x - 1);
            }
            recurseLeft = false;
        }
        if (img.at(x - 1, y) == HORIZONTAL_WALL) {
            recurseLeft = false;
        }    

        if (img.at(x, y - 1) == HORIZONTAL_WALL) {
            if (y - 1 < bestRect.getY0()) {
                bestRect.setY0(y - 1);
            }
            recurseTop = false;
        }        
        if (img.at(x, y - 1) == VERTICAL_WALL) {
            recurseTop = false;
        }    

        if (img.at(x, y + 1) == HORIZONTAL_WALL) {
            if (y + 1 > bestRect.getYf()) {
                bestRect.setYf(y + 1);
            }
            recurseBottom = false;
        }
        if (img.at(x, y + 1) == VERTICAL_WALL) {
            recurseBottom = false;
        }    
        visited[x][y] = bestRect.getId();    

        if (recurseLeft) {
            floodFill(x - 1, y, bestRect);
        }
        if (recurseRight)  {
            floodFill(x + 1, y, bestRect);
        }
        if (recurseTop){
            floodFill(x, y - 1, bestRect);
        }
        if (recurseBottom) {
            floodFill(x, y + 1, bestRect);
        }            
    }

    private Div findDivById(char id, List<Div> divs) {
        for (Div div : divs) {
            if (div.getId() == id) { 
                return div;
            }
        }
        return null;
    }    
    
    private void printNode(Div node, String tab) {
        System.out.printf("%s<div>\n", tab);
        if (node.getId() != ' ') {
            System.out.printf("%1$s%1$s%2$c\n", tab, node.getId());
        }
        
        for (Div child : node.getChildren()) {
            printNode(child, tab + "   ");
        }
        System.out.printf("%s</div>\n", tab);
    }

    private void printNode(Div root) {    
        printNode(root, "");
    }
    
    public void solveAndPrintDivs() { 
        List<Pair<Integer, Integer>> notWallChars = getNotWallCharsCoordinates();
        
        resetVisitedArray();
        
        List<Div> divs = new ArrayList<Div>();
        for (Pair<Integer, Integer> xy : notWallChars) {
            char id = img.at(xy.getElement0(), xy.getElement1());
            Div bestDiv = new Div(id);
            floodFill(xy.getElement0(), xy.getElement1(), bestDiv);
            divs.add(bestDiv);            
        }
        
        Div root = new Div(' ', 0, 0, img.width(), img.height());
        
        for (Div div : divs) { 
            char idOfParent = div.searchEnclosingDiv(visited, img.width(), img.height());
            
            if (idOfParent != ' ') { 
                Div parentDiv = findDivById(idOfParent, divs);
                parentDiv.getChildren().add(div);
            } else {
                root.getChildren().add(div);
            } 
        }
        
        printNode(root);
    }

    private List<Pair<Integer, Integer>> getNotWallCharsCoordinates() {
        List<Pair<Integer, Integer>> notWallChars = new ArrayList<Pair<Integer, Integer>>();

        final char EMPTY_SPACE = AsciiImage.EMPTY_SPACE;
        final char VERTICAL_WALL = img.getHorizontalWall();
        final char HORIZONTAL_WALL = img.getVerticalWall();
        final char wallCharacters[] = { VERTICAL_WALL, HORIZONTAL_WALL };
        
        // First scan the image searching for those non wall characters
        int w = img.width();
        int h = img.height();
        
        for (int y = 0; y < h; y++) { 
            for (int x = 0; x < w; x++) {
                char asciiPixel = img.at(x, y);
                if (asciiPixel == EMPTY_SPACE) { 
                    continue;
                }

                boolean isWallChar = false;
                for (char c : wallCharacters) {
                    if (asciiPixel == c) {
                        isWallChar = true;
                        break;
                    }
                }
                if (!isWallChar) {
                    notWallChars.add(Pair.createPair(x, y));
                }
            }
        }
        
        return notWallChars;
    }
}
