package com.ecommarceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.fragment.SignInFragment;
import com.general.files.GeneralFunctions;
import com.general.files.LoginWithFacebook;
import com.view.MTextView;

public class AppLoginActivity extends BaseActivity {


    public GeneralFunctions generalFunc;

    AppCompatImageView closeLoginImgView;
    public MTextView headerTxtView;

    ImageView fbImgView;
    CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login);

        FacebookSdk.setApplicationId(getResources().getString(R.string.FACEBOOK_ID));
        FacebookSdk.sdkInitialize(getActContext());

        generalFunc = new GeneralFunctions(getActContext());

        closeLoginImgView = findViewById(R.id.closeLoginImgView);
        headerTxtView = findViewById(R.id.headerTxtView);
        fbImgView = findViewById(R.id.fbImgView);

        closeLoginImgView.setOnClickListener(new setOnClickList());
        fbImgView.setOnClickListener(new setOnClickList());

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
                case R.id.fbImgView:
                    mCallbackManager = CallbackManager.Factory.create();
                    new LoginWithFacebook(getActContext(), mCallbackManager);
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
