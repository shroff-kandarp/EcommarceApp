package com.ecommarceapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

public class SignInActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;

    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MButton btn_type2;

    MTextView forgetPassTxt;
    MTextView goToRegisterTxtView;

    AppCompatImageView closeSignInImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        generalFunc = new GeneralFunctions(getActContext());

        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        passwordBox = (MaterialEditText) findViewById(R.id.passwordBox);
        forgetPassTxt = (MTextView) findViewById(R.id.forgetPassTxt);
        goToRegisterTxtView = (MTextView) findViewById(R.id.goToRegisterTxtView);
        closeSignInImgView = findViewById(R.id.closeSignInImgView);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        setLabels();

        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(new setOnClickList());
        closeSignInImgView.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), getResources().getColor(R.color.appThemeColor_1), goToRegisterTxtView);
    }


    public void setLabels() {
        emailBox.setBothText("Email", "Enter your email");
        passwordBox.setBothText("Password", "Enter your password");

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
                case R.id.closeSignInImgView:
                    Utils.hideKeyboard(getActContext());
                    SignInActivity.super.onBackPressed();
                    break;

            }
        }
    }
}
