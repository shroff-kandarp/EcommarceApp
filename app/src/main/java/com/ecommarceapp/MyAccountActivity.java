package com.ecommarceapp;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.general.files.AddDrawer;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MTextView;
import com.view.bottombar.BottomBar;
import com.view.bottombar.OnTabSelectListener;

import java.util.HashMap;

public class MyAccountActivity extends AppCompatActivity implements OnTabSelectListener {

    MTextView titleTxt;
    public GeneralFunctions generalFunc;

    View logOutArea;

    LinearLayout yourOrdersArea;
    LinearLayout loginSecurityArea;
    LinearLayout accountArea;
    LinearLayout addressBookArea;
    LinearLayout messageArea;
    LinearLayout wishListArea;
    LinearLayout profileArea;
    LinearLayout personalizationArea;
    LinearLayout manageSellerProductsArea;
    LinearLayout becomeAsellerArea;

    MTextView becomeSellerTxtView;

    AddDrawer addDrawer;

    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        generalFunc = new GeneralFunctions(getActContext());

        addDrawer = new AddDrawer(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        logOutArea = findViewById(R.id.logOutArea);
        yourOrdersArea = (LinearLayout) findViewById(R.id.yourOrdersArea);
        loginSecurityArea = (LinearLayout) findViewById(R.id.loginSecurityArea);
        accountArea = (LinearLayout) findViewById(R.id.accountArea);
        addressBookArea = (LinearLayout) findViewById(R.id.addressBookArea);
        messageArea = (LinearLayout) findViewById(R.id.messageArea);
        wishListArea = (LinearLayout) findViewById(R.id.wishListArea);
        profileArea = (LinearLayout) findViewById(R.id.profileArea);
        becomeAsellerArea = (LinearLayout) findViewById(R.id.becomeAsellerArea);
        personalizationArea = (LinearLayout) findViewById(R.id.personalizationArea);
        manageSellerProductsArea = (LinearLayout) findViewById(R.id.manageSellerProductsArea);
        becomeSellerTxtView = (MTextView) findViewById(R.id.becomeSellerTxtView);
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);


        setLabels();
        bottomBar.setDefaultTab(R.id.tab_my_acc);
        bottomBar.setOnTabSelectListener(this);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 0), Color.parseColor("#DEDEDE"), yourOrdersArea);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 0), Color.parseColor("#DEDEDE"), accountArea);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 0), Color.parseColor("#DEDEDE"), messageArea);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 0), Color.parseColor("#DEDEDE"), personalizationArea);
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), logOutArea);

        yourOrdersArea.setOnClickListener(new setOnClickList());
        loginSecurityArea.setOnClickListener(new setOnClickList());
        addressBookArea.setOnClickListener(new setOnClickList());
        messageArea.setOnClickListener(new setOnClickList());
        wishListArea.setOnClickListener(new setOnClickList());
        profileArea.setOnClickListener(new setOnClickList());
        becomeAsellerArea.setOnClickListener(new setOnClickList());
        logOutArea.setOnClickListener(new setOnClickList());
        manageSellerProductsArea.setOnClickListener(new setOnClickList());

//        checkCustomerIsSeller();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (addDrawer != null) {
            addDrawer.findUserCartCount();
        }
        checkCustomerIsSeller();
        bottomBar.setDefaultTab(R.id.tab_my_acc);
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    MyAccountActivity.super.onBackPressed();
                    break;
                case R.id.logOutArea:
                    confirmSignOut();
                    break;
                case R.id.loginSecurityArea:
                    (new StartActProcess(getActContext())).startAct(ChangePasswordActivity.class);
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
                    (new StartActProcess(getActContext())).startAct(EditProfileActivity.class);
                    break;
                case R.id.yourOrdersArea:
                    (new StartActProcess(getActContext())).startAct(OrdersListActivity.class);
                    break;
                case R.id.becomeAsellerArea:
                    (new StartActProcess(getActContext())).startActForResult(BecomeSellerActivity.class, Utils.BECOME_SELLER_REQ_CODE);
                    break;
                case R.id.manageSellerProductsArea:
                    (new StartActProcess(getActContext())).startAct(ManageStoreProductsListActivity.class);
                    break;

            }
        }
    }

    public void confirmSignOut() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                if (btn_id == 1) {
                    generateAlert.closeAlertBox();
                    generalFunc.signOut();
                } else if (btn_id == 0) {
                    generateAlert.closeAlertBox();
                }
            }
        });
        generateAlert.setContentMessage("Confirm", "Are you sure, you want to logout from this device?");
        generateAlert.setPositiveBtn("YES");
        generateAlert.setNegativeBtn("NO");
        generateAlert.showAlertBox();
    }

    public void setLabels() {
        titleTxt.setText("Account");
    }

    public void checkCustomerIsSeller() {

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "checkCustomerSeller");
        parameters.put("customer_id", generalFunc.getMemberId());

        Utils.printLog("parameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        becomeSellerTxtView.setText("Store Information");
                        becomeAsellerArea.setVisibility(View.VISIBLE);
                        manageSellerProductsArea.setVisibility(View.VISIBLE);
//                        if (generalFunc.getJsonValue("IS_ALL_INFO_DONE", responseString).equalsIgnoreCase("Yes")) {
////                            becomeAsellerArea.setVisibility(View.GONE);
//                            becomeSellerTxtView.setText("Store Information");
//                        } else {
//                            becomeAsellerArea.setVisibility(View.VISIBLE);
//                        }
                    } else {
                        becomeAsellerArea.setVisibility(View.VISIBLE);
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
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
                (new StartActProcess(getActContext())).startAct(DealsActivity.class);
                break;
            case R.id.tab_my_acc:
                break;
        }
    }
}
