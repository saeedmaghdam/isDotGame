package com.iser.isdotgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class DotBoxGame extends View {
    Context context;
    private int rows, cols, r, space;
    private float TOUCH_TOLERANCE = 0;
    private float TOUCH_TOLERANCE_SMALL = 0;
    private float TOUCH_TOLERANCE_LARGE = 0;
    private Dot[][] dots;
    private Lines lines;
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

    private String gameSessionViewId;
    private Helper helper;
    private boolean isItMyTurn = false;

    Canvas canvas;

    private Boxes boxes;

    HubConnection hubConnection;

    String userViewId;
    BaseActivity activity;
    View player1Line;
    View player2Line;
    TextView rate;
    TextView coins;

    int shadowNormalRadius = 7;
    int shadowDotRadius = 10;
    int shadowDeepRadius = 20;
    Line lastLineDrawn;

    SignalRService service;
    Handler handler;

    private SoundPlayer soundPlayer;

    private Line currentLine;
    private int lineIndexInDoNextStep;
    private boolean canISelectItInDoNextStep;

    public void setIsItMyTurn(boolean isItMyTurn) {
        this.isItMyTurn = isItMyTurn;
    }

    public DotBoxGame(Context context, boolean isSinglePlayerMode, String gameSessionId, Helper helper, HubConnection hubConnection, Handler handler) {
        super(context);
        this.context = context;
        this.handler = handler;
        activity = (BaseActivity) context;
        player1Line = (View)activity.findViewById(R.id.player1Line);
        player2Line = (View)activity.findViewById(R.id.player2Line);
        rate = (TextView) activity.findViewById(R.id.rate);
        coins = (TextView)activity.findViewById(R.id.coins);
        rows = 6;
        cols = 6;

        this.isSinglePlayerMode = isSinglePlayerMode;

        setLayerType(LAYER_TYPE_SOFTWARE, paint);

        lines = new Lines();
        boxes = new Boxes(rows, cols);

        dots = new Dot[rows][cols];

        soundPlayer = new SoundPlayer(context);
//        soundPlayer.playBackgroundSound();

        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((Activity) getContext()).getWindowManager()
                .getDefaultDisplay()
                .getMetrics(displayMetrics);

        this.gameSessionViewId = gameSessionId;
        this.helper = new Helper(context);

        if (!isSinglePlayerMode) {
//            this.hubConnection = HubConnectionBuilder.create(this.helper.getHubUrl()).build();
//            this.hubConnection = hubConnection;
            service = new SignalRService(context, handler);
            activity.setUpdateMethod(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    CheckFields();
                    return null;
                }
            });

//        hubConnection = HubConnectionBuilder.create(this.helper.getHubUrl()).build();
            this.hubConnection = service.getHubConnection();

            this.hubConnection.on("ItsMyTurn", () -> {
//                Line currentLine = null;
//                for (Line line : lines.getList()) {
//                    if (line.getIndex() == selectedLineByCompetitorIndex) {
//                        currentLine = line;
//                        break;
//                    }
//                }
//
//                if (currentLine != null) {
//                    if (!currentLine.getIsSelected()) {
//                        currentLine.setIsSelected(true);
//                        currentLine.getPaint().setColor(Color.parseColor(BLineColor));
//                        for (Box box : boxes.getBoxes(currentLine)) {
//                            box.updateLine(currentLine);
//                            if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
////                                isItMyTurn = true;
//                                box.getPaint().setColor(Color.parseColor(BBoxColor));
//                                box.setOwner("2");
//                            }
//                        }
//                    }
//                }

                isItMyTurn = true;
//                ChangeTurn("A");

//                invalidate();
            });

            this.hubConnection.on("SelectALine", (selectedLineByCompetitorIndex) -> {
//            ((Activity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    soundPlayer.playHit2Sound();
//                    Log.d("ooooooooo", String.valueOf(selectedLineByCompetitorIndex));
//                    Line currentLine = null;
//                    for (Line line : lines.getList()) {
//                        if (line.getIndex() == selectedLineByCompetitorIndex) {
//                            currentLine = line;
//                            Log.d("ooooooooo", "Found a line");
//                            break;
//                        }
//                    }
//
//                    if (currentLine != null) {
//                        Log.d("ooooooooo", "Line is not null!");
//                        lastLineDrawn = currentLine;
//                        if (!currentLine.getIsSelected()) {
//                            Log.d("ooooooooo", "Line is not selected currently");
//                            currentLine.setOwner("2");
//                            currentLine.setIsSelected(true);
//                            currentLine.getPaint().setColor(Color.parseColor(BLineColor));
//                            for (Box box : boxes.getBoxes(currentLine)) {
//                                box.updateLine(currentLine);
//                                if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
////                                isItMyTurn = true;
//                                    Log.d("ooooooooo", "Box founded!");
//                                    box.getPaint().setColor(Color.parseColor(BBoxColor));
//                                    box.setOwner("2");
//                                }
//                            }
//
////                        this.postInvalidate();
//                        }
//                    }
//
//                    postInvalidate();
//                }
//            });





                soundPlayer.playHit2Sound();
//                Log.d("ooooooooo", String.valueOf(selectedLineByCompetitorIndex));
                Line currentLine = null;
                for (Line line : lines.getList()) {
                    if (line.getIndex() == selectedLineByCompetitorIndex) {
                        currentLine = line;
//                        Log.d("ooooooooo", "Found a line");
                        break;
                    }
                }

                if (currentLine != null) {
//                    Log.d("ooooooooo", "Line is not null!");
                    lastLineDrawn = currentLine;
                    if (!currentLine.getIsSelected()) {
//                        Log.d("ooooooooo", "Line is not selected currently");
                        currentLine.setOwner("2");
                        currentLine.setIsSelected(true);
                        currentLine.getPaint().setColor(Color.parseColor(BLineColor));
                        for (Box box : boxes.getBoxes(currentLine)) {
                            box.updateLine(currentLine);
                            if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
//                                isItMyTurn = true;
                                Log.d("ooooooooo", "Box founded!");
                                box.getPaint().setColor(Color.parseColor(BBoxColor));
                                box.setOwner("2");
                            }
                        }

//                        this.postInvalidate();
                    }
                }


                postInvalidate();
//                DotBoxGame.this.invalidate();
//                DotBoxGame.this.drawLayout();
//                this.invalidate();
//                draw(canvas);
            }, Integer.class);

            this.hubConnection.on("WhoAmI", (userViewId) -> {
                this.userViewId = userViewId;
            }, String.class);

            this.hubConnection.on("WhosTurn", (userViewId) -> {
                if (this.userViewId.equals(userViewId)) {
                    isItMyTurn = true;
//                    ChangeTurn("A");
                }
                else {
                    isItMyTurn = false;
//                    ChangeTurn("B");
                }
            }, String.class);

            this.hubConnection.on("Init", (userViewId, firstPlayerViewId, rate, coins) -> {
                this.userViewId = userViewId;

                if (this.userViewId.equals(firstPlayerViewId)) {
                    isItMyTurn = true;
                }
                else {
                    isItMyTurn = false;
                }

                this.rate.setText(rate);
                this.coins.setText(coins);
            }, String.class, String.class, String.class, String.class);
        } else {
            isItMyTurn = true;
            ChangeTurn("A");
        }

        if (!isSinglePlayerMode) {
            if (this.hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                try {
                    this.hubConnection.start().blockingAwait();
                } catch (Exception ex) {
                    helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                }
            }
            if (this.hubConnection.getConnectionState() == HubConnectionState.CONNECTED){
                try {
//                    // Who Am I?
//                    this.hubConnection.send("WhoAmI", helper.getUserUniqueId(), helper.getUsername());
//
//                    // Who's Turn Is It?
//                    this.hubConnection.send("WhosTurn", helper.getUserUniqueId(), helper.getUsername(), this.gameSessionViewId);
                    this.hubConnection.send("Init", helper.getUserUniqueId(), helper.getUsername(), this.gameSessionViewId);
                    helper.Sleep();
                } catch (Exception ex){
                    helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                }
            }
        }
    }

    private void CheckFields(){

    }

    private void ChangeTurn(String player){
//        ((Activity)context).runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        });
        if (!isSinglePlayerMode || 1==1) {
            if (player.toLowerCase().equals("a")) {
                player1Line.setVisibility(VISIBLE);
                player2Line.setVisibility(INVISIBLE);
            } else if (player.toLowerCase().equals("b")) {
                player1Line.setVisibility(INVISIBLE);
                player2Line.setVisibility(VISIBLE);
            }
//            ((MultiPlayingActivity)activity).findViewById(R.id.primaryLayout).invalidate();
//            ((MultiPlayingActivity)activity).findViewById(R.id.primaryLayout).invalidate();
//            player1Line.invalidate();
//            player2Line.invalidate();
        } else {
            player1Line.setVisibility(INVISIBLE);
            player2Line.setVisibility(INVISIBLE);
        }

        this.invalidate();
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

        // Reset line shadow colors
        for (Line line : lines.getList()) {
            if (line.getOwner() != null) {
                if (line.getOwner().equals("1")) {
                    line.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(ALineColor));
                } else {
                    line.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(BLineColor));
                }
            }
        }

        // Draw lines
        for (Line line : lines.getList()) {
            if (lastLineDrawn != null && line.equals(lastLineDrawn)){
                lastLineDrawn.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.RED);
            }
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
//                aapaint.setAlpha(255);
                // paint.setXfermode(xfermode);
                paint.setAntiAlias(true);
                paint.setShadowLayer(shadowDotRadius, 0, 0, Color.parseColor(DotColor));
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
                    rowCord = space * row + (space / 7);
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

        if (!isSinglePlayerMode) {
            if (isItMyTurn)
                ChangeTurn("A");
            else
                ChangeTurn("B");
        }

        CheckIfGameIsFinished();

