package com.iser.isdotgame;

public class Dot {
    private float rowCord, colCord;
    private int row, col;

    public Dot(float rowCord, float colCord, int row, int col){
        this.rowCord = rowCord;
        this.colCord = colCord;
        this.row = row;
        this.col = col;
    }

    public float getRowCord(){return rowCord;}
    public float getColCord(){return colCord;}
    public float getRow(){return row;}
    public float getCol(){return col;}
}
