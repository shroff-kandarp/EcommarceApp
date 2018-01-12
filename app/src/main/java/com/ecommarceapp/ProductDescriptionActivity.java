package com.ecommarceapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.MainPageCategoryRecycleAdapter;
import com.adapter.ProductRatingBarAdapter;
import com.bannerslider.banners.Banner;
import com.bannerslider.banners.RemoteBanner;
import com.bannerslider.events.OnBannerClickListener;
import com.bannerslider.views.BannerSlider;
import com.general.files.AddDrawer;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
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

    MTextView productNameTxtView;
    MTextView productPriceTxtView;
    MTextView avgRatingsTxtView;
    MTextView totalRatingsTxtView;
    MTextView descriptionTxtView;
    MTextView addToCartTxtView;
    View similarArea;
    MTextView buyNowTxtView;
    View wishListArea;
    View shareArea;
    ImageView wishListImgView;

    GeneralFunctions generalFunc;

    ProgressBar loading_product_details;
    ProgressBar loadingRelatedProducts;
    View contentView;
    ErrorView errorView;

    BannerSlider bannerSlider;

    AddDrawer addDrawer;


    RecyclerView relatedProductsRecyclerView;
    RecyclerView productRatingRecyclerView;

    MainPageCategoryRecycleAdapter relatedProductsAdapter;
    ArrayList<HashMap<String, String>> relatedProductsDataList = new ArrayList<>();

    ProductRatingBarAdapter ratingAdapter;
    String shareData = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);

        generalFunc = new GeneralFunctions(getActContext());
        addDrawer = new AddDrawer(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        contentView = findViewById(R.id.contentView);
        loading_product_details = (ProgressBar) findViewById(R.id.loading_product_details);
        bannerSlider = (BannerSlider) findViewById(R.id.bannerSlider);
        errorView = (ErrorView) findViewById(R.id.errorView);
        wishListArea = findViewById(R.id.wishListArea);
        wishListImgView = (ImageView) findViewById(R.id.wishListImgView);
        loadingRelatedProducts = (ProgressBar) findViewById(R.id.loadingRelatedProducts);
        relatedProductsRecyclerView = (RecyclerView) findViewById(R.id.relatedProductsRecyclerView);
        productRatingRecyclerView = (RecyclerView) findViewById(R.id.productRatingRecyclerView);

        productNameTxtView = (MTextView) findViewById(R.id.productNameTxtView);
        productPriceTxtView = (MTextView) findViewById(R.id.productPriceTxtView);
        avgRatingsTxtView = (MTextView) findViewById(R.id.avgRatingsTxtView);
        totalRatingsTxtView = (MTextView) findViewById(R.id.totalRatingsTxtView);
        descriptionTxtView = (MTextView) findViewById(R.id.descriptionTxtView);
        addToCartTxtView = (MTextView) findViewById(R.id.addToCartTxtView);
        buyNowTxtView = (MTextView) findViewById(R.id.buyNowTxtView);
        similarArea = findViewById(R.id.similarArea);
        shareArea = findViewById(R.id.shareArea);

        relatedProductsAdapter = new MainPageCategoryRecycleAdapter(getActContext(), relatedProductsDataList, generalFunc, false);
        ratingAdapter = new ProductRatingBarAdapter(getActContext(), list_ratings, generalFunc, false);

        relatedProductsRecyclerView.setAdapter(relatedProductsAdapter);
        productRatingRecyclerView.setAdapter(ratingAdapter);

        relatedProductsRecyclerView.setNestedScrollingEnabled(false);
        productRatingRecyclerView.setNestedScrollingEnabled(false);

//        (findViewById(R.id.ratingArea)).setVisibility(View.GONE);
        setLabels();

        shareArea.setOnClickListener(new setOnClickList());
        similarArea.setOnClickListener(new setOnClickList());
        addToCartTxtView.setOnClickListener(new setOnClickList());
        buyNowTxtView.setOnClickListener(new setOnClickList());
        wishListArea.setOnClickListener(new setOnClickList());
        getProductDetails();

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (relatedProductsAdapter.getItemViewType(position)) {
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
        relatedProductsAdapter.setOnItemClickListener(new MainPageCategoryRecycleAdapter.OnItemClickListener() {
            @Override
            public void onItemClickList(View v, int position) {
                HashMap<String, String> data = relatedProductsDataList.get(position);
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


        relatedProductsRecyclerView.setLayoutManager(mLayoutManager);
        productRatingRecyclerView.setLayoutManager(new LinearLayoutManager(getActContext()));
        findReviews();
        getRelatedProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (addDrawer != null) {
            addDrawer.findUserCartCount();
        }
    }

    public void setLabels() {
        titleTxt.setText(Html.fromHtml(getIntent().getStringExtra("name")));
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Bundle bn = new Bundle();

            switch (view.getId()) {
                case R.id.backImgView:
                    ProductDescriptionActivity.super.onBackPressed();
                    break;
                case R.id.addToCartTxtView:
                    addItemToCart(false);
                    break;
                case R.id.buyNowTxtView:
                    addItemToCart(true);
                    break;
                case R.id.wishListArea:
                    addItemToWishList();
                    break;
                case R.id.similarArea:
                    bn.putString("PRODUCT_NAME", productNameTxtView.getText().toString());
                    (new StartActProcess(getActContext())).startActWithData(SearchProductsActivity.class, bn);
                    break;
                case R.id.shareArea:
                    try {
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, shareData);

                        startActivity(Intent.createChooser(share, "Share Product"));
                    } catch (Exception e) {
                    }
                    break;

            }
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
                            relatedProductsDataList.get(position).put("isWishlisted", "No");
                        } else {
                            relatedProductsDataList.get(position).put("isWishlisted", "Yes");
                        }

                        relatedProductsAdapter.notifyDataSetChanged();
                    }
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
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
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        if (generalFunc.getJsonValue("isDelete", responseString).equalsIgnoreCase("yes")) {
                            wishListImgView.setImageResource(R.mipmap.ic_fav_border);
                        } else {
                            wishListImgView.setImageResource(R.mipmap.ic_fav_fill);
                        }
                    }
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    ArrayList<HashMap<String, String>> list_ratings = new ArrayList<>();

    public void findReviews() {
        list_ratings.clear();
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getAllReviewsOfProduct");
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
        parameters.put("isLimited", "Yes");
        parameters.put("customer_id", "" + generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), false, generalFunc);
//        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("RatingResponseData", "Data::" + responseString);

                if (responseString != null && !responseString.equals("")) {
                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail) {
                        JSONArray obj_arr = generalFunc.getJsonArray("message", responseString);

                        ((SimpleRatingBar) findViewById(R.id.totalAvgRating)).setRating(generalFunc.parseFloat(0, generalFunc.getJsonValue("TotalRating", responseString)));
                        ((MTextView) findViewById(R.id.totalReviewsTxtView)).setText(generalFunc.getJsonValue("TotalRatingCount", responseString) + " Reviews");
                        ((MTextView) findViewById(R.id.totalRatingsReviewsTxtView)).setText(generalFunc.getJsonValue("TotalRating", responseString));
                        if (obj_arr != null) {

                            for (int i = 0; i < obj_arr.length(); i++) {

                                JSONObject obj_temp = generalFunc.getJsonObject(obj_arr, i);

                                HashMap<String, String> data_map = new HashMap<>();

                                data_map.put("text", generalFunc.getJsonValue("text", obj_temp));
                                data_map.put("rating", generalFunc.getJsonValue("rating", obj_temp));
                                data_map.put("date_added", generalFunc.getJsonValue("date_added", obj_temp));
                                data_map.put("customer_name", generalFunc.getJsonValue("customer_name", obj_temp));
                                data_map.put("TYPE", "" + ProductRatingBarAdapter.TYPE_ITEM);

                                list_ratings.add(data_map);
                            }

                            ratingAdapter.notifyDataSetChanged();

                            if (list_ratings.size() > 0 && obj_arr.length() > 0) {
                                (findViewById(R.id.ratingArea)).setVisibility(View.VISIBLE);
                            } else {
                                (findViewById(R.id.ratingArea)).setVisibility(View.GONE);
                            }
                        } else {
                            (findViewById(R.id.ratingArea)).setVisibility(View.GONE);
                        }
                    } else {
                        (findViewById(R.id.ratingArea)).setVisibility(View.GONE);
                    }
                } else {

                    (findViewById(R.id.ratingArea)).setVisibility(View.GONE);
                }
            }
        });
        exeWebServer.execute();
    }

    public void addItemToCart(final boolean isGotoPlaceOrder) {
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

                    if (isGotoPlaceOrder) {
                        new StartActProcess(getActContext()).startActForResult(PlaceOrderActivity.class, Utils.PLACE_ORDER_REQ_CODE);
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }
                    addDrawer.findUserCartCount();
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

                        JSONObject msgObj = generalFunc.getJsonObject(Utils.message_str, responseString);

                        JSONArray wishListDataArr = generalFunc.getJsonArray("UserWishListData", responseString);
                        ArrayList<String> wishListProductIdsList = new ArrayList<>();
                        if (wishListDataArr != null) {
                            for (int i = 0; i < wishListDataArr.length(); i++) {

                                JSONObject obj_temp = generalFunc.getJsonObject(wishListDataArr, i);

                                wishListProductIdsList.add(generalFunc.getJsonValue("product_id", obj_temp));
                            }
                        }

                        if (wishListProductIdsList.contains(getIntent().getStringExtra("product_id"))) {
                            wishListImgView.setImageResource(R.mipmap.ic_fav_fill);
                        }

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

                        shareData = generalFunc.getJsonValue("SHARE_DATA", msgObj);
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

    public void getRelatedProducts() {
        if (loadingRelatedProducts.getVisibility() != View.VISIBLE) {
            loadingRelatedProducts.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "relatedProducts");
        parameters.put("product_id", getIntent().getStringExtra("product_id"));
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
                                relatedProductsDataList.add(dataMap_products);
                            }
                            relatedProductsAdapter.notifyDataSetChanged();

                        } else {
//                            ((MTextView) findViewById(R.id.noProductsTxtView)).setText("No related products found");
                            ((MTextView) findViewById(R.id.noProductsTxtView)).setVisibility(View.VISIBLE);
                        }
                    } else {
//                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
//                        ((MTextView) findViewById(R.id.noProductsTxtView)).setText("No related products found");
                        ((MTextView) findViewById(R.id.noProductsTxtView)).setVisibility(View.VISIBLE);
                    }
                } else {
//                    ((MTextView) findViewById(R.id.noProductsTxtView)).setText("No related products found");
                    ((MTextView) findViewById(R.id.noProductsTxtView)).setVisibility(View.VISIBLE);
                }
                loadingRelatedProducts.setVisibility(View.GONE);
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
