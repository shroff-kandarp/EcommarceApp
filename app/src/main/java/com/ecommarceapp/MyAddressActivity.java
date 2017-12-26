package com.ecommarceapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.CustomerAddressRecycleAdapter;
import com.general.files.AddDrawer;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MyAddressActivity extends AppCompatActivity implements CustomerAddressRecycleAdapter.OnItemClickListener {
    MTextView titleTxt;
    MTextView noAddressTxtView;
    GeneralFunctions generalFunc;
    ProgressBar loading;
    ErrorView errorView;

    AddDrawer addDrawer;
    MButton btn_type2;
    MButton addAddressBtn;

    RecyclerView dataRecyclerView;
    CustomerAddressRecycleAdapter adapter;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    View noAddressArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);

        generalFunc = new GeneralFunctions(getActContext());

        addDrawer = new AddDrawer(getActContext());
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();
        addAddressBtn = ((MaterialRippleLayout) findViewById(R.id.addAddressBtn)).getChildView();

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        noAddressTxtView = (MTextView) findViewById(R.id.noAddressTxtView);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        noAddressArea = findViewById(R.id.noAddressArea);
        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);

        setLabels();
        btn_type2.setId(Utils.generateViewId());
        addAddressBtn.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(new setOnClickList());
        addAddressBtn.setOnClickListener(new setOnClickList());

        adapter = new CustomerAddressRecycleAdapter(getActContext(), dataList, generalFunc, false);
        adapter.setOnItemClickListener(this);
        dataRecyclerView.setAdapter(adapter);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), noAddressArea);
        loadCustomerAddresses();

        if (getCallingActivity() != null) {
            (findViewById(R.id.searchImgView)).setVisibility(View.GONE);
            (findViewById(R.id.listChangeImgView)).setVisibility(View.GONE);
            (findViewById(R.id.cartArea)).setVisibility(View.GONE);
            (findViewById(R.id.menuImgView)).setVisibility(View.GONE);
            (findViewById(R.id.backImgView)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.backImgView)).setOnClickListener(new setOnClickList());

        }
    }

    private void loadCustomerAddresses() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }
        noAddressArea.setVisibility(View.GONE);
        addAddressBtn.setVisibility(View.GONE);
        dataList.clear();
        adapter.notifyDataSetChanged();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "loadCustomerAddress");
        parameters.put("customer_id", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                if (responseString != null && !responseString.equals("")) {

                    closeLoader();

                    loadData(responseString);
                } else {
                    generateErrorView();
                }
            }
        });
        exeWebServer.execute();
    }

    public void loadData(String responseString) {
        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail == true) {

                JSONArray msgArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                if (msgArr != null) {
                    for (int i = 0; i < msgArr.length(); i++) {

                        JSONObject obj_temp = generalFunc.getJsonObject(msgArr, i);

                        HashMap<String, String> map = new HashMap<>();

                        map.put("address_id", generalFunc.getJsonValue("address_id", obj_temp).trim());
                        map.put("customer_id", generalFunc.getJsonValue("customer_id", obj_temp).trim());
                        map.put("firstname", generalFunc.getJsonValue("firstname", obj_temp).trim());
                        map.put("lastname", generalFunc.getJsonValue("lastname", obj_temp).trim());
                        map.put("company", generalFunc.getJsonValue("company", obj_temp).trim());
                        map.put("address_1", generalFunc.getJsonValue("address_1", obj_temp).trim());
                        map.put("address_2", generalFunc.getJsonValue("address_2", obj_temp).trim());
                        map.put("city", generalFunc.getJsonValue("city", obj_temp).trim());
                        map.put("postcode", generalFunc.getJsonValue("postcode", obj_temp).trim());
                        map.put("country_id", generalFunc.getJsonValue("country_id", obj_temp).trim());
                        map.put("zone_id", generalFunc.getJsonValue("zone_id", obj_temp).trim());
                        map.put("custom_field", generalFunc.getJsonValue("custom_field", obj_temp).trim());
                        map.put("checkpoint_id", generalFunc.getJsonValue("checkpoint_id", obj_temp).trim());
                        map.put("TYPE", "" + CustomerAddressRecycleAdapter.TYPE_ITEM);

                        dataList.add(map);

                    }

                    adapter.notifyDataSetChanged();
                }
                addAddressBtn.setVisibility(View.VISIBLE);
            } else {
                noAddressTxtView.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                noAddressArea.setVisibility(View.VISIBLE);
                addAddressBtn.setVisibility(View.GONE);
            }
        } else {
            generateErrorView();
        }
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "Error", "Please check your internet connection and try again");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                loadCustomerAddresses();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (addDrawer != null) {
            addDrawer.findUserCartCount();
        }
    }

    @Override
    public void onItemClickList(View v, int btnType, int position) {
        switch (btnType) {
            case -1:
                Intent intent = new Intent();
                intent.putExtra("DATA", dataList.get(position));
                setResult(RESULT_OK, intent);
                MyAddressActivity.super.onBackPressed();
                break;
            case 0:
                removeSelectedAddress(position);
                break;
            case 1:
                editSelectedAddress(position);
                break;
        }
    }

    public void removeSelectedAddress(final int position) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                if (btn_id == 1) {
                    generateAlert.closeAlertBox();
                    confirmRemoveAddress(position);
                } else if (btn_id == 0) {
                    generateAlert.closeAlertBox();
                }
            }
        });
        generateAlert.setContentMessage("Confirm", "Are you sure, you want to remove selected address?");
        generateAlert.setPositiveBtn("YES");
        generateAlert.setNegativeBtn("NO");
        generateAlert.showAlertBox();
    }

    public void confirmRemoveAddress(int position) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "deleteCustomerAddress");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("address_id", dataList.get(position).get("address_id"));


        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        loadCustomerAddresses();
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

    public void editSelectedAddress(int position) {

        Intent int_edit_address = new Intent(MyAddressActivity.this, AddAddressActivity.class);
        int_edit_address.putExtra("ADDRESS_DATA", dataList.get(position));

        startActivityForResult(int_edit_address, Utils.ADD_ADDRESS_REQ_CODE);
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.backImgView) {
                MyAddressActivity.super.onBackPressed();

            } else if (i == R.id.menuImgView) {
                MyAddressActivity.super.onBackPressed();

            } else if (i == btn_type2.getId()) {
                openAddAddress();

            } else if (i == addAddressBtn.getId()) {
                openAddAddress();

            }
        }
    }

    public void openAddAddress() {
        (new StartActProcess(getActContext())).startActForResult(AddAddressActivity.class, Utils.ADD_ADDRESS_REQ_CODE);
    }

    public void setLabels() {
        titleTxt.setText("Address book");
        btn_type2.setText("Add New Address");
        addAddressBtn.setText("Add New Address");
    }


    public Context getActContext() {
        return MyAddressActivity.this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ADD_ADDRESS_REQ_CODE && resultCode == RESULT_OK) {
            loadCustomerAddresses();
        }
    }
}
