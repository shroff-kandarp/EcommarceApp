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
import com.ecommarceapp.R;
import com.ecommarceapp.SelectAttributesActivity;
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
public class ProductAddAttributesFragment extends Fragment implements BlockingStep {


    View view;

    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    MTextView noAttributesAvailTxtView;
    MTextView addMoreImgTxtView;
    MButton productInfoAddBtn;
    LinearLayout attributesContainerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_attributes, container, false);

        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();
        attributesContainerView = (LinearLayout) view.findViewById(R.id.attributesContainerView);
        addMoreImgTxtView = (MTextView) view.findViewById(R.id.addMoreImgTxtView);
        noAttributesAvailTxtView = (MTextView) view.findViewById(R.id.noAttributesAvailTxtView);

        addMoreImgTxtView.setOnClickListener(new setOnClickList());
        productInfoAddBtn.setOnClickListener(new setOnClickList());
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
        noAttributesAvailTxtView.setVisibility(View.GONE);

        attributesContainerView.removeAllViews();
        attributesContainerView.setVisibility(View.GONE);

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

        attributesContainerView.removeAllViews();
        attributesContainerView.setVisibility(View.GONE);

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        String productTag = generalFunc.getJsonValue("ProductTag", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);
        JSONArray productAttributeData = generalFunc.getJsonArray("ProductAttributeData", responseString);

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        if (productAttributeData != null) {

            for (int i = 0; i < productAttributeData.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(productAttributeData, i);
                addAttribute(generalFunc.getJsonValue("attribute_id", obj_temp), generalFunc.getJsonValue("ATTRIBUTE_NAME", obj_temp) + ": \n" + generalFunc.getJsonValue("text", obj_temp));
            }
            noAttributesAvailTxtView.setVisibility(productAttributeData.length() > 0 ? View.GONE : View.VISIBLE);
        } else {
            noAttributesAvailTxtView.setVisibility(View.VISIBLE);
        }
    }

    public void addAttribute(final String attribute_id, String text) {

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View categoryView = inflater.inflate(R.layout.add_link_category_item, null);

        MTextView categoryNameTxtView = (MTextView) categoryView.findViewById(R.id.categoryNameTxtView);
        categoryNameTxtView.setText(text);
        ImageView removeImgView = (ImageView) categoryView.findViewById(R.id.removeImgView);
        removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteAttribute(attribute_id);
            }
        });

        attributesContainerView.addView(categoryView);
        attributesContainerView.setVisibility(View.VISIBLE);
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
            }
            if (view.getId() == addMoreImgTxtView.getId()) {

                Bundle bn = new Bundle();
                bn.putString("product_id", manageProductAct.product_id);
                (new StartActProcess(getActContext())).startActForResult(getCurrentFragment(), SelectAttributesActivity.class, Utils.SELECT_ATTRIBUTES_REQ_CODE, bn);
            }
        }
    }

    public void confirmDeleteAttribute(final String attribute_id) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    deleteAttribute(attribute_id);
                }
            }
        });
        generateAlert.setContentMessage("", "Are you sure, you want to delete selected attribute?");
        generateAlert.setPositiveBtn("OK");
        generateAlert.setNegativeBtn("Cancel");

        generateAlert.showAlertBox();
    }

    public void deleteAttribute(String attribute_id) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "deleteProductAttribute");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("attribute_id", attribute_id);

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

        if (requestCode == Utils.SELECT_ATTRIBUTES_REQ_CODE && resultCode == manageProductAct.RESULT_OK) {
            getProductDetails();
        }

    }
}
