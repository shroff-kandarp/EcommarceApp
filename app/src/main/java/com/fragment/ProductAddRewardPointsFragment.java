package com.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecommarceapp.ManageStoreProductActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductAddRewardPointsFragment extends Fragment implements BlockingStep {


    View view;

    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    MButton productInfoAddBtn;
    MaterialEditText rewardPointBox;
    MaterialEditText defaultRewardPointBox;
    MaterialEditText femaleRewardPointBox;
    MaterialEditText maleRewardPointBox;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_reward_points, container, false);
        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();
        rewardPointBox = (MaterialEditText) view.findViewById(R.id.rewardPointBox);
        defaultRewardPointBox = (MaterialEditText) view.findViewById(R.id.defaultRewardPointBox);
        femaleRewardPointBox = (MaterialEditText) view.findViewById(R.id.femaleRewardPointBox);
        maleRewardPointBox = (MaterialEditText) view.findViewById(R.id.maleRewardPointBox);

        productInfoAddBtn.setOnClickListener(new setOnClickList());
        setLabels();
        return view;
    }

    public void setLabels() {
        productInfoAddBtn.setText("Update Reward Information");
        rewardPointBox.setBothText("Reward points");
        defaultRewardPointBox.setText("100");

        defaultRewardPointBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        femaleRewardPointBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        maleRewardPointBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        defaultRewardPointBox.setHideUnderline(true);
        femaleRewardPointBox.setHideUnderline(true);
        maleRewardPointBox.setHideUnderline(true);

        defaultRewardPointBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        femaleRewardPointBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        maleRewardPointBox.setInputType(InputType.TYPE_CLASS_NUMBER);

    }

    public void continueExecution() {
        if (!manageProductAct.product_id.equals("")) {
            getProductDetails();
        }
    }

    public void getProductDetails() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getStoreProductInfo");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        displayInformation(responseString);
                    } else {
                        generatePageError();
//                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }

                } else {
                    generatePageError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void displayInformation(String responseString) {

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        String productTag = generalFunc.getJsonValue("ProductTag", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }
    }

    public void generatePageError() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    manageProductAct.backImgView.performClick();
                }
            }
        });
        generateAlert.setContentMessage("", "Some error occurred. Please check your internet or try again later.");
        generateAlert.setPositiveBtn("OK");

        generateAlert.showAlertBox();
    }

    public Context getActContext() {
        return manageProductAct.getActContext();
    }

    public Fragment getCurrentFragment() {
        return this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == productInfoAddBtn.getId()) {
                checkData();
            }
        }
    }

    public void checkData() {

    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
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
}
