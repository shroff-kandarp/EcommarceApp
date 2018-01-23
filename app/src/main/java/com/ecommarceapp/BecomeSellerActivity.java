package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;

public class BecomeSellerActivity extends AppCompatActivity {

    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_seller);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());

        setLabels();

        buildSteppers();
    }

    public void buildSteppers() {
        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step1TxtView)));

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step1CompleteImgView)));


        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step2TxtView)));

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step2CompleteImgView)));


        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step3TxtView)));

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step3CompleteImgView)));
    }

    public void setLabels() {
        titleTxt.setText("Seller Info");
    }

    public Context getActContext() {
        return BecomeSellerActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard((Activity) getActContext());
            if (i == R.id.backImgView) {
                BecomeSellerActivity.super.onBackPressed();
            }
        }
    }
}
