package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.adapter.AddStoreProductSteppersAdapter;
import com.general.files.GeneralFunctions;
import com.stepstone.stepper.StepperLayout;
import com.utils.Utils;
import com.view.MTextView;

public class ManageStoreProductActivity extends AppCompatActivity {

    MTextView titleTxt;
    public ImageView backImgView;

    public GeneralFunctions generalFunc;

    private StepperLayout mStepperLayout;

    public Uri fileUri;

    public String product_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_store_product);


        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());

        product_id = getIntent().getStringExtra("product_id") != null ? getIntent().getStringExtra("product_id") : "";
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);
        mStepperLayout.setAdapter(new AddStoreProductSteppersAdapter(getSupportFragmentManager(), getActContext()));
        setLabels();
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


    public void setLabels() {
        titleTxt.setText(product_id.equals("") ? "Add Product" : "Edit Product");
    }

    public Context getActContext() {
        return ManageStoreProductActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard((Activity) getActContext());
            if (i == R.id.backImgView) {
                ManageStoreProductActivity.super.onBackPressed();
            }
        }
    }
}
