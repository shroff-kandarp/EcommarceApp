package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.adapter.OrdersListPagerAdapter;
import com.fragment.OrdersFragment;
import com.general.files.GeneralFunctions;
import com.view.MTextView;

public class OrdersListActivity extends AppCompatActivity {

    ImageView backImgView;

    MTextView titleTxt;

    public GeneralFunctions generalFunc;

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_list);

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);

        generalFunc = new GeneralFunctions(getActContext());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        titleTxt.setText("My Orders");
        backImgView.setOnClickListener(new setOnClickList());
    }


    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == backImgView.getId()) {
                OrdersListActivity.super.onBackPressed();
            }
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        OrdersListPagerAdapter adapter = new OrdersListPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(prepareFrag("Processing"), "Awaiting Shipment");
        adapter.addFrag(prepareFrag("Shipped"), "Shipped");
        adapter.addFrag(prepareFrag("Complete"), "Awaiting Reviews");
        adapter.addFrag(prepareFrag("Refunded"), "Refund & Dispute");
        adapter.addFrag(prepareFrag("Complete"), "Completed");
        adapter.addFrag(prepareFrag("Canceled"), "Canceled");
        viewPager.setAdapter(adapter);
    }

    public Fragment prepareFrag(String pageStatus) {
        Bundle bundle = new Bundle();
        bundle.putString("PAGE_STATUS", pageStatus);

        OrdersFragment ordersFrag = new OrdersFragment();
        ordersFrag.setArguments(bundle);

        return ordersFrag;
    }

    private void setupTabIcons() {

        View tabOne = LayoutInflater.from(this).inflate(R.layout.item_tab_order, null);
        ImageView tabOneImgView = (ImageView) tabOne.findViewById(R.id.iconImgView);
        MTextView tabOneTitleTxtView = (MTextView) tabOne.findViewById(R.id.titleTxtView);
        tabOneImgView.setImageResource(R.mipmap.ic_processing);
        tabOneTitleTxtView.setText("Awaiting Shipment");


        View tabTwo = LayoutInflater.from(this).inflate(R.layout.item_tab_order, null);
        ImageView tabTwoImgView = (ImageView) tabTwo.findViewById(R.id.iconImgView);
        MTextView tabTwoTitleTxtView = (MTextView) tabTwo.findViewById(R.id.titleTxtView);
        tabTwoImgView.setImageResource(R.mipmap.ic_order_shipped);
        tabTwoTitleTxtView.setText("Shipped");


        View tabThree = LayoutInflater.from(this).inflate(R.layout.item_tab_order, null);
        ImageView tabThreeImgView = (ImageView) tabThree.findViewById(R.id.iconImgView);
        MTextView tabThreeTitleTxtView = (MTextView) tabThree.findViewById(R.id.titleTxtView);
        tabThreeImgView.setImageResource(R.mipmap.ic_order_reviews);
        tabThreeTitleTxtView.setText("Awaiting Reviews");

        View tab4 = LayoutInflater.from(this).inflate(R.layout.item_tab_order, null);
        ImageView tab4ImgView = (ImageView) tab4.findViewById(R.id.iconImgView);
        MTextView tab4TitleTxtView = (MTextView) tab4.findViewById(R.id.titleTxtView);
        tab4ImgView.setImageResource(R.mipmap.ic_order_refund);
        tab4TitleTxtView.setText("Refund & Dispute");

        View tab5 = LayoutInflater.from(this).inflate(R.layout.item_tab_order, null);
        ImageView tab5ImgView = (ImageView) tab5.findViewById(R.id.iconImgView);
        MTextView tab5TitleTxtView = (MTextView) tab5.findViewById(R.id.titleTxtView);
        tab5ImgView.setImageResource(R.mipmap.ic_order_completed);
        tab5TitleTxtView.setText("Completed");

        View tab6 = LayoutInflater.from(this).inflate(R.layout.item_tab_order, null);
        ImageView tab6ImgView = (ImageView) tab6.findViewById(R.id.iconImgView);
        MTextView tab6TitleTxtView = (MTextView) tab6.findViewById(R.id.titleTxtView);
        tab6ImgView.setImageResource(R.mipmap.ic_order_cancelled);
        tab6TitleTxtView.setText("Canceled");


        tabLayout.getTabAt(0).setCustomView(tabOne);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
        tabLayout.getTabAt(2).setCustomView(tabThree);
        tabLayout.getTabAt(3).setCustomView(tab4);
        tabLayout.getTabAt(4).setCustomView(tab5);
        tabLayout.getTabAt(5).setCustomView(tab6);


    }

    public Context getActContext() {
        return OrdersListActivity.this;
    }
}