//        PercentRelativeLayout playersInfoLayout = ((Activity)context).findViewById(R.id.playersInfoLayout);
//        playersInfoLayout.invalidate();

    }

    private void doNextStep(Line line){
        Handler handler1 = new Handler();

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
                canISelectItInDoNextStep = false;

                for (lineIndexInDoNextStep = 0; lineIndexInDoNextStep < lineIndexes.size(); lineIndexInDoNextStep++) {
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            // Actions to do after 1 seconds
//
//
//                        }
//                    }, 1000);
//                    invalidate();
//                    try {
//                        Thread.sleep(500);
//                    }
//                    catch (InterruptedException e){
//                        Log.d("ooooooooooooooooooo", e.getStackTrace().toString());
//                    }

                    boolean isItAndroidsTurn = false;
                    switch (lineIndexInDoNextStep) {
                        case 1:
                            canISelectItInDoNextStep = false;
                            Line line1 = box.getLine1();
                            lastLineDrawn = line1;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
                                }
                            }
                            if (canISelectItInDoNextStep) {
                                line1.setOwner("2");
                                line1.setIsSelected(true);
                                line1.getPaint().setColor(Color.parseColor(BLineColor));
                                line1.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line1)) {
                                    box1.updateLine(line1);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        isItAndroidsTurn = true;
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }

