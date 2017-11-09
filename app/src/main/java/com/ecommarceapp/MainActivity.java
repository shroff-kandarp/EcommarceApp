package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.DrawerMenuRecycleAdapter;
import com.adapter.MainPageCategoryRecycleAdapter;
import com.bannerslider.banners.Banner;
import com.bannerslider.banners.RemoteBanner;
import com.bannerslider.events.OnBannerClickListener;
import com.bannerslider.views.BannerSlider;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    GeneralFunctions generalFunc;

    BannerSlider bannerSlider;
    RecyclerView categoryRecyclerView;
    RecyclerView menuRecyclerView;
    ProgressBar loading_category;
    MainPageCategoryRecycleAdapter adapter;
    DrawerMenuRecycleAdapter drawerAdapter;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();
    ArrayList<HashMap<String, String>> menuDataList = new ArrayList<>();

    ImageView menuImgView;
    public DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generalFunc = new GeneralFunctions(getActContext());

        menuImgView = (ImageView) findViewById(R.id.menuImgView);
        bannerSlider = (BannerSlider) findViewById(R.id.bannerSlider);
        categoryRecyclerView = (RecyclerView) findViewById(R.id.categoryRecyclerView);
        menuRecyclerView = (RecyclerView) findViewById(R.id.menuRecyclerView);
        loading_category = (ProgressBar) findViewById(R.id.loading_category);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        adapter = new MainPageCategoryRecycleAdapter(getActContext(), dataList, generalFunc, false);
        drawerAdapter = new DrawerMenuRecycleAdapter(getActContext(), menuDataList, generalFunc, false);

        categoryRecyclerView.setAdapter(adapter);
        menuRecyclerView.setAdapter(drawerAdapter);
        categoryRecyclerView.setNestedScrollingEnabled(false);
        menuRecyclerView.setNestedScrollingEnabled(false);

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

        categoryRecyclerView.setLayoutManager(mLayoutManager);

        getBanners();

        generateCategories();

        buildMenu();
    }

    public void checkDrawerState() {
        if (mDrawerLayout.isDrawerOpen(Gravity.LEFT) == true) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void buildMenu() {
        menuDataList.add(getMenuItem("Home", "" + R.mipmap.ic_menu_home, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));
        menuDataList.add(getMenuItem("All Categories", "" + R.mipmap.ic_menu_all_categories, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));

        menuDataList.add(getMenuItem("My Profile", "" + R.mipmap.ic_menu_home, "" + DrawerMenuRecycleAdapter.TYPE_HEADER));

        menuDataList.add(getMenuItem("My Account", "" + R.mipmap.ic_menu_my_acc, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));
        menuDataList.add(getMenuItem("My Orders", "" + R.mipmap.ic_menu_orders, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));
        menuDataList.add(getMenuItem("My Cart", "" + R.mipmap.ic_cart, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));
        menuDataList.add(getMenuItem("My Wishlist", "" + R.mipmap.ic_favorite_black_24dp, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));

        menuDataList.add(getMenuItem("Help & Support", "" + R.mipmap.ic_menu_home, "" + DrawerMenuRecycleAdapter.TYPE_HEADER));
        menuDataList.add(getMenuItem("Contact Us", "" + R.mipmap.ic_contact_us, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));
        menuDataList.add(getMenuItem("Terms & Conditions", "" + R.mipmap.ic_contact_us, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));
        menuDataList.add(getMenuItem("Help Center", "" + R.mipmap.ic_contact_us, "" + DrawerMenuRecycleAdapter.TYPE_ITEM));
        drawerAdapter.notifyDataSetChanged();

        drawerAdapter.setOnItemClickListener(new DrawerMenuRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClickList(View v, int position) {
                if (position == 1) {
                    (new StartActProcess(getActContext())).startAct(AllCategoriesActivity.class);
                }
            }
        });

        menuImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkDrawerState();
            }
        });
    }

    public HashMap<String, String> getMenuItem(String name, String imgRes, String itemType) {
        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("Icon", imgRes);
        data.put("TYPE", itemType);

        return data;
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
        /*Call<Object> call = RestClient.getClient().getResponse(params);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response) {
                if (response.isSuccess()) {
                    // request successful (status code 200, 201)

                    Utils.printLog("DeviceDetail", "response = " + new Gson().toJson( response.body()));
//                    String action = result.getAction();

//                    if (action.equals("1")) {
//
//
//                    }
                } else {
//                    generalFunc.showGeneralMessage("", "Please try again later.");
                }
            }

            @Override
            public void onFailure(Throwable t) {
//                generalFunc.showGeneralMessage("", "Please try again later.");
            }
        });*/
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
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getCategories");

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
                                    String catId = generalFunc.getJsonValue("category_id", obj_product);
                                    HashMap<String, String> dataMap_products = new HashMap<>();
                                    dataMap_products.put("name", name);
                                    dataMap_products.put("category_id", catId);
                                    dataMap_products.put("product_id", productId);
                                    dataMap_products.put("description", productDes);
                                    dataMap_products.put("image", productImg);
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
}
