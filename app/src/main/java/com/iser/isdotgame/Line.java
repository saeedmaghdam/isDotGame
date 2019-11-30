package com.iser.isdotgame;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

public class Line {
    Dot startDot, endDot;

    private Paint paint;
    private Path path;
    private boolean isSelected = false;
    private String orientation;
    private int index;
    private String owner;

    public boolean getIsSelected(){return isSelected;}
    public void setIsSelected(boolean val){isSelected = val;}
    public String getOrientation(){return orientation;}
    public String getOwner(){return owner;}

    public void setOwner(String owner){this.owner = owner;}
//    public void setOrientation(String val){orientation = val;}

    public Line(int index, Dot startDot, Dot endDot) {
        paint = new Paint();
        path = new Path();
        setIndex(index);

        this.startDot = startDot;
        this.endDot = endDot;

        paint.setAntiAlias(true);
        paint.setStrokeWidth(8);
        paint.setColor(Color.BLACK);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(Color.TRANSPARENT);

        path.moveTo(startDot.getColCord(), startDot.getRowCord());
        path.lineTo(endDot.getColCord(), endDot.getRowCord());

        if (startDot.getRow() == endDot.getRow())
            orientation = "h";
        else if (startDot.getCol() == endDot.getCol())
            orientation = "v";
    }

    public Path getPath() {
        return path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setColor(int color){
        paint.setColor(color);
    }

//    public void reset(){
//        path.reset();
//        path.moveTo(startDot.getColCord(), startDot.getRowCord());
//        path.lineTo(endDot.getColCord(), endDot.getRowCord());
//    }

    public int getIndex() {return index;}
    public void setIndex(int index){this.index = index;}

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Line)) {
            return false;
        }

        Line line = (Line)o;

        boolean result = false;
        if ((startDot.getRowCord() == line.startDot.getRowCord())
                && (startDot.getColCord() == line.startDot.getColCord())
                && (endDot.getRowCord() == line.endDot.getRowCord())
                && (endDot.getColCord() == line.endDot.getColCord()))
            result = true;

        return result;
    }
}
