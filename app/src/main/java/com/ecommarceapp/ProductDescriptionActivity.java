package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bannerslider.banners.Banner;
import com.bannerslider.banners.RemoteBanner;
import com.bannerslider.events.OnBannerClickListener;
import com.bannerslider.views.BannerSlider;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductDescriptionActivity extends AppCompatActivity {

    MTextView titleTxt;
    ImageView backImgView;

    MTextView productNameTxtView;
    MTextView productPriceTxtView;
    MTextView avgRatingsTxtView;
    MTextView totalRatingsTxtView;
    MTextView descriptionTxtView;
    MTextView addToCartTxtView;
    View wishListArea;

    GeneralFunctions generalFunc;

    ProgressBar loading_product_details;
    View contentView;
    ErrorView errorView;

    BannerSlider bannerSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        contentView = findViewById(R.id.contentView);
        loading_product_details = (ProgressBar) findViewById(R.id.loading_product_details);
        bannerSlider = (BannerSlider) findViewById(R.id.bannerSlider);
        errorView = (ErrorView) findViewById(R.id.errorView);
        wishListArea = findViewById(R.id.wishListArea);

        productNameTxtView = (MTextView) findViewById(R.id.productNameTxtView);
        productPriceTxtView = (MTextView) findViewById(R.id.productPriceTxtView);
        avgRatingsTxtView = (MTextView) findViewById(R.id.avgRatingsTxtView);
        totalRatingsTxtView = (MTextView) findViewById(R.id.totalRatingsTxtView);
        descriptionTxtView = (MTextView) findViewById(R.id.descriptionTxtView);
        addToCartTxtView = (MTextView) findViewById(R.id.addToCartTxtView);

        generalFunc = new GeneralFunctions(getActContext());

        setLabels();

        backImgView.setOnClickListener(new setOnClickList());
        addToCartTxtView.setOnClickListener(new setOnClickList());
        wishListArea.setOnClickListener(new setOnClickList());
        getProductDetails();
    }


    public void setLabels() {
        titleTxt.setText(getIntent().getStringExtra("name"));
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    ProductDescriptionActivity.super.onBackPressed();
                    break;
                case R.id.addToCartTxtView:
                    addItemToCart();
                    break;
                case R.id.wishListArea:
                    addItemToWishList();
                    break;

            }
        }
    }

    public void addItemToWishList() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "addProductToWishList");
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
        parameters.put("category_id", getIntent().getStringExtra("category_id"));
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

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void addItemToCart() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "addProductToCart");
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
        parameters.put("category_id", getIntent().getStringExtra("category_id"));
        parameters.put("quantity", "1");
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("ResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void getProductDetails() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading_product_details.getVisibility() != View.VISIBLE) {
            loading_product_details.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getProductInfo");
        parameters.put("product_id", getIntent().getStringExtra("product_id"));

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

                        JSONObject msgObj = generalFunc.getJsonObject(Utils.message_str, responseString);

                        Utils.printLog("ResponseData", "::" + msgObj.toString());
                        JSONArray msgArr = generalFunc.getJsonArray("MoreImages", msgObj);

                        List<Banner> banners = new ArrayList<>();
                        banners.add(new RemoteBanner(generalFunc.getJsonValue("image", msgObj)));

                        if (msgArr != null) {
                            for (int i = 0; i < msgArr.length(); i++) {
                                String imgUrl = generalFunc.getJsonValue("image", generalFunc.getJsonObject(msgArr, i));
                                banners.add(new RemoteBanner(imgUrl));
                            }

                        }

                        productNameTxtView.setText(generalFunc.getJsonValue("name", msgObj));
                        avgRatingsTxtView.setText(generalFunc.getJsonValue("TotalRating", msgObj) + " *");
                        totalRatingsTxtView.setText(generalFunc.getJsonValue("TotalRatingCount", msgObj) + " Ratings");
                        productPriceTxtView.setText(generalFunc.getJsonValue("price", msgObj));

                        Utils.printLog("description", "::description::" + msgObj.toString());
                        Utils.printLog("description", "::11description::" + generalFunc.getJsonValue("description", msgObj));
                        descriptionTxtView.setText(Html.fromHtml(Utils.html2text(generalFunc.getJsonValue("description", msgObj))));

                        setBannerData(banners);

                        contentView.setVisibility(View.VISIBLE);
                        closeLoader();

                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }
                } else {
                    generateErrorView();
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

    public void closeLoader() {
        if (loading_product_details.getVisibility() == View.VISIBLE) {
            loading_product_details.setVisibility(View.GONE);
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
                getProductDetails();
            }
        });
    }


    public Context getActContext() {
        return ProductDescriptionActivity.this;
    }
}
