package com.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ecommarceapp.ManageStoreProductActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductAddDataFragment extends Fragment implements BlockingStep {


    View view;

    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    MaterialEditText skuBox;
    MaterialEditText upcBox;
    MaterialEditText eanBox;
    MaterialEditText janBox;
    MaterialEditText isbnBox;
    MaterialEditText mpnBox;
    MaterialEditText locationBox;
    MaterialEditText seoURLBox;
    MaterialEditText lengthBox;
    MaterialEditText widthBox;
    MaterialEditText heightBox;
    MaterialEditText weightBox;
    MaterialEditText sortOrderBox;

    MTextView dateSelectTxtView;

    RadioGroup shippingRadioGroup;
    RadioButton shippingYesRadioBtn;
    RadioButton shippingNoRadioBtn;

    AppCompatSpinner taxClassSpinner;
    AppCompatSpinner subtractStockSpinner;
    AppCompatSpinner outOfStockStatusSpinner;
    AppCompatSpinner lengthClassSpinner;
    AppCompatSpinner weightClassSpinner;
    AppCompatSpinner statusSpinner;

    MButton productInfoAddBtn;

    ArrayList<String> taxClassData = new ArrayList<>();
    ArrayList<String> taxClassDataIds = new ArrayList<>();

    ArrayList<String> lengthClassData = new ArrayList<>();
    ArrayList<String> lengthClassDataIds = new ArrayList<>();

    ArrayList<String> weightClassData = new ArrayList<>();
    ArrayList<String> weightClassDataIds = new ArrayList<>();

    ArrayList<String> subtractStockData = new ArrayList<>();
    ArrayList<String> statusData = new ArrayList<>();

    ArrayList<String> outOfStockStatusData = new ArrayList<>();
    ArrayList<String> outOfStockStatusDataIds = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_data, container, false);

        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;

        skuBox = (MaterialEditText) view.findViewById(R.id.skuBox);
        upcBox = (MaterialEditText) view.findViewById(R.id.upcBox);
        eanBox = (MaterialEditText) view.findViewById(R.id.eanBox);
        janBox = (MaterialEditText) view.findViewById(R.id.janBox);
        isbnBox = (MaterialEditText) view.findViewById(R.id.isbnBox);
        mpnBox = (MaterialEditText) view.findViewById(R.id.mpnBox);
        locationBox = (MaterialEditText) view.findViewById(R.id.locationBox);
        seoURLBox = (MaterialEditText) view.findViewById(R.id.seoURLBox);
        lengthBox = (MaterialEditText) view.findViewById(R.id.lengthBox);
        widthBox = (MaterialEditText) view.findViewById(R.id.widthBox);
        heightBox = (MaterialEditText) view.findViewById(R.id.heightBox);
        weightBox = (MaterialEditText) view.findViewById(R.id.weightBox);
        sortOrderBox = (MaterialEditText) view.findViewById(R.id.sortOrderBox);
        taxClassSpinner = (AppCompatSpinner) view.findViewById(R.id.taxClassSpinner);
        subtractStockSpinner = (AppCompatSpinner) view.findViewById(R.id.subtractStockSpinner);
        outOfStockStatusSpinner = (AppCompatSpinner) view.findViewById(R.id.outOfStockStatusSpinner);
        lengthClassSpinner = (AppCompatSpinner) view.findViewById(R.id.lengthClassSpinner);
        weightClassSpinner = (AppCompatSpinner) view.findViewById(R.id.weightClassSpinner);
        statusSpinner = (AppCompatSpinner) view.findViewById(R.id.statusSpinner);
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();
        dateSelectTxtView = (MTextView) view.findViewById(R.id.dateSelectTxtView);
        shippingRadioGroup = (RadioGroup) view.findViewById(R.id.shippingRadioGroup);
        shippingYesRadioBtn = (RadioButton) view.findViewById(R.id.shippingYesRadioBtn);
        shippingNoRadioBtn = (RadioButton) view.findViewById(R.id.shippingNoRadioBtn);
        setLabels();
        productInfoAddBtn.setOnClickListener(new setOnClickList());
        dateSelectTxtView.setOnClickListener(new setOnClickList());

        productInfoAddBtn.setId(Utils.generateViewId());
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), taxClassSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), subtractStockSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), outOfStockStatusSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), lengthClassSpinner);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), weightClassSpinner);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), statusSpinner);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), dateSelectTxtView);

        dateSelectTxtView.setText(getCurrentDate());
        lengthBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        widthBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        heightBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        weightBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        sortOrderBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        return view;
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public void setLabels() {
        skuBox.setBothText("SKU", "Stock keeping unit");
        upcBox.setBothText("UPC", "Universal product code");
        eanBox.setBothText("EAN", "European article number");
        janBox.setBothText("JAN", "Japanese article number");
        isbnBox.setBothText("ISBN", "International standard book number");
        mpnBox.setBothText("MPN", "Manufacturer part number");
        locationBox.setBothText("Location", "Location");
        seoURLBox.setBothText("SEO Url", "SEO Url");
        lengthBox.setBothText("", "Length");
        widthBox.setBothText("", "Width");
        heightBox.setBothText("", "Height");
        weightBox.setBothText("Weight", "Weight");
        sortOrderBox.setBothText("Sort Order", "Sort Order");

        productInfoAddBtn.setText("Edit Product Data");
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
        String ProductSEOURL = generalFunc.getJsonValue("ProductSEOURL", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);
        JSONArray taxDataArr = generalFunc.getJsonArray("TaxData", responseString);
        JSONArray weightClassDataArr = generalFunc.getJsonArray("WeightClassData", responseString);
        JSONArray lengthClassDataArr = generalFunc.getJsonArray("LengthClassData", responseString);
        JSONArray stockStatusData = generalFunc.getJsonArray("StockStatusData", responseString);

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        skuBox.setText(generalFunc.getJsonValue("sku", productData));
        upcBox.setText(generalFunc.getJsonValue("upc", productData));
        eanBox.setText(generalFunc.getJsonValue("ean", productData));
        janBox.setText(generalFunc.getJsonValue("jan", productData));
        isbnBox.setText(generalFunc.getJsonValue("isbn", productData));
        mpnBox.setText(generalFunc.getJsonValue("mpn", productData));
        locationBox.setText(generalFunc.getJsonValue("location", productData));
        dateSelectTxtView.setText(generalFunc.getJsonValue("date_available", productData).equals("") ? getCurrentDate() : generalFunc.getJsonValue("date_available", productData));
        lengthBox.setText(generalFunc.getJsonValue("length", productData));
        widthBox.setText(generalFunc.getJsonValue("width", productData));
        heightBox.setText(generalFunc.getJsonValue("height", productData));
        weightBox.setText(generalFunc.getJsonValue("weight", productData));
        sortOrderBox.setText(generalFunc.getJsonValue("sort_order", productData));
        seoURLBox.setText(ProductSEOURL);

        if (generalFunc.getJsonValue("shipping", productData).equalsIgnoreCase("1")) {
            shippingYesRadioBtn.setChecked(true);
        } else {
            shippingNoRadioBtn.setChecked(true);
        }

        int tax_class_id = 0;
        int stock_status_id = 0;
        int length_class_id = 0;
        int weight_class_id = 0;
        if (taxDataArr != null) {
            taxClassData.clear();
            taxClassDataIds.clear();
            taxClassData.add("--- None ---");
            taxClassDataIds.add("0");
            for (int i = 0; i < taxDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(taxDataArr, i);
                taxClassData.add(generalFunc.getJsonValue("title", obj_temp));
                taxClassDataIds.add(generalFunc.getJsonValue("tax_class_id", obj_temp));

                if (generalFunc.getJsonValue("tax_class_id", productData).equalsIgnoreCase(generalFunc.getJsonValue("tax_class_id", obj_temp))) {
                    tax_class_id = i + 1;
                }

            }
        }

        if (stockStatusData != null) {
            outOfStockStatusData.clear();
            outOfStockStatusDataIds.clear();
            for (int i = 0; i < stockStatusData.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(stockStatusData, i);
                outOfStockStatusData.add(generalFunc.getJsonValue("name", obj_temp));
                outOfStockStatusDataIds.add(generalFunc.getJsonValue("stock_status_id", obj_temp));
                if (generalFunc.getJsonValue("stock_status_id", productData).equalsIgnoreCase(generalFunc.getJsonValue("stock_status_id", obj_temp))) {
                    stock_status_id = i;
                }
            }
        }

        if (lengthClassDataArr != null) {

            lengthClassData.clear();
            lengthClassDataIds.clear();
            for (int i = 0; i < lengthClassDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(lengthClassDataArr, i);
                lengthClassData.add(generalFunc.getJsonValue("title", obj_temp));
                lengthClassDataIds.add(generalFunc.getJsonValue("length_class_id", obj_temp));

                if (generalFunc.getJsonValue("length_class_id", productData).equalsIgnoreCase(generalFunc.getJsonValue("length_class_id", obj_temp))) {
                    length_class_id = i;
                }
            }

        }
        if (weightClassDataArr != null) {

            weightClassData.clear();
            weightClassDataIds.clear();
            for (int i = 0; i < weightClassDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(weightClassDataArr, i);
                weightClassData.add(generalFunc.getJsonValue("title", obj_temp));
                weightClassDataIds.add(generalFunc.getJsonValue("weight_class_id", obj_temp));

                if (generalFunc.getJsonValue("weight_class_id", productData).equalsIgnoreCase(generalFunc.getJsonValue("weight_class_id", obj_temp))) {
                    weight_class_id = i;
                }
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, taxClassData);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        taxClassSpinner.setAdapter(dataAdapter);

        if (tax_class_id != 0) {
            taxClassSpinner.setSelection(tax_class_id);
        }


        ArrayAdapter<String> weightClassAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, weightClassData);
        weightClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightClassSpinner.setAdapter(weightClassAdapter);
        weightClassSpinner.setSelection(weight_class_id);

        ArrayAdapter<String> lengthClassAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, lengthClassData);
        lengthClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lengthClassSpinner.setAdapter(lengthClassAdapter);
        lengthClassSpinner.setSelection(length_class_id);


        subtractStockData.clear();
        statusData.clear();

        subtractStockData.add("Yes");
        subtractStockData.add("No");

        statusData.add("Enabled");
        statusData.add("Disabled");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, statusData);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);

        if (generalFunc.getJsonValue("status", productData).equalsIgnoreCase("0")) {
            statusSpinner.setSelection(1);
        }

        ArrayAdapter<String> subtractStockAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, subtractStockData);
        subtractStockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subtractStockSpinner.setAdapter(subtractStockAdapter);

        if (generalFunc.getJsonValue("subtract", productData).equalsIgnoreCase("0")) {
            subtractStockSpinner.setSelection(1);
        }
        ArrayAdapter<String> outOfStockStatusAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, outOfStockStatusData);
        outOfStockStatusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outOfStockStatusSpinner.setAdapter(outOfStockStatusAdapter);

        outOfStockStatusSpinner.setSelection(stock_status_id);

