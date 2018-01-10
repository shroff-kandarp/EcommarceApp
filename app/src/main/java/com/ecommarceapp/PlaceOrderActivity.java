package com.ecommarceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adapter.PlaceOrderRecyclerAdapter;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.utils.Utils;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PlaceOrderActivity extends AppCompatActivity {

    GeneralFunctions generalFunc;
    ImageView backImgView;

    MTextView titleTxt;
    ProgressBar loading;
    MTextView totalPriceTxtView;
    MTextView makePayTxtView;
    MTextView addressTxtView;
    MTextView choosePayTxtView;
    View layout_payment;
    View chooseAddressArea;
    View choosePaymentOptionView;

    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    RecyclerView dataRecyclerView;
    PlaceOrderRecyclerAdapter adapter;

    HashMap<String, String> selectedAddressData = null;

    String paymentOption = "";

    String totalPriceOrig = "";

    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        generalFunc = new GeneralFunctions(getActContext());

        backImgView = (ImageView) findViewById(R.id.backImgView);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        dataRecyclerView = (RecyclerView) findViewById(R.id.dataRecyclerView);
        loading = (ProgressBar) findViewById(R.id.loading);
        totalPriceTxtView = (MTextView) findViewById(R.id.totalPriceTxtView);
        makePayTxtView = (MTextView) findViewById(R.id.makePayTxtView);
        addressTxtView = (MTextView) findViewById(R.id.addressTxtView);
        choosePayTxtView = (MTextView) findViewById(R.id.choosePayTxtView);
        layout_payment = findViewById(R.id.layout_payment);
        chooseAddressArea = findViewById(R.id.chooseAddressArea);
        choosePaymentOptionView = findViewById(R.id.choosePaymentOptionView);

        layout_payment.setVisibility(View.GONE);
        adapter = new PlaceOrderRecyclerAdapter(getActContext(), dataList, generalFunc, false);
        dataRecyclerView.setAdapter(adapter);
        makePayTxtView.setOnClickListener(new setOnClickList());
        backImgView.setOnClickListener(new setOnClickList());
        chooseAddressArea.setOnClickListener(new setOnClickList());
        choosePaymentOptionView.setOnClickListener(new setOnClickList());

        titleTxt.setText("Place Order");
        getUserCart();
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == backImgView.getId()) {
                PlaceOrderActivity.super.onBackPressed();
            } else if (view.getId() == makePayTxtView.getId()) {

                checkData();

            } else if (view.getId() == choosePaymentOptionView.getId()) {

                (new StartActProcess(getActContext())).startActForResult(PaymentOptionActivity.class, Utils.CHOOSE_PAY_OPTION_REQ_CODE);
            } else if (view.getId() == chooseAddressArea.getId()) {

                (new StartActProcess(getActContext())).startActForResult(MyAddressActivity.class, Utils.CHOOSE_ADDRESS_REQ_CODE);
            }
        }
    }

    public void checkData() {
        if (selectedAddressData == null) {
            generalFunc.showGeneralMessage("", "Please select address");
            return;
        }

        if (paymentOption.equalsIgnoreCase("")) {
            generalFunc.showGeneralMessage("", "Please select payment method");
            return;
        }

        if (paymentOption.equalsIgnoreCase("CASH")) {
            createOrder();
        } else {
//            initializePayUMoney();
        }
    }

    /*public void initializePayUMoney() {

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(GeneralFunctions.parseDouble(0.0, totalPriceOrig))                          // Payment amount
                .setTxnId("" + System.currentTimeMillis())                                             // Transaction ID
                .setPhone("9687455197")                                           // User Phone number
                .setProductName("Product Buy")                   // Product Name or description
                .setFirstName("Temp")                              // User First name
                .setEmail("temp@gmail.com")                                            // User Email ID
                .setIsDebug(true)
                .setKey("O15vkB")
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setMerchantId("4819816");


        mPaymentParams = builder.build();
        mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

        PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PlaceOrderActivity.this, R.style.AppTheme_default, false);

    }

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        stringBuilder.append("qauKbEAJ");// Salt

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }


    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }*/

    public void createOrder() {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "createOrder");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("address_id", selectedAddressData.get("address_id"));
        parameters.put("PAYMENT_OPTION", paymentOption);

        Utils.printLog("CreateORDERParams", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
//        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setIsDeviceTokenGenerate(true, "vDeviceToken");
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(final String responseString) {

                Utils.printLog("CreateORDERResponseData", "Data::" + responseString);


                if (responseString != null && !responseString.equals("")) {

                    (new StartActProcess(getActContext())).setOkResult();
                    final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
                    generateAlert.setCancelable(false);
                    generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
                        @Override
                        public void handleBtnClick(int btn_id) {
                            backImgView.performClick();
                        }
                    });
                    generateAlert.setContentMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    generateAlert.setPositiveBtn("OK");
                    generateAlert.showAlertBox();
                } else {
                    generalFunc.showGeneralMessage("", "Please try again later");
                }

            }
        });
        exeWebServer.execute();
    }


    public void getUserCart() {
        dataList.clear();
        adapter.notifyDataSetChanged();
        layout_payment.setVisibility(View.GONE);

        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "getUserCart");
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

                    dataList.clear();
                    adapter.notifyDataSetChanged();
                    if (isDataAvail) {
                        JSONArray msgArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                        dataList.clear();
                        adapter.notifyDataSetChanged();

                        layout_payment.setVisibility(View.VISIBLE);
                        totalPriceTxtView.setText(generalFunc.getJsonValue("TotalPrice", responseString));
                        totalPriceOrig = generalFunc.getJsonValue("TotalPriceOrig", responseString);

                        if (msgArr != null) {

                            for (int i = 0; i < msgArr.length(); i++) {
                                JSONObject obj_cart = generalFunc.getJsonObject(msgArr, i);

                                String productImg = generalFunc.getJsonValue("image", obj_cart);
                                String productName = generalFunc.getJsonValue("name", obj_cart);
                                String productDes = generalFunc.getJsonValue("description", obj_cart);
                                String productId = generalFunc.getJsonValue("product_id", obj_cart);
                                String price = generalFunc.getJsonValue("price", obj_cart);
//                                String category_id = generalFunc.getJsonValue("category_id", obj_cart);
                                String cart_id = generalFunc.getJsonValue("cart_id", obj_cart);
                                String quantity = generalFunc.getJsonValue("quantity", obj_cart);

                                Utils.printLog("quantity", "::" + quantity);
                                HashMap<String, String> dataMap_products = new HashMap<>();
                                dataMap_products.put("name", productName);
                                dataMap_products.put("cart_id", cart_id);
//                                dataMap_products.put("category_id", category_id);
                                dataMap_products.put("product_id", productId);
                                dataMap_products.put("price", price);
                                dataMap_products.put("description", Utils.html2text(productDes));
                                dataMap_products.put("image", productImg);
                                dataMap_products.put("quantity", quantity);
                                dataMap_products.put("TYPE", "" + PlaceOrderRecyclerAdapter.TYPE_ITEM);

                                dataList.add(dataMap_products);

                            }

                            loading.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            layout_payment.setVisibility(View.VISIBLE);
                        }
                    } else {
                        generalFunc.showGeneralMessage("", "Please try again later");
                    }
                    closeLoader();
                } else {
                    generalFunc.showGeneralMessage("", "Please try again later");
                }

            }
        });
        exeWebServer.execute();

    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
        }
    }

    public Context getActContext() {
        return PlaceOrderActivity.this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Utils.CHOOSE_ADDRESS_REQ_CODE && resultCode == RESULT_OK) {

            selectedAddressData = (HashMap<String, String>) data.getSerializableExtra("DATA");
            addressTxtView.setText(selectedAddressData.get("firstname") + " " + selectedAddressData.get("firstname") + "\n" + selectedAddressData.get("address_1") + ", " + selectedAddressData.get("address_2") + ", " + selectedAddressData.get("city") + ", " + selectedAddressData.get("postcode"));
        }
        if (requestCode == Utils.CHOOSE_PAY_OPTION_REQ_CODE && resultCode == RESULT_OK && data != null) {

            paymentOption = data.getStringExtra("CHOOSE_OPTION");
            Utils.printLog("paymentOption", "::" + paymentOption);

            if (paymentOption.equalsIgnoreCase("CASH")) {
                choosePayTxtView.setText("Payment: Cash On Delivery");
            } else {
                choosePayTxtView.setText("Payment: Pay U Money");
            }
        }

        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                } else {
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d("PlaceOrder", "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d("PlaceOrder", "Both objects are null!");
            }
        }
    }
}