//                            if (isItAndroidsTurn){
//                                soundPlayer.playBoxSelectionSound();
//                            } else {
//                                soundPlayer.playHit2Sound();
//                            }

                            break;
                        case 2:
                            canISelectItInDoNextStep = false;
                            Line line2 = box.getLine2();
                            lastLineDrawn = line2;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
                                }
                            }
                            if (canISelectItInDoNextStep) {
                                line2.setOwner("2");
                                line2.setIsSelected(true);
                                line2.getPaint().setColor(Color.parseColor(BLineColor));
                                line2.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line2)) {
                                    box1.updateLine(line2);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        isItAndroidsTurn = true;
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }

//                            if (isItAndroidsTurn){
//                                soundPlayer.playBoxSelectionSound();
//                            } else {
//                                soundPlayer.playHit2Sound();
//                            }

                            break;
                        case 3:
                            canISelectItInDoNextStep = false;
                            Line line3 = box.getLine3();
                            lastLineDrawn = line3;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
                                }
                            }
                            if (canISelectItInDoNextStep) {
                                line3.setOwner("2");
                                line3.setIsSelected(true);
                                line3.getPaint().setColor(Color.parseColor(BLineColor));
                                line3.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line3)) {
                                    box1.updateLine(line3);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        isItAndroidsTurn = true;
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }

//                            if (isItAndroidsTurn){
//                                soundPlayer.playBoxSelectionSound();
//                            } else {
//                                soundPlayer.playHit2Sound();
//                            }

                            break;
                        case 4:
                            canISelectItInDoNextStep = false;
                            Line line4 = box.getLine4();
                            lastLineDrawn = line4;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
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
                                        canISelectItInDoNextStep = true;
                                } else {
                                    canISelectItInDoNextStep = true;
                                }
                            }
                            if (canISelectItInDoNextStep) {
                                line4.setOwner("2");
                                line4.setIsSelected(true);
                                line4.getPaint().setColor(Color.parseColor(BLineColor));
                                line4.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(BLineColor));
                                for (Box box1 : boxes.getBoxes(line4)) {
                                    box1.updateLine(line4);
                                    if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                                        isItAndroidsTurn = true;
                                        box.getPaint().setColor(Color.parseColor(BBoxColor));
                                        box.setOwner("2");
                                    }
                                }
                            }

