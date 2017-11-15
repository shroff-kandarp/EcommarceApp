package com.general.files;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.adapter.DrawerMenuRecycleAdapter;
import com.ecommarceapp.AllCategoriesActivity;
import com.ecommarceapp.R;
import com.ecommarceapp.SearchProductsActivity;
import com.ecommarceapp.UserCartActivity;
import com.ecommarceapp.WishListActivity;

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
    public static final String MENU_MY_ORDERS = "4";
    public static final String MENU_MY_CART = "5";
    public static final String MENU_MY_WISH_LIST = "6";
    public static final String MENU_MY_HELP_SUPPORT = "7";
    public static final String MENU_MY_CONTACT_US = "8";
    public static final String MENU_MY_TERMS_CONDITION = "9";
    public static final String MENU_MY_HELP_CENTER = "10";

    View view;
    Context mContext;

    View left_linear;

    ImageView menuImgView;
    ImageView cartImgView;
    ImageView searchImgView;
    public DrawerLayout mDrawerLayout;
    RecyclerView menuRecyclerView;

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

        drawerAdapter = new DrawerMenuRecycleAdapter(getActContext(), menuDataList, generalFunc, false);

        menuRecyclerView.setAdapter(drawerAdapter);
        menuRecyclerView.setNestedScrollingEnabled(false);


        left_linear.setOnClickListener(new setOnClickList());
        cartImgView.setOnClickListener(new setOnClickList());
        searchImgView.setOnClickListener(new setOnClickList());

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
        menuDataList.add(getMenuItem("My Orders", "" + R.mipmap.ic_menu_orders, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_ORDERS));
        menuDataList.add(getMenuItem("My Cart", "" + R.mipmap.ic_cart, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_CART));
        menuDataList.add(getMenuItem("My Wishlist", "" + R.mipmap.ic_favorite_black_24dp, "" + DrawerMenuRecycleAdapter.TYPE_ITEM, MENU_MY_WISH_LIST));

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
        switch (menuDataList.get(position).get("ID")) {
            case MENU_ALL_CATEGORIES:
                (new StartActProcess(getActContext())).startAct(AllCategoriesActivity.class);
                break;
            case MENU_MY_WISH_LIST:
                (new StartActProcess(getActContext())).startAct(WishListActivity.class);
                break;
            case MENU_MY_CART:
                (new StartActProcess(getActContext())).startAct(UserCartActivity.class);
                break;
        }
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


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.menuImgView:
                    checkDrawerState();
                    break;
                case R.id.cartImgView:
                    (new StartActProcess(getActContext())).startAct(UserCartActivity.class);
                    break;
                case R.id.searchImgView:
                    (new StartActProcess(getActContext())).startAct(SearchProductsActivity.class);
                    break;
            }
        }
    }
}
