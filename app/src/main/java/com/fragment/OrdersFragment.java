package com.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adapter.OrderListRecyclerAdapter;
import com.ecommarceapp.OrdersListActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;
import com.view.MTextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OrdersFragment extends Fragment implements OrderListRecyclerAdapter.OnItemClickListener {

    View view;
    ArrayList<HashMap<String, String>> dataList = new ArrayList<>();

    OrderListRecyclerAdapter adapter;

    RecyclerView dataRecyclerView;
    ProgressBar loading;
    ErrorView errorView;
    MTextView noOrdersTxtView;

    GeneralFunctions generalFunc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_orders, container, false);

        dataRecyclerView = (RecyclerView) view.findViewById(R.id.dataRecyclerView);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        errorView = (ErrorView) view.findViewById(R.id.errorView);
        noOrdersTxtView = (MTextView) view.findViewById(R.id.noOrdersTxtView);

        generalFunc = ((OrdersListActivity) getActivity()).generalFunc;

        adapter = new OrderListRecyclerAdapter(getActContext(), dataList, generalFunc, false);
        adapter.setOnItemClickListener(this);
        dataRecyclerView.setAdapter(adapter);

        loadCustomerOrders();

        return view;
    }

    private void loadCustomerOrders() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }
        noOrdersTxtView.setVisibility(View.GONE);
        dataList.clear();
        adapter.notifyDataSetChanged();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getUserOrders");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("orderStatus", getArguments().getString("PAGE_STATUS"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {


                if (responseString != null && !responseString.equals("")) {

                    closeLoader();

                    loadData(responseString);
                } else {
                    generateErrorView();
                }
            }
        });
        exeWebServer.execute();
    }

    public void loadData(String responseString) {
        if (responseString != null && !responseString.equals("")) {

            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail == true) {

                JSONArray msgArr = generalFunc.getJsonArray(Utils.message_str, responseString);

                if (msgArr != null) {
                    for (int i = 0; i < msgArr.length(); i++) {

                        JSONObject obj_temp = generalFunc.getJsonObject(msgArr, i);

                        HashMap<String, String> map = new HashMap<>();

                        map.put("order_id", generalFunc.getJsonValue("order_id", obj_temp).trim());
                        map.put("orderStatus", generalFunc.getJsonValue("orderStatus", obj_temp).trim());
                        map.put("product_id", generalFunc.getJsonValue("product_id", obj_temp).trim());
                        map.put("productName", generalFunc.getJsonValue("productName", obj_temp).trim());
                        map.put("productModel", generalFunc.getJsonValue("productModel", obj_temp).trim());
                        map.put("productQuantity", generalFunc.getJsonValue("productQuantity", obj_temp).trim());
                        map.put("productPrice", generalFunc.getJsonValue("productPrice", obj_temp).trim());
                        map.put("totalPrice", generalFunc.getJsonValue("totalPrice", obj_temp).trim());
                        map.put("image", generalFunc.getJsonValue("image", obj_temp).trim());
                        map.put("TYPE", "" + adapter.TYPE_ITEM);

                        dataList.add(map);

                    }

                    adapter.notifyDataSetChanged();
                }
            } else {
                noOrdersTxtView.setText(generalFunc.getJsonValue(Utils.message_str, responseString));
                noOrdersTxtView.setVisibility(View.VISIBLE);
            }
        } else {
            generateErrorView();
        }
    }

    public void closeLoader() {
        if (loading.getVisibility() == View.VISIBLE) {
            loading.setVisibility(View.GONE);
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
                loadCustomerOrders();
            }
        });
    }

    public Context getActContext() {
        return ((OrdersListActivity) getActivity()).getActContext();
    }

    @Override
    public void onItemClickList(View v, int btn_type, int position) {
        switch (btn_type) {
            case -1:
                break;
            case 0:
                break;
            case 1:
                removeSelectedOrder(position);
                break;
        }
    }

    public void removeSelectedOrder(final int position) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                if (btn_id == 1) {
                    generateAlert.closeAlertBox();
                    confirmRemoveOrder(position);
                } else if (btn_id == 0) {
                    generateAlert.closeAlertBox();
                }
            }
        });
        generateAlert.setContentMessage("Confirm", "Are you sure, you want to cancel selected order?");
        generateAlert.setPositiveBtn("YES");
        generateAlert.setNegativeBtn("NO");
        generateAlert.showAlertBox();
    }

    public void confirmRemoveOrder(int position) {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("type", "cancelCustomerOrder");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("order_id", dataList.get(position).get("order_id"));


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
                        loadCustomerOrders();
                    } else {
                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }
}
