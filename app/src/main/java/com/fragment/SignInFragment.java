package com.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecommarceapp.AppLoginActivity;
import com.ecommarceapp.MainActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    View view;


    GeneralFunctions generalFunc;

    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MButton btn_type2;

    MTextView forgetPassTxt;
    MTextView goToRegisterTxtView;

    AppLoginActivity appLoginAct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        appLoginAct = (AppLoginActivity) getActivity();

        generalFunc = appLoginAct.generalFunc;

        emailBox = (MaterialEditText) view.findViewById(R.id.emailBox);
        passwordBox = (MaterialEditText) view.findViewById(R.id.passwordBox);
        forgetPassTxt = (MTextView) view.findViewById(R.id.forgetPassTxt);
        goToRegisterTxtView = (MTextView) view.findViewById(R.id.goToRegisterTxtView);

        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();

        setLabels();

        btn_type2.setId(Utils.generateViewId());

        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);

        btn_type2.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());
        goToRegisterTxtView.setOnClickListener(new setOnClickList());

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), getResources().getColor(R.color.appThemeColor_1), goToRegisterTxtView);

        return view;
    }

    public void setLabels() {
        emailBox.setBothText("Email", "Enter your email");
        passwordBox.setBothText("Password", "Enter your password");

        btn_type2.setText("SignIn");
        appLoginAct.headerTxtView.setText("Sign in to access your orders, wishlist and your account.");
    }

    public Context getActContext() {
        return appLoginAct;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.goToRegisterTxtView) {
                SignUpFragment signUpFrag = new SignUpFragment();
                appLoginAct.signUpFrag = signUpFrag;
                appLoginAct.loadFragment(signUpFrag);

            } else if (i == btn_type2.getId()) {
                checkData();
            }
        }
    }

    public void checkData() {
        boolean emailEntered = Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, "Invalid email"))
                : Utils.setErrorFields(emailBox, "Required");

        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, "Password should not contain whitespace.")
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, "Password must be" + " " + Utils.minPasswordLength + " or more character long.")))
                : Utils.setErrorFields(passwordBox, "Required");

        if (emailEntered == false || passwordEntered == false) {
            return;
        }

        signInUser();
    }

    public void signInUser() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "signIn");
        parameters.put("password", Utils.getText(passwordBox));
        parameters.put("email", Utils.getText(emailBox));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        generalFunc.storeUserData(generalFunc.getJsonValue(Utils.message_str, responseString));
                        (new StartActProcess(getActContext())).startAct(MainActivity.class);
                        ActivityCompat.finishAffinity((Activity) getActContext());
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }
}
