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
public class ProductAddGeneralFragment extends Fragment implements BlockingStep {


    View view;
    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    MaterialEditText productNameBox;
    MaterialEditText productModelBox;
    MaterialEditText productDescriptionBox;
    MaterialEditText productPriceBox;
    MaterialEditText quantityBox;
    MaterialEditText minQTYBox;
    MaterialEditText metaTagTitleBox;
    MaterialEditText metaTagDescBox;
    MaterialEditText metaTagKeywordsBox;
    MaterialEditText productTagsBox;
    MButton productInfoAddBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_product_add_general, container, false);


        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;

        productNameBox = (MaterialEditText) view.findViewById(R.id.productNameBox);
        productModelBox = (MaterialEditText) view.findViewById(R.id.productModelBox);
        productDescriptionBox = (MaterialEditText) view.findViewById(R.id.productDescriptionBox);
        productPriceBox = (MaterialEditText) view.findViewById(R.id.productPriceBox);
        quantityBox = (MaterialEditText) view.findViewById(R.id.quantityBox);
        minQTYBox = (MaterialEditText) view.findViewById(R.id.minQTYBox);
        metaTagTitleBox = (MaterialEditText) view.findViewById(R.id.metaTagTitleBox);
        metaTagDescBox = (MaterialEditText) view.findViewById(R.id.metaTagDescBox);
        metaTagKeywordsBox = (MaterialEditText) view.findViewById(R.id.metaTagKeywordsBox);
        productTagsBox = (MaterialEditText) view.findViewById(R.id.productTagsBox);

        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();

        productInfoAddBtn.setId(Utils.generateViewId());

        Utils.setMultiLineEditBox(productDescriptionBox);
        Utils.setMultiLineEditBox(metaTagDescBox);
        Utils.setMultiLineEditBox(metaTagKeywordsBox);
        setLabels();

        productPriceBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        quantityBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        minQTYBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        productInfoAddBtn.setOnClickListener(new setOnClickList());

        return view;
    }

    public void continueExecution() {

        if (!manageProductAct.product_id.equals("")) {
            getProductDetails();
        }
    }

    public void setLabels() {
        productNameBox.setBothText("Product Name", "Enter product name");
        productModelBox.setBothText("Product Model", "Enter product model");
        productDescriptionBox.setBothText("Description", "Enter product description");
        productPriceBox.setBothText("Price", "Enter product price");
        quantityBox.setBothText("Quantity", "Enter quantity");
        minQTYBox.setBothText("Minimum Quantity", "Enter min quantity");
        metaTagTitleBox.setBothText("Meta Tag Title", "Enter meta tag title");
        metaTagDescBox.setBothText("Meta Tag Description", "Enter meta tag description");
        metaTagKeywordsBox.setBothText("Meta Tag Keywords", "Enter meta tag keywords");
        productTagsBox.setBothText("Product Tags", "Enter product tags");
        productInfoAddBtn.setText(!manageProductAct.product_id.equals("") ? "Edit Product Info" : "Add Product");
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
        boolean productNameEntered = Utils.checkText(productNameBox) ? true : Utils.setErrorFields(productNameBox, "Required");
        boolean productModelEntered = Utils.checkText(productModelBox) ? true : Utils.setErrorFields(productModelBox, "Required");
        boolean productDescriptionEntered = Utils.checkText(productDescriptionBox) ? true : Utils.setErrorFields(productDescriptionBox, "Required");

        boolean productPriceEntered = Utils.checkText(productPriceBox) ? true : Utils.setErrorFields(productDescriptionBox, "Required");

        boolean productQuantityEntered = Utils.checkText(quantityBox) ? true : Utils.setErrorFields(productDescriptionBox, "Required");

        boolean productMinQuantityEntered = Utils.checkText(minQTYBox) ? true : Utils.setErrorFields(productDescriptionBox, "Required");

//        boolean metaTagTitleDescriptionEntered = Utils.checkText(metaTagTitleBox) ? true : Utils.setErrorFields(metaTagTitleBox, "Required");
//        boolean metaTagDescriptionEntered = Utils.checkText(metaTagDescBox) ? true : Utils.setErrorFields(metaTagDescBox, "Required");
//        boolean metaTagKeywordsEntered = Utils.checkText(metaTagKeywordsBox) ? true : Utils.setErrorFields(metaTagKeywordsBox, "Required");
//        boolean productTagsEntered = Utils.checkText(productTagsBox) ? true : Utils.setErrorFields(productTagsBox, "Required");

        if (productNameEntered == false || productModelEntered == false || productDescriptionEntered == false || productPriceEntered == false || productQuantityEntered == false || productMinQuantityEntered == false/* || metaTagTitleDescriptionEntered == false
                || metaTagDescriptionEntered == false || metaTagKeywordsEntered == false || productTagsEntered == false*/) {
            return;
        }

        updateProduct();
    }

    public void updateProduct() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateStoreProduct");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_name", Utils.getText(productNameBox));
        parameters.put("product_model", Utils.getText(productModelBox));
        parameters.put("product_description", Utils.getText(productDescriptionBox));
        parameters.put("product_price", Utils.getText(productPriceBox));
        parameters.put("product_quantity", Utils.getText(quantityBox));
        parameters.put("product_min_quantity", Utils.getText(minQTYBox));
        parameters.put("meta_tag_title", Utils.getText(metaTagTitleBox));
        parameters.put("meta_tag_description", Utils.getText(metaTagDescBox));
        parameters.put("meta_tag_keywords", Utils.getText(metaTagKeywordsBox));
        parameters.put("product_tag_keywords", Utils.getText(productTagsBox));
        parameters.put("product_id", manageProductAct.product_id);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        if (manageProductAct.product_id.equals("")) {
                            String product_id = generalFunc.getJsonValue("product_id", responseString);
                            manageProductAct.product_id = product_id;
                            manageProductAct.setResult(manageProductAct.RESULT_OK);
                        }
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

    public void displayInformation(String responseString) {

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        productModelBox.setText(generalFunc.getJsonValue("model", productData));
        productPriceBox.setText(generalFunc.getJsonValue("price", productData));
        quantityBox.setText(generalFunc.getJsonValue("quantity", productData));
        minQTYBox.setText(generalFunc.getJsonValue("minimum", productData));
        productNameBox.setText(generalFunc.getJsonValue("name", productDescriptionData));
        productDescriptionBox.setText(generalFunc.getJsonValue("description", productDescriptionData));
        metaTagTitleBox.setText(generalFunc.getJsonValue("meta_title", productDescriptionData));
        metaTagDescBox.setText(generalFunc.getJsonValue("meta_description", productDescriptionData));
        metaTagKeywordsBox.setText(generalFunc.getJsonValue("meta_keyword", productDescriptionData));
        productTagsBox.setText(generalFunc.getJsonValue("tag", productDescriptionData));
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (!manageProductAct.product_id.equals("")) {
            callback.goToNextStep();
        } else {
            generalFunc.showGeneralMessage("", "Please add product first.");
        }
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
