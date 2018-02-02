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
import android.widget.LinearLayout;

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
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
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

    LinearLayout rewardPointContainer;

    HashMap<String, String> data_reward_group = new HashMap<>();

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_reward_points, container, false);
        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();
        rewardPointBox = (MaterialEditText) view.findViewById(R.id.rewardPointBox);

        rewardPointContainer = (LinearLayout) view.findViewById(R.id.rewardPointContainer);

        productInfoAddBtn.setOnClickListener(new setOnClickList());
        setLabels();
        return view;
    }

    public void setLabels() {
        productInfoAddBtn.setText("Update Reward Information");
        rewardPointBox.setBothText("Reward points");
        rewardPointBox.setInputType(InputType.TYPE_CLASS_NUMBER);
//        defaultRewardPointBox.setText("100");
//
//        defaultRewardPointBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        femaleRewardPointBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        maleRewardPointBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//
//        defaultRewardPointBox.setHideUnderline(true);
//        femaleRewardPointBox.setHideUnderline(true);
//        maleRewardPointBox.setHideUnderline(true);
//
//        defaultRewardPointBox.setInputType(InputType.TYPE_CLASS_NUMBER);
//        femaleRewardPointBox.setInputType(InputType.TYPE_CLASS_NUMBER);
//        maleRewardPointBox.setInputType(InputType.TYPE_CLASS_NUMBER);

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
        parameters.put("isLoadGeneralData", "Yes");

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
        rewardPointContainer.removeAllViews();

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        JSONArray productRewardData = generalFunc.getJsonArray("ProductRewardData", responseString);
        String productTag = generalFunc.getJsonValue("ProductTag", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        JSONArray customerGroupDataArr = generalFunc.getJsonArray("CustomerGroupData", generalFunc.getJsonObject("GeneralData", responseString));
        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        rewardPointBox.setText(generalFunc.getJsonValue("points", productData));

        if (productRewardData != null) {
            for (int i = 0; i < productRewardData.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(productRewardData, i);

                String points = generalFunc.getJsonValue("points", obj_temp);
                String customer_group_id = generalFunc.getJsonValue("customer_group_id", obj_temp);
                data_reward_group.put("" + customer_group_id, points);
            }
        }
        if (customerGroupDataArr != null) {
            for (int i = 0; i < customerGroupDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(customerGroupDataArr, i);
                generateView(obj_temp, customerGroupDataArr);
            }
        }
    }

    public void generateView(JSONObject obj_temp, JSONArray customerGroupDataArr) {
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rewardPointView = inflater.inflate(R.layout.item_reward_point, null);

        MTextView headerTxtView = (MTextView) rewardPointView.findViewById(R.id.headerTxtView);
        MaterialEditText valueInputBox = (MaterialEditText) rewardPointView.findViewById(R.id.valueInputBox);
        headerTxtView.setText(generalFunc.getJsonValue("name", obj_temp));

        valueInputBox.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        valueInputBox.setHideUnderline(true);
        valueInputBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        rewardPointView.setTag(generalFunc.getJsonValue("customer_group_id", obj_temp));
        String pointsValue = data_reward_group.get(generalFunc.getJsonValue("customer_group_id", obj_temp));
        if (pointsValue != null) {
            valueInputBox.setText(pointsValue);
        }
        rewardPointContainer.addView(rewardPointView);
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
                updateData();
            }
        }
    }

    public void updateData() {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "updateProductRewardPoints");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("rewardPoints", Utils.getText(rewardPointBox));
        for (int i = 0; i < rewardPointContainer.getChildCount(); i++) {
            View childView = rewardPointContainer.getChildAt(i);
            String customer_group_id = (String) childView.getTag();
            parameters.put("CUSTOMER_GROUP_ID_" + customer_group_id, Utils.getText((MaterialEditText) childView.findViewById(R.id.valueInputBox)));
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {

                        setLabels();

                        getProductDetails();
                    }
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
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
