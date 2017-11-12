package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.adapter.AllCategoriesRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AllCategoriesActivity extends AppCompatActivity implements AllCategoriesRecyclerAdapter.OnItemClickListener {
    MTextView titleTxt;
    ImageView backImgView;

    GeneralFunctions generalFunc;
    ProgressBar loading;
    ErrorView errorView;

    RecyclerView dataRecyclerView;
    AllCategoriesRecyclerAdapter adapter;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        generalFunc = new GeneralFunctions(getActContext());

        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);

        adapter = new AllCategoriesRecyclerAdapter(getActContext(), dataList, generalFunc, false);
        dataRecyclerView.setAdapter(adapter);
        dataRecyclerView.setNestedScrollingEnabled(false);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dataRecyclerView.getContext(),
                LinearLayout.VERTICAL);
        dataRecyclerView.addItemDecoration(dividerItemDecoration);

        adapter.setOnItemClickListener(this);
        setLabels();

        backImgView.setOnClickListener(new setOnClickList());

        loadAllCategories();
    }

    @Override
    public void onItemClickList(View v, int position) {
        HashMap<String, String> data = dataList.get(position);
        if (data.get("TYPE").equals("" + adapter.TYPE_HEADER)) {
            Bundle bn = new Bundle();
            bn.putString("category_id", data.get("category_id"));
            bn.putString("name", data.get("name"));
            (new StartActProcess(getActContext())).startActWithData(ListAllProductsActivity.class, bn);
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    AllCategoriesActivity.super.onBackPressed();
                    break;

            }
        }
    }

    public void setLabels() {
        titleTxt.setText("All Categories");
    }

    public void loadAllCategories() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getAllCategories");

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
                        String name = generalFunc.getJsonValue("name", obj_cat);
                        String category_id = generalFunc.getJsonValue("category_id", obj_cat);

//                                Utils.printLog("CatName","::name::"+name);
                        HashMap<String, String> dataMap_cat = new HashMap<>();
                        dataMap_cat.put("name", name);
                        dataMap_cat.put("category_id", category_id);
                        dataMap_cat.put("TYPE", "" + adapter.TYPE_HEADER);
                        dataList.add(dataMap_cat);
                    }

                    adapter.notifyDataSetChanged();
                }
            } else {

            }
        } else {

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
                loadAllCategories();
            }
        });
    }

    public Context getActContext() {
        return AllCategoriesActivity.this;
    }
}
