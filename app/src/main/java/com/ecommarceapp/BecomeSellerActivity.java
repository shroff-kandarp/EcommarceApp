package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.adapter.SellerSteppersAdapter;
import com.fragment.SellerBankInfoFragment;
import com.fragment.StoreDetailFragment;
import com.fragment.StoreInfoFragment;
import com.general.files.GeneralFunctions;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;

public class BecomeSellerActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    MTextView titleTxt;
    ImageView backImgView;

    public GeneralFunctions generalFunc;

    private StepperLayout mStepperLayout;

    public Uri fileUri;

    public SellerSteppersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_become_seller);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());

        setLabels();
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);

        ArrayList<Step> step_list_frag = new ArrayList<>();
        step_list_frag.add(new StoreInfoFragment());
        step_list_frag.add(new StoreDetailFragment());
        step_list_frag.add(new SellerBankInfoFragment());

        ArrayList<String> stepListTitle = new ArrayList<>();
        stepListTitle.add("Store Info");
        stepListTitle.add("Store Detail");
        stepListTitle.add("Bank Details");

        adapter = new SellerSteppersAdapter(getSupportFragmentManager(), getActContext(), step_list_frag, stepListTitle);

        mStepperLayout.setAdapter(adapter);
        mStepperLayout.setListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }
    /*public void buildSteppers() {
        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step1TxtView)));

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step1CompleteImgView)));


        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step2TxtView)));

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step2CompleteImgView)));


        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step3TxtView)));

        new CreateRoundedView(getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), 0, getResources().getColor(R.color.appThemeColor_1), (findViewById(R.id.step3CompleteImgView)));
    }*/

    public void setLabels() {
        titleTxt.setText("Seller Profile");
    }

    public Context getActContext() {
        return BecomeSellerActivity.this;
    }

    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {

        if (newStepPosition == 0) {
            ((StoreInfoFragment) adapter.list_step_frag.get(newStepPosition)).getSellerData();
        }
        if (newStepPosition == 1) {
            ((StoreDetailFragment) adapter.list_step_frag.get(newStepPosition)).getSellerData();
        }
        if (newStepPosition == 2) {
            ((SellerBankInfoFragment) adapter.list_step_frag.get(newStepPosition)).getSellerData();
        }
        Utils.printLog("newStepPosition", "::" + newStepPosition);
    }

    @Override
    public void onReturn() {

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