//        taxClassSpinner.setSelection(0);
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

        if (!manageProductAct.isFinishing()) {
            generateAlert.showAlertBox();
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

                        dateSelectTxtView.setText("" + year + "-" + month + "-" + day);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle("From Date");
        dpd.show(manageProductAct.getFragmentManager(), "Datepickerdialog");
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
                updateProductData();
            } else if (view.getId() == dateSelectTxtView.getId()) {
                chooseDate();
            }
        }
    }

    public void updateProductData() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateStoreProductData");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("sku", Utils.getText(skuBox));
        parameters.put("upc", Utils.getText(upcBox));
        parameters.put("ean", Utils.getText(eanBox));
        parameters.put("jan", Utils.getText(janBox));
        parameters.put("isbn", Utils.getText(isbnBox));
        parameters.put("mpn", Utils.getText(mpnBox));
        parameters.put("location", Utils.getText(locationBox));
        parameters.put("tax_class_id", taxClassDataIds.get(taxClassSpinner.getSelectedItemPosition()));
        parameters.put("subtract", subtractStockData.get(subtractStockSpinner.getSelectedItemPosition()).equalsIgnoreCase("Yes") ? "1" : "0");
        parameters.put("stock_status_id", outOfStockStatusDataIds.get(outOfStockStatusSpinner.getSelectedItemPosition()));
        parameters.put("shipping", shippingYesRadioBtn.isChecked() ? "1" : "0");
        parameters.put("seo_url", Utils.getText(seoURLBox));
        parameters.put("date_available", dateSelectTxtView.getText().toString());
        parameters.put("length", Utils.getText(lengthBox));
        parameters.put("width", Utils.getText(widthBox));
        parameters.put("height", Utils.getText(heightBox));
        parameters.put("length_class_id", lengthClassDataIds.get(lengthClassSpinner.getSelectedItemPosition()));
        parameters.put("weight", Utils.getText(weightBox));
        parameters.put("weight_class_id", weightClassDataIds.get(weightClassSpinner.getSelectedItemPosition()));
        parameters.put("status", statusData.get(statusSpinner.getSelectedItemPosition()).equalsIgnoreCase("Enabled") ? "1" : "0");
        parameters.put("sort_order", Utils.getText(sortOrderBox));

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
}
