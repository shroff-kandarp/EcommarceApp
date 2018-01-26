package com.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ecommarceapp.BecomeSellerActivity;
import com.ecommarceapp.R;
import com.ecommarceapp.SelectCountryActivity;
import com.ecommarceapp.SelectStateActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.ImageFilePath;
import com.general.files.ImageSourceDialog;
import com.general.files.SetOnTouchList;
import com.general.files.StartActProcess;
import com.general.files.UploadImage;
import com.squareup.picasso.Picasso;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreInfoFragment extends Fragment implements BlockingStep, UploadImage.SetResponseListener {


    View view;

    GeneralFunctions generalFunc;
    BecomeSellerActivity myAccAct;

    MaterialEditText storeNameBox;
    MaterialEditText storeEmailBox;
    MaterialEditText storePhoneBox;
    MaterialEditText countryBox;
    MaterialEditText stateBox;
    MaterialEditText cityBox;
    MaterialEditText storePostalCodeBox;
    MaterialEditText storeTINBox;
    View containerView;

    ImageView logoImgView;
    ImageView addOrChangeLogoImgView;
    MButton storeNameAddBtn;
    MButton storeInfoAddBtn;
    View logoArea;
    View countrySelectArea;

    MTextView addOrChangeLogoTxtView;
    JSONObject storeData;

    String imgSelectType = "";

    boolean isCountrySelected = false;
    boolean isZoneSelected = false;
    String iCountryId = "";
    String zone_id = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_store_info, container, false);

        myAccAct = (BecomeSellerActivity) getActivity();
        generalFunc = myAccAct.generalFunc;


        storeNameBox = (MaterialEditText) view.findViewById(R.id.storeNameBox);
        storeEmailBox = (MaterialEditText) view.findViewById(R.id.storeEmailBox);
        storePhoneBox = (MaterialEditText) view.findViewById(R.id.storePhoneBox);
        countryBox = (MaterialEditText) view.findViewById(R.id.countryBox);
        stateBox = (MaterialEditText) view.findViewById(R.id.stateBox);
        cityBox = (MaterialEditText) view.findViewById(R.id.cityBox);
        storePostalCodeBox = (MaterialEditText) view.findViewById(R.id.storePostalCodeBox);
        storeTINBox = (MaterialEditText) view.findViewById(R.id.storeTINBox);
        containerView = view.findViewById(R.id.containerView);
        logoArea = view.findViewById(R.id.logoArea);
        countrySelectArea = view.findViewById(R.id.logoArea);
        logoImgView = (ImageView) view.findViewById(R.id.logoImgView);
        addOrChangeLogoImgView = (ImageView) view.findViewById(R.id.addOrChangeLogoImgView);
        addOrChangeLogoTxtView = (MTextView) view.findViewById(R.id.addOrChangeLogoTxtView);
        storeNameAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.storeNameAddBtn)).getChildView();
        storeInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.storeInfoAddBtn)).getChildView();
        storeNameAddBtn.setId(Utils.generateViewId());
        storeInfoAddBtn.setId(Utils.generateViewId());

        setLabels();
        getSellerData();
        storeEmailBox.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        storePhoneBox.setInputType(InputType.TYPE_CLASS_NUMBER);
        storePostalCodeBox.setInputType(InputType.TYPE_CLASS_NUMBER);

        storeNameAddBtn.setOnClickListener(new setOnClickList());
        storeInfoAddBtn.setOnClickListener(new setOnClickList());
        logoArea.setOnClickListener(new setOnClickList());
        removeInput();
        return view;
    }

    public void setLabels() {
        storeNameBox.setBothText("Store Name", "Enter store name");
        storeEmailBox.setBothText("Store Email", "Enter store email");
        storePhoneBox.setBothText("Store Phone", "Enter store phone");
        countryBox.setBothText("Store Country", "Select store country");
        stateBox.setBothText("Store State", "Select store state");
        cityBox.setBothText("Store City", "Enter store city");
        storePostalCodeBox.setBothText("Store PostalCode", "Enter store postal code");
        storeTINBox.setBothText("Store TIN", "Enter store TIN(Tax Identification number)");
        storeNameAddBtn.setText("Continue");
        storeInfoAddBtn.setText("Update Store Info");
    }

    public void removeInput() {
        Utils.removeInput(countryBox);
        Utils.removeInput(stateBox);

        countryBox.setOnTouchListener(new SetOnTouchList());
        stateBox.setOnTouchListener(new SetOnTouchList());

        countryBox.setOnClickListener(new setOnClickList());
        stateBox.setOnClickListener(new setOnClickList());
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

                    Utils.printLog("responseString:", "::" + responseString);
                    containerView.setVisibility(View.VISIBLE);
                    if (isDataAvail == true) {
                        storeNameBox.setEnabled(false);
                        (view.findViewById(R.id.storeNameAddBtn)).setVisibility(View.GONE);

                        JSONObject msgObj = generalFunc.getJsonObject(Utils.message_str, responseString);
                        storeData = msgObj;
//                        storeNameBox.setText(generalFunc.getJsonValue("store_name", msgObj));


                        displayStoreData();
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
                        JSONObject msgObj = generalFunc.getJsonObject("venderData", responseString);
                        storeData = msgObj;

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

    @Override
    public void onFileUploadResponse(String responseString, String type) {
        if (type.equalsIgnoreCase("STORE_LOGO")) {
            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail) {
                String logoUrl = generalFunc.getJsonValue("LogoUrl", responseString);
                Utils.printLog("logoUrl", "::" + logoUrl);


                Picasso.with(getActContext())
                        .load(logoUrl)
                        .into(logoImgView, null);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
            }
        }
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
                imgSelectType = "STORE_LOGO";
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
            } else if (view.getId() == countryBox.getId()) {
                new StartActProcess(getActContext()).startActForResult(getCurrentFragment(),
                        SelectCountryActivity.class, Utils.SELECT_COUNTRY_REQ_CODE);
            } else if (view.getId() == stateBox.getId()) {
                if (iCountryId.equals("")) {
                    generalFunc.showGeneralMessage("", "Please select country");
                    return;
                }
                Bundle bn = new Bundle();
                bn.putString("iCountryId", iCountryId);
                new StartActProcess(getActContext()).startActForResult(getCurrentFragment(),
                        SelectStateActivity.class, Utils.SELECT_STATE_REQ_CODE, bn);
            } else if (view.getId() == storeInfoAddBtn.getId()) {
                checkData();
            }
        }
    }

    public void checkData() {
        boolean nameEntered = Utils.checkText(storeNameBox) ? true : Utils.setErrorFields(storeNameBox, "Required");
        boolean emailEntered = Utils.checkText(storeEmailBox) ?
                (generalFunc.isEmailValid(Utils.getText(storeEmailBox)) ? true : Utils.setErrorFields(storeEmailBox, "Invalid email"))
                : Utils.setErrorFields(storeEmailBox, "Required");
        boolean mobileEntered = Utils.checkText(storePhoneBox) ? true : Utils.setErrorFields(storePhoneBox, "Required");
        boolean countryEntered = isCountrySelected ? true : false;
        boolean stateEntered = isZoneSelected ? true : false;
        boolean cityEntered = Utils.checkText(cityBox) ? true : Utils.setErrorFields(cityBox, "Required");
        boolean postalEntered = Utils.checkText(storePostalCodeBox) ? true : Utils.setErrorFields(storePostalCodeBox, "Required");
        boolean tinEntered = Utils.checkText(storeTINBox) ? true : Utils.setErrorFields(storeTINBox, "Required");

        if (nameEntered == false || emailEntered == false || mobileEntered == false
                || countryEntered == false || stateEntered == false || cityEntered == false || postalEntered == false || tinEntered == false) {
            return;
        }
        updateStoreData();
    }

    public void updateStoreData() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateSellerStoreData");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("id", generalFunc.getJsonValue("id", storeData));
        parameters.put("store_name", Utils.getText(storeNameBox));
        parameters.put("store_email", Utils.getText(storeEmailBox));
        parameters.put("store_mobile", Utils.getText(storePhoneBox));
        parameters.put("store_country", iCountryId);
        parameters.put("store_zone", zone_id);
        parameters.put("store_city", Utils.getText(cityBox));
        parameters.put("store_postal", Utils.getText(storePostalCodeBox));
        parameters.put("store_tin", Utils.getText(storeTINBox));

        Utils.printLog("parameters", "::" + parameters.toString());
        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        JSONObject msgObj = generalFunc.getJsonObject("venderData", responseString);
                        storeData = msgObj;
                        displayStoreData();
                    }
                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));

                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void displayStoreData() {
        storeNameBox.setText(generalFunc.getJsonValue("store_name", storeData));
        storeEmailBox.setText(generalFunc.getJsonValue("store_email", storeData));
        storePhoneBox.setText(generalFunc.getJsonValue("store_phone", storeData));
        cityBox.setText(generalFunc.getJsonValue("store_city", storeData));
        storePostalCodeBox.setText(generalFunc.getJsonValue("store_zipcode", storeData));
        storeTINBox.setText(generalFunc.getJsonValue("store_tin", storeData));

        String store_country = generalFunc.getJsonValue("store_country", storeData);
        if (!store_country.equals("")) {
            countryBox.setText(generalFunc.getJsonValue("CountryName", storeData));
            iCountryId = store_country;
            isCountrySelected = true;
        }

        String store_state = generalFunc.getJsonValue("store_state", storeData);
        if (!store_state.equals("")) {
            stateBox.setText(generalFunc.getJsonValue("StateName", storeData));
            zone_id = store_state;
            isZoneSelected = true;
        }


        String store_logo = generalFunc.getJsonValue("store_logo", storeData);
        if (!store_logo.equalsIgnoreCase("")) {
            Picasso.with(getActContext())
                    .load(store_logo)
                    .into(logoImgView, null);
            addOrChangeLogoTxtView.setText("Change Logo");
            addOrChangeLogoImgView.setImageResource(R.mipmap.ic_edit);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (storeData == null) {
            return;
        }

        Utils.printLog("Image", "Selected::");

        if (requestCode == ImageSourceDialog.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            boolean isStoragePermissionAvail = generalFunc.isStoragePermissionGranted();
            if (resultCode == myAccAct.RESULT_OK) {
                // successfully captured the image
                // display it in image view

                ArrayList<String[]> paramsList = new ArrayList<>();
                paramsList.add(Utils.generateImageParams("customer_id", generalFunc.getMemberId()));

                paramsList.add(Utils.generateImageParams("store_id", generalFunc.getJsonValue("id", storeData)));
                paramsList.add(Utils.generateImageParams("type", "uploadStoreLogo"));

                String selPath = new ImageFilePath().getPath(getActContext(), myAccAct.fileUri);

                if (Utils.isValidImageResolution(selPath) == true && isStoragePermissionAvail) {
//                    new UploadImage(getActContext(), selPath, Utils.TempProfileImageName, paramsList,imgSelectType).execute();

                    UploadImage uploadImg = new UploadImage(getActContext(), selPath, Utils.TempProfileImageName, paramsList, imgSelectType);
                    uploadImg.setLoadingMessage(imgSelectType.equalsIgnoreCase("STORE_LOGO") ? "Uploading your store's logo. Please wait..." : "Loading");
                    uploadImg.setResponseListener(this);
                    uploadImg.execute();
                } else {
                    generalFunc.showGeneralMessage("", "Please select image which has minimum is 256 * 256 resolution.");
                }


            } else if (resultCode == RESULT_CANCELED) {

            } else {
                generalFunc.showGeneralMessage("", "Failed to capture image. Please try again.");
            }
        }
        if (requestCode == ImageSourceDialog.SELECT_PICTURE) {
            if (resultCode == myAccAct.RESULT_OK) {
                boolean isStoragePermissionAvail = generalFunc.isStoragePermissionGranted();


                ArrayList<String[]> paramsList = new ArrayList<>();
                paramsList.add(Utils.generateImageParams("customer_id", generalFunc.getMemberId()));

                paramsList.add(Utils.generateImageParams("store_id", generalFunc.getJsonValue("id", storeData)));
                paramsList.add(Utils.generateImageParams("type", "uploadStoreLogo"));

                Uri selectedImageUri = data.getData();


                String selectedImagePath = new ImageFilePath().getPath(getActContext(), selectedImageUri);


                if (Utils.isValidImageResolution(selectedImagePath) == true && isStoragePermissionAvail) {

                    UploadImage uploadImg = new UploadImage(getActContext(), selectedImagePath, Utils.TempProfileImageName, paramsList, imgSelectType);
                    uploadImg.setLoadingMessage(imgSelectType.equalsIgnoreCase("STORE_LOGO") ? "Uploading your store's logo. Please wait..." : "Loading");
                    uploadImg.setResponseListener(this);
                    uploadImg.execute();
                } else {
                    generalFunc.showGeneralMessage("", "Please select image which has minimum is 256 * 256 resolution.");
                }

            }
        }


        if (requestCode == Utils.SELECT_COUNTRY_REQ_CODE && resultCode == myAccAct.RESULT_OK && data != null) {
//            vCountryCode = data.getStringExtra("vCountryCode");
//            vPhoneCode = data.getStringExtra("vPhoneCode");
            iCountryId = data.getStringExtra("iCountryId");
            String vCountry = data.getStringExtra("vCountry");
            isCountrySelected = true;

            countryBox.setText(vCountry);
        }

        if (requestCode == Utils.SELECT_STATE_REQ_CODE && resultCode == myAccAct.RESULT_OK && data != null) {
//            vCountryCode = data.getStringExtra("vCountryCode");
//            vPhoneCode = data.getStringExtra("vPhoneCode");
            zone_id = data.getStringExtra("zone_id");
            String zone_name = data.getStringExtra("zone_name");
            isZoneSelected = true;

            stateBox.setText(zone_name);
        }
    }
}
