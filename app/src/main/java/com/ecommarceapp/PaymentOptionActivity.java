package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MTextView;

public class PaymentOptionActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;
    ImageView backImgView;

    MTextView titleTxt;

    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_option);

        generalFunc = new GeneralFunctions(getActContext());

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        backImgView.setOnClickListener(new setOnClickList());

        titleTxt.setText("Select Payment Option");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Utils.printLog("POSITION", "::" + radioGroup.getCheckedRadioButtonId());
                Bundle bn = new Bundle();
                switch (checkedId) {
                    case R.id.cashOnDeliveryRadioBtn:
                        bn.putString("CHOOSE_OPTION", "CASH");
                        (new StartActProcess(getActContext())).setOkResult(bn);
                        backImgView.performClick();
                        break;
                    case R.id.payUMoneyRadioBtn:
                        bn.putString("CHOOSE_OPTION", "PAYUMONEY");
                        (new StartActProcess(getActContext())).setOkResult(bn);
                        backImgView.performClick();
                        break;
                }
            }
        });
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == backImgView.getId()) {
                PaymentOptionActivity.super.onBackPressed();
            }
        }
    }

    public Context getActContext() {
        return PaymentOptionActivity.this;
    }
}
