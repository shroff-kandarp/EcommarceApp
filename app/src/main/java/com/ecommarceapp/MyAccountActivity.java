package com.ecommarceapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.general.files.AddDrawer;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MTextView;
import com.view.bottombar.BottomBar;
import com.view.bottombar.OnTabSelectListener;

public class MyAccountActivity extends AppCompatActivity implements OnTabSelectListener {

    MTextView titleTxt;
    GeneralFunctions generalFunc;

    LinearLayout yourOrdersArea;
    LinearLayout loginSecurityArea;
    LinearLayout accountArea;
    LinearLayout addressBookArea;
    LinearLayout messageArea;
    LinearLayout wishListArea;
    LinearLayout profileArea;
    LinearLayout personalizationArea;

    AddDrawer addDrawer;

    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        generalFunc = new GeneralFunctions(getActContext());

        addDrawer = new AddDrawer(getActContext());

        titleTxt = findViewById(R.id.titleTxt);
        yourOrdersArea = findViewById(R.id.yourOrdersArea);
        loginSecurityArea = findViewById(R.id.loginSecurityArea);
        accountArea = findViewById(R.id.accountArea);
        addressBookArea = findViewById(R.id.addressBookArea);
        messageArea = findViewById(R.id.messageArea);
        wishListArea = findViewById(R.id.wishListArea);
        profileArea = findViewById(R.id.profileArea);
        personalizationArea = findViewById(R.id.personalizationArea);
        bottomBar = findViewById(R.id.bottomBar);


        setLabels();
        bottomBar.setDefaultTab(R.id.tab_my_acc);
        bottomBar.setOnTabSelectListener(this);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), yourOrdersArea);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), accountArea);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), messageArea);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), personalizationArea);

        yourOrdersArea.setOnClickListener(new setOnClickList());
        loginSecurityArea.setOnClickListener(new setOnClickList());
        addressBookArea.setOnClickListener(new setOnClickList());
        messageArea.setOnClickListener(new setOnClickList());
        wishListArea.setOnClickListener(new setOnClickList());
        profileArea.setOnClickListener(new setOnClickList());
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (addDrawer != null) {
            addDrawer.findUserCartCount();
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    MyAccountActivity.super.onBackPressed();
                    break;
                case R.id.loginSecurityArea:
                    break;
                case R.id.addressBookArea:
                    (new StartActProcess(getActContext())).startAct(MyAddressActivity.class);
                    break;
                case R.id.messageArea:
                    break;
                case R.id.wishListArea:
                    addDrawer.openWishList();
                    break;
                case R.id.profileArea:
                    break;

            }
        }
    }

    public void setLabels() {
        titleTxt.setText("Account");
    }


    public Context getActContext() {
        return MyAccountActivity.this;
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
                break;
        }
    }
}
