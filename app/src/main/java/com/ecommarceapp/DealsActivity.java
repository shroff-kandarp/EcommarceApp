package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.MainPageCategoryRecycleAdapter;
import com.general.files.AddDrawer;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;
import com.view.bottombar.BottomBar;
import com.view.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DealsActivity extends AppCompatActivity implements OnTabSelectListener {

    MTextView titleTxt;
    GeneralFunctions generalFunc;

    ProgressBar loading;
    ErrorView errorView;
    MTextView noProductsTxtView;

    RecyclerView productsRecyclerView;

    MainPageCategoryRecycleAdapter adapter;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    AddDrawer addDrawer;

    GridLayoutManager mGridLayoutManager;
    ImageView listChangeImgView;

    String CURRENT_PRODUCT_DISPLAY_MODE = "GRID";

    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals);


        generalFunc = new GeneralFunctions(getActContext());

        addDrawer = new AddDrawer(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        noProductsTxtView = (MTextView) findViewById(R.id.noProductsTxtView);

        productsRecyclerView = (RecyclerView) findViewById(R.id.productsRecyclerView);

        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        listChangeImgView = (ImageView) findViewById(R.id.listChangeImgView);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        adapter = new MainPageCategoryRecycleAdapter(getActContext(), dataList, generalFunc, false);

        productsRecyclerView.setAdapter(adapter);

        productsRecyclerView.setNestedScrollingEnabled(false);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
                if (v == null) {
                    addItemToWishList(data.get("product_id"), data.get("category_id"), position);

                    return;
                }

                if (data.get("TYPE").equals("" + MainPageCategoryRecycleAdapter.TYPE_ITEM)) {
                    Bundle bn = new Bundle();
                    bn.putString("product_id", data.get("product_id"));
                    bn.putString("category_id", data.get("category_id"));
                    bn.putString("name", data.get("name"));
                    (new StartActProcess(getActContext())).startActWithData(ProductDescriptionActivity.class, bn);
                }
            }
        });


        productsRecyclerView.setLayoutManager(mGridLayoutManager);

        setLabels();

        listChangeImgView.setOnClickListener(new setOnClickList());

        bottomBar.setDefaultTab(R.id.tab_deals);
        bottomBar.setOnTabSelectListener(this);
        findProducts();
    }


    public void addItemToWishList(String product_id, String category_id, final int position) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "addProductToWishList");
        parameters.put("product_id", product_id);
        parameters.put("category_id", category_id);
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        Utils.printLog("WishListParameters::", "::" + parameters.toString());

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
                        if (generalFunc.getJsonValue("isDelete", responseString).equalsIgnoreCase("yes")) {
                            dataList.get(position).put("isWishlisted", "No");
                        } else {
                            dataList.get(position).put("isWishlisted", "Yes");
                        }

                        adapter.notifyDataSetChanged();
                    }
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (addDrawer != null) {
            addDrawer.findUserCartCount();
        }

        bottomBar.setDefaultTab(R.id.tab_deals);

        getWishListData();
    }

    public void setLabels() {
        titleTxt.setText("Deals");
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
        if (noProductsTxtView.getVisibility() != View.GONE) {
            noProductsTxtView.setVisibility(View.GONE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getCurrentDeals");
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
                        JSONArray wishListDataArr = generalFunc.getJsonArray("UserWishListData", responseString);
                        ArrayList<String> wishListProductIdsList = new ArrayList<>();
                        if (wishListDataArr != null) {
                            for (int i = 0; i < wishListDataArr.length(); i++) {

                                JSONObject obj_temp = generalFunc.getJsonObject(wishListDataArr, i);

                                wishListProductIdsList.add(generalFunc.getJsonValue("product_id", obj_temp));
                            }
                        }
                        if (msgArr != null) {

                            for (int i = 0; i < msgArr.length(); i++) {
                                JSONObject obj_temp = generalFunc.getJsonObject(msgArr, i);
                                String productImg = generalFunc.getJsonValue("image", obj_temp);
                                String productName = generalFunc.getJsonValue("name", obj_temp);
                                String productDes = generalFunc.getJsonValue("description", obj_temp);
                                String productId = generalFunc.getJsonValue("product_id", obj_temp);
                                String price = generalFunc.getJsonValue("price", obj_temp);
                                String date_end = generalFunc.getJsonValue("date_end", obj_temp);
                                String catId = generalFunc.getJsonValue("category_id", obj_temp);

                                HashMap<String, String> dataMap_products = new HashMap<>();
                                dataMap_products.put("name", productName);
                                dataMap_products.put("category_id", catId);
                                dataMap_products.put("product_id", productId);
                                dataMap_products.put("price", price);
                                dataMap_products.put("description", Utils.html2text(productDes));
                                dataMap_products.put("image", productImg);
                                dataMap_products.put("date_end", date_end);
                                dataMap_products.put("isWishlisted", wishListProductIdsList.contains(productId) == true ? "Yes" : "No");

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

    public Context getActContext() {
        return DealsActivity.this;
    }

    public void getWishListData() {
        if (dataList.size() < 1) {
            return;
        }
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getUserWishListData");
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        Utils.printLog("WishListParameters::", "::" + parameters.toString());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        JSONArray wishListDataArr = generalFunc.getJsonArray("UserWishListData", responseString);

                        ArrayList<String> wishListProductIdsList = new ArrayList<>();
                        if (wishListDataArr != null) {
                            for (int i = 0; i < wishListDataArr.length(); i++) {

                                JSONObject obj_temp = generalFunc.getJsonObject(wishListDataArr, i);

                                wishListProductIdsList.add(generalFunc.getJsonValue("product_id", obj_temp));
                            }
                        }
                        for (int i = 0; i < dataList.size(); i++) {

                            dataList.get(i).put("isWishlisted", wishListProductIdsList.contains(dataList.get(i).get("product_id")) == true ? "Yes" : "No");
                        }

                        adapter.notifyDataSetChanged();
                    }

                } else {
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onTabSelected(int tabId) {

        switch (tabId) {
            case R.id.tab_home:
                addDrawer.goToHome();
                break;
            case R.id.tab_category:
                addDrawer.openAllCategories();
                break;
            case R.id.tab_deals:
                break;
            case R.id.tab_my_acc:
                if (generalFunc.isUserLoggedIn()) {
                    addDrawer.openMyAccount();
                } else {
                    addDrawer.openSignIn();
                }
                break;
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    DealsActivity.super.onBackPressed();
                    break;
                case R.id.listChangeImgView:
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
                findProducts();
            }
        });
    }
}
