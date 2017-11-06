package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

public class AppLoginActivity extends BaseActivity {

    MButton signInBtn;
    MButton signUpBtn;

    GeneralFunctions generalFunc;

    MTextView skipTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login);

        generalFunc = new GeneralFunctions(getActContext());

        signInBtn = ((MaterialRippleLayout) findViewById(R.id.signInBtn)).getChildView();
        signUpBtn = ((MaterialRippleLayout) findViewById(R.id.signUpBtn)).getChildView();
        skipTxtView = (MTextView) findViewById(R.id.skipTxtView);

        signInBtn.setId(Utils.generateViewId());
        signUpBtn.setId(Utils.generateViewId());

        signInBtn.setOnClickListener(new setOnClickList());
        signUpBtn.setOnClickListener(new setOnClickList());
        skipTxtView.setOnClickListener(new setOnClickList());

        signInBtn.setText("SignIn");
        signUpBtn.setText("Register");
    }

    public Context getActContext() {
        return AppLoginActivity.this;
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == signInBtn.getId()) {
                (new StartActProcess(getActContext())).startAct(SignInActivity.class);

            } else if (i == signUpBtn.getId()) {
                (new StartActProcess(getActContext())).startAct(RegisterActivity.class);

            } else if (i == skipTxtView.getId()) {
                (new StartActProcess(getActContext())).startAct(MainActivity.class);
            }
        }
    }
}
