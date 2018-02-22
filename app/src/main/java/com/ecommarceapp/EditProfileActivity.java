package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.SetOnTouchList;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {


    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;


    MaterialEditText fNameBox;
    MaterialEditText lNameBox;
    MaterialEditText emailBox;
    MaterialEditText countryBox;
    MaterialEditText mobileBox;

    MTextView dobSelectTxtView;

    RadioButton maleRadioBtn;
    RadioButton feMaleRadioBtn;

    MButton btn_type2;

    boolean isCountrySelected = false;
    String iCountryId = "";
    View containerView;
    boolean isDOBSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);

        fNameBox = (MaterialEditText) findViewById(R.id.fNameBox);
        lNameBox = (MaterialEditText) findViewById(R.id.lNameBox);
        emailBox = (MaterialEditText) findViewById(R.id.emailBox);
        countryBox = (MaterialEditText) findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) findViewById(R.id.mobileBox);
        containerView = findViewById(R.id.containerView);
        maleRadioBtn = (RadioButton) findViewById(R.id.maleRadioBtn);
        feMaleRadioBtn = (RadioButton) findViewById(R.id.feMaleRadioBtn);
        dobSelectTxtView = (MTextView) findViewById(R.id.dobSelectTxtView);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        setLabels();


        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), dobSelectTxtView);
        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        fNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        lNameBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        emailBox.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mobileBox.setImeOptions(EditorInfo.IME_ACTION_DONE);

        removeInput();

        countryBox.setShowClearButton(false);

        btn_type2.setId(Utils.generateViewId());

        backImgView.setOnClickListener(new setOnClickList());

        btn_type2.setOnClickListener(new setOnClickList());
        dobSelectTxtView.setOnClickListener(new setOnClickList());

        getProfileData();
    }

    public void setLabels() {
        titleTxt.setText("Edit Profile Info");

        fNameBox.setBothText("First Name");
        lNameBox.setBothText("Last Name");
        emailBox.setBothText("Email");
        countryBox.setBothText("Country");
        mobileBox.setBothText("Mobile");

        btn_type2.setText("Update Information");
    }

    public Context getActContext() {
        return EditProfileActivity.this;
    }


    public void removeInput() {
        Utils.removeInput(countryBox);

        countryBox.setOnTouchListener(new SetOnTouchList());

        countryBox.setOnClickListener(new setOnClickList());
    }


    public void getProfileData() {
        containerView.setVisibility(View.GONE);
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getProfileInformation");
        parameters.put("customer_id", generalFunc.getMemberId());
        /*parameters.put("firstname", Utils.getText(fNameBox));
        parameters.put("lastname", Utils.getText(lNameBox));
        parameters.put("telephone", Utils.getText(mobileBox));
        parameters.put("country", Utils.getText(countryBox));
        parameters.put("country_id", iCountryId);
        parameters.put("email", Utils.getText(emailBox));*/

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        JSONObject obj_msg = generalFunc.getJsonObject(Utils.message_str, responseString);
                        fNameBox.setText(generalFunc.getJsonValue("firstname", obj_msg));
                        lNameBox.setText(generalFunc.getJsonValue("lastname", obj_msg));
                        mobileBox.setText(generalFunc.getJsonValue("telephone", obj_msg));
                        emailBox.setText(generalFunc.getJsonValue("email", obj_msg));

                        String countryId = generalFunc.retriveValue("COUNTRY_ID");
                        String countryName = generalFunc.retriveValue("COUNTRY_NAME");
                        if (countryId != null && !countryId.equals("") && countryName != null && !countryName.equals("")) {
                            iCountryId = countryId;
                            isCountrySelected = true;
                            countryBox.setText(countryName);
                        }

                        if (generalFunc.getJsonValue("gender", obj_msg).equalsIgnoreCase("male")) {
                            maleRadioBtn.setChecked(true);
                        } else if (generalFunc.getJsonValue("gender", obj_msg).equalsIgnoreCase("female")) {
                            feMaleRadioBtn.setChecked(true);
                        }

                        if (generalFunc.getJsonValue("dob", obj_msg).equals("")) {

                            dobSelectTxtView.setText("Select date of birth");
                        } else {
                            isDOBSelected = true;
                            dobSelectTxtView.setText(generalFunc.getJsonValue("dob", obj_msg));
                        }
                        containerView.setVisibility(View.VISIBLE);
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

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                Utils.hideKeyboard((Activity) getActContext());
                EditProfileActivity.super.onBackPressed();

            } else if (i == btn_type2.getId()) {
                checkData();
            } else if (view.getId() == countryBox.getId()) {
                new StartActProcess(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            } else if (view.getId() == dobSelectTxtView.getId()) {
                chooseDate();
            }
        }
    }

    public void chooseDate() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String day = dayOfMonth < 10 ? ("0" + dayOfMonth) : ("" + dayOfMonth);
                        String month = monthOfYear < 10 ? ("0" + monthOfYear) : ("" + monthOfYear);

                        dobSelectTxtView.setText("" + year + "-" + month + "-" + day);
                        isDOBSelected = true;
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle("Select Date of birth");
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void checkData() {
        boolean fNameEntered = Utils.checkText(fNameBox) ? true : Utils.setErrorFields(fNameBox, "Required");
        boolean lNameEntered = Utils.checkText(lNameBox) ? true : Utils.setErrorFields(lNameBox, "Required");
        boolean emailEntered = Utils.checkText(emailBox) ?
                (generalFunc.isEmailValid(Utils.getText(emailBox)) ? true : Utils.setErrorFields(emailBox, "Invalid email"))
                : Utils.setErrorFields(emailBox, "Required");
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, "Required");
        boolean countryEntered = isCountrySelected ? true : false;
        if (countryEntered == false) {
            Utils.setErrorFields(countryBox, "Required");
        }
        if (fNameEntered == false || lNameEntered == false || emailEntered == false || mobileEntered == false
                || countryEntered == false) {
            return;
        }

        updateProfileData();
    }


    public void updateProfileData() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updateProfileInformation");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("firstname", Utils.getText(fNameBox));
        parameters.put("lastname", Utils.getText(lNameBox));
        parameters.put("telephone", Utils.getText(mobileBox));
        parameters.put("country", Utils.getText(countryBox));
        parameters.put("country_id", iCountryId);
        parameters.put("email", Utils.getText(emailBox));
        parameters.put("dob", isDOBSelected == true ? dobSelectTxtView.getText().toString() : "");
        parameters.put("gender", maleRadioBtn.isChecked() ? "Male" : (feMaleRadioBtn.isChecked() ? "Female" : ""));


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {

                        generalFunc.storedata("COUNTRY_ID", iCountryId);
                        generalFunc.storedata("COUNTRY_NAME", Utils.getText(countryBox));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == RESULT_OK && data != null) {
//            vCountryCode = data.getStringExtra("vCountryCode");
//            vPhoneCode = data.getStringExtra("vPhoneCode");
            iCountryId = data.getStringExtra("iCountryId");
            String vCountry = data.getStringExtra("vCountry");
            isCountrySelected = true;

            countryBox.setText(vCountry);
        }
    }
}
