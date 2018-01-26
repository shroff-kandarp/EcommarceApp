package com.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecommarceapp.BecomeSellerActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.MButton;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellerBankInfoFragment extends Fragment implements BlockingStep {

    View view;

    GeneralFunctions generalFunc;

    BecomeSellerActivity myAccAct;

    View containerView;

    MaterialEditText storeBankDetailBox;
    MButton storeBankUpdateBtn;
    JSONObject storeData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_seller_bank_info, container, false);
        myAccAct = (BecomeSellerActivity) getActivity();
        generalFunc = myAccAct.generalFunc;

        containerView = view.findViewById(R.id.containerView);
        storeBankDetailBox = (MaterialEditText) view.findViewById(R.id.storeBankDetailBox);
        storeBankUpdateBtn = ((MaterialRippleLayout) view.findViewById(R.id.storeBankUpdateBtn)).getChildView();

        Utils.setMultiLineEditBox(storeBankDetailBox);
        setLabels();

        getSellerData();
        storeBankUpdateBtn.setOnClickListener(new setOnClickList());
        return view;
    }

    public void setLabels() {
        storeBankDetailBox.setBothText("", "Enter your store's bank details.");
        storeBankUpdateBtn.setText("Update Bank Info");
    }

    public void getSellerData() {
        containerView.setVisibility(View.INVISIBLE);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCustomerSellerData");
        parameters.put("customer_id", generalFunc.getMemberId());

        Utils.printLog("parameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    Utils.printLog("responseString:", "::" + responseString);

                    if (isDataAvail == true) {
                        containerView.setVisibility(View.VISIBLE);

                        JSONObject msgObj = generalFunc.getJsonObject(Utils.message_str, responseString);
                        storeData = msgObj;
//                        storeNameBox.setText(generalFunc.getJsonValue("store_name", msgObj));


                        displayStoreData();
                    } else {
                        generalFunc.showGeneralMessage("Please check back later", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }


    public Context getActContext() {
        return myAccAct.getActContext();
    }

    public Fragment getCurrentFragment() {
        return this;
    }


    public void displayStoreData() {

        storeBankDetailBox.setText(generalFunc.getJsonValue("store_bank_details", storeData));

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == storeBankUpdateBtn.getId()) {
                checkData();
            }
        }
    }

    public void checkData() {
        boolean storeBankInfoEntered = Utils.checkText(storeBankDetailBox) ? true : Utils.setErrorFields(storeBankDetailBox, "Required");

        if (storeBankInfoEntered == false) {
            return;
        }
        updateStoreData();
    }

    public void updateStoreData() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateSellerStoreData");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("id", generalFunc.getJsonValue("id", storeData));
        parameters.put("store_name", generalFunc.getJsonValue("store_name", storeData));
        parameters.put("store_email", generalFunc.getJsonValue("store_email", storeData));
        parameters.put("store_mobile", generalFunc.getJsonValue("store_phone", storeData));
        parameters.put("store_country", generalFunc.getJsonValue("store_country", storeData));
        parameters.put("store_zone", generalFunc.getJsonValue("store_state", storeData));
        parameters.put("store_city", generalFunc.getJsonValue("store_city", storeData));
        parameters.put("store_postal", generalFunc.getJsonValue("store_zipcode", storeData));
        parameters.put("store_tin", generalFunc.getJsonValue("store_tin", storeData));

        parameters.put("store_description", generalFunc.getJsonValue("store_description", storeData));
        parameters.put("store_address", generalFunc.getJsonValue("store_address", storeData));
        parameters.put("store_shipping_policy", generalFunc.getJsonValue("store_shipping_policy", storeData));
        parameters.put("store_return_policy", generalFunc.getJsonValue("store_return_policy", storeData));
        parameters.put("store_meta_keywords", generalFunc.getJsonValue("store_meta_keywords", storeData));
        parameters.put("store_meta_description", generalFunc.getJsonValue("store_meta_descriptions", storeData));
        parameters.put("store_shipping_charge", generalFunc.getJsonValue("store_shipping_charge", storeData));
        parameters.put("store_seo_url", generalFunc.getJsonValue("SEO_URL", storeData));
        parameters.put("store_bank", Utils.getText(storeBankDetailBox));

        Utils.printLog("parameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        JSONObject msgObj = generalFunc.getJsonObject("venderData", responseString);
                        storeData = msgObj;
                        displayStoreData();
                    }
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        if (storeData != null) {
            callback.goToPrevStep();
        }
    }
}
