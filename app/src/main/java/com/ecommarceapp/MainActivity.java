package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.MainPageCategoryRecycleAdapter;
import com.bannerslider.banners.Banner;
import com.bannerslider.banners.RemoteBanner;
import com.bannerslider.events.OnBannerClickListener;
import com.bannerslider.views.BannerSlider;
import com.general.files.AddDrawer;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.bottombar.BottomBar;
import com.view.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements OnTabSelectListener {


    GeneralFunctions generalFunc;

    BannerSlider bannerSlider;
    RecyclerView categoryRecyclerView;
    ProgressBar loading_category;
    MainPageCategoryRecycleAdapter adapter;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    AddDrawer addDrawer;

    BottomBar bottomBar;
    GridLayoutManager mGridLayoutManager;
    ImageView listChangeImgView;

    String CURRENT_PRODUCT_DISPLAY_MODE = "GRID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generalFunc = new GeneralFunctions(getActContext());

        addDrawer = new AddDrawer(getActContext());

        bannerSlider = (BannerSlider) findViewById(R.id.bannerSlider);
        categoryRecyclerView = (RecyclerView) findViewById(R.id.categoryRecyclerView);
        loading_category = (ProgressBar) findViewById(R.id.loading_category);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        listChangeImgView = (ImageView) findViewById(R.id.listChangeImgView);

        adapter = new MainPageCategoryRecycleAdapter(getActContext(), dataList, generalFunc, false);

        categoryRecyclerView.setAdapter(adapter);
        categoryRecyclerView.setNestedScrollingEnabled(false);

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
                } else if (data.get("TYPE").equals("" + MainPageCategoryRecycleAdapter.TYPE_HEADER)) {
                    Bundle bn = new Bundle();
                    bn.putString("category_id", data.get("category_id"));
                    bn.putString("name", data.get("name"));
                    (new StartActProcess(getActContext())).startActWithData(ListAllProductsActivity.class, bn);
                }
            }
        });

        categoryRecyclerView.setLayoutManager(mGridLayoutManager);
        getBanners();

        bottomBar.setDefaultTab(R.id.tab_home);
        bottomBar.setOnTabSelectListener(this);

        listChangeImgView.setOnClickListener(new setOnClickList());

        generateCategories();
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
        bottomBar.setDefaultTab(R.id.tab_home);
        getWishListData();
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

    public void getBanners() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getBanners");

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
//        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {

                        JSONArray msgArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        if (msgArr != null) {
                            List<Banner> banners = new ArrayList<>();

                            for (int i = 0; i < msgArr.length(); i++) {
                                String imgUrl = generalFunc.getJsonValue("image", generalFunc.getJsonObject(msgArr, i));
                                banners.add(new RemoteBanner(imgUrl));
                            }

                            setBannerData(banners);
                        }
                    } else {

                    }
                } else {

                }
            }
        });
        exeWebServer.execute();
    }

    public void setBannerData(List<Banner> banners) {

        /*List<Banner> banners = new ArrayList<>();

        banners.add(new RemoteBanner("https://www.france-hotel-guide.com/en/blog/wp-content/uploads/2017/02/paris-shopping.jpg"));
        banners.add(new RemoteBanner("http://www.rentacarbestprice.com/wp-content/uploads/2016/10/Shopping-in-Valencia.jpg"));
        banners.add(new RemoteBanner("https://cache-graphicslib.viator.com/graphicslib/thumbs360x240/3151/SITours/teen-shopping-and-fashion-accessories-tour-in-paris-in-paris-47145.jpg"));
        banners.add(new RemoteBanner("http://www.royaloxfordhotel.co.uk/images/things-to-do/Shopping.jpg"));*/

        bannerSlider.setBanners(banners);

        bannerSlider.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onClick(int position) {
//                Toast.makeText(MainActivity.this, "Banner with position " + String.valueOf(position) + " clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Context getActContext() {
        return MainActivity.this;
    }

    public void generateCategories() {
        loading_category.setVisibility(View.VISIBLE);
        dataList.clear();
        adapter.notifyDataSetChanged();

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getCategories");
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
                                JSONObject obj_cat = generalFunc.getJsonObject(msgArr, i);
                                String name = generalFunc.getJsonValue("name", obj_cat);
                                String category_id = generalFunc.getJsonValue("category_id", obj_cat);

//                                Utils.printLog("CatName","::name::"+name);
                                HashMap<String, String> dataMap_cat = new HashMap<>();
                                dataMap_cat.put("name", name);
                                dataMap_cat.put("category_id", category_id);
                                dataMap_cat.put("TYPE", "" + MainPageCategoryRecycleAdapter.TYPE_HEADER);
                                dataList.add(dataMap_cat);

                                JSONArray subItemsArr = generalFunc.getJsonArray("SubItems", obj_cat);
                                for (int j = 0; j < subItemsArr.length(); j++) {
                                    JSONObject obj_product = generalFunc.getJsonObject(subItemsArr, j);

                                    String productImg = generalFunc.getJsonValue("image", obj_product);
                                    String productName = generalFunc.getJsonValue("name", obj_product);
                                    String productDes = generalFunc.getJsonValue("description", obj_product);
                                    String productId = generalFunc.getJsonValue("product_id", obj_product);
                                    String price = generalFunc.getJsonValue("price", obj_product);
                                    String catId = generalFunc.getJsonValue("category_id", obj_product);
                                    HashMap<String, String> dataMap_products = new HashMap<>();
                                    dataMap_products.put("name", name);
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

                            loading_category.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {

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
                break;
            case R.id.tab_category:
                addDrawer.openAllCategories();
                break;
            case R.id.tab_deals:
                (new StartActProcess(getActContext())).startAct(DealsActivity.class);
                break;
            case R.id.tab_my_acc:
                if (generalFunc.isUserLoggedIn()) {
                    addDrawer.openMyAccount();
                } else {
                    addDrawer.openSignIn();
                }
                break;
            default:
                break;
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.listChangeImgView:
                    if (CURRENT_PRODUCT_DISPLAY_MODE.equals("GRID")) {
                        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));
                        CURRENT_PRODUCT_DISPLAY_MODE = "LIST";
                        adapter.CURRENT_PRODUCT_DISPLAY_MODE = "LIST";
                        adapter.notifyDataSetChanged();
                        categoryRecyclerView.setAdapter(adapter);
                        listChangeImgView.setImageResource(R.mipmap.ic_view_grid);
                    } else {
                        categoryRecyclerView.setLayoutManager(mGridLayoutManager);
                        CURRENT_PRODUCT_DISPLAY_MODE = "GRID";
                        adapter.CURRENT_PRODUCT_DISPLAY_MODE = "GRID";
                        adapter.notifyDataSetChanged();
                        categoryRecyclerView.setAdapter(adapter);
                        listChangeImgView.setImageResource(R.mipmap.ic_view_list);
                    }
                    break;
            }
        }
    }
}
