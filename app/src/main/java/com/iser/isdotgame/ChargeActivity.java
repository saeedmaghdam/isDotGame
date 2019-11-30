package com.iser.isdotgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import com.zarinpal.ewallets.purchase.OnCallbackRequestPaymentListener;
import com.zarinpal.ewallets.purchase.OnCallbackVerificationPaymentListener;
import com.zarinpal.ewallets.purchase.PaymentRequest;
import com.zarinpal.ewallets.purchase.ZarinPal;

public class ChargeActivity extends AppCompatActivity {
    Helper helper;
    private static int coins;
    private static long amount;
    private HubConnection hubConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charge);

        helper = new Helper(this);
        hubConnection = HubConnectionBuilder.create(this.helper.getHubUrl()).build();

        if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
            try {
                hubConnection.start().blockingAwait();
            } catch (Exception ex) {
                helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
            }
        }

        paymentResult();

        Button ol2000 = findViewById(R.id.ol2000);
        ol2000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(100);
            }
        });

        Button ol3000 = findViewById(R.id.ol3000);
        ol3000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(210);
            }
        });

        Button ol4000 = findViewById(R.id.ol4000);
        ol4000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(320);
            }
        });

        Button ol5000 = findViewById(R.id.ol5000);
        ol5000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pay(430);
            }
        });

        Button btnSetName = findViewById(R.id.btnSetName);
        btnSetName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText txtName = findViewById(R.id.txtName);

                String name = txtName.getText().toString();
                if (!TextUtils.isEmpty(name)){
                    if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
                        try {
                            hubConnection.start().blockingAwait();
                        } catch (Exception ex) {
                            helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                        }
                    }
                    if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                        try {
                            hubConnection.send("UpdateName", helper.getUserUniqueId(), helper.getUsername(), name);
                        } catch (Exception ex) {
                            helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
                        }
                    }
                }
            }
        });
    }

    private void paymentResult(){
        Uri data = getIntent().getData();
        ZarinPal.getPurchase(this).verificationPayment(data, new OnCallbackVerificationPaymentListener() {
            @Override
            public void onCallbackResultVerificationPayment(boolean isPaymentSuccess, String refID, PaymentRequest paymentRequest) {
//                if (hubConnection.getConnectionState() == HubConnectionState.DISCONNECTED) {
//                    try {
//                        hubConnection.start().blockingAwait();
//                    } catch (Exception ex) {
//                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
//                    }
//                }
//                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
//                    try {
//                        if (isPaymentSuccess) {
//                            hubConnection.send("Charge", helper.getUserUniqueId(), helper.getUsername(), refID, coins, amount);
//                        }
//                        else {
//                            hubConnection.send("Charge", helper.getUserUniqueId(), helper.getUsername(), refID, "error", amount);
//                        }
//                    } catch (Exception ex) {
//                        helper.showMessage("در حال حاضر ارتباط با سرور قطع می باشد!");
//                    }
//                }

                if (isPaymentSuccess) {
                    helper.showMessage("پرداخت با موفقیت انجام شد، " + coins + " سکه به موجودی شما اضافه شد.");
                }
                else{
                    helper.showMessage("پرداخت موفقیت آمیز نبود");
                }
            }
        });
    }

    private void pay(int coins){
        long amount = 1000;

        switch (coins){
            case 100:
//                amount = 2000;
                amount = 100;
                break;
            case 210:
                amount = 101;
                break;
            case 320:
                amount = 102;
                break;
            case 430:
                amount = 103;
                break;
        }
        this.coins = coins;
        this.amount = amount;

        ZarinPal purchase = ZarinPal.getPurchase(this);
        PaymentRequest payment = ZarinPal.getPaymentRequest();

        payment.setMerchantID("ccadd3e2-10d2-11ea-8171-000c295eb8fc");
        payment.setAmount(amount);
        payment.setDescription("خرید " + String.valueOf(coins) + " سکه");
        payment.setCallbackURL("app://app");
//        payment.setMobile("09355106005");
//        payment.setEmail("imannamix@gmail.com");


        ZarinPal.getPurchase(getApplicationContext()).startPayment(payment, new OnCallbackRequestPaymentListener() {
            @Override
            public void onCallbackResultPaymentRequest(int status, String authority, Uri paymentGatewayUri, Intent intent) {
                if (status == 100)
                    startActivity(intent);
                else
                    helper.showMessage("خطا در ایجاد درخواست پرداخت");
            }
        });
    }
}
