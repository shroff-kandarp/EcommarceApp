package com.fragment;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ecommarceapp.BecomeSellerActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.ImageFilePath;
import com.general.files.ImageSourceDialog;
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
public class StoreDetailFragment extends Fragment implements BlockingStep, UploadImage.SetResponseListener {

    View view;

    GeneralFunctions generalFunc;

    BecomeSellerActivity myAccAct;

    View containerView;
    View bannerArea;
    ImageView bannerImgView;
    ImageView addOrChangeBannerImgView;
    MTextView bannerInfoTxtView;
    MaterialEditText storeDescriptionBox;
    MaterialEditText storeAddressBox;
    MaterialEditText storeShippingPolicyBox;
    MaterialEditText storeReturnPolicyBox;
    MaterialEditText storeMetaKeywordsBox;
    MaterialEditText storeMetaDescBox;
    MaterialEditText storeShippingChargeBox;
    MaterialEditText storeSEOUrlBox;
    JSONObject storeData;

    MButton storeDescUpdateBtn;

    String imgSelectType = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_store_detail, container, false);

        myAccAct = (BecomeSellerActivity) getActivity();
        generalFunc = myAccAct.generalFunc;

        bannerArea = view.findViewById(R.id.bannerArea);
        containerView = view.findViewById(R.id.containerView);
        bannerImgView = (ImageView) view.findViewById(R.id.bannerImgView);
        addOrChangeBannerImgView = (ImageView) view.findViewById(R.id.addOrChangeBannerImgView);
        bannerInfoTxtView = (MTextView) view.findViewById(R.id.bannerInfoTxtView);
        storeDescriptionBox = (MaterialEditText) view.findViewById(R.id.storeDescriptionBox);
        storeAddressBox = (MaterialEditText) view.findViewById(R.id.storeAddressBox);
        storeShippingPolicyBox = (MaterialEditText) view.findViewById(R.id.storeShippingPolicyBox);
        storeReturnPolicyBox = (MaterialEditText) view.findViewById(R.id.storeReturnPolicyBox);
        storeMetaKeywordsBox = (MaterialEditText) view.findViewById(R.id.storeMetaKeywordsBox);
        storeMetaDescBox = (MaterialEditText) view.findViewById(R.id.storeMetaDescBox);
        storeShippingChargeBox = (MaterialEditText) view.findViewById(R.id.storeShippingChargeBox);
        storeSEOUrlBox = (MaterialEditText) view.findViewById(R.id.storeSEOUrlBox);
        storeDescUpdateBtn = ((MaterialRippleLayout) view.findViewById(R.id.storeDescUpdateBtn)).getChildView();


        Utils.setMultiLineEditBox(storeDescriptionBox);
        Utils.setMultiLineEditBox(storeAddressBox);
        Utils.setMultiLineEditBox(storeShippingPolicyBox);
        Utils.setMultiLineEditBox(storeReturnPolicyBox);
        Utils.setMultiLineEditBox(storeMetaKeywordsBox);
        Utils.setMultiLineEditBox(storeMetaDescBox);

        setLabels();

        bannerArea.setOnClickListener(new setOnClickList());
        storeDescUpdateBtn.setOnClickListener(new setOnClickList());

        getSellerData();
        return view;
    }

    public void setLabels() {
        storeDescriptionBox.setBothText("Store Description", "Enter store description");
        storeAddressBox.setBothText("Store Address", "Enter store's address");
        storeShippingPolicyBox.setBothText("Store Shipping Policy", "Enter store's shipping policy");
        storeReturnPolicyBox.setBothText("Store Return Policy", "Enter store's return policy");
        storeMetaKeywordsBox.setBothText("Store Meta-Data Keywords", "Enter store's meta-data keywords");
        storeMetaDescBox.setBothText("Store Meta Description", "Enter store's meta description");
        storeShippingChargeBox.setBothText("Store Shipping Charge", "Enter store's shipping charge");
        storeSEOUrlBox.setBothText("Store's SEO Url", "Enter store's seo url");

        storeDescUpdateBtn.setText("Update description");
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

                    if (isDataAvail == true) {
                        containerView.setVisibility(View.VISIBLE);

                        JSONObject msgObj = generalFunc.getJsonObject(Utils.message_str, responseString);
                        storeData = msgObj;
//                        storeNameBox.setText(generalFunc.getJsonValue("store_name", msgObj));


                        displayStoreData();
                    } else {
                        generalFunc.showGeneralMessage("Please check back later", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void displayStoreData() {

        storeDescriptionBox.setText(generalFunc.getJsonValue("store_description", storeData));
        storeAddressBox.setText(generalFunc.getJsonValue("store_address", storeData));
        storeShippingPolicyBox.setText(generalFunc.getJsonValue("store_shipping_policy", storeData));
        storeReturnPolicyBox.setText(generalFunc.getJsonValue("store_return_policy", storeData));
        storeMetaKeywordsBox.setText(generalFunc.getJsonValue("store_meta_keywords", storeData));
        storeMetaDescBox.setText(generalFunc.getJsonValue("store_meta_descriptions", storeData));
        storeShippingChargeBox.setText(generalFunc.getJsonValue("store_shipping_charge", storeData));
        storeSEOUrlBox.setText(generalFunc.getJsonValue("SEO_URL", storeData));

        String store_banner = generalFunc.getJsonValue("store_banner", storeData);
        if (!store_banner.equalsIgnoreCase("")) {
            Picasso.with(getActContext())
                    .load(store_banner)
                    .into(bannerImgView, null);
            bannerInfoTxtView.setText("Change store banner. (For best view banner size 900x300px)");
            addOrChangeBannerImgView.setImageResource(R.mipmap.ic_edit);
        }
    }

    public Context getActContext() {
        return myAccAct.getActContext();
    }

    public Fragment getCurrentFragment() {
        return this;
    }

    @Override
    public void onFileUploadResponse(String responseString, String type) {
        if (type.equalsIgnoreCase("STORE_BANNER")) {
            boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

            if (isDataAvail) {
                String bannerUrl = generalFunc.getJsonValue("BannerUrl", responseString);
                Utils.printLog("bannerUrl", "::" + bannerUrl);

                Picasso.with(getActContext())
                        .load(bannerUrl)
                        .into(bannerImgView, null);
                bannerInfoTxtView.setText("Change store banner. (For best view banner size 900x300px)");
                addOrChangeBannerImgView.setImageResource(R.mipmap.ic_edit);
            } else {
                generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
            }
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int viewId = view.getId();
            if (viewId == bannerArea.getId()) {
                imgSelectType = "STORE_BANNER";
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
            } else if (view.getId() == storeDescUpdateBtn.getId()) {
                checkData();
            }
        }
    }

    public void checkData() {
        boolean storeDescriptionEntered = Utils.checkText(storeDescriptionBox) ? true : Utils.setErrorFields(storeDescriptionBox, "Required");
        boolean storeAddressEntered = Utils.checkText(storeAddressBox) ? true : Utils.setErrorFields(storeAddressBox, "Required");
        boolean storeShippingPolicyEntered = Utils.checkText(storeShippingPolicyBox) ? true : Utils.setErrorFields(storeShippingPolicyBox, "Required");
        boolean storeReturnPolicyEntered = Utils.checkText(storeReturnPolicyBox) ? true : Utils.setErrorFields(storeReturnPolicyBox, "Required");
        boolean storeMetaKeywordsEntered = Utils.checkText(storeMetaKeywordsBox) ? true : Utils.setErrorFields(storeMetaKeywordsBox, "Required");
        boolean storeMetaDesEntered = Utils.checkText(storeMetaDescBox) ? true : Utils.setErrorFields(storeMetaDescBox, "Required");
        boolean storeShippingChargeEntered = Utils.checkText(storeShippingChargeBox) ? true : Utils.setErrorFields(storeShippingChargeBox, "Required");
        boolean storeSEOUrlEntered = Utils.checkText(storeSEOUrlBox) ? true : Utils.setErrorFields(storeSEOUrlBox, "Required");

        if (storeDescriptionEntered == false || storeAddressEntered == false || storeShippingPolicyEntered == false
                || storeReturnPolicyEntered == false || storeMetaKeywordsEntered == false || storeMetaDesEntered == false || storeShippingChargeEntered == false || storeSEOUrlEntered == false) {
            return;
        }
        updateStoreData();
    }

    public void updateStoreData() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateSellerStoreData");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("id", generalFunc.getJsonValue("id", storeData));
        parameters.put("store_name", generalFunc.getJsonValue("store_name", storeData));
        parameters.put("store_email", generalFunc.getJsonValue("store_email", storeData));
        parameters.put("store_mobile", generalFunc.getJsonValue("store_phone", storeData));
        parameters.put("store_country", generalFunc.getJsonValue("store_country", storeData));
        parameters.put("store_zone", generalFunc.getJsonValue("store_state", storeData));
        parameters.put("store_city", generalFunc.getJsonValue("store_city", storeData));
        parameters.put("store_postal", generalFunc.getJsonValue("store_zipcode", storeData));
        parameters.put("store_tin", generalFunc.getJsonValue("store_tin", storeData));
        parameters.put("store_description", Utils.getText(storeDescriptionBox));
        parameters.put("store_address", Utils.getText(storeAddressBox));
        parameters.put("store_shipping_policy", Utils.getText(storeShippingPolicyBox));
        parameters.put("store_return_policy", Utils.getText(storeReturnPolicyBox));
        parameters.put("store_meta_keywords", Utils.getText(storeMetaKeywordsBox));
        parameters.put("store_meta_description", Utils.getText(storeMetaDescBox));
        parameters.put("store_shipping_charge", Utils.getText(storeShippingChargeBox));
        parameters.put("store_seo_url", Utils.getText(storeSEOUrlBox));
        parameters.put("store_bank", generalFunc.getJsonValue("store_bank_details", storeData));

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
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (storeData != null) {
            callback.goToNextStep();
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        if (storeData != null) {
            callback.goToPrevStep();
        }
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
                paramsList.add(Utils.generateImageParams("type", "uploadStoreBanner"));

                String selPath = new ImageFilePath().getPath(getActContext(), myAccAct.fileUri);

                if (Utils.isValidImageResolution(selPath) == true && isStoragePermissionAvail) {
//                    new UploadImage(getActContext(), selPath, Utils.TempProfileImageName, paramsList,imgSelectType).execute();

                    UploadImage uploadImg = new UploadImage(getActContext(), selPath, Utils.TempProfileImageName, paramsList, imgSelectType);
                    uploadImg.setLoadingMessage(imgSelectType.equalsIgnoreCase("STORE_BANNER") ? "Uploading your store's banner. Please wait..." : "Loading");
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
                paramsList.add(Utils.generateImageParams("type", "uploadStoreBanner"));

                Uri selectedImageUri = data.getData();


                String selectedImagePath = new ImageFilePath().getPath(getActContext(), selectedImageUri);


                if (Utils.isValidImageResolution(selectedImagePath) == true && isStoragePermissionAvail) {

                    UploadImage uploadImg = new UploadImage(getActContext(), selectedImagePath, Utils.TempProfileImageName, paramsList, imgSelectType);
                    uploadImg.setLoadingMessage(imgSelectType.equalsIgnoreCase("STORE_Banner") ? "Uploading your store's banner. Please wait..." : "Loading");
                    uploadImg.setResponseListener(this);
                    uploadImg.execute();
                } else {
                    generalFunc.showGeneralMessage("", "Please select image which has minimum is 256 * 256 resolution.");
                }

            }
        }

    }
}
