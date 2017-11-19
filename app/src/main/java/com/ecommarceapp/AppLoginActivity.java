package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.fragment.SignInFragment;
import com.general.files.GeneralFunctions;
import com.view.MTextView;

public class AppLoginActivity extends BaseActivity {


    public GeneralFunctions generalFunc;

    AppCompatImageView closeLoginImgView;
    public MTextView headerTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login);

        generalFunc = new GeneralFunctions(getActContext());

        closeLoginImgView = findViewById(R.id.closeLoginImgView);
        headerTxtView = findViewById(R.id.headerTxtView);

        closeLoginImgView.setOnClickListener(new setOnClickList());

        loadFragment(new SignInFragment());
    }

    public void loadFragment(Fragment fragment) {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainerView, fragment).commit();
    }

    public Context getActContext() {
        return AppLoginActivity.this;
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.closeLoginImgView:
//                    Utils.hideKeyboard(AppLoginActivity.this);
                    AppLoginActivity.super.onBackPressed();
                    break;
            }
        }
    }
}
