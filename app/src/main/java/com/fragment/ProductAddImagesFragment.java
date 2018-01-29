package com.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adapter.ProductImagesRecyclerAdapter;
import com.ecommarceapp.ManageStoreProductActivity;
import com.ecommarceapp.R;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.ImageFilePath;
import com.general.files.ImageSourceDialog;
import com.general.files.UploadImage;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.GenerateAlertBox;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductAddImagesFragment extends Fragment implements BlockingStep, ProductImagesRecyclerAdapter.OnItemClickListener, UploadImage.SetResponseListener {

    View view;

    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    MButton productInfoAddBtn;
    MTextView noImgTxtView;
    MTextView addMoreImgTxtView;

    RecyclerView productImagesRecyclerView;
    ProductImagesRecyclerAdapter adapter;

    ArrayList<HashMap<String, String>> listData = new ArrayList<>();

    View containerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_images, container, false);

        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();

        productImagesRecyclerView = (RecyclerView) view.findViewById(R.id.productImagesRecyclerView);
        noImgTxtView = (MTextView) view.findViewById(R.id.noImgTxtView);
        addMoreImgTxtView = (MTextView) view.findViewById(R.id.addMoreImgTxtView);
        containerView = view.findViewById(R.id.containerView);

        addMoreImgTxtView.setOnClickListener(new setOnClickList());
        productInfoAddBtn.setOnClickListener(new setOnClickList());

        adapter = new ProductImagesRecyclerAdapter(getActContext(), listData, generalFunc, false);
        adapter.setOnItemClickListener(this);
        productImagesRecyclerView.setAdapter(adapter);

        setLabels();


        new CreateRoundedView(getActContext().getResources().getColor(R.color.appThemeColor_1), Utils.dipToPixels(getActContext(), 25), Utils.dipToPixels(getActContext(), 0), Color.parseColor("#DEDEDE"), addMoreImgTxtView);

        return view;
    }

    public void setLabels() {
        productInfoAddBtn.setText("Update Information");
    }

    public void continueExecution() {
        if (!manageProductAct.product_id.equals("")) {
            getProductImages();
        }
    }

    public void getProductImages() {
        noImgTxtView.setVisibility(View.GONE);
        containerView.setVisibility(View.GONE);

        listData.clear();
        adapter.notifyDataSetChanged();

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getSellerProductImages");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        displayInformation(responseString);
                    } else {
                        generatePageError();
                    }

                } else {
                    generatePageError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void displayInformation(String responseString) {

        JSONArray messageData = generalFunc.getJsonArray(Utils.message_str, responseString);

        if (messageData == null) {
            generatePageError();
            return;
        }

        if (messageData.length() > 0) {

            for (int i = 0; i < messageData.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(messageData, i);

                HashMap<String, String> mapData = new HashMap<>();
                mapData.put("imageURL", generalFunc.getJsonValue("imageURL", obj_temp));
                mapData.put("product_image_id", generalFunc.getJsonValue("product_image_id", obj_temp));
                mapData.put("TYPE", "" + ProductImagesRecyclerAdapter.TYPE_ITEM);

                listData.add(mapData);
            }
            adapter.notifyDataSetChanged();
            noImgTxtView.setVisibility(View.GONE);
        } else {
            noImgTxtView.setVisibility(View.VISIBLE);
        }
        containerView.setVisibility(View.VISIBLE);
    }

    public void generatePageError() {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    manageProductAct.backImgView.performClick();
                }
            }
        });
        generateAlert.setContentMessage("", "Some error occurred. Please check your internet or try again later.");
        generateAlert.setPositiveBtn("OK");

        generateAlert.showAlertBox();
    }

    public Context getActContext() {
        return manageProductAct.getActContext();
    }

    public Fragment getCurrentFragment() {
        return this;
    }

    @Override
    public void onItemClickList(View v, int btn_type, int position) {

        if (btn_type == 1) {
            confirmDeleteProductImg(position);
        }

    }

    public void confirmDeleteProductImg(final int position) {
        final GenerateAlertBox generateAlert = new GenerateAlertBox(getActContext());
        generateAlert.setCancelable(false);
        generateAlert.setBtnClickList(new GenerateAlertBox.HandleAlertBtnClick() {
            @Override
            public void handleBtnClick(int btn_id) {
                generateAlert.closeAlertBox();

                if (btn_id == 1) {
                    deleteProductImage(position);
                }
            }
        });
        generateAlert.setContentMessage("", "Are you sure to delete image?");
        generateAlert.setPositiveBtn("OK");
        generateAlert.setNegativeBtn("Cancel");

        generateAlert.showAlertBox();
    }

    public void deleteProductImage(int position) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "deleteSellerProductImages");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("product_image_id", listData.get(position).get("product_image_id"));

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        getProductImages();
                    }

                    generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                } else {
                    generalFunc.showError();
                }
            }
        });
        exeWebServer.execute();
    }

    @Override
    public void onFileUploadResponse(String responseString, String type) {
        boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

        if (isDataAvail) {
            getProductImages();
        } else {
            generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
        }
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == productInfoAddBtn.getId()) {
                checkData();
            } else if (view.getId() == addMoreImgTxtView.getId()) {
                addNewImage();
            }
        }
    }

    public void checkData() {

    }

    public void addNewImage() {
        if (generalFunc.isCameraStoragePermissionGranted()) {

            ImageSourceDialog dialog = new ImageSourceDialog(getActContext(), manageProductAct.fileUri, getCurrentFragment());
            dialog.setFileUriListener(new ImageSourceDialog.FileURICreateListener() {
                @Override
                public void onFileUriCreate(Uri fileUri) {
                    manageProductAct.fileUri = fileUri;
                }
            });
            dialog.run();
        }
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        callback.goToNextStep();
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {
        callback.goToPrevStep();
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

        Utils.printLog("Image", "Selected::");

        if (requestCode == ImageSourceDialog.CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            boolean isStoragePermissionAvail = generalFunc.isStoragePermissionGranted();
            if (resultCode == manageProductAct.RESULT_OK) {
                // successfully captured the image
                // display it in image view

                ArrayList<String[]> paramsList = new ArrayList<>();
                paramsList.add(Utils.generateImageParams("customer_id", generalFunc.getMemberId()));

                paramsList.add(Utils.generateImageParams("product_id", manageProductAct.product_id));
                paramsList.add(Utils.generateImageParams("type", "addSellerProductImages"));

                String selPath = new ImageFilePath().getPath(getActContext(), manageProductAct.fileUri);

                if (Utils.isValidImageResolution(selPath) == true && isStoragePermissionAvail) {
//                    new UploadImage(getActContext(), selPath, Utils.TempProfileImageName, paramsList,imgSelectType).execute();

                    UploadImage uploadImg = new UploadImage(getActContext(), selPath, Utils.TempProfileImageName, paramsList, "");
                    uploadImg.setLoadingMessage("Uploading your product's image...");
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
            if (resultCode == manageProductAct.RESULT_OK) {
                boolean isStoragePermissionAvail = generalFunc.isStoragePermissionGranted();


                ArrayList<String[]> paramsList = new ArrayList<>();
                paramsList.add(Utils.generateImageParams("customer_id", generalFunc.getMemberId()));

                paramsList.add(Utils.generateImageParams("product_id", manageProductAct.product_id));
                paramsList.add(Utils.generateImageParams("type", "addSellerProductImages"));

                Uri selectedImageUri = data.getData();


                String selectedImagePath = new ImageFilePath().getPath(getActContext(), selectedImageUri);


                if (Utils.isValidImageResolution(selectedImagePath) == true && isStoragePermissionAvail) {

                    UploadImage uploadImg = new UploadImage(getActContext(), selectedImagePath, Utils.TempProfileImageName, paramsList, "");
                    uploadImg.setLoadingMessage("Uploading your product's image...");
                    uploadImg.setResponseListener(this);
                    uploadImg.execute();
                } else {
                    generalFunc.showGeneralMessage("", "Please select image which has minimum is 256 * 256 resolution.");
                }

            }
        }

    }
}
