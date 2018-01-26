package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.MTextView;

public class ManageStoreProductsListActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;

    MTextView titleTxt;
    ImageView backImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_store_products_list);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());
    }

    public Context getActContext() {
        return ManageStoreProductsListActivity.this;
    }

    public void setLabels() {
        titleTxt.setText("Store Products");
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard((Activity) getActContext());
            if (i == R.id.backImgView) {
                ManageStoreProductsListActivity.super.onBackPressed();
            }
        }
    }
}
