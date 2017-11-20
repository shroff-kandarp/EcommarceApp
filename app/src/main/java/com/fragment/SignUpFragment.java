package com.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.ecommarceapp.SelectCountryActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.SetOnTouchList;
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
public class SignUpFragment extends Fragment {

    View view;
    GeneralFunctions generalFunc;

    MaterialEditText nameBox;
    MaterialEditText countryBox;
    MaterialEditText mobileBox;
    MaterialEditText emailBox;
    MaterialEditText passwordBox;


    MTextView goToSignInTxtView;

    MButton btn_type2;

    AppLoginActivity appLoginAct;

    boolean isCountrySelected = false;
    String iCountryId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        appLoginAct = (AppLoginActivity) getActivity();
        generalFunc = appLoginAct.generalFunc;

        nameBox = view.findViewById(R.id.nameBox);
        countryBox = view.findViewById(R.id.countryBox);
        mobileBox = view.findViewById(R.id.mobileBox);
        emailBox = view.findViewById(R.id.emailBox);
        passwordBox = view.findViewById(R.id.passwordBox);
        goToSignInTxtView = view.findViewById(R.id.goToSignInTxtView);

        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(new setOnClickList());
        goToSignInTxtView.setOnClickListener(new setOnClickList());


        passwordBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordBox.setTypeface(generalFunc.getDefaultFont(getActContext()));
        emailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        setLabels();
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), getResources().getColor(R.color.appThemeColor_1), goToSignInTxtView);
        removeInput();
        return view;
    }

    public void setLabels() {
        nameBox.setBothText("Name", "Enter your name");
        emailBox.setBothText("Email", "Enter your email");
        passwordBox.setBothText("Password", "Enter your password");
        mobileBox.setBothText("Mobile No.", "Enter your mobile no.");
        countryBox.setBothText("Country");

        btn_type2.setText("SignUp");
        appLoginAct.headerTxtView.setText("Your details are safe with us. We won't share your details with anyone.");
    }


    public void removeInput() {
        Utils.removeInput(countryBox);

        countryBox.setOnTouchListener(new SetOnTouchList());

        countryBox.setOnClickListener(new setOnClickList());
    }

    public Context getActContext() {
        return appLoginAct;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == btn_type2.getId()) {
                checkData();
            } else if (view.getId() == goToSignInTxtView.getId()) {

                appLoginAct.loadFragment(new SignInFragment());
            } else if (view.getId() == countryBox.getId()) {
                new StartActProcess(getActContext()).startActForResult(appLoginAct.signUpFrag,
                        SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            }
        }
    }

    public void checkData() {
        boolean nameEntered = Utils.checkText(nameBox) ? true : Utils.setErrorFields(nameBox, "Required");
        boolean emailEntered = Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, "Invalid email"))
                : Utils.setErrorFields(emailBox, "Required");
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, "Required");
        boolean countryEntered = isCountrySelected ? true : false;

        boolean passwordEntered = Utils.checkText(passwordBox) ?
                (Utils.getText(passwordBox).contains(" ") ? Utils.setErrorFields(passwordBox, "Password should not contain whitespace.")
                        : (Utils.getText(passwordBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(passwordBox, "Password must be" + " " + Utils.minPasswordLength + " or more character long.")))
                : Utils.setErrorFields(passwordBox, "Required");

        if (nameEntered == false || emailEntered == false || mobileEntered == false
                || countryEntered == false || passwordEntered == false) {
            return;
        }

        registerUser();
    }

    public void registerUser() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "registerUser");
        parameters.put("name", Utils.getText(nameBox));
        parameters.put("mobile", Utils.getText(mobileBox));
        parameters.put("country", Utils.getText(countryBox));
        parameters.put("country_id", iCountryId);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == appLoginAct.RESULT_OK && data != null) {
//            vCountryCode = data.getStringExtra("vCountryCode");
//            vPhoneCode = data.getStringExtra("vPhoneCode");
            iCountryId = data.getStringExtra("iCountryId");
            String vCountry = data.getStringExtra("vCountry");
            isCountrySelected = true;

            countryBox.setText(vCountry);
        }
    }
}
