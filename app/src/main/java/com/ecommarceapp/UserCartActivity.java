package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.adapter.CartRecyclerAdapter;
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

public class UserCartActivity extends BaseActivity implements CartRecyclerAdapter.OnItemClickListener {

    MTextView titleTxt;

    GeneralFunctions generalFunc;
    ErrorView errorView;
    ProgressBar loading;
    MTextView totalPriceTxtView;
    MTextView makePayTxtView;

    View layout_payment;

    View layout_cart_empty;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    RecyclerView dataRecyclerView;
    CartRecyclerAdapter adapter;

    AddDrawer addDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        generalFunc = new GeneralFunctions(getActContext());
        addDrawer = new AddDrawer(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);

        layout_cart_empty = findViewById(R.id.layout_cart_empty);
        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);
        totalPriceTxtView = (MTextView) findViewById(R.id.totalPriceTxtView);
        makePayTxtView = (MTextView) findViewById(R.id.makePayTxtView);
        errorView = (ErrorView) findViewById(R.id.errorView);
        layout_payment = findViewById(R.id.layout_payment);

        adapter = new CartRecyclerAdapter(getActContext(), dataList, generalFunc, false);
        dataRecyclerView.setAdapter(adapter);
        dataRecyclerView.setNestedScrollingEnabled(false);

        adapter.setOnItemClickListener(this);
        setLabels();

        getUserCart();
    }


    public void setLabels() {
        titleTxt.setText("Cart");
    }

    public void getUserCart() {
        dataList.clear();
        adapter.notifyDataSetChanged();
        layout_payment.setVisibility(View.GONE);
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getUserCart");
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

                        layout_payment.setVisibility(View.VISIBLE);
                        totalPriceTxtView.setText(generalFunc.getJsonValue("TotalPrice", responseString));

                        if (msgArr != null) {

                            for (int i = 0; i < msgArr.length(); i++) {
                                JSONObject obj_cart = generalFunc.getJsonObject(msgArr, i);

                                String productImg = generalFunc.getJsonValue("image", obj_cart);
                                String productName = generalFunc.getJsonValue("name", obj_cart);
                                String productDes = generalFunc.getJsonValue("description", obj_cart);
                                String productId = generalFunc.getJsonValue("product_id", obj_cart);
                                String price = generalFunc.getJsonValue("price", obj_cart);
                                String category_id = generalFunc.getJsonValue("category_id", obj_cart);
                                String cart_id = generalFunc.getJsonValue("cart_id", obj_cart);
                                String quantity = generalFunc.getJsonValue("quantity", obj_cart);

                                Utils.printLog("quantity", "::" + quantity);
                                HashMap<String, String> dataMap_products = new HashMap<>();
                                dataMap_products.put("name", productName);
                                dataMap_products.put("cart_id", cart_id);
                                dataMap_products.put("category_id", category_id);
                                dataMap_products.put("product_id", productId);
                                dataMap_products.put("price", price);
                                dataMap_products.put("description", Utils.html2text(productDes));
                                dataMap_products.put("image", productImg);
                                dataMap_products.put("quantity", quantity);
                                dataMap_products.put("TYPE", "" + CartRecyclerAdapter.TYPE_ITEM);

                                dataList.add(dataMap_products);

                            }

                            loading.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        layout_cart_empty.setVisibility(View.VISIBLE);
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
                deleteItemFromCart(position);
                break;
            case 1:
                break;
        }
    }

    @Override
    public void onQTYChangeList(View v, int quantity, int position) {
        changeQuantity(quantity, position);
    }

    @Override
    public void onMoveToWishClickList(View v, int position) {
        moveProductToWishList(position);
    }

    public void moveProductToWishList(int position) {

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "moveItemToWishList");
        parameters.put("product_id", dataList.get(position).get("product_id"));
        parameters.put("category_id", dataList.get(position).get("category_id"));
        parameters.put("cart_id", dataList.get(position).get("cart_id"));
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        Utils.printLog("changeQuantityParameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                    getUserCart();
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void changeQuantity(int quantity, int position) {
        Utils.printLog("quantity", ":m:" + quantity);
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "changeQuantity");
        parameters.put("product_id", dataList.get(position).get("product_id"));
        parameters.put("category_id", dataList.get(position).get("category_id"));
        parameters.put("cart_id", dataList.get(position).get("cart_id"));
        parameters.put("customer_id", "" + generalFunc.getMemberId());
        parameters.put("quantity", "" + quantity);

        Utils.printLog("changeQuantityParameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                    getUserCart();
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void deleteItemFromCart(int position) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "deleteItemFromCart");
        parameters.put("product_id", dataList.get(position).get("product_id"));
        parameters.put("category_id", dataList.get(position).get("category_id"));
        parameters.put("cart_id", dataList.get(position).get("cart_id"));
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        Utils.printLog("deleteFromCartParameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                    getUserCart();
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
                    UserCartActivity.super.onBackPressed();
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
                getUserCart();
            }
        });
    }


    public Context getActContext() {
        return UserCartActivity.this;
    }

}
