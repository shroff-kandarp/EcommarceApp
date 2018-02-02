package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.AttributesRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.models.AttributesParentItem;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectAttributesActivity extends AppCompatActivity implements ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {

    MTextView titleTxt;
    //    ImageView backImgView;
    MTextView noAttributesTxtView;

    GeneralFunctions generalFunc;
    ProgressBar loading;
    ErrorView errorView;

    ImageView backImgView;
    ExpandableListView lvExp;
    AttributesRecyclerAdapter adapter;

    ArrayList<AttributesParentItem> dataList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_attributes);

        generalFunc = new GeneralFunctions(getActContext());

        lvExp = (ExpandableListView) findViewById(R.id.lvExp);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        noAttributesTxtView = (MTextView) findViewById(R.id.noAttributesTxtView);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        adapter = new AttributesRecyclerAdapter(getActContext(), dataList);
        lvExp.setAdapter(adapter);

        backImgView.setOnClickListener(new setOnClickList());

        lvExp.setOnChildClickListener(this);
        lvExp.setOnGroupClickListener(this);
        setLabels();


        loadAttributes();

    }

    public void setLabels() {

        titleTxt.setText("Related Products");

    }

    public void loadAttributes() {

        dataList.clear();
        adapter.notifyDataSetChanged();
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (noAttributesTxtView.getVisibility() == View.VISIBLE) {
            noAttributesTxtView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getAllAttributes");
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
                        JSONObject obj_cat = generalFunc.getJsonObject(msgArr, i);
                        JSONArray subCategoriesArr = generalFunc.getJsonArray("SubList", obj_cat);

                        String name = generalFunc.getJsonValue("name", obj_cat);
                        String attribute_group_id = generalFunc.getJsonValue("attribute_group_id", obj_cat);

                        AttributesParentItem parentItem = new AttributesParentItem();
//                                Utils.printLog("CatName","::name::"+name);

                        parentItem.setAttributeGroup_id(attribute_group_id);
                        parentItem.setName(name);

                        ArrayList<HashMap<String, String>> subCatList = new ArrayList<>();
                        for (int j = 0; j < subCategoriesArr.length(); j++) {
                            JSONObject obj_sub = generalFunc.getJsonObject(subCategoriesArr, j);

                            String subAttName = generalFunc.getJsonValue("name", obj_sub);
                            String subAttId = generalFunc.getJsonValue("attribute_id", obj_sub);

                            HashMap<String, String> dataMap_cat = new HashMap<>();
                            dataMap_cat.put("name", subAttName);
                            dataMap_cat.put("attribute_id", subAttId);

                            subCatList.add(dataMap_cat);
                        }
                        parentItem.setSubAttributeList(subCatList);
//                        dataMap_cat.put("TYPE", "" + adapter.TYPE_HEADER);
                        dataList.add(parentItem);
                    }

                    adapter.notifyDataSetChanged();
                }
            } else {
                noAttributesTxtView.setVisibility(View.VISIBLE);
            }
        } else {
            noAttributesTxtView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long l) {
//        AttributesParentItem parentItem = dataList.get(position);
//        if (parentItem.getSubAttributeList().size() == 0) {
//            Bundle bn = new Bundle();
//            bn.putString("attribute_id", parentItem.getAttribute_id());
//            bn.putString("name", parentItem.getName());
//
//            if(getCallingActivity() != null){
//                (new StartActProcess(getActContext())).setOkResult(bn);
//                SelectAttributesActivity.super.onBackPressed();
//            }else{
//                (new StartActProcess(getActContext())).startActWithData(ListAllProductsActivity.class, bn);
//            }
//        }
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
        AttributesParentItem parentItem = dataList.get(groupPosition);
        HashMap<String, String> childItem = parentItem.getSubAttributeList().get(childPosition);

//        Bundle bn = new Bundle();
//        bn.putString("attribute_id", childItem.get("attribute_id"));
//
//        bn.putString("name", parentItem.getName() + " > " + childItem.get("name"));
//        (new StartActProcess(getActContext())).setOkResult(bn);
//        SelectAttributesActivity.super.onBackPressed();

        showDescriptionBox(childItem.get("attribute_id"));

        return false;
    }

    public void showDescriptionBox(final String attribute_id) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActContext());
        builder.setTitle("Enter Description");

        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.input_box_view, null);
        builder.setView(dialogView);

        final MaterialEditText input = (MaterialEditText) dialogView.findViewById(R.id.editBox);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Utils.hideKeyboard((Activity) getActContext());
                if (!input.getText().toString().trim().equals("")) {
                    addAttribute(input.getText().toString().trim(), attribute_id);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.support.v7.app.AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    public void addAttribute(String description, String attribute_id) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "addProductAttribute");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
        parameters.put("attribute_id", attribute_id);
        parameters.put("description", description);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        generateSuccessMsg(responseString);
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

    public void generateSuccessMsg(String responseString) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {

                    (new StartActProcess(getActContext())).setOkResult();
                    SelectAttributesActivity.super.onBackPressed();
                }
            }
        });
        generateAlert.setContentMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
        generateAlert.setPositiveBtn("OK");

        generateAlert.showAlertBox();
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    public void generateErrorView() {

        closeLoader();

        generalFunc.generateErrorView(errorView, "", "Please check your internet connection OR try again later.");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                loadAttributes();
            }
        });
    }

    public Context getActContext() {
        return SelectAttributesActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    SelectAttributesActivity.super.onBackPressed();
                    break;

            }
        }
    }
}