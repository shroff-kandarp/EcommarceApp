package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.adapter.DrawerMenuRecycleAdapter;
import com.ecommarceapp.AllCategoriesActivity;
import com.ecommarceapp.AppLoginActivity;
import com.ecommarceapp.ContactUsActivity;
import com.ecommarceapp.MainActivity;
import com.ecommarceapp.MyAccountActivity;
import com.ecommarceapp.MyMessagesActivity;
import com.ecommarceapp.OrdersListActivity;
import com.ecommarceapp.R;
import com.ecommarceapp.SearchProductsActivity;
import com.ecommarceapp.StaticPageActivity;
import com.ecommarceapp.UserCartActivity;
import com.ecommarceapp.WishListActivity;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shroff on 15-Nov-17.
 */

public class AddDrawer implements DrawerMenuRecycleAdapter.OnItemClickListener {


    public static final String MENU_HOME = "0";
    public static final String MENU_ALL_CATEGORIES = "1";
    public static final String MENU_MY_PROFILE = "2";
    public static final String MENU_MY_ACCOUNT = "3";
    public static final String MENU_MY_MESSAGES = "4";
    public static final String MENU_MY_ORDERS = "5";
    public static final String MENU_MY_CART = "6";
    public static final String MENU_MY_WISH_LIST = "7";
    public static final String MENU_MY_HELP_SUPPORT = "8";
    public static final String MENU_MY_CONTACT_US = "9";
    public static final String MENU_MY_TERMS_CONDITION = "10";
    public static final String MENU_MY_HELP_CENTER = "11";

    View view;
    Context mContext;

    View left_linear;

    ImageView menuImgView;
    ImageView cartImgView;
    ImageView searchImgView;
    MTextView cartCountTxt;
    MTextView topHTxtView;
    public DrawerLayout mDrawerLayout;
    RecyclerView menuRecyclerView;
    View cartArea;

    GeneralFunctions generalFunc;
    ArrayList<HashMap<String, String>> menuDataList = new ArrayList<>();

    DrawerMenuRecycleAdapter drawerAdapter;

    public AddDrawer(Context mContext) {
        this.mContext = mContext;

        View actView = GeneralFunctions.getCurrentView((Activity) getActContext());
        this.view = actView;

        generalFunc = new GeneralFunctions(mContext);
        buildDrawer();
    }

    public void buildDrawer() {
        left_linear = view.findViewById(R.id.left_linear);
        menuImgView = (ImageView) view.findViewById(R.id.menuImgView);
        searchImgView = (ImageView) view.findViewById(R.id.searchImgView);
        cartImgView = (ImageView) view.findViewById(R.id.cartImgView);
        mDrawerLayout = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        menuRecyclerView = (RecyclerView) view.findViewById(R.id.menuRecyclerView);
        cartCountTxt = (MTextView) view.findViewById(R.id.cartCountTxt);
        topHTxtView = (MTextView) view.findViewById(R.id.topHTxtView);
        cartArea = view.findViewById(R.id.cartArea);

        drawerAdapter = new DrawerMenuRecycleAdapter(getActContext(), menuDataList, generalFunc, false);

        menuRecyclerView.setAdapter(drawerAdapter);
        menuRecyclerView.setNestedScrollingEnabled(false);

        new CreateRoundedView(mContext.getResources().getColor(R.color.appThemeColor_TXT_1), Utils.dipToPixels(mContext, 12), 0, mContext.getResources().getColor(R.color.appThemeColor_1), cartCountTxt);
        cartCountTxt.setTextColor(mContext.getResources().getColor(R.color.appThemeColor_1));
        left_linear.setOnClickListener(new setOnClickList());
        cartImgView.setOnClickListener(new setOnClickList());
        searchImgView.setOnClickListener(new setOnClickList());
        topHTxtView.setOnClickListener(new setOnClickList());

        if (mContext instanceof UserCartActivity) {
            cartArea.setVisibility(View.GONE);
        }

        if (generalFunc.isUserLoggedIn() == false) {
            topHTxtView.setText("Hello, \nSign In OR Sign Up.");
        } else {
            topHTxtView.setText("Welcome");
            findUserInfo();
        }
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
        menuDataList.add(getMenuItem("Home", "" + R.mipmap.ic_menu_home, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_HOME));
        menuDataList.add(getMenuItem("All Categories", "" + R.mipmap.ic_menu_all_categories, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_ALL_CATEGORIES));

        menuDataList.add(getMenuItem("My Profile", "" + R.mipmap.ic_menu_home, "" + DrawerMenuRecycleAdapter.TYPE_HEADER, MENU_MY_PROFILE));

        menuDataList.add(getMenuItem("My Account", "" + R.mipmap.ic_menu_my_acc, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_ACCOUNT));
        menuDataList.add(getMenuItem("My Messages", "" + R.mipmap.ic_menu_message, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_MESSAGES));
        menuDataList.add(getMenuItem("My Orders", "" + R.mipmap.ic_menu_orders, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_ORDERS));
        menuDataList.add(getMenuItem("My Cart", "" + R.mipmap.ic_cart, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_CART));
        menuDataList.add(getMenuItem("My Wishlist", "" + R.mipmap.ic_menu_wishlist, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_WISH_LIST));

        menuDataList.add(getMenuItem("Help & Support", "" + R.mipmap.ic_menu_home, "" + DrawerMenuRecycleAdapter.TYPE_HEADER, MENU_MY_HELP_SUPPORT));
        menuDataList.add(getMenuItem("Contact Us", "" + R.mipmap.ic_contact_us, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_CONTACT_US));
        menuDataList.add(getMenuItem("Terms & Conditions", "" + R.mipmap.ic_menu_terms, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_TERMS_CONDITION));
        menuDataList.add(getMenuItem("Help Center", "" + R.mipmap.ic_menu_help_center, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_HELP_CENTER));
        drawerAdapter.notifyDataSetChanged();

        drawerAdapter.setOnItemClickListener(this);

        menuImgView.setOnClickListener(new setOnClickList());
    }

