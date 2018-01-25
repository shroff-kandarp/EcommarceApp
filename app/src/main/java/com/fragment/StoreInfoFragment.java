package com.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecommarceapp.BecomeSellerActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.ImageSourceDialog;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.MButton;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreInfoFragment extends Fragment implements BlockingStep {


    View view;

    GeneralFunctions generalFunc;
    BecomeSellerActivity myAccAct;

    MaterialEditText storeNameBox;
    View containerView;

    MButton storeNameAddBtn;
    View logoArea;

    JSONObject storeData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_store_info, container, false);

        myAccAct = (BecomeSellerActivity) getActivity();
        generalFunc = myAccAct.generalFunc;


        storeNameBox = (MaterialEditText) view.findViewById(R.id.storeNameBox);
        containerView = view.findViewById(R.id.containerView);
        logoArea = view.findViewById(R.id.logoArea);

        storeNameAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.storeNameAddBtn)).getChildView();
        storeNameAddBtn.setId(Utils.generateViewId());

        setLabels();
        getSellerData();

        storeNameAddBtn.setOnClickListener(new setOnClickList());
        logoArea.setOnClickListener(new setOnClickList());

        return view;
    }

    public void setLabels() {
        storeNameBox.setBothText("Store Name", "Enter store name");
        storeNameAddBtn.setText("Continue");
    }

    public Context getActContext() {
        return myAccAct.getActContext();
    }

    public void getSellerData() {
        containerView.setVisibility(View.INVISIBLE);
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getCustomerSellerData");
        parameters.put("customer_id", generalFunc.getMemberId());

        Utils.printLog("parameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    containerView.setVisibility(View.VISIBLE);
                    if (isDataAvail == true) {
                        storeNameBox.setEnabled(false);
                        (view.findViewById(R.id.storeNameAddBtn)).setVisibility(View.GONE);

                        JSONObject msgObj = generalFunc.getJsonObject(Utils.message_str, responseString);
                        storeData = msgObj;
                        storeNameBox.setText(generalFunc.getJsonValue("store_name", msgObj));
                    } else {
                        storeNameBox.setEnabled(true);
                        (view.findViewById(R.id.storeNameAddBtn)).setVisibility(View.VISIBLE);
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void createStore() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "createCustomerSellerStore");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("store_name", Utils.getText(storeNameBox));

        Utils.printLog("parameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        storeNameBox.setEnabled(false);
                        (view.findViewById(R.id.storeNameAddBtn)).setVisibility(View.GONE);
                    } else {
                        storeNameBox.setEnabled(true);
                        (view.findViewById(R.id.storeNameAddBtn)).setVisibility(View.VISIBLE);
                    }
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public Fragment getCurrentFragment() {
        return this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == storeNameAddBtn.getId()) {
                boolean nameEntered = Utils.checkText(storeNameBox) ? true : Utils.setErrorFields(storeNameBox, "Required");

                if (nameEntered) {
                    createStore();
                }
            } else if (viewId == logoArea.getId()) {
                if (generalFunc.isCameraStoragePermissionGranted()) {
                    ImageSourceDialog dialog = new ImageSourceDialog(getActContext(), myAccAct.fileUri, getCurrentFragment());
                    dialog.setFileUriListener(new ImageSourceDialog.FileURICreateListener() {
                        @Override
                        public void onFileUriCreate(Uri fileUri) {
                            myAccAct.fileUri = fileUri;
                        }
                    });
                    dialog.run();
                }
            }
        }
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {

    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        return null;
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {

    }
}
