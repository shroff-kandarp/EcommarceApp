package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.general.files.SetOnTouchList;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

public class RegisterActivity extends AppCompatActivity {


    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;


    MaterialEditText fNameBox;
    MaterialEditText lNameBox;
    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MaterialEditText countryBox;
    MaterialEditText mobileBox;

    MButton btn_type2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        fNameBox = (MaterialEditText) findViewById(R.id.fNameBox);
        lNameBox = (MaterialEditText) findViewById(R.id.lNameBox);
        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        countryBox = (MaterialEditText) findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) findViewById(R.id.mobileBox);
        passwordBox = (MaterialEditText) findViewById(R.id.passwordBox);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        setLabels();


        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        fNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        passwordBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        removeInput();

        countryBox.setShowClearButton(false);

        btn_type2.setId(Utils.generateViewId());

        backImgView.setOnClickListener(new setOnClickList());

        btn_type2.setOnClickListener(new setOnClickList());
    }

    public void setLabels() {
        titleTxt.setText("Register");

        fNameBox.setBothText("First Name");
        lNameBox.setBothText("Last Name");
        emailBox.setBothText("Email");
        countryBox.setBothText("Country");
        mobileBox.setBothText("Mobile");
        passwordBox.setBothText("Password");

        btn_type2.setText("Register");
    }

    public Context getActContext() {
        return RegisterActivity.this;
    }


    public void removeInput() {
        Utils.removeInput(countryBox);

        countryBox.setOnTouchListener(new SetOnTouchList());

        countryBox.setOnClickListener(new setOnClickList());
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    Utils.hideKeyboard(getActContext());
                    RegisterActivity.super.onBackPressed();
                    break;

            }
        }
    }
}
