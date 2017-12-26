package com.ecommarceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.SetOnTouchList;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import java.util.HashMap;

public class AddAddressActivity extends AppCompatActivity {

    ImageView backImgView;
    MTextView titleTxt;

    MaterialEditText nameBox;
    MaterialEditText countryBox;
    MaterialEditText mobileBox;
    MaterialEditText address1Box;
    MaterialEditText address2Box;
    MaterialEditText cityBox;
    MaterialEditText postalBox;

    MButton btn_type2;

    boolean isCountrySelected = false;
    String iCountryId = "";

    GeneralFunctions generalFunc;

    HashMap<String, String> ADDRESS_DATA = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);


        generalFunc = new GeneralFunctions(getActContext());

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        nameBox = (MaterialEditText) findViewById(R.id.nameBox);
        countryBox = (MaterialEditText) findViewById(R.id.countryBox);
        mobileBox = (MaterialEditText) findViewById(R.id.mobileBox);
        address1Box = (MaterialEditText) findViewById(R.id.address1Box);
        address2Box = (MaterialEditText) findViewById(R.id.address2Box);
        cityBox = (MaterialEditText) findViewById(R.id.cityBox);
        postalBox = (MaterialEditText) findViewById(R.id.postalBox);

        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        btn_type2.setId(Utils.generateViewId());

        mobileBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        postalBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        backImgView.setOnClickListener(new setOnClickList());
        btn_type2.setOnClickListener(new setOnClickList());
        setLabels();

        removeInput();
    }


    public void removeInput() {
        Utils.removeInput(countryBox);

        countryBox.setOnTouchListener(new SetOnTouchList());

        countryBox.setOnClickListener(new setOnClickList());
    }

    private void setLabels() {
        nameBox.setBothText("Name", "Enter name");
        mobileBox.setBothText("Mobile No.", "Enter mobile no.");
        address1Box.setBothText("Address 1", "Enter address 1.");
        address2Box.setBothText("Address 2(Optional)", "Enter address 2(optional).");
        cityBox.setBothText("City", "Enter city.");
        postalBox.setBothText("Postal Code", "Enter postal code.");
        countryBox.setBothText("Country");
        titleTxt.setText("New Address");
        btn_type2.setText("Add");

        ADDRESS_DATA = (HashMap<String, String>) getIntent().getSerializableExtra("ADDRESS_DATA");

        if (ADDRESS_DATA != null) {
            nameBox.setText(ADDRESS_DATA.get("firstname") + " " + ADDRESS_DATA.get("lastname"));
            address1Box.setText(ADDRESS_DATA.get("address_1"));
            address2Box.setText(ADDRESS_DATA.get("address_2"));
            cityBox.setText(ADDRESS_DATA.get("city"));
            postalBox.setText(ADDRESS_DATA.get("postcode"));
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == backImgView.getId()) {
                AddAddressActivity.super.onBackPressed();
            } else if (view.getId() == btn_type2.getId()) {
                checkData();
            } else if (view.getId() == countryBox.getId()) {
                new StartActProcess(getActContext()).startActForResult(SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            }
        }
    }

    public Context getActContext() {
        return AddAddressActivity.this;
    }

    public void checkData() {
        boolean nameEntered = Utils.checkText(nameBox) ? true : Utils.setErrorFields(nameBox, "Required");
        boolean mobileEntered = Utils.checkText(mobileBox) ? true : Utils.setErrorFields(mobileBox, "Required");
        boolean address1Entered = Utils.checkText(address1Box) ? true : Utils.setErrorFields(address1Box, "Required");
        boolean cityEntered = Utils.checkText(cityBox) ? true : Utils.setErrorFields(cityBox, "Required");
        boolean postalCodeEntered = Utils.checkText(postalBox) ? true : Utils.setErrorFields(postalBox, "Required");
        boolean countryEntered = isCountrySelected ? true : false;


        if (nameEntered == false || address1Entered == false || mobileEntered == false
                || countryEntered == false || cityEntered == false || postalCodeEntered == false) {
            return;
        }
        addCustomerAddress();
    }

    public void addCustomerAddress() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "addCustomerAddress");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("firstname", Utils.getText(nameBox));
        parameters.put("lastname", "");
        parameters.put("company", "");
        parameters.put("address_1", Utils.getText(address1Box));
        parameters.put("address_2", Utils.getText(address2Box));
        parameters.put("city", Utils.getText(cityBox));
        parameters.put("postcode", Utils.getText(postalBox));
        parameters.put("mobile", Utils.getText(mobileBox));
        parameters.put("country", Utils.getText(countryBox));
        parameters.put("country_id", iCountryId);
        if (ADDRESS_DATA != null) {
            parameters.put("address_id", ADDRESS_DATA.get("address_id"));
        }

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

                        (new StartActProcess(getActContext())).setOkResult();
                        backImgView.performClick();
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
