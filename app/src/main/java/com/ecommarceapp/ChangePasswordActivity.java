package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity {

    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;

    MaterialEditText previousPassBox;
    MaterialEditText newPassBox;
    MaterialEditText reEnterNewPassBox;

    MButton btn_type2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        previousPassBox = (MaterialEditText) findViewById(R.id.previousPassBox);
        newPassBox = (MaterialEditText) findViewById(R.id.newPassBox);
        reEnterNewPassBox = (MaterialEditText) findViewById(R.id.reEnterNewPassBox);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        setLabels();

        backImgView.setOnClickListener(new setOnClickList());
        btn_type2.setId(Utils.generateViewId());
        btn_type2.setOnClickListener(new setOnClickList());

        previousPassBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        previousPassBox.setTypeface(generalFunc.getDefaultFont(getActContext()));

        newPassBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        newPassBox.setTypeface(generalFunc.getDefaultFont(getActContext()));

        reEnterNewPassBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        reEnterNewPassBox.setTypeface(generalFunc.getDefaultFont(getActContext()));
    }

    public void setLabels() {
        titleTxt.setText("Change Password");
        previousPassBox.setBothText("Current Password", "Enter current password");
        newPassBox.setBothText("New Password", "Enter new password");
        reEnterNewPassBox.setBothText("Re Enter New Password", "Re Enter new password");
        btn_type2.setText("Change Password");

    }

    public Context getActContext() {
        return ChangePasswordActivity.this;
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                Utils.hideKeyboard((Activity) getActContext());
                ChangePasswordActivity.super.onBackPressed();

            } else if (i == btn_type2.getId()) {
                checkData();
            }
        }
    }

    public void checkData() {
        boolean previousPasswordEntered = Utils.checkText(previousPassBox) ?
                (Utils.getText(previousPassBox).contains(" ") ? Utils.setErrorFields(previousPassBox, "Password should not contain whitespace.")
                        : (Utils.getText(previousPassBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(previousPassBox, "Password must be" + " " + Utils.minPasswordLength + " or more character long.")))
                : Utils.setErrorFields(previousPassBox, "Required");

        boolean newPasswordEntered = Utils.checkText(newPassBox) ?
                (Utils.getText(newPassBox).contains(" ") ? Utils.setErrorFields(newPassBox, "Password should not contain whitespace.")
                        : (Utils.getText(newPassBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(newPassBox, "Password must be" + " " + Utils.minPasswordLength + " or more character long.")))
                : Utils.setErrorFields(newPassBox, "Required");

        boolean reNewPasswordEntered = Utils.checkText(reEnterNewPassBox) ?
                (Utils.getText(reEnterNewPassBox).contains(" ") ? Utils.setErrorFields(reEnterNewPassBox, "Password should not contain whitespace.")
                        : (Utils.getText(reEnterNewPassBox).length() >= Utils.minPasswordLength ? true : Utils.setErrorFields(reEnterNewPassBox, "Password must be" + " " + Utils.minPasswordLength + " or more character long.")))
                : Utils.setErrorFields(reEnterNewPassBox, "Required");

        if (previousPasswordEntered == false || newPasswordEntered == false || reNewPasswordEntered == false) {
            return;
        }

        if (!Utils.getText(reEnterNewPassBox).equals(Utils.getText(newPassBox))) {
            Utils.setErrorFields(reEnterNewPassBox, "Password does not match");
            return;
        }

        updatePassword();
    }

    public void updatePassword() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "changePassword");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("currentPassword", Utils.getText(previousPassBox));
        parameters.put("newPassword", Utils.getText(newPassBox));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                        generateAlert.setCancelable(false);
                        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                            @Override
                            public void handleBtnClick(int btn_id) {
                                if (btn_id == 1) {
                                    backImgView.performClick();
                                }
                            }
                        });
                        generateAlert.setContentMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                        generateAlert.setPositiveBtn("Ok");
                        generateAlert.showAlertBox();
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
