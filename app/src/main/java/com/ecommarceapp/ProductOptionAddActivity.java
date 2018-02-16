package com.ecommarceapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.text.InputType;
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
import com.view.editBox.MaterialEditText;
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


    ArrayList<String> radioOptionDataID = new ArrayList<>();
    ArrayList<String> radioOptionDataList = new ArrayList<>();
    ArrayList<String> subtractStockDataList = new ArrayList<>();
    ArrayList<String> priceDataList = new ArrayList<>();
    ArrayList<String> pointsDataList = new ArrayList<>();
    ArrayList<String> weightDataList = new ArrayList<>();

    AppCompatSpinner optionDataSpinner;
    AppCompatSpinner requiredSpinner;
    AppCompatSpinner radioOptionValueSpinner;
    AppCompatSpinner subtractStockSpinner;
    AppCompatSpinner priceSpinner;
    AppCompatSpinner pointSpinner;
    AppCompatSpinner weightSpinner;

    MaterialEditText textBox;
    MaterialEditText quantityBox;
    MaterialEditText priceBox;
    MaterialEditText pointsBox;
    MaterialEditText weightBox;

    MTextView dateSelectTxtView;
    MTextView timeSelectTxtView;


    String currentSelectedName = "";
    String currentSelectedOptionId = "";

    View containerView;
    View dateArea;
    View requiredArea;
    View textArea;
    View timeArea;
    View radioArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_option_add);

        generalFunc = new GeneralFunctions(getActContext());
        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        productInfoAddBtn = ((MaterialRippleLayout) findViewById(R.id.productInfoAddBtn)).getChildView();
        optionDataSpinner = (AppCompatSpinner) findViewById(R.id.optionDataSpinner);
        priceSpinner = (AppCompatSpinner) findViewById(R.id.priceSpinner);
        radioOptionValueSpinner = (AppCompatSpinner) findViewById(R.id.radioOptionValueSpinner);
        subtractStockSpinner = (AppCompatSpinner) findViewById(R.id.subtractStockSpinner);
        weightSpinner = (AppCompatSpinner) findViewById(R.id.weightSpinner);
        pointSpinner = (AppCompatSpinner) findViewById(R.id.pointSpinner);
        requiredSpinner = (AppCompatSpinner) findViewById(R.id.requiredSpinner);
        dateSelectTxtView = (MTextView) findViewById(R.id.dateSelectTxtView);
        timeSelectTxtView = (MTextView) findViewById(R.id.timeSelectTxtView);
        containerView = findViewById(R.id.containerView);
        dateArea = findViewById(R.id.dateArea);
        requiredArea = findViewById(R.id.requiredArea);
        textArea = findViewById(R.id.textArea);
        timeArea = findViewById(R.id.timeArea);
        radioArea = findViewById(R.id.radioArea);
        textBox = (MaterialEditText) findViewById(R.id.textBox);
        quantityBox = (MaterialEditText) findViewById(R.id.quantityBox);
        priceBox = (MaterialEditText) findViewById(R.id.priceBox);
        pointsBox = (MaterialEditText) findViewById(R.id.pointsBox);
        weightBox = (MaterialEditText) findViewById(R.id.weightBox);

        dateSelectTxtView.setOnClickListener(new setOnClickList());
        productInfoAddBtn.setOnClickListener(new setOnClickList());
        productInfoAddBtn.setId(Utils.generateViewId());
        backImgView.setOnClickListener(new setOnClickList());

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), subtractStockSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), weightSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), priceSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), pointSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), optionDataSpinner);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), radioOptionValueSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), requiredSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), dateSelectTxtView);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), timeSelectTxtView);

        setLabels();

        continueExecution();
    }

    public void setLabels() {
        titleTxt.setText("Options");

        productInfoAddBtn.setText("Add Option");

        textBox.setBothText("Option value", "Enter Option Value");
        quantityBox.setBothText("Quantity", "Enter quantity");
        priceBox.setBothText("Price", "Enter price");
        pointsBox.setBothText("Points", "Enter points");
        weightBox.setBothText("Weight", "Enter weight");


        priceBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        quantityBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        pointsBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        weightBox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
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
        radioOptionDataID.clear();
        radioOptionDataList.clear();
        subtractStockDataList.clear();
        priceDataList.clear();
        pointsDataList.clear();
        weightDataList.clear();

        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        JSONArray optionDescDataArr = generalFunc.getJsonArray("OptionDescData", generalFunc.getJsonObject("GeneralData", responseString));

        JSONArray optionValueDescDataArr = generalFunc.getJsonArray("OptionValueDescData", generalFunc.getJsonObject("GeneralData", responseString));

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        String radioOptionId = "";

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
                if (name.equalsIgnoreCase("radio")) {
                    radioOptionId = generalFunc.getJsonValue("option_id", obj_temp);
                }
            }
        }

        if (!radioOptionId.equals("") && optionValueDescDataArr != null) {
            for (int i = 0; i < optionValueDescDataArr.length(); i++) {
                JSONObject temp_obj = generalFunc.getJsonObject(optionValueDescDataArr, i);
                String option_id = generalFunc.getJsonValue("option_id", temp_obj);
                if (option_id.equalsIgnoreCase(radioOptionId)) {

                    radioOptionDataID.add(generalFunc.getJsonValue("option_id", temp_obj));
                    radioOptionDataList.add(Utils.html2text(generalFunc.getJsonValue("name", temp_obj)));
                }
            }

        }


        ArrayAdapter<String> radioOptionAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, radioOptionDataList);
        radioOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radioOptionValueSpinner.setAdapter(radioOptionAdapter);


        requiredDataList.add("Yes");
        requiredDataList.add("No");

        subtractStockDataList.add("Yes");
        subtractStockDataList.add("No");

        weightDataList.add("+");
        weightDataList.add("-");

        pointsDataList.add("+");
        pointsDataList.add("-");

        priceDataList.add("+");
        priceDataList.add("-");

        ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, weightDataList);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(weightAdapter);

        ArrayAdapter<String> pointsAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, pointsDataList);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointSpinner.setAdapter(pointsAdapter);

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, priceDataList);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);

        ArrayAdapter<String> subTractStockAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, subtractStockDataList);
        subTractStockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subtractStockSpinner.setAdapter(subTractStockAdapter);

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
                textArea.setVisibility(View.GONE);
                timeArea.setVisibility(View.GONE);
                radioArea.setVisibility(View.GONE);
                if (name.equalsIgnoreCase("Date") || name.equalsIgnoreCase("Delivery Date") || name.equalsIgnoreCase("Date & Time")) {
                    dateArea.setVisibility(View.VISIBLE);
                }

                if (name.equalsIgnoreCase("text") || name.equalsIgnoreCase("textArea")) {
                    textArea.setVisibility(View.VISIBLE);
                }
                if (name.equalsIgnoreCase("time")) {
                    timeArea.setVisibility(View.VISIBLE);
                }
                if (name.equalsIgnoreCase("radio") || name.equalsIgnoreCase("Checkbox")) {
                    radioArea.setVisibility(View.VISIBLE);
                }

                if (position != 0) {
                    currentSelectedName = name;
                    currentSelectedOptionId = optionDataID.get(position);
                    requiredArea.setVisibility(View.VISIBLE);
                } else {
                    currentSelectedName = "";
                    currentSelectedOptionId = "";
                    dateArea.setVisibility(View.GONE);
                    textArea.setVisibility(View.GONE);
                    timeArea.setVisibility(View.GONE);
                    radioArea.setVisibility(View.GONE);
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
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateProductOptions");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
        parameters.put("option_id", optionDataID.get(optionDataSpinner.getSelectedItemPosition()));

        if (currentSelectedName.equalsIgnoreCase("date") || currentSelectedName.equalsIgnoreCase("Delivery Date") || currentSelectedName.equalsIgnoreCase("Date & Time")) {
            parameters.put("value", dateSelectTxtView.getText().toString());

        } else if (currentSelectedName.equalsIgnoreCase("time")) {
            parameters.put("value", timeSelectTxtView.getText().toString());

        } else if (currentSelectedName.equalsIgnoreCase("text") || currentSelectedName.equalsIgnoreCase("textArea")) {
            parameters.put("value", textBox.getText().toString());
        }

        parameters.put("required", requiredDataList.get(requiredSpinner.getSelectedItemPosition()).equalsIgnoreCase("Yes") ? "1" : "0");

        if (currentSelectedName.equalsIgnoreCase("radio") || currentSelectedName.equalsIgnoreCase("checkbox")) {

            parameters.put("option_value_id", radioOptionDataID.get(radioOptionValueSpinner.getSelectedItemPosition()));
            parameters.put("quantity", Utils.getText(quantityBox));
            parameters.put("price", Utils.getText(priceBox));
            parameters.put("points", Utils.getText(pointsBox));
            parameters.put("weight", Utils.getText(weightBox));
            parameters.put("subtract", subtractStockDataList.get(subtractStockSpinner.getSelectedItemPosition()).equalsIgnoreCase("Yes") ? "1" : "0");
            parameters.put("price_prefix", priceDataList.get(subtractStockSpinner.getSelectedItemPosition()));
            parameters.put("points_prefix", pointsDataList.get(pointSpinner.getSelectedItemPosition()));
            parameters.put("weight_prefix", weightDataList.get(weightSpinner.getSelectedItemPosition()));
        } else {

            parameters.put("option_value_id", "0");
        }

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {

                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
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
