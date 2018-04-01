package rd.com.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.math.BigDecimal;

import rd.com.demo.R;

public class TestPagamento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pagamento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*

        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("Item", new BigDecimal("1000")))
                .setSite(Sites.BRASIL)
                .addExcludedPaymentType(PaymentTypes.TICKET) //Handle exclusions by payment types
                .addExcludedPaymentMethod(PaymentMethods.ARGENTINA.VISA) //Exclude specific payment methods
                .setMaxInstallments(1) //Limit the amount of installments
                .build();
        startMercadoPagoCheckout(checkoutPreference);
    }
    private void startMercadoPagoCheckout(CheckoutPreference checkoutPreference) {

        DecorationPreference decorationPreference = new DecorationPreference.Builder()
                .setBaseColor(ContextCompat.getColor(getApplicationContext(), R.color.primaryColor))
                .enableDarkFont() //Optional
                .build();

        new MercadoPagoCheckout.Builder()
                .setActivity(this)
                .setPublicKey("TEST-0bcb758a-63d1-4ad1-80a9-e4a1b874b4aa")
                .setCheckoutPreference(checkoutPreference)
                .setDecorationPreference(decorationPreference)
                .startForPaymentData();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE) {
                PaymentData paymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);

                Toast.makeText(getApplicationContext(), "Sucesso", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    //Resolve error in checkout
                } else {
                    //Resolve canceled checkout
                }
            }
        }


*/
    }
}
