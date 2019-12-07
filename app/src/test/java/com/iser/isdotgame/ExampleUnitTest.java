package com.iser.isdotgame;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionState;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    protected Handler handler;
    private int selectALineCounter;
    private int iPlayedMyTurnCounter;

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void SelectALine(){
        Context context = new BaseActivity();
//        Context context = new Application();
        Helper helper = new Helper(context);
        SignalRService service = new SignalRService(context, this.handler);

        HubConnection hubConnection = service.getHubConnection();

        hubConnection.on("TestResult", (selectALineCounter, iPlayedMyTurnCounter) -> {
            int a = Integer.parseInt(selectALineCounter);
            int b = Integer.parseInt(iPlayedMyTurnCounter);
            Assert.assertEquals(selectALineCounter+iPlayedMyTurnCounter, a+b);
        }, String.class, String.class);

        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection.start().blockingAwait();
            } catch (Exception ex) {
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            try {
                hubConnection.send("StartTest");
                selectALineCounter = 0;
                iPlayedMyTurnCounter = 0;
            } catch (Exception ex) {
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }

        // Loop the rest codes
        for(int i = 0; i < 100; i++) {
            if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                try {
                    hubConnection.send("SelectALine", "2dc0ea11881ca812", "", "6F61DCAA-0A64-4455-BB6C-8FB694BE22D3", 0);
                    selectALineCounter++;
                } catch (Exception ex) {
                    helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                }
            }
            if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                try {
                    hubConnection.send("IPlayedMyTurn", "2dc0ea11881ca812", "", "6F61DCAA-0A64-4455-BB6C-8FB694BE22D3", 0);
                    iPlayedMyTurnCounter++;
                } catch (Exception ex) {
                    helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                }
            }
        }

        // Finished the test and listen to results
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            try {
                hubConnection.send("EndTest");
            } catch (Exception ex) {
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }
    }
}