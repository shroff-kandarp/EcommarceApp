package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

public class SignInActivity extends AppCompatActivity {


    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;

    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MButton btn_type2;

    MTextView forgetPassTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        passwordBox = (MaterialEditText) findViewById(R.id.passwordBox);
        forgetPassTxt = (MTextView) findViewById(R.id.forgetPassTxt);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        setLabels();

        btn_type2.setId(Utils.generateViewId());

        backImgView.setOnClickListener(new setOnClickList());

        btn_type2.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());
    }


    public void setLabels() {
        titleTxt.setText("SignIn");
        emailBox.setBothText("Email", "Enter your email");
        passwordBox.setBothText("Password", "Enter your password");

        forgetPassTxt.setText("Forget Password?");
        btn_type2.setText("SignIn");
    }

    public Context getActContext() {
        return SignInActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    Utils.hideKeyboard(getActContext());
                    SignInActivity.super.onBackPressed();
                    break;

            }
        }
    }
}