    @Override
    public void onItemClickList(View v, int position) {
        closeDrawer();
        Bundle bn = new Bundle();
        switch (menuDataList.get(position).get("ID")) {
            case MENU_HOME:
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    mContext.startActivity(intent);
                goToHome();

//                (new StartActProcess(getActContext())).startAct(MainActivity.class);
                break;
            case MENU_ALL_CATEGORIES:
                openAllCategories();
                break;
            case MENU_MY_ORDERS:
                if (generalFunc.isUserLoggedIn()) {
                    (new StartActProcess(getActContext())).startAct(OrdersListActivity.class);
                } else {
                    openSignIn();
                }
                break;
            case MENU_MY_MESSAGES:
                if (generalFunc.isUserLoggedIn()) {
                    (new StartActProcess(getActContext())).startAct(MyMessagesActivity.class);
                } else {
                    openSignIn();
                }
                break;
            case MENU_MY_ACCOUNT:
                openMyAccount();
                break;
            case MENU_MY_WISH_LIST:
                openWishList();
                break;
            case MENU_MY_CART:
                if (generalFunc.isUserLoggedIn()) {
                    (new StartActProcess(getActContext())).startAct(UserCartActivity.class);
                } else {
                    openSignIn();
                }
                break;
            case MENU_MY_TERMS_CONDITION:
                bn.putString("staticpage", "3");
                new StartActProcess(getActContext()).startActWithData(StaticPageActivity.class, bn);
                break;
            case MENU_MY_CONTACT_US:

                new StartActProcess(getActContext()).startAct(ContactUsActivity.class);
                break;
        }
    }

    public void openWishList() {

        if (generalFunc.isUserLoggedIn()) {
            (new StartActProcess(getActContext())).startAct(WishListActivity.class);
        } else {
            openSignIn();
        }
    }

    public void openMyAccount() {

        if (generalFunc.isUserLoggedIn()) {
            (new StartActProcess(getActContext())).startAct(MyAccountActivity.class);
        } else {
            openSignIn();
        }
    }

    public void openAllCategories() {
        (new StartActProcess(getActContext())).startAct(AllCategoriesActivity.class);
    }

    public void goToHome() {

        if (!(mContext instanceof MainActivity)) {
            (new StartActProcess(getActContext())).startAct(MainActivity.class);
            ActivityCompat.finishAffinity((Activity) mContext);
        }
    }

    public void openSignIn() {
        (new StartActProcess(getActContext())).startAct(AppLoginActivity.class);
    }

    public Context getActContext() {
        return mContext;
    }

    public HashMap<String, String> getMenuItem(String name, String imgRes, String itemType, String itemID) {
        HashMap<String, String> data = new HashMap<>();
        data.put("name", name);
        data.put("Icon", imgRes);
        data.put("TYPE", itemType);
        data.put("ID", itemID);

        return data;
    }

    public void findUserCartCount() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getUserCartCount");
        parameters.put("customer_id", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                    if (isDataAvail == true) {
                        cartCountTxt.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                        cartCountTxt.setVisibility(View.VISIBLE);

                    } else {
                        cartCountTxt.setVisibility(View.GONE);
                    }
                } else {
                }
            }
        });
        exeWebServer.execute();
    }

    public void findUserInfo() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getUserInfo");
        parameters.put("customer_id", generalFunc.getMemberId());

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);
                    if (isDataAvail == true) {
                        JSONObject obj_msg = generalFunc.getJsonObject(Utils.message_str, responseString);

                        topHTxtView.setText("Welcome, " + generalFunc.getJsonValue("firstname", obj_msg) + " " + generalFunc.getJsonValue("lastname", obj_msg));
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
                case R.id.menuImgView:
                    checkDrawerState();
                    break;
                case R.id.cartImgView:
                    if (generalFunc.isUserLoggedIn()) {
                        (new StartActProcess(getActContext())).startAct(UserCartActivity.class);
                    } else {
                        openSignIn();
                    }
                    break;
                case R.id.searchImgView:
                    (new StartActProcess(getActContext())).startAct(SearchProductsActivity.class);
                    break;
                case R.id.topHTxtView:
                    if (!generalFunc.isUserLoggedIn()) {
                        openSignIn();
                    }
                    break;
            }
        }
    }
}
