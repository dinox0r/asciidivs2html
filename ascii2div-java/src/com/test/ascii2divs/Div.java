package com.test.ascii2divs;

import java.util.ArrayList;
import java.util.List;

import static com.test.ascii2divs.Utils.*;

public class Div {
    private char id;
    private int x0;
    private int y0;
    private int xf;
    private int yf;
    private List<Div> children;
    
    Div(char id) {
        this.id = id;
        x0 = Integer.MAX_VALUE;
        y0 = Integer.MAX_VALUE;
        xf = Integer.MIN_VALUE;
        yf = Integer.MIN_VALUE;
        children = new ArrayList<Div>();
    }
    
    Div(char id, int x0, int y0, int xf, int yf) { 
        this.id = id;
        this.x0 = x0;
        this.y0 = y0;
        this.xf = xf;
        this.yf = yf;
        children = new ArrayList<Div>();
    }
    
    public char searchEnclosingDiv(char[][] visited, int w, int h) {
        int offsetX[] = { x0 - 1, x0    , xf + 1, xf };
        int offsetY[] = { y0    , y0 - 1, yf    , yf + 1 };
        for (int i = 0; i < 4; i++) { 
            int x = offsetX[i];
            int y = offsetY[i];
            
            
            x = clamp(x, 0, w);
            y = clamp(y, 0, h);
            
            if (visited[x][y] != ' ') { 
                return visited[x][y];
            }
        }
        return ' ';        
    }
    
    public char getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    public int getX0() {
        return x0;
    }

    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getY0() {
        return y0;
    }

    public void setY0(int y0) {
        this.y0 = y0;
    }

    public int getXf() {
        return xf;
    }

    public void setXf(int xf) {
        this.xf = xf;
    }

    public int getYf() {
        return yf;
    }

    public void setYf(int yf) {
        this.yf = yf;
    }

    public List<Div> getChildren() {
        return children;
    }

    public void setChildren(List<Div> children) {
        this.children = children;
    }    

    @Override
    public String toString() {
        return String.format("{ id = %c, coordinates = [%d, %d], [%d, %d] }",  id, x0, y0, xf, yf);
    }
}