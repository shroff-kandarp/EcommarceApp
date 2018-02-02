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
import com.ecommarceapp.ProductDiscountAddActivity;
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
public class ProductAddSpecialFragment extends Fragment implements BlockingStep {


    View view;

    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    MButton productInfoAddBtn;
    LinearLayout discountsContainerView;
    MTextView noDiscountAvailTxtView;
    MTextView addMoreImgTxtView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_special, container, false);

        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();
        discountsContainerView = (LinearLayout) view.findViewById(R.id.discountsContainerView);
        noDiscountAvailTxtView = (MTextView) view.findViewById(R.id.noDiscountAvailTxtView);
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
        discountsContainerView.removeAllViews();

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
        discountsContainerView.removeAllViews();

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        JSONArray productDiscountData = generalFunc.getJsonArray("ProductSpecialData", responseString);
//        String productTag = generalFunc.getJsonValue("ProductTag", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        if (productDiscountData != null) {
            for (int i = 0; i < productDiscountData.length(); i++) {
                addDiscountView(generalFunc.getJsonObject(productDiscountData, i));
            }
            if (productDiscountData.length() > 0) {
                noDiscountAvailTxtView.setVisibility(View.GONE);
            } else {
                noDiscountAvailTxtView.setVisibility(View.VISIBLE);
            }
        } else {
            noDiscountAvailTxtView.setVisibility(View.VISIBLE);
        }
    }

    public void addDiscountView(final JSONObject obj_temp) {

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View categoryView = inflater.inflate(R.layout.item_discount_design, null);

        MTextView headerTxtView = (MTextView) categoryView.findViewById(R.id.headerTxtView);
        MTextView dataTxtView = (MTextView) categoryView.findViewById(R.id.dataTxtView);
        headerTxtView.setText("Group: " + generalFunc.getJsonValue("CUSTOMER_GROUP_NAME", obj_temp));

        dataTxtView.setText("Priority: " + generalFunc.getJsonValue("priority", obj_temp) + "\n" + "Price: " + generalFunc.getJsonValue("price", obj_temp) + "\n" + "Date Start: " + generalFunc.getJsonValue("date_start", obj_temp) + "\n" + "Date End: " + generalFunc.getJsonValue("date_end", obj_temp));
        ImageView removeImgView = (ImageView) categoryView.findViewById(R.id.removeImgView);
        removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDeleteSpecial(generalFunc.getJsonValue("product_special_id", obj_temp));
            }
        });

        new CreateRoundedView(Color.parseColor("#F2F2F2"), Utils.dipToPixels(getActContext(), 8), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), categoryView);
        discountsContainerView.addView(categoryView);
        discountsContainerView.setVisibility(View.VISIBLE);
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

    public void confirmDeleteSpecial(final String product_special_id) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    deleteSpecial(product_special_id);
                }
            }
        });
        generateAlert.setContentMessage("", "Are you sure, you want to delete?");
        generateAlert.setPositiveBtn("OK");
        generateAlert.setNegativeBtn("Cancel");

        generateAlert.showAlertBox();
    }

    public void deleteSpecial(String product_special_id) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "deleteProductSpecial");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("product_special_id", product_special_id);

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
                bn.putString("PAGE_MODE", "SPECIAL");
                (new StartActProcess(getActContext())).startActForResult(getCurrentFragment(), ProductDiscountAddActivity.class, Utils.ADD_DISCOUNT_REQ_CODE, bn);
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

        if (requestCode == Utils.ADD_DISCOUNT_REQ_CODE && resultCode == manageProductAct.RESULT_OK) {
            getProductDetails();
        }
    }
}
