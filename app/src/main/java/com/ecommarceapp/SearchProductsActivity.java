package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.MainPageCategoryRecycleAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;
import com.view.editBox.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchProductsActivity extends AppCompatActivity implements TextWatcher {

    ImageView backImgView;
    MaterialEditText searchBox;

    RecyclerView productsRecyclerView;

    GeneralFunctions generalFunc;

    MTextView noProductsTxtView;

    String currentSearchQuery;
    ProgressBar loading;
    ErrorView errorView;

    MainPageCategoryRecycleAdapter adapter;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        setContentView(R.layout.activity_search_products);

        generalFunc = new GeneralFunctions(getActContext());

        backImgView = (ImageView) findViewById(R.id.backImgView);
        noProductsTxtView = (MTextView) findViewById(R.id.noProductsTxtView);
        searchBox = (MaterialEditText) findViewById(R.id.searchBox);
        productsRecyclerView = (RecyclerView) findViewById(R.id.productsRecyclerView);

        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);

        adapter = new MainPageCategoryRecycleAdapter(getActContext(), dataList, generalFunc, false);

        productsRecyclerView.setAdapter(adapter);

        productsRecyclerView.setNestedScrollingEnabled(false);

        backImgView.setOnClickListener(new setOnClickList());
        searchBox.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        searchBox.addTextChangedListener(this);


        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case MainPageCategoryRecycleAdapter.TYPE_HEADER:
                        return 2;
                    case MainPageCategoryRecycleAdapter.TYPE_FOOTER:
                        return 2;

                    case MainPageCategoryRecycleAdapter.TYPE_ITEM:
                        return 1;

                    default:
                        return 1;
                }
            }
        });
        adapter.setOnItemClickListener(new MainPageCategoryRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClickList(View v, int position) {
                HashMap<String, String> data = dataList.get(position);
                if (data.get("TYPE").equals("" + MainPageCategoryRecycleAdapter.TYPE_ITEM)) {
                    Bundle bn = new Bundle();
                    bn.putString("product_id", data.get("product_id"));
                    bn.putString("category_id", data.get("category_id"));
                    bn.putString("name", data.get("name"));
                    (new StartActProcess(getActContext())).startActWithData(ProductDescriptionActivity.class, bn);
                }
            }
        });


        productsRecyclerView.setLayoutManager(mLayoutManager);

        setLabels();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setLabels() {
        searchBox.setBothText("Search products");
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        currentSearchQuery = "" + charSequence;
        findProducts(currentSearchQuery);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onBackPressed() {
        Utils.hideKeyboard((Activity) getActContext());
        super.onBackPressed();
    }

    public void findProducts(final String searchQuery) {
        dataList.clear();
        adapter.notifyDataSetChanged();

        if (searchQuery.trim().equals("")) {
            if (loading.getVisibility() == View.VISIBLE) {
                loading.setVisibility(View.GONE);
            }
            return;
        }

        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }
        if (noProductsTxtView.getVisibility() != View.GONE) {
            noProductsTxtView.setVisibility(View.GONE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "searchProducts");
        parameters.put("searchQuery", searchQuery);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
//        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (!currentSearchQuery.equals(searchQuery)) {
                    return;
                }

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                    dataList.clear();
                    adapter.notifyDataSetChanged();

                    if (isDataAvail == true) {
                        JSONArray msgArr = generalFunc.getJsonArray(Utils.message_str, responseString);
                        if (msgArr != null) {

                            for (int i = 0; i < msgArr.length(); i++) {
                                JSONObject obj_temp = generalFunc.getJsonObject(msgArr, i);
                                String productImg = generalFunc.getJsonValue("image", obj_temp);
                                String productName = generalFunc.getJsonValue("name", obj_temp);
                                String productDes = generalFunc.getJsonValue("description", obj_temp);
                                String productId = generalFunc.getJsonValue("product_id", obj_temp);
                                String price = generalFunc.getJsonValue("price", obj_temp);
                                String catId = generalFunc.getJsonValue("category_id", obj_temp);

                                HashMap<String, String> dataMap_products = new HashMap<>();
                                dataMap_products.put("name", productName);
                                dataMap_products.put("category_id", catId);
                                dataMap_products.put("product_id", productId);
                                dataMap_products.put("price", price);
                                dataMap_products.put("description", Utils.html2text(productDes));
                                dataMap_products.put("image", productImg);
                                dataMap_products.put("TYPE", "" + MainPageCategoryRecycleAdapter.TYPE_ITEM);
                                dataList.add(dataMap_products);
                            }
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        noProductsTxtView.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                        noProductsTxtView.setVisibility(View.VISIBLE);
                    }

                    closeLoader();
                } else {
                    generateErrorView();
                }
            }
        });
        exeWebServer.execute();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    onBackPressed();
                    break;

            }
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
                findProducts(currentSearchQuery);
            }
        });
    }

    public Context getActContext() {
        return SearchProductsActivity.this;
    }
}
