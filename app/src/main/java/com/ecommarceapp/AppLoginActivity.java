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
import com.fragment.SignUpFragment;
import com.general.files.GeneralFunctions;
import com.general.files.LoginWithFacebook;
import com.general.files.LoginWithGoogle;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.utils.Utils;
import com.view.MTextView;

public class AppLoginActivity extends BaseActivity {


    public GeneralFunctions generalFunc;

    AppCompatImageView closeLoginImgView;
    public MTextView headerTxtView;

    ImageView fbImgView;
    ImageView googleImgView;
    CallbackManager mCallbackManager;
    public SignUpFragment signUpFrag;

    LoginWithGoogle loginWithGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login);

        FacebookSdk.setApplicationId(getResources().getString(R.string.FACEBOOK_ID));
        FacebookSdk.sdkInitialize(getActContext());
        FacebookSdk.setWebDialogTheme(R.style.FBDialogtheme);

        mCallbackManager = CallbackManager.Factory.create();
        generalFunc = new GeneralFunctions(getActContext());

        closeLoginImgView = (AppCompatImageView) findViewById(R.id.closeLoginImgView);
        headerTxtView = (MTextView) findViewById(R.id.headerTxtView);
        fbImgView = (ImageView) findViewById(R.id.fbImgView);
        googleImgView = (ImageView) findViewById(R.id.googleImgView);

        closeLoginImgView.setOnClickListener(new setOnClickList());
        fbImgView.setOnClickListener(new setOnClickList());
        googleImgView.setOnClickListener(new setOnClickList());

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
                    new LoginWithFacebook(getActContext(), mCallbackManager);
                    break;
                case R.id.googleImgView:
                    loginWithGoogle = new LoginWithGoogle(getActContext());
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.GOOGLE_SIGN_IN_REQ_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            loginWithGoogle.handleSignInResult(result);
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
