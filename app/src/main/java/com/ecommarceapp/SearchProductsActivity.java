package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

    GridLayoutManager mGridLayoutManager;
    ImageView listChangeImgView;


    boolean mIsLoading = false;
    boolean isNextPageAvailable = false;

    int next_page_str = 1;

    String CURRENT_PRODUCT_DISPLAY_MODE = "GRID";

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
        listChangeImgView = (ImageView) findViewById(R.id.listChangeImgView);

        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);

        adapter = new MainPageCategoryRecycleAdapter(getActContext(), dataList, generalFunc, false);

        productsRecyclerView.setAdapter(adapter);

        productsRecyclerView.setNestedScrollingEnabled(false);

        backImgView.setOnClickListener(new setOnClickList());
        searchBox.setFloatingLabel(MaterialEditText.FLOATING_LABEL_NONE);
        searchBox.addTextChangedListener(this);


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

        listChangeImgView.setOnClickListener(new setOnClickList());

        productsRecyclerView.setLayoutManager(mGridLayoutManager);

        productsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                int lastInScreen = firstVisibleItemPosition + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(mIsLoading) && isNextPageAvailable == true) {

                    mIsLoading = true;
                    adapter.addFooterView();

                    findProducts(true, currentSearchQuery);

                } else if (isNextPageAvailable == false) {
                    adapter.removeFooterView();
                }
            }
        });

        setLabels();

        if (getIntent().getStringExtra("PRODUCT_NAME") != null && !getIntent().getStringExtra("PRODUCT_NAME").equals("")) {
            searchBox.setText(getIntent().getStringExtra("PRODUCT_NAME"));
        }
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


        getWishListData();
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
        findProducts(false, currentSearchQuery);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onBackPressed() {
        Utils.hideKeyboard((Activity) getActContext());
        super.onBackPressed();
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

    ExecuteWebServerUrl exeServerUrlTask;

    public void findProducts(final boolean isFromRecurring, final String searchQuery) {

        if (isFromRecurring == false) {
            dataList.clear();
            adapter.notifyDataSetChanged();
            next_page_str = 1;
        }

        if (searchQuery.trim().equals("")) {
            if (loading.getVisibility() == View.VISIBLE) {
                loading.setVisibility(View.GONE);
            }
            next_page_str = 1;
            adapter.removeFooterView();
            isNextPageAvailable = false;
            return;
        }

        if (exeServerUrlTask != null) {
            exeServerUrlTask.cancel();
            exeServerUrlTask = null;
        }

        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }

        if (loading.getVisibility() != View.VISIBLE && isFromRecurring == false) {
            loading.setVisibility(View.VISIBLE);
        }

        if (noProductsTxtView.getVisibility() != View.GONE) {
            noProductsTxtView.setVisibility(View.GONE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "searchProducts");
        parameters.put("searchQuery", searchQuery);
        parameters.put("page", "" + next_page_str);
        parameters.put("customer_id", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
//        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeServerUrlTask = exeWebServer;
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (!currentSearchQuery.equals(searchQuery)) {
                    return;
                }

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isFromRecurring == false) {
                        dataList.clear();
                        adapter.notifyDataSetChanged();
                    }

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
                                String catId = generalFunc.getJsonValue("category_id", obj_temp);

                                HashMap<String, String> dataMap_products = new HashMap<>();
                                dataMap_products.put("name", productName);
                                dataMap_products.put("category_id", catId);
                                dataMap_products.put("product_id", productId);
                                dataMap_products.put("price", price);
                                dataMap_products.put("description", Utils.html2text(productDes));
                                dataMap_products.put("image", productImg);
                                dataMap_products.put("isWishlisted", wishListProductIdsList.contains(productId) == true ? "Yes" : "No");

                                dataMap_products.put("TYPE", "" + MainPageCategoryRecycleAdapter.TYPE_ITEM);
                                dataList.add(dataMap_products);
                            }
                        }

                        adapter.notifyDataSetChanged();
                        isNextPageAvailable = true;
                        next_page_str = next_page_str + 1;
                        adapter.removeFooterView();
                    } else {
                        if (next_page_str < 2) {

                            noProductsTxtView.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                            noProductsTxtView.setVisibility(View.VISIBLE);
                        }
                        isNextPageAvailable = false;

                        adapter.removeFooterView();
                    }

                    mIsLoading = false;

                    closeLoader();
                } else {
                    if (isFromRecurring == false) {

                        generateErrorView();
                    }
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
                findProducts(false, currentSearchQuery);
            }
        });
    }

    public Context getActContext() {
        return SearchProductsActivity.this;
    }
}
