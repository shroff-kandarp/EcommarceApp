package com.ecommarceapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
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

public class ProductDiscountAddActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;

    MTextView titleTxt;
    ImageView backImgView;
    MaterialEditText quantityBox;
    MaterialEditText priorityBox;
    MaterialEditText priceBox;
    MTextView dateStartSelectTxtView;
    MTextView dateEndSelectTxtView;

    ArrayList<String> customerGroupDataID = new ArrayList<>();
    ArrayList<String> customerGroupDataList = new ArrayList<>();

    AppCompatSpinner customerGroupSpinner;

    MButton productInfoAddBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_discount_add);

        generalFunc = new GeneralFunctions(getActContext());
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        dateStartSelectTxtView = (MTextView) findViewById(R.id.dateStartSelectTxtView);
        dateEndSelectTxtView = (MTextView) findViewById(R.id.dateEndSelectTxtView);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        customerGroupSpinner = (AppCompatSpinner) findViewById(R.id.customerGroupSpinner);

        quantityBox = (MaterialEditText) findViewById(R.id.quantityBox);
        priorityBox = (MaterialEditText) findViewById(R.id.priorityBox);
        priceBox = (MaterialEditText) findViewById(R.id.priceBox);
        productInfoAddBtn = ((MaterialRippleLayout) findViewById(R.id.productInfoAddBtn)).getChildView();

        productInfoAddBtn.setOnClickListener(new setOnClickList());
        productInfoAddBtn.setId(Utils.generateViewId());
        backImgView.setOnClickListener(new setOnClickList());
        dateStartSelectTxtView.setOnClickListener(new setOnClickList());
        dateEndSelectTxtView.setOnClickListener(new setOnClickList());
        setLabels();


        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), dateStartSelectTxtView);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), dateEndSelectTxtView);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), customerGroupSpinner);

        dateStartSelectTxtView.setText(getCurrentDate());
        dateEndSelectTxtView.setText(getCurrentDate());
        continueExecution();
    }

    public void setLabels() {
        titleTxt.setText(getIntent().getStringExtra("PAGE_MODE").equals("DISCOUNT") ? "Add Discount" : "Add Special");
        quantityBox.setBothText("Quantity", "Enter quantity");
        priorityBox.setBothText("Priority", "Enter priority");
        priceBox.setBothText("Price", "Enter price");

        if (!getIntent().getStringExtra("PAGE_MODE").equals("DISCOUNT")) {
            quantityBox.setVisibility(View.GONE);

            productInfoAddBtn.setText("Add Product Special");
        } else {

            productInfoAddBtn.setText("Add Product Discount");
        }

        quantityBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        priorityBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }

    public void continueExecution() {
        getProductDetails();
    }

    public void getProductDetails() {
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
        customerGroupDataID.clear();
        customerGroupDataList.clear();

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        JSONArray productRewardData = generalFunc.getJsonArray("ProductRewardData", responseString);
        String productTag = generalFunc.getJsonValue("ProductTag", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        JSONArray customerGroupDataArr = generalFunc.getJsonArray("CustomerGroupData", generalFunc.getJsonObject("GeneralData", responseString));
        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        if (customerGroupDataArr != null) {
            for (int i = 0; i < customerGroupDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(customerGroupDataArr, i);
                customerGroupDataID.add(generalFunc.getJsonValue("customer_group_id", obj_temp));
                customerGroupDataList.add(generalFunc.getJsonValue("name", obj_temp));
            }
        }

        ArrayAdapter<String> customerGroupAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, customerGroupDataList);
        customerGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customerGroupSpinner.setAdapter(customerGroupAdapter);

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

    public void chooseStartDate() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String day = dayOfMonth < 10 ? ("0" + dayOfMonth) : ("" + dayOfMonth);
                        String month = monthOfYear < 10 ? ("0" + monthOfYear) : ("" + monthOfYear);

                        dateStartSelectTxtView.setText("" + year + "-" + month + "-" + day);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle("Start Date");
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void chooseEndDate() {

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        String day = dayOfMonth < 10 ? ("0" + dayOfMonth) : ("" + dayOfMonth);
                        String month = monthOfYear < 10 ? ("0" + monthOfYear) : ("" + monthOfYear);

                        dateEndSelectTxtView.setText("" + year + "-" + month + "-" + day);
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle("From Date");
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void updateData() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", getIntent().getStringExtra("product_id"));

        if (getIntent().getStringExtra("PAGE_MODE").equals("DISCOUNT")) {
            parameters.put("type", "addProductDiscount");
            parameters.put("quantity", Utils.getText(quantityBox));
        } else {
            parameters.put("type", "addProductSpecial");
        }
        parameters.put("priority", Utils.getText(priorityBox));
        parameters.put("price", Utils.getText(priceBox));
        parameters.put("date_start", dateStartSelectTxtView.getText().toString());
        parameters.put("date_end", dateEndSelectTxtView.getText().toString());
        parameters.put("customer_group_id", customerGroupDataID.get(customerGroupSpinner.getSelectedItemPosition()));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        (new StartActProcess(getActContext())).setOkResult();
                        backImgView.performClick();
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

    public Context getActContext() {
        return ProductDiscountAddActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                ProductDiscountAddActivity.super.onBackPressed();

            } else if (i == R.id.dateStartSelectTxtView) {
                chooseStartDate();

            } else if (i == R.id.dateEndSelectTxtView) {
                chooseEndDate();

            } else if (i == productInfoAddBtn.getId()) {
                updateData();

            }
        }
    }
}
