package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ProgressBar;

import com.adapter.MainPageCategoryRecycleAdapter;
import com.bannerslider.banners.Banner;
import com.bannerslider.banners.RemoteBanner;
import com.bannerslider.events.OnBannerClickListener;
import com.bannerslider.views.BannerSlider;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.CreateRoundedView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    GeneralFunctions generalFunc;

    BannerSlider bannerSlider;
    RecyclerView categoryRecyclerView;
    ProgressBar loading_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        generalFunc = new GeneralFunctions(getActContext());

        bannerSlider = (BannerSlider) findViewById(R.id.bannerSlider);
        categoryRecyclerView = (RecyclerView) findViewById(R.id.categoryRecyclerView);
        loading_category = (ProgressBar) findViewById(R.id.loading_category);

        new CreateRoundedView(getResources().getColor(android.R.color.transparent), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), getResources().getColor(R.color.appThemeColor_TXT_1), findViewById(R.id.searchArea));

        getBanners();

        generateCategories();
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
                            ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

                            for (int i = 0; i < msgArr.length(); i++) {
                                JSONObject obj_cat = generalFunc.getJsonObject(msgArr, i);
                                String name = generalFunc.getJsonValue("name", obj_cat);
                                String category_id = generalFunc.getJsonValue("category_id", obj_cat);

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

                            MainPageCategoryRecycleAdapter adpter = new MainPageCategoryRecycleAdapter(getActContext(), dataList, generalFunc, false);
                            categoryRecyclerView.setAdapter(adpter);
                            adpter.notifyDataSetChanged();
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
