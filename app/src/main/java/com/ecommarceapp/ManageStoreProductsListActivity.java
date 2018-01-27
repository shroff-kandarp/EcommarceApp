package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.adapter.StoreProductsRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ManageStoreProductsListActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;

    MTextView titleTxt;
    MTextView noProductsTxt;
    ImageView backImgView;

    RecyclerView productsRecyclerView;

    ProgressBar loading;
    ErrorView errorView;
    LinearLayout noDataArea;

    MButton btn_type2;
    ImageView listChangeImgView;
    ImageView rightImgView;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    String CURRENT_PRODUCT_DISPLAY_MODE = "GRID";

    GridLayoutManager mGridLayoutManager;
    StoreProductsRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_store_products_list);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        noProductsTxt = (MTextView) findViewById(R.id.noProductsTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        productsRecyclerView = (RecyclerView) findViewById(R.id.productListRecyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        noDataArea = (LinearLayout) findViewById(R.id.noDataArea);
        listChangeImgView = (ImageView) findViewById(R.id.listChangeImgView);
        rightImgView = (ImageView) findViewById(R.id.rightImgView);
        btn_type2 = ((MaterialRippleLayout) findViewById(R.id.btn_type2)).getChildView();

        btn_type2.setId(Utils.generateViewId());
        backImgView.setOnClickListener(new setOnClickList());
        btn_type2.setOnClickListener(new setOnClickList());
        rightImgView.setOnClickListener(new setOnClickList());
        listChangeImgView.setOnClickListener(new setOnClickList());

        setLabels();


        adapter = new StoreProductsRecyclerAdapter(getActContext(), dataList, generalFunc, false);

        productsRecyclerView.setAdapter(adapter);

        productsRecyclerView.setNestedScrollingEnabled(false);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case StoreProductsRecyclerAdapter.TYPE_HEADER:
                        return 2;
                    case StoreProductsRecyclerAdapter.TYPE_FOOTER:
                        return 2;

                    case StoreProductsRecyclerAdapter.TYPE_ITEM:
                        return 1;

                    default:
                        return 1;
                }
            }
        });
        adapter.setOnItemClickListener(new StoreProductsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClickList(View v, int position, int btnType) {
                HashMap<String, String> data = dataList.get(position);


                if (data.get("TYPE").equals("" + StoreProductsRecyclerAdapter.TYPE_ITEM) && btnType == 0) {
                    Bundle bn = new Bundle();
                    bn.putString("product_id", data.get("product_id"));
                    bn.putString("category_id", data.get("category_id"));
                    bn.putString("name", data.get("name"));
                    (new StartActProcess(getActContext())).startActWithData(ProductDescriptionActivity.class, bn);
                }

                if (btnType == 1) {

                    Bundle bn = new Bundle();
                    bn.putString("product_id", dataList.get(position).get("product_id"));
                    (new StartActProcess(getActContext())).startActForResult(ManageStoreProductActivity.class, bn, Utils.ADD_PRODUCT_STORE_REQ_CODE);
                }

                if (btnType == 2) {
                    confirmDeleteProduct(position);
                }
            }
        });
        productsRecyclerView.setLayoutManager(mGridLayoutManager);
        listChangeImgView.setOnClickListener(new setOnClickList());

        findProducts();
    }

    public void confirmDeleteProduct(final int position) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    deleteStoreProduct(dataList.get(position).get("product_id"));
                }
            }
        });
        generateAlert.setContentMessage("", "Are you sure to remove that product?.");
        generateAlert.setPositiveBtn("OK");
        generateAlert.setNegativeBtn("Cancel");

        generateAlert.showAlertBox();
    }

    public void deleteStoreProduct(String product_id) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "deleteStoreProduct");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", product_id);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        findProducts();

                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
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

    public Context getActContext() {
        return ManageStoreProductsListActivity.this;
    }

    public void setLabels() {
        titleTxt.setText("Store Products");
        btn_type2.setText("Add Products");
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard((Activity) getActContext());
            if (i == R.id.backImgView) {
                ManageStoreProductsListActivity.super.onBackPressed();
            } else if (i == btn_type2.getId()) {
                (new StartActProcess(getActContext())).startActForResult(ManageStoreProductActivity.class, Utils.ADD_PRODUCT_STORE_REQ_CODE);
            } else if (i == R.id.rightImgView) {
                (new StartActProcess(getActContext())).startActForResult(ManageStoreProductActivity.class, Utils.ADD_PRODUCT_STORE_REQ_CODE);
            } else if (i == R.id.listChangeImgView) {
                if (CURRENT_PRODUCT_DISPLAY_MODE.equals("GRID")) {
                    productsRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));
                    CURRENT_PRODUCT_DISPLAY_MODE = "LIST";
                    adapter.CURRENT_PRODUCT_DISPLAY_MODE = "LIST";
                    adapter.notifyDataSetChanged();
                    productsRecyclerView.setAdapter(adapter);
                    listChangeImgView.setImageResource(R.mipmap.ic_view_grid);
                } else {
                    productsRecyclerView.setLayoutManager(mGridLayoutManager);
                    CURRENT_PRODUCT_DISPLAY_MODE = "GRID";
                    adapter.CURRENT_PRODUCT_DISPLAY_MODE = "GRID";
                    adapter.notifyDataSetChanged();
                    productsRecyclerView.setAdapter(adapter);
                    listChangeImgView.setImageResource(R.mipmap.ic_view_list);
                }
            }
        }
    }

    public void findProducts() {
        dataList.clear();
        adapter.notifyDataSetChanged();

        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }
        if (noDataArea.getVisibility() != View.GONE) {
            noDataArea.setVisibility(View.GONE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getListOfStoreProducts");
        parameters.put("customer_id", generalFunc.getMemberId());

        Utils.printLog("ProductsCategoryParameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
//        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

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

                                dataMap_products.put("TYPE", "" + StoreProductsRecyclerAdapter.TYPE_ITEM);
                                dataList.add(dataMap_products);
                            }

                            rightImgView.setVisibility(View.VISIBLE);
                            listChangeImgView.setVisibility(View.VISIBLE);
                        }

                        adapter.notifyDataSetChanged();

                    } else {
                        noProductsTxt.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                        noDataArea.setVisibility(View.VISIBLE);
                    }

                    closeLoader();
                } else {
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

        generalFunc.generateErrorView(errorView, "Error", "Please check your internet connection or try again");

        if (errorView.getVisibility() != View.VISIBLE) {
            errorView.setVisibility(View.VISIBLE);
        }
        errorView.setOnRetryListener(new ErrorView.RetryListener() {
            @Override
            public void onRetry() {
                findProducts();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.ADD_PRODUCT_STORE_REQ_CODE && resultCode == RESULT_OK) {
            findProducts();
        }
    }
}
