package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.RelatedProductsRecyclerAdapter;
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

public class RelatedProductsActivity extends AppCompatActivity implements RelatedProductsRecyclerAdapter.OnItemClickListener {

    MTextView titleTxt;
    ImageView backImgView;
    GeneralFunctions generalFunc;
    ProgressBar loading;
    ErrorView errorView;

    MTextView noProductsTxtView;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    RecyclerView dataRecyclerView;
    RelatedProductsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_related_products);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        noProductsTxtView = (MTextView) findViewById(R.id.noProductsTxtView);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);

        adapter = new RelatedProductsRecyclerAdapter(getActContext(), dataList, generalFunc, false);
        dataRecyclerView.setAdapter(adapter);
        dataRecyclerView.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(this);
        backImgView.setOnClickListener(new setOnClickList());

        setLabels();

        loadRelatedProduct();
    }

    public void setLabels() {

        titleTxt.setText("Related Products");

    }

    public void loadRelatedProduct() {

        dataList.clear();
        adapter.notifyDataSetChanged();
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (noProductsTxtView.getVisibility() == View.VISIBLE) {
            noProductsTxtView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getRelatedProducts");
        parameters.put("manufacturer_id", getIntent().getStringExtra("manufacturer_id"));
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
        parameters.put("customer_id", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                if (responseString != null && !responseString.equals("")) {

                    closeLoader();
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    dataList.clear();
                    adapter.notifyDataSetChanged();
                    if (isDataAvail) {
                        JSONArray msgArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        dataList.clear();
                        adapter.notifyDataSetChanged();
                        if (msgArr != null) {

                            for (int i = 0; i < msgArr.length(); i++) {
                                JSONObject obj_cart = generalFunc.getJsonObject(msgArr, i);

                                String productName = generalFunc.getJsonValue("productName", obj_cart);
                                String product_id = generalFunc.getJsonValue("product_id", obj_cart);

                                HashMap<String, String> dataMap_products = new HashMap<>();
                                dataMap_products.put("productName", Utils.html2text(productName));
                                dataMap_products.put("product_id", product_id);

                                dataMap_products.put("TYPE", "" + RelatedProductsRecyclerAdapter.TYPE_ITEM);

                                dataList.add(dataMap_products);

                            }

                            loading.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                        noProductsTxtView.setVisibility(View.GONE);
                    } else {
                        noProductsTxtView.setVisibility(View.VISIBLE);
                    }
                } else {
                    noProductsTxtView.setVisibility(View.GONE);
                    generateErrorView();
                }
            }
        });
        exeWebServer.execute();
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
                loadRelatedProduct();
            }
        });
    }

    public Context getActContext() {
        return RelatedProductsActivity.this;
    }

    @Override
    public void onItemClickList(View v, int btn_type, int position) {

        Bundle bn = new Bundle();
        bn.putString("product_id", dataList.get(position).get("product_id"));
        bn.putString("productName", dataList.get(position).get("productName"));

        (new StartActProcess(getActContext())).setOkResult(bn);
        backImgView.performClick();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    RelatedProductsActivity.super.onBackPressed();
                    break;

            }
        }
    }
}
