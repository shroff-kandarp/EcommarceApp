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
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

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
        timeSelectTxtView.setOnClickListener(new setOnClickList());
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
        if (getIntent().getStringExtra("option_id") != null) {

            productInfoAddBtn.setText("Edit Option");
        } else {

            productInfoAddBtn.setText("Add Option");
        }

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
        productInfoAddBtn.setVisibility(View.GONE);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getStoreProductInfo");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
        parameters.put("isLoadGeneralData", "Yes");
        if (getIntent().getStringExtra("product_option_id") != null) {
            parameters.put("product_option_id", getIntent().getStringExtra("product_option_id"));
            parameters.put("option_id", getIntent().getStringExtra("option_id"));
        }

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

    JSONObject productOptionData = null;

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
        JSONObject productOptionData = generalFunc.getJsonObject("ProductOptionData", responseString);
        this.productOptionData = productOptionData;

        JSONArray optionDescDataArr = generalFunc.getJsonArray("OptionDescData", generalFunc.getJsonObject("GeneralData", responseString));

        JSONArray optionValueDescDataArr = generalFunc.getJsonArray("OptionValueDescData", generalFunc.getJsonObject("GeneralData", responseString));

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        String optionDataId = "";
        int selectedOptionId = -1;

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
                if (getIntent().getStringExtra("option_id") != null && getIntent().getStringExtra("option_id").equalsIgnoreCase(generalFunc.getJsonValue("option_id", obj_temp))) {
                    selectedOptionId = i + 1;
                }
                if (name.equalsIgnoreCase("radio")) {
                    optionDataId = generalFunc.getJsonValue("option_id", obj_temp);
                }
            }
        }

        int selectedOptionValueId = -1;
        if (!optionDataId.equals("") && optionValueDescDataArr != null) {
            for (int i = 0; i < optionValueDescDataArr.length(); i++) {
                JSONObject temp_obj = generalFunc.getJsonObject(optionValueDescDataArr, i);
                String option_id = generalFunc.getJsonValue("option_id", temp_obj);
                if (option_id.equalsIgnoreCase(optionDataId)) {

                    radioOptionDataID.add(generalFunc.getJsonValue("option_value_id", temp_obj));
                    radioOptionDataList.add(Utils.html2text(generalFunc.getJsonValue("name", temp_obj)));

                    if (getIntent().getStringExtra("option_value_id") != null && generalFunc.getJsonValue("option_value_id", temp_obj).equals(getIntent().getStringExtra("option_value_id"))) {
                        selectedOptionValueId = radioOptionDataID.size() - 1;
                    }
                }

            }

        }

        ArrayAdapter<String> radioOptionAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, radioOptionDataList);
        radioOptionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        radioOptionValueSpinner.setAdapter(radioOptionAdapter);

        if (selectedOptionValueId != -1) {
            radioOptionValueSpinner.setSelection(selectedOptionValueId);
        }

        int selectedRequiredId = -1;
        requiredDataList.add("Yes");
        requiredDataList.add("No");

        if (getIntent().getStringExtra("option_id") != null && productOptionData != null) {
            String required = generalFunc.getJsonValue("required", productOptionData);
            selectedRequiredId = required.equalsIgnoreCase("no") ? 1 : 0;
        }

        int selectedSubtractId = -1;

        subtractStockDataList.add("Yes");
        subtractStockDataList.add("No");
        if (getIntent().getStringExtra("option_id") != null && productOptionData != null) {
            String subtract = generalFunc.getJsonValue("subtract", productOptionData);
            selectedSubtractId = subtract.equalsIgnoreCase("no") ? 1 : 0;
        }

        int selectedWeightDataId = -1;
        weightDataList.add("+");
        weightDataList.add("-");
        if (getIntent().getStringExtra("option_id") != null && productOptionData != null) {
            String weight_prefix = generalFunc.getJsonValue("weight_prefix", productOptionData);
            selectedWeightDataId = weight_prefix.equalsIgnoreCase("-") ? 1 : 0;
        }

        int selectedPointsId = -1;
        pointsDataList.add("+");
        pointsDataList.add("-");

        if (getIntent().getStringExtra("option_id") != null && productOptionData != null) {
            String points_prefix = generalFunc.getJsonValue("points_prefix", productOptionData);
            selectedPointsId = points_prefix.equalsIgnoreCase("-") ? 1 : 0;
        }

        int priceDataId = -1;
        priceDataList.add("+");
        priceDataList.add("-");
        if (getIntent().getStringExtra("option_id") != null && productOptionData != null) {
            String price_prefix = generalFunc.getJsonValue("price_prefix", productOptionData);
            priceDataId = price_prefix.equalsIgnoreCase("+") ? 0 : 1;
        }

        ArrayAdapter<String> weightAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, weightDataList);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightSpinner.setAdapter(weightAdapter);

        if (selectedWeightDataId != -1) {
            weightSpinner.setSelection(selectedWeightDataId);
        }

        ArrayAdapter<String> pointsAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, pointsDataList);
        pointsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointSpinner.setAdapter(pointsAdapter);
        if (selectedPointsId != -1) {
            pointSpinner.setSelection(selectedPointsId);
        }

        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, priceDataList);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(priceAdapter);
        if (priceDataId != -1) {
            priceSpinner.setSelection(priceDataId);
        }

        ArrayAdapter<String> subTractStockAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, subtractStockDataList);
        subTractStockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subtractStockSpinner.setAdapter(subTractStockAdapter);

        if (selectedSubtractId != -1) {
            subtractStockSpinner.setSelection(selectedSubtractId);
        }
        ArrayAdapter<String> customerGroupAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, optionDataList);
        customerGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        optionDataSpinner.setAdapter(customerGroupAdapter);


        ArrayAdapter<String> requiredDateAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, requiredDataList);
        requiredDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requiredSpinner.setAdapter(requiredDateAdapter);

        if (selectedRequiredId != -1) {
            requiredSpinner.setSelection(selectedRequiredId);
        }

        optionDataSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                setOptionSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (selectedOptionId != -1) {
            optionDataSpinner.setSelection(selectedOptionId);
            optionDataSpinner.setEnabled(false);
            setOptionSelection(selectedOptionId);
        }
        if (productOptionData != null) {
            quantityBox.setText(generalFunc.getJsonValue("quantity", productOptionData));
            priceBox.setText(generalFunc.getJsonValue("price", productOptionData));
            pointsBox.setText(generalFunc.getJsonValue("points", productOptionData));
            weightBox.setText(generalFunc.getJsonValue("weight", productOptionData));

        }
    }


    public void setOptionSelection(int position) {
        String name = optionDataList.get(position);

        Utils.printLog("OptionName", "::" + name);
        dateArea.setVisibility(View.GONE);
        textArea.setVisibility(View.GONE);
        timeArea.setVisibility(View.GONE);
        radioArea.setVisibility(View.GONE);
        if (name.equalsIgnoreCase("Date") || name.equalsIgnoreCase("Delivery Date") || name.equalsIgnoreCase("Date & Time")) {
            dateArea.setVisibility(View.VISIBLE);

            if (productOptionData != null) {
                dateSelectTxtView.setText(generalFunc.getJsonValue("value", productOptionData));
            }
        }

        if (name.equalsIgnoreCase("text") || name.equalsIgnoreCase("textArea")) {
            textArea.setVisibility(View.VISIBLE);
            if (productOptionData != null) {
                textBox.setText(generalFunc.getJsonValue("value", productOptionData));
            }

        }
        if (name.equalsIgnoreCase("time")) {
            timeArea.setVisibility(View.VISIBLE);
            if (productOptionData != null) {
                timeSelectTxtView.setText(generalFunc.getJsonValue("value", productOptionData));
            }

        }
        if (name.equalsIgnoreCase("radio") || name.equalsIgnoreCase("Checkbox")) {
            radioArea.setVisibility(View.VISIBLE);
        }

        if (position != 0) {
            currentSelectedName = name;
            currentSelectedOptionId = optionDataID.get(position);
            requiredArea.setVisibility(View.VISIBLE);
            productInfoAddBtn.setVisibility(View.VISIBLE);
        } else {
            currentSelectedName = "";
            currentSelectedOptionId = "";
            dateArea.setVisibility(View.GONE);
            textArea.setVisibility(View.GONE);
            timeArea.setVisibility(View.GONE);
            radioArea.setVisibility(View.GONE);
            requiredArea.setVisibility(View.GONE);
            productInfoAddBtn.setVisibility(View.GONE);
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
        if (getIntent().getStringExtra("product_option_id") != null) {
            parameters.put("product_option_id", getIntent().getStringExtra("product_option_id"));
            parameters.put("option_id", getIntent().getStringExtra("option_id"));
            parameters.put("product_option_value_id", getIntent().getStringExtra("product_option_value_id"));

            parameters.put("isUpdate", "Yes");
        }
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
            parameters.put("price_prefix", priceDataList.get(priceSpinner.getSelectedItemPosition()));
            parameters.put("points_prefix", pointsDataList.get(pointSpinner.getSelectedItemPosition()));
            parameters.put("weight_prefix", weightDataList.get(weightSpinner.getSelectedItemPosition()));
        } else {

            parameters.put("option_value_id", "0");
        }

        Utils.printLog("price_prefix", "::" + parameters.get("price_prefix"));
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

                        if (currentSelectedName.equalsIgnoreCase("Date & Time") || currentSelectedName.equalsIgnoreCase("time")) {
                            chooseTime("" + year + "-" + month + "-" + day);
                        } else {
                            dateSelectTxtView.setText("" + year + "-" + month + "-" + day);
                        }
                    }
                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setTitle("Select Date");
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void chooseTime(final String date) {

        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
                        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
                        String minuteString = minute < 10 ? "0" + minute : "" + minute;
                        String secondString = second < 10 ? "0" + second : "" + second;
                        String time = hourString + ":" + minuteString + ":" + secondString;
                        Utils.printLog("Time", ":" + time);

                        if (date.equals("")) {
                            timeSelectTxtView.setText("" + time);

                        } else {
                            dateSelectTxtView.setText("" + date + " " + time);
                        }

                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                true
        );
        tpd.setTitle("From Time");
        tpd.show(getFragmentManager(), "Timepickerdialog");
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
            } else if (i == timeSelectTxtView.getId()) {
                chooseTime("");
            } else if (i == productInfoAddBtn.getId()) {
                updateData();
            }
        }
    }
}
