package com.ecommarceapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ProductOptionAddActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;

    MTextView titleTxt;
    ImageView backImgView;
    MButton productInfoAddBtn;


    ArrayList<String> optionDataID = new ArrayList<>();
    ArrayList<String> optionDataList = new ArrayList<>();
    ArrayList<String> requiredDataList = new ArrayList<>();

    AppCompatSpinner optionDataSpinner;
    AppCompatSpinner requiredSpinner;

    MTextView dateSelectTxtView;

    View containerView;
    View dateArea;
    View requiredArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_option_add);

        generalFunc = new GeneralFunctions(getActContext());
        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        productInfoAddBtn = ((MaterialRippleLayout) findViewById(R.id.productInfoAddBtn)).getChildView();
        optionDataSpinner = (AppCompatSpinner) findViewById(R.id.optionDataSpinner);
        requiredSpinner = (AppCompatSpinner) findViewById(R.id.requiredSpinner);
        dateSelectTxtView = (MTextView) findViewById(R.id.dateSelectTxtView);
        containerView = findViewById(R.id.containerView);
        dateArea = findViewById(R.id.dateArea);
        requiredArea = findViewById(R.id.requiredArea);

        dateSelectTxtView.setOnClickListener(new setOnClickList());
        productInfoAddBtn.setOnClickListener(new setOnClickList());
        productInfoAddBtn.setId(Utils.generateViewId());
        backImgView.setOnClickListener(new setOnClickList());

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), optionDataSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), requiredSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), dateSelectTxtView);

        setLabels();

        continueExecution();
    }

    public void setLabels() {
        titleTxt.setText("Options");

        productInfoAddBtn.setText("Add Option");

    }

    public void continueExecution() {
        getProductDetails();
    }

    public void getProductDetails() {
        requiredArea.setVisibility(View.GONE);
        containerView.setVisibility(View.GONE);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getStoreProductInfo");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
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
                        containerView.setVisibility(View.VISIBLE);
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
        optionDataID.clear();
        optionDataList.clear();
        requiredDataList.clear();

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        JSONArray optionDescDataArr = generalFunc.getJsonArray("OptionDescData", generalFunc.getJsonObject("GeneralData", responseString));
        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        if (optionDescDataArr != null) {
            optionDataID.add("");
            optionDataList.add("Choose Option");
            for (int i = 0; i < optionDescDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(optionDescDataArr, i);
                String name = generalFunc.getJsonValue("name", obj_temp);
                if (!name.equalsIgnoreCase("Select") && !name.equalsIgnoreCase("Size")) {
                    optionDataID.add(generalFunc.getJsonValue("option_id", obj_temp));
                    optionDataList.add(Utils.html2text(name));
                }
            }
        }

        requiredDataList.add("Yes");
        requiredDataList.add("No");

        ArrayAdapter<String> customerGroupAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, optionDataList);
        customerGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionDataSpinner.setAdapter(customerGroupAdapter);
        ArrayAdapter<String> requiredDateAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, requiredDataList);
        requiredDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requiredSpinner.setAdapter(requiredDateAdapter);

        optionDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                String name = optionDataList.get(position);

                dateArea.setVisibility(View.GONE);
                if (name.equalsIgnoreCase("Date") || name.equalsIgnoreCase("Delivery Date")) {
                    dateArea.setVisibility(View.VISIBLE);
                }

                if (position != 0) {
                    requiredArea.setVisibility(View.VISIBLE);
                } else {
                    requiredArea.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void generatePageError() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    backImgView.performClick();
                }
            }
        });
        generateAlert.setContentMessage("", "Some error occurred. Please check your internet or try again later.");
        generateAlert.setPositiveBtn("OK");

        generateAlert.showAlertBox();
    }

    public void updateData() {

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
        dpd.setTitle("Select Date");
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public Context getActContext() {
        return ProductOptionAddActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                ProductOptionAddActivity.super.onBackPressed();

            } else if (i == dateSelectTxtView.getId()) {
                chooseDate();
            } else if (i == productInfoAddBtn.getId()) {
                updateData();
            }
        }
    }
}
