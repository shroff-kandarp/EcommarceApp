package com.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ecommarceapp.ManageStoreProductActivity;
import com.ecommarceapp.ProductOptionAddActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductAddOptionsFragment extends Fragment implements BlockingStep {


    View view;

    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    MButton productInfoAddBtn;
    LinearLayout optionsContainerView;
    MTextView noOptionsAvailTxtView;
    MTextView addMoreImgTxtView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_options, container, false);
        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();
        optionsContainerView = (LinearLayout) view.findViewById(R.id.optionsContainerView);
        noOptionsAvailTxtView = (MTextView) view.findViewById(R.id.noOptionsAvailTxtView);
        addMoreImgTxtView = (MTextView) view.findViewById(R.id.addMoreImgTxtView);

        productInfoAddBtn.setOnClickListener(new setOnClickList());
        addMoreImgTxtView.setOnClickListener(new setOnClickList());
        setLabels();


        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), Utils.dipToPixels(getActContext(), 0), Color.parseColor("#DEDEDE"), addMoreImgTxtView);

        return view;
    }

    public void setLabels() {
        productInfoAddBtn.setText("Update Information");
    }

    public void continueExecution() {
        if (!manageProductAct.product_id.equals("")) {
            getProductDetails();
        }
    }

    public void getProductDetails() {
        noOptionsAvailTxtView.setVisibility(View.GONE);
        optionsContainerView.removeAllViews();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getProductOptions");
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
                        JSONArray optionDataArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        if (optionDataArr != null) {
                            for (int i = 0; i < optionDataArr.length(); i++) {
                                JSONObject obj_temp = generalFunc.getJsonObject(optionDataArr, i);

                                addOptionView(obj_temp);
                            }
                        } else {
                            noOptionsAvailTxtView.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                            noOptionsAvailTxtView.setVisibility(View.VISIBLE);
                            optionsContainerView.setVisibility(View.GONE);
                        }
                    } else {
                        noOptionsAvailTxtView.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                        noOptionsAvailTxtView.setVisibility(View.VISIBLE);
                        optionsContainerView.setVisibility(View.GONE);

                    }

                } else {
                    generatePageError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void addOptionView(final JSONObject obj_temp) {

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View categoryView = inflater.inflate(R.layout.item_option_design, null);

        MTextView headerTxtView = (MTextView) categoryView.findViewById(R.id.headerTxtView);
        MTextView dataTxtView = (MTextView) categoryView.findViewById(R.id.dataTxtView);
        headerTxtView.setText("Option: " + generalFunc.getJsonValue("option_name", obj_temp));

        String required = generalFunc.getJsonValue("required", obj_temp);
        if (required.equalsIgnoreCase("yes")) {
            headerTxtView.setText(headerTxtView.getText().toString() + " (Required)");
        }

        headerTxtView.setText(Utils.html2text(headerTxtView.getText().toString()));

        final String option_value_id = generalFunc.getJsonValue("option_value_id", obj_temp);
        if (!option_value_id.trim().equals("")) {
            dataTxtView.setText("Option Value: " + generalFunc.getJsonValue("option_value_name", obj_temp) + "\n" + "Quantity: " + generalFunc.getJsonValue("quantity", obj_temp) + "\n" + "Subtract Stock: " + generalFunc.getJsonValue("subtract", obj_temp) + "\n" + "Price: (" + generalFunc.getJsonValue("price_prefix", obj_temp) + ") " + generalFunc.getJsonValue("price", obj_temp) + "\n" + "Points: (" + generalFunc.getJsonValue("points_prefix", obj_temp) + ") " + generalFunc.getJsonValue("points", obj_temp) + "\n" + "Weight: (" + generalFunc.getJsonValue("weight_prefix", obj_temp) + ") " + generalFunc.getJsonValue("weight", obj_temp));
        } else {
            dataTxtView.setText(generalFunc.getJsonValue("value", obj_temp));
        }

        ImageView removeImgView = (ImageView) categoryView.findViewById(R.id.removeImgView);
        removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteOption(generalFunc.getJsonValue("product_option_id", obj_temp), option_value_id);
            }
        });

        new CreateRoundedView(Color.parseColor("#F2F2F2"), Utils.dipToPixels(getActContext(), 8), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), categoryView);
        optionsContainerView.addView(categoryView);
        optionsContainerView.setVisibility(View.VISIBLE);
    }

    public void confirmDeleteOption(final String product_option_id, final String option_value_id) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    deleteOption(product_option_id, option_value_id);
                }
            }
        });
        generateAlert.setContentMessage("", "Are you sure, you want to delete?");
        generateAlert.setPositiveBtn("OK");
        generateAlert.setNegativeBtn("Cancel");

        generateAlert.showAlertBox();
    }

    public void deleteOption(String product_option_id, String option_value_id) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "deleteProductOption");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("product_option_id", product_option_id);
        parameters.put("option_value_id", option_value_id);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
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
            if (view.getId() == addMoreImgTxtView.getId()) {
                Bundle bn = new Bundle();
                bn.putString("product_id", manageProductAct.product_id);
                (new StartActProcess(getActContext())).startActForResult(getCurrentFragment(), ProductOptionAddActivity.class, Utils.ADD_OPTION_REQ_CODE, bn);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ADD_OPTION_REQ_CODE && resultCode == manageProductAct.RESULT_OK) {
            getProductDetails();
        }
    }
}
