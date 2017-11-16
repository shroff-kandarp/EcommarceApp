package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.adapter.WishListRecycleAdapter;
import com.general.files.AddDrawer;
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

public class WishListActivity extends AppCompatActivity implements WishListRecycleAdapter.OnItemClickListener {


    MTextView titleTxt;

    GeneralFunctions generalFunc;
    ErrorView errorView;
    ProgressBar loading;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    RecyclerView dataRecyclerView;
    WishListRecycleAdapter adapter;

    MTextView noProductsTxtView;
    AddDrawer addDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wish_list);

        generalFunc = new GeneralFunctions(getActContext());
        addDrawer = new AddDrawer(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        noProductsTxtView = (MTextView) findViewById(R.id.noProductsTxtView);

        adapter = new WishListRecycleAdapter(getActContext(), dataList, generalFunc, false);
        dataRecyclerView.setAdapter(adapter);
        dataRecyclerView.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(this);
        setLabels();

        getUserWishList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (addDrawer != null) {
            addDrawer.findUserCartCount();
        }
    }

    public void setLabels() {
        titleTxt.setText("My Wishlist");
    }

    public void getUserWishList() {
        dataList.clear();
        adapter.notifyDataSetChanged();
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getUserWishList");
        parameters.put("customer_id", generalFunc.getMemberId());

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
                    if (isDataAvail) {
                        JSONArray msgArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        dataList.clear();
                        adapter.notifyDataSetChanged();


                        if (msgArr != null) {

                            for (int i = 0; i < msgArr.length(); i++) {
                                JSONObject obj_cart = generalFunc.getJsonObject(msgArr, i);

                                String productImg = generalFunc.getJsonValue("image", obj_cart);
                                String productName = generalFunc.getJsonValue("name", obj_cart);
                                String productDes = generalFunc.getJsonValue("description", obj_cart);
                                String productId = generalFunc.getJsonValue("product_id", obj_cart);
                                String price = generalFunc.getJsonValue("price", obj_cart);
                                String category_id = generalFunc.getJsonValue("category_id", obj_cart);
                                String iWishListId = generalFunc.getJsonValue("iWishListId", obj_cart);

                                HashMap<String, String> dataMap_products = new HashMap<>();
                                dataMap_products.put("name", productName);
                                dataMap_products.put("iWishListId", iWishListId);
                                dataMap_products.put("category_id", category_id);
                                dataMap_products.put("product_id", productId);
                                dataMap_products.put("price", price);
                                dataMap_products.put("description", Utils.html2text(productDes));
                                dataMap_products.put("image", productImg);
                                dataMap_products.put("TYPE", "" + WishListRecycleAdapter.TYPE_ITEM);

                                dataList.add(dataMap_products);

                            }

                            loading.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
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

    @Override
    public void onItemClickList(View v, int btn_type, int position) {
        switch (btn_type) {
            case -1:
                Bundle bn = new Bundle();
                bn.putString("product_id", dataList.get(position).get("product_id"));
                bn.putString("category_id", dataList.get(position).get("category_id"));
                bn.putString("name", dataList.get(position).get("name"));
                (new StartActProcess(getActContext())).startActWithData(ProductDescriptionActivity.class, bn);
                break;
            case 0:
                deleteItemFromWishList(position);
                break;
            case 1:
                moveProductToCart(position);
                break;
        }
    }

    public void moveProductToCart(int position) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "moveProductToCart");
        parameters.put("product_id", dataList.get(position).get("product_id"));
        parameters.put("category_id", dataList.get(position).get("category_id"));
        parameters.put("iWishListId", dataList.get(position).get("iWishListId"));
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        Utils.printLog("moveProductToCartParameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                    getUserWishList();
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void deleteItemFromWishList(int position) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "deleteItemFromWishList");
        parameters.put("product_id", dataList.get(position).get("product_id"));
        parameters.put("category_id", dataList.get(position).get("category_id"));
        parameters.put("iWishListId", dataList.get(position).get("iWishListId"));
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        Utils.printLog("deleteFromWishListParameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                    getUserWishList();
                } else {
                    generalFunc.showError();
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
                    WishListActivity.super.onBackPressed();
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
                getUserWishList();
            }
        });
    }


    public Context getActContext() {
        return WishListActivity.this;
    }
}
