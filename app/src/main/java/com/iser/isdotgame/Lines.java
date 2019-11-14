package com.iser.isdotgame;

import java.util.ArrayList;
import java.util.List;

public class Lines {
    private List<Line> lines;

    public Lines(){
        lines = new ArrayList<Line>();
    }

    public void Add(Line line){
        lines.add(line);
    }

    public Line getLine(Dot startDot, Dot endDot){
        Line result = null;
        for (Line line : lines){
            if (line.startDot.equals(startDot) && line.endDot.equals(endDot)) {
                result = line;
                break;
            }
        }

        return result;
    }

    public Line getLine(int startDotRow, int startDotCol, int endDotRow, int endDotCol){
        Line result = null;
        for (Line line : lines){
            if (line.startDot.getRow() == startDotRow && line.startDot.getCol() == startDotCol
                    && line.endDot.getRow() == endDotRow && line.endDot.getCol() == endDotCol) {
                result = line;
                break;
            }
        }

        return result;
    }

    public List<Line> getList(){return lines;}
}
