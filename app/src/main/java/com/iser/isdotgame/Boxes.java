package com.iser.isdotgame;

import java.util.ArrayList;
import java.util.List;

public class Boxes {
    private List<Box> boxes;
    private int rows = 0;
    private int cols = 0;
    private int i = 1;
    private boolean oddRow = true;
    private static final String colorA = "#eedbff";
    private static final String colorB = "#f4ebfc";

    public Boxes(int rows, int cols){
        this.rows = rows;
        this.cols = cols;
        boxes = new ArrayList<Box>();
    }

    public void Add(Box box){
        String color = "";
        if (rows % 2 == 0) {
            if (boxes.size() % 2 == 0)
                color = colorA;
            else
                color = colorB;
        }
        else{
            if (oddRow){
                if (boxes.size() % 2 == 0)
                    color = colorA;
                else
                    color = colorB;
            } else {
                if (boxes.size() % 2 == 0)
                    color = colorB;
                else
                    color = colorA;
            }
            if (i++ % (rows - 1) == 0) {
                oddRow = !oddRow;
            }
        }

        Box temp = box;
        temp.setColor(color);
        boxes.add(temp);
    }

    private int mod(int x, int y)
    {
        int result = x % y;
        return result < 0? result + y : result;
    }

    public Box getBox(int row, int col){
        Box result = null;
        for (Box box : boxes){
            if (box.getRow() == row && box.getCol() == col) {
                result = box;
                break;
            }
        }

        return result;
    }

    public Box getBox(int index){
        int row = index / (cols - 1);
        int col = index % (cols - 1);

        return getBox(row, col);
    }

    public List<Box> getBoxes(Line line){
        List<Box> result = new ArrayList<Box>();
        for (Box box : boxes){
            if (box.getLine1().equals(line) || box.getLine2().equals(line) || box.getLine3().equals(line) || box.getLine4().equals(line)) {
                result.add(box);
            }
        }

        return result;
    }

    public List<Box> getList(){return boxes;}
}
