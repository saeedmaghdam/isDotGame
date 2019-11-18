package com.iser.isdotgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DotBoxGame extends View {
    Context context;
    private int rows, cols, r, space;
    private float TOUCH_TOLERANCE = 0;
    private float TOUCH_TOLERANCE_SMALL = 0;
    private float TOUCH_TOLERANCE_LARGE = 0;
    private Dot[][] dots;
    private Lines lines;
    Line currentLine = null;
    boolean isInitialized = false;
    boolean isGameFinished = false;
    boolean isSinglePlayerMode = true;

    private static final String ALineColor = "#32b92d";
    private static final String ABoxColor = "#54d04f";
    private static final String BLineColor = "#ffcb00";
    private static final String BBoxColor = "#ffe066";
    private static final String DotColor = "#4349b9";

    private Paint paint = new Paint();
    private Path path = new Path();

    private int gameSessionId;
    private Helper helper;
    private boolean isItMyTurn = false;

    Canvas canvas;

    private Boxes boxes;

    public void setGameSessionId(int gameSessionId){
        this.gameSessionId = gameSessionId;
    }
//    public void setHubConnection(HubConnection hubConnection) { this.hubConnection = hubConnection; }
    public void setIsItMyTurn(boolean isItMyTurn) {
        this.isItMyTurn = isItMyTurn;
    }

    public DotBoxGame(Context context, boolean isSinglePlayerMode, int gameSessionId, Helper helper) {
        super(context);
        this.context = context;

        rows = 7;
        cols = 7;

        this.isSinglePlayerMode = isSinglePlayerMode;

        lines = new Lines();
        boxes = new Boxes(rows, cols);

        dots = new Dot[rows][cols];

        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

//        this.hubConnection = hubConnection;
        this.gameSessionId = gameSessionId;
        this.helper = helper;

        if (!isSinglePlayerMode) {
////            hubConnection.stop();
//
//            this.priorAvtivityHubConnection = hubConnection;
//            this.hubConnection = HubConnectionBuilder.create("http://192.168.1.253:54343/mainhub").build();
//
//            this.hubConnection.on("ItsMyTurn", () -> {
//                isItMyTurn = true;
//            });
        }

//        if (this.hubConnection != null) {
//            if (this.hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
//                this.hubConnection.start().blockingAwait();
//
////            hubConnection.on("ItsMyTurn", () -> {
////                isItMyTurn = true;
////            });
//        }
    }

    public void GameIsStarted(){
//        this.priorAvtivityHubConnection.stop().blockingAwait();
//        if (this.hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
//        {
//            this.hubConnection.start().blockingAwait();
//        }
//        if (this.hubConnection.getConnectionState() == HubConnectionState.CONNECTED)
//            this.hubConnection.send("IsItMyTurn");
    }

    private void drawLayout(){
        // Draw Boxes
        for (Box box : boxes.getList()){
//            if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()){
//                box.getPaint().setColor(Color.parseColor("#8bd9e4"));
//            }

            canvas.drawRect(box.getRect(), box.getPaint());
        }

        // Draw lines
        for (Line line : lines.getList()) {
                canvas.drawPath(line.getPath(), line.getPaint());
        }

        // Draw dots
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Dot dot = dots[row][col];

                Paint paint = new Paint();
                // PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.parseColor(DotColor));
                paint.setAlpha(255);
                // paint.setXfermode(xfermode);
                paint.setAntiAlias(true);
                // setBackgroundColor(Color.BLACK);
                canvas.drawCircle(dot.getColCord(), dot.getRowCord(), r, paint);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;

        if (!isInitialized) {
            isInitialized = true;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;

            r = width / 100;
            TOUCH_TOLERANCE_SMALL = 1 * r;
            TOUCH_TOLERANCE = 2 * r;
            TOUCH_TOLERANCE_LARGE = 3 * r;
            space = width / (cols + 1);

            // Calculate and prepare the dots
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int rowCord, colCord;
                    colCord = space * col + space;
                    rowCord = space * row + space;
                    dots[row][col] = new Dot(rowCord, colCord, row, col);
                }
            }

            int index = 0;

            // Calculate horizontal lines
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols - 1; col++) {
                    lines.Add(new Line(index++, dots[row][col], dots[row][col + 1]));
                }
            }

            // Calculate vertical lines
            for (int row = 0; row < rows - 1; row++) {
                for (int col = 0; col < cols; col++) {
                    lines.Add(new Line(index++, dots[row][col], dots[row + 1][col]));
                }
            }

            // Calculate boxes
            for (int row = 0; row < rows - 1; row++){
                for (int col = 0; col < cols - 1; col++){
                    Line line1 = lines.getLine(row + 0, col + 0, row + 0, col + 1);
                    Line line2 = lines.getLine(row + 0, col + 1, row + 1, col + 1);
                    Line line3 = lines.getLine(row + 1, col + 0, row + 1, col + 1);
                    Line line4 = lines.getLine(row + 0, col + 0, row + 1, col + 0);

                    boxes.Add(new Box(row, col, line1, line2, line3, line4));
                }
            }

            drawLayout();
        }
        else{
            // Re-draw
            drawLayout();
        }
    }

    private void doNextStep(Line line){
        if (!isGameFinished) {
            checkIfPcCanFillAnyEmptyBox(line);
            checkAllBoxesIfPcCanFillAnyEmptyBox();

            List<Integer> indexes = new ArrayList<Integer>();
            for (int i = 0; i < (rows - 1) * (cols - 1); i++) {
                indexes.add(i);
            }
            Collections.shuffle(indexes);

            for (int i = 0; i < indexes.size(); i++) {
                List<Integer> lineIndexes = new ArrayList<Integer>();
                for (int j = 1; j <= 4; j++) {
                    lineIndexes.add(j);
                }
                Collections.shuffle(lineIndexes);

                Box box = boxes.getBox(indexes.indexOf(i));
                int row = box.getRow();
                int col = box.getCol();
                boolean canISelectIt = false;

                for (int j = 0; j < lineIndexes.size(); j++) {
                    switch (j) {
                        case 1:
                            canISelectIt = false;
                            Line line1 = box.getLine1();
                            if (!line1.getIsSelected()) {
                                Box topBox = boxes.getBox(row - 1, col);
                                if (topBox != null) {
                                    int selectedCount = 0;
                                    if (topBox.getLine1IsSelected())
                                        selectedCount++;
                                    if (topBox.getLine2IsSelected())
                                        selectedCount++;
                                    if (topBox.getLine4IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }

                                Box bottomBox = boxes.getBox(row + 1, col);
                                if (bottomBox != null) {
                                    int selectedCount = 0;
                                    if (bottomBox.getLine2IsSelected())
                                        selectedCount++;
                                    if (bottomBox.getLine3IsSelected())
                                        selectedCount++;
                                    if (bottomBox.getLine4IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }
                            }
                            if (canISelectIt) {
                                line1.setIsSelected(true);
                                line1.getPaint().setColor(Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line1)) {
                                    box1.updateLine(line1);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }
                            break;
                        case 2:
                            canISelectIt = false;
                            Line line2 = box.getLine2();
                            if (!line2.getIsSelected()) {
                                Box leftBox = boxes.getBox(row, col - 1);
                                if (leftBox != null) {
                                    int selectedCount = 0;
                                    if (leftBox.getLine1IsSelected())
                                        selectedCount++;
                                    if (leftBox.getLine3IsSelected())
                                        selectedCount++;
                                    if (leftBox.getLine4IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }

                                Box rightBox = boxes.getBox(row, col + 1);
                                if (rightBox != null) {
                                    int selectedCount = 0;
                                    if (rightBox.getLine1IsSelected())
                                        selectedCount++;
                                    if (rightBox.getLine2IsSelected())
                                        selectedCount++;
                                    if (rightBox.getLine3IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }
                            }
                            if (canISelectIt) {
                                line2.setIsSelected(true);
                                line2.getPaint().setColor(Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line2)) {
                                    box1.updateLine(line2);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }
                            break;
                        case 3:
                            canISelectIt = false;
                            Line line3 = box.getLine3();
                            if (!line3.getIsSelected()) {
                                Box topBox = boxes.getBox(row - 1, col);
                                if (topBox != null) {
                                    int selectedCount = 0;
                                    if (topBox.getLine1IsSelected())
                                        selectedCount++;
                                    if (topBox.getLine2IsSelected())
                                        selectedCount++;
                                    if (topBox.getLine4IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }

                                Box bottomBox = boxes.getBox(row + 1, col);
                                if (bottomBox != null) {
                                    int selectedCount = 0;
                                    if (bottomBox.getLine2IsSelected())
                                        selectedCount++;
                                    if (bottomBox.getLine3IsSelected())
                                        selectedCount++;
                                    if (bottomBox.getLine4IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }
                            }
                            if (canISelectIt) {
                                line3.setIsSelected(true);
                                line3.getPaint().setColor(Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line3)) {
                                    box1.updateLine(line3);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }
                            break;
                        case 4:
                            canISelectIt = false;
                            Line line4 = box.getLine4();
                            if (!line4.getIsSelected()) {
                                Box leftBox = boxes.getBox(row, col - 1);
                                if (leftBox != null) {
                                    int selectedCount = 0;
                                    if (leftBox.getLine1IsSelected())
                                        selectedCount++;
                                    if (leftBox.getLine3IsSelected())
                                        selectedCount++;
                                    if (leftBox.getLine4IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }

                                Box rightBox = boxes.getBox(row, col + 1);
                                if (rightBox != null) {
                                    int selectedCount = 0;
                                    if (rightBox.getLine1IsSelected())
                                        selectedCount++;
                                    if (rightBox.getLine2IsSelected())
                                        selectedCount++;
                                    if (rightBox.getLine3IsSelected())
                                        selectedCount++;

                                    if (selectedCount <= 1)
                                        canISelectIt = true;
                                } else {
                                    canISelectIt = true;
                                }
                            }
                            if (canISelectIt) {
                                line4.setIsSelected(true);
                                line4.getPaint().setColor(Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line4)) {
                                    box1.updateLine(line4);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }
                            break;
                    }
                    if (canISelectIt) {
                        break;
                    }
                }

                if (canISelectIt) {
                    break;
                }
            }
        }
    }

    private void CheckIfGameIsFinished(){
        boolean isFinishedYet = true;
        for(Box box : boxes.getList()){
            if (TextUtils.isEmpty(box.getOwner())){
                isFinishedYet = false;
                break;
            }
        }

        if (isFinishedYet){
//            String message = "شما برنده بازی شدید!";
//            MyDialog myDialog = new MyDialog(message);
//            myDialog.show(fragmentManager, "example dialog");
            Toast toast=Toast.makeText(this.context,"Hello Javatpoint",Toast.LENGTH_SHORT);
            toast.setMargin(50,50);
            toast.show();
        }
    }

    private void checkAllBoxesIfPcCanFillAnyEmptyBox(){
        for(Box box : boxes.getList()){
            int selectedCount = 0;
            if (box.getLine1IsSelected())
                selectedCount++;
            if (box.getLine2IsSelected())
                selectedCount++;
            if (box.getLine3IsSelected())
                selectedCount++;
            if (box.getLine4IsSelected())
                selectedCount++;

            if (selectedCount == 3) { // It can be selected
                Line currentLine = null;
                if (!box.getLine1IsSelected())
                    currentLine = box.getLine1();
                else if (!box.getLine2IsSelected())
                    currentLine = box.getLine2();
                else if (!box.getLine3IsSelected())
                    currentLine = box.getLine3();
                else if (!box.getLine4IsSelected())
                    currentLine = box.getLine4();

                currentLine.setIsSelected(true);
                currentLine.getPaint().setColor(Color.parseColor(BLineColor));
                for (Box box1 : boxes.getBoxes(currentLine)) {
                    box1.updateLine(currentLine);
                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                        box.setOwner("2");
                    }
                }

                checkIfPcCanFillAnyEmptyBox(currentLine);
            }
        }
    }

    private void checkIfPcCanFillAnyEmptyBox(Line line){
        for(Box box : boxes.getBoxes(line)){
            int selectedCount = 0;
            if (box.getLine1IsSelected())
                selectedCount++;
            if (box.getLine2IsSelected())
                selectedCount++;
            if (box.getLine3IsSelected())
                selectedCount++;
            if (box.getLine4IsSelected())
                selectedCount++;

            if (selectedCount == 3) { // It can be selected
                Line currentLine = null;
                if (!box.getLine1IsSelected())
                    currentLine = box.getLine1();
                else if (!box.getLine2IsSelected())
                    currentLine = box.getLine2();
                else if (!box.getLine3IsSelected())
                    currentLine = box.getLine3();
                else if (!box.getLine4IsSelected())
                    currentLine = box.getLine4();

                currentLine.setIsSelected(true);
                currentLine.getPaint().setColor(Color.parseColor(BLineColor));
                for (Box box1 : boxes.getBoxes(currentLine)) {
                    box1.updateLine(currentLine);
                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                        box.setOwner("2");
                    }
                }

                checkIfPcCanFillAnyEmptyBox(currentLine);
            } else if (selectedCount == 4){
                if (TextUtils.isEmpty(box.getOwner())){
                    box.getPaint().setColor(Color.parseColor(BBoxColor));
                    box.setOwner("2");
                }
            }
        }
    }

    private void touch_start(float x, float y) {
        if (!isGameFinished) {
            boolean isItMyTurn = false;
            for (Line line : lines.getList()) {
                if (line.startDot.getColCord() == line.endDot.getColCord()) {
                    // Line is horizontal
                    if ((y > line.startDot.getRowCord() + TOUCH_TOLERANCE)
                            && (x > line.startDot.getColCord() - TOUCH_TOLERANCE_LARGE)
                            && (y < line.endDot.getRowCord() - TOUCH_TOLERANCE)
                            && (x < line.endDot.getColCord() + TOUCH_TOLERANCE_LARGE)
                    )
                        currentLine = line;
                } else {
                    // Line is vertical
                    if ((y > line.startDot.getRowCord() - TOUCH_TOLERANCE_LARGE)
                            && (x > line.startDot.getColCord() + TOUCH_TOLERANCE)
                            && (y < line.endDot.getRowCord() + TOUCH_TOLERANCE_LARGE)
                            && (x < line.endDot.getColCord() - TOUCH_TOLERANCE)
                    )
                        currentLine = line;
                }

                if (currentLine != null) {
                    if (!currentLine.getIsSelected()) {
                        currentLine.setIsSelected(true);
                        currentLine.getPaint().setColor(Color.parseColor(ALineColor));
                        for (Box box : boxes.getBoxes(currentLine)) {
                            box.updateLine(currentLine);
                            if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                isItMyTurn = true;
                                box.getPaint().setColor(Color.parseColor(ABoxColor));
                                box.setOwner("1");
                            }
                        }

                        if (!isItMyTurn) {
                            if (isSinglePlayerMode) {
                                CheckIfGameIsFinished();
                                doNextStep(currentLine);
                                CheckIfGameIsFinished();
                            } else if (!isSinglePlayerMode) {
                                // Send the selected lines infor to the competitor
//                                hubConnection.send("IPlayedMyTurn", gameSessionId, helper.getUserUniqueId(), helper.getUsername(), currentLine.getIndex());
                                this.isItMyTurn = false;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void touch_move(float x, float y) {

    }

    private void touch_up(float x, float y) {
        currentLine = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isSinglePlayerMode || (!isSinglePlayerMode && isItMyTurn)) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
//                touch_move(x, y);
//                invalidate();

                    break;
                case MotionEvent.ACTION_UP:
                    touch_up(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }

            return true;
        }
        return false;
    }

//    public static float pxFromDp(final Context context, final float dp) {
//        return dp * context.getResources().getDisplayMetrics().density;
//    }
}
