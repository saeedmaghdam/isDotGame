package com.iser.isdotgame;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.text.TextUtils;

public class Box {
    private int row;
    private int col;
    private Dot dot00;
    private Dot dot01;
    private Dot dot10;
    private Dot dot11;
    private Line line1;
    private Line line2;
    private Line line3;
    private Line line4;
    private Paint paint;
    private Rect rect;
    private String color;
    private String owner;

    private boolean line1IsSelected, line2IsSelected, line3IsSelected, line4IsSelected;

    public void setRow(int row){this.row = row;}
    public void setCol(int col){this.col = col;}
    public void setDot00(Dot dot){dot00 = dot;}
    public void setDot01(Dot dot){dot01 = dot;}
    public void setDot10(Dot dot){dot10 = dot;}
    public void setDot11(Dot dot){dot11 = dot;}
    public void setLine1(Line line){line1 = line;}
    public void setLine2(Line line){line2 = line;}
    public void setLine3(Line line){line3 = line;}
    public void setLine4(Line line){line4 = line;}
    public void setColor(String color){
        this.color = color;
        paint.setColor(Color.parseColor(color));
    }
    public void setOwner(String owner){this.owner = owner;}

    public int getRow() {return row;}
    public int getCol() {return col;}
    public Dot getDot00() {return dot00;}
    public Dot getDot01() {return dot01;}
    public Dot getDot10() {return dot10;}
    public Dot getDot11() {return dot11;}
    public Line getLine1() {return line1;}
    public Line getLine2() {return line2;}
    public Line getLine3() {return line3;}
    public Line getLine4() {return line4;}
    public Paint getPaint() {
        return paint;
    }
    public Rect getRect() {
        return rect;
    }
    public boolean getLine1IsSelected(){return line1IsSelected;}
    public boolean getLine2IsSelected(){return line2IsSelected;}
    public boolean getLine3IsSelected(){return line3IsSelected;}
    public boolean getLine4IsSelected(){return line4IsSelected;}
    public String getColor(){return color;}
    public String getOwner(){return owner;}

    public Box(int row, int col, Line line1, Line line2, Line line3, Line line4){
        this.row = row;
        this.col = col;
        this.dot00 = line1.startDot;
        this.dot01 = line1.endDot;
        this.dot10 = line3.startDot;
        this.dot11 = line3.endDot;
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.line4 = line4;
        paint = new Paint();
        color = "#F7F7F7";
        paint.setColor(Color.parseColor(color));
//        paint.setAlpha(100);
//        paint.setStyle(Paint.Style.FILL);
        rect = new Rect(Math.round(dot00.getColCord()), Math.round(dot00.getRowCord()), Math.round(dot01.getColCord()), Math.round(dot10.getRowCord())  );

        //Draw transparent shape
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
//        canvas.drawRoundRect(circleRect, radius, radius, paint);
    }

    public void updateLine(Line line){
        if (getLine1().equals(line)) {
            setLine1(line);
            line1IsSelected = true;
        }
        else if (getLine2().equals(line)) {
            setLine2(line);
            line2IsSelected = true;
        }
        else if (getLine3().equals(line)) {
            setLine3(line);
            line3IsSelected = true;
        }
        else if (getLine4().equals(line)) {
            setLine4(line);
            line4IsSelected = true;
        }
    }
}
