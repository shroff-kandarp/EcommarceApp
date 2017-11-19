package com.general.files;

import android.content.Context;
import android.os.Bundle;

import com.ecommarceapp.AppLoginActivity;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.FirebaseAuth;
import com.utils.Utils;

import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by Shroff on 19-Nov-17.
 */

public class LoginWithFacebook implements FacebookCallback<LoginResult> {
    Context mContext;

    CallbackManager mCallbackManager;

    AppLoginActivity appLoginAct;

    GeneralFunctions generalFunc;

    private FirebaseAuth mAuth;

    public LoginWithFacebook(Context mContext, CallbackManager mCallbackManager) {
        this.mContext = mContext;
        this.mCallbackManager = mCallbackManager;

        appLoginAct = (AppLoginActivity) mContext;
        mAuth = FirebaseAuth.getInstance();
        generalFunc = appLoginAct.generalFunc;
        initializeFacebookLogin();
    }

    public void initializeFacebookLogin() {

        LoginButton loginButton = new LoginButton(mContext);


        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_about_me"));

        loginButton.registerCallback(mCallbackManager, this);
        loginButton.performClick();
    }

    @Override
    public void onSuccess(LoginResult loginResult) {

        Utils.showLoader(mContext);
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject me,
                            GraphResponse response) {
                        // Application code
                        Utils.closeLoader();

                        if (response.getError() != null) {
                            // handle error
                            Utils.printLog("onError", "onError:" + response.getError());

                            generalFunc.showGeneralMessage("", "Please try again later.");

                        } else {

                            String email_str = generalFunc.getJsonValue("email", me.toString());
                            String name_str = generalFunc.getJsonValue("name", me.toString());
                            String first_name_str = generalFunc.getJsonValue("first_name", me.toString());
                            String last_name_str = generalFunc.getJsonValue("last_name", me.toString());
                            String fb_id_str = generalFunc.getJsonValue("id", me.toString());

                            registerFbUser(email_str, first_name_str, last_name_str, fb_id_str);

                            generalFunc.logOUTFrmFB();
                        }
                    }


                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void registerFbUser(String email_str, String first_name_str, String last_name_str, String fb_id_str) {
        Utils.printLog("email_str", ":" + email_str);
        Utils.printLog("first_name_str", ":" + first_name_str);
        Utils.printLog("last_name_str", ":" + last_name_str);
        Utils.printLog("fb_id_str", ":" + fb_id_str);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError(FacebookException error) {

    }
}
