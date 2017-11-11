package com.ecommarceapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.view.MTextView;

public class UserCartActivity extends BaseActivity {

    MTextView titleTxt;
    ImageView backImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        backImgView.setOnClickListener(new setOnClickList());
    }


    public void setLabels() {
        titleTxt.setText("Cart");
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    UserCartActivity.super.onBackPressed();
                    break;
            }
        }
    }

}