//                            if (isItAndroidsTurn){
//                                soundPlayer.playBoxSelectionSound();
//                            } else {
//                                soundPlayer.playHit2Sound();
//                            }

                            break;
                    }

                    invalidate();
                    if (canISelectItInDoNextStep) {
                        break;
                    }
                }

                if (canISelectItInDoNextStep) {
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

        if (isFinishedYet && !isGameFinished){
            isItMyTurn = false;
            isGameFinished = true;

            int aCount = 0;
            int bCount = 0;
            for(Box box : boxes.getList()){
                if (box.getOwner().equals("1"))
                    aCount++;
                else
                    bCount++;
            }

            String message = "";
            ((Activity)context).stopService(new Intent(context, SoundService.class));
            if (aCount > bCount) {
                soundPlayer.playWinSound();
                message = "تبریک! شما برنده شدید.";
            }
            else if (aCount < bCount) {
                soundPlayer.playLooseSound();
                message = "تبریک! بازی خوبی بود ولی حریفتان برنده بازی شده است.";
            }
            else if (aCount == bCount) {
                soundPlayer.playEqualSound();
                message = "تبریک! بازی خوبی بود و شما باهم مساوی شدید.";
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 1 seconds
                    ((Activity)context).startService(new Intent(context, SoundService.class));
                }
            }, 1000);

//            String message = "شما برنده بازی شدید!";
//            MyDialog myDialog = new MyDialog(message);
//            myDialog.show(fragmentManager, "example dialog");
//            Toast toast=Toast.makeText(this.context,message,Toast.LENGTH_SHORT);
//            toast.setMargin(50,50);
//            toast.show();
            helper.showMessage(message);
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

                currentLine.setOwner("2");
                currentLine.setIsSelected(true);
                currentLine.getPaint().setColor(Color.parseColor(BLineColor));
                currentLine.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(BLineColor));
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

                currentLine.setOwner("2");
                currentLine.setIsSelected(true);
                currentLine.setOwner("2");
                currentLine.getPaint().setColor(Color.parseColor(BLineColor));
                currentLine.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(BLineColor));
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
        currentLine = null;
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

                if (currentLine != null)
                    break;
            }

            if (currentLine != null) {
                if (!currentLine.getIsSelected()) {
                    if (!isSinglePlayerMode) {
                        if (this.hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED)
                            this.hubConnection.start().blockingAwait();
                        if (this.hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                            try {
                                this.hubConnection.send("SelectALine", helper.getUserUniqueId(), helper.getUsername(), gameSessionViewId, currentLine.getIndex());
                                helper.Sleep();
                            } catch (Exception ex) {
                                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                            }
                        }
                    }

                    currentLine.setOwner("1");
                    currentLine.setIsSelected(true);
                    currentLine.getPaint().setColor(Color.parseColor(ALineColor));
                    currentLine.getPaint().setShadowLayer(shadowNormalRadius, 0, 0, Color.parseColor(ALineColor));
                    for (Box box : boxes.getBoxes(currentLine)) {
                        box.updateLine(currentLine);
                        if (box.getLine1IsSelected() && box.getLine2IsSelected() && box.getLine3IsSelected() && box.getLine4IsSelected()) {
                            isItMyTurn = true;
                            box.getPaint().setColor(Color.parseColor(ABoxColor));
                            box.setOwner("1");
                        }
                    }

                    if (isItMyTurn){
                        soundPlayer.playBoxSelectionSound();
                    } else {
                        soundPlayer.playHit1Sound();
                    }

//                        CheckIfGameIsFinished();

                    if (!isItMyTurn) {
                        if (isSinglePlayerMode) {
                            ChangeTurn("B");
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // Actions to do after 10 seconds
                                    soundPlayer.playHit2Sound();
                                    doNextStep(currentLine);
                                    ChangeTurn("A");
                                }
                            }, 500);

                        } else if (!isSinglePlayerMode) {
//                                CheckIfGameIsFinished();
                            lastLineDrawn = null;

                            // Send the selected lines infor to the competitor
                            if (this.hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                                try {
                                    this.hubConnection.start().blockingAwait();
                                } catch (Exception ex) {
                                    helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                                }
                            }
                            if (this.hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                                try {
                                    this.hubConnection.send("IPlayedMyTurn", helper.getUserUniqueId(), helper.getUsername(), gameSessionViewId, currentLine.getIndex());
                                    helper.Sleep();
                                } catch (Exception ex) {
                                    helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                                }
                                this.isItMyTurn = false;
//                                    ChangeTurn("B");
                            }
                        }
                    }
                }
            }
        }
    }

    private void touch_move(float x, float y) {

    }

    private void touch_up(float x, float y) {
        //currentLine = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((isSinglePlayerMode && isItMyTurn) || (!isSinglePlayerMode && isItMyTurn)) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
//                    this.postInvalidate();
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
//                touch_move(x, y);
//                invalidate();
                    break;
                case MotionEvent.ACTION_UP:
//                    touch_up(x, y);
//                    invalidate();

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
