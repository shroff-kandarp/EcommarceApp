package com.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ecommarceapp.AllCategoriesActivity;
import com.ecommarceapp.ManageStoreProductActivity;
import com.ecommarceapp.R;
import com.ecommarceapp.RelatedProductsActivity;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductAddLinksFragment extends Fragment implements BlockingStep {


    View view;

    ManageStoreProductActivity manageProductAct;
    GeneralFunctions generalFunc;

    AppCompatSpinner manufacturerSpinner;
    MTextView categoryTxtView;
    AppCompatSpinner filterSpinner;
    MTextView storesTxtView;
    AppCompatSpinner downloadsSpinner;
    MTextView relatedProductsTxtView;

    LinearLayout categoryContainerView;
    LinearLayout filterContainerView;
    LinearLayout downloadsContainerView;
    LinearLayout relatedProductsContainerView;

    MButton productInfoAddBtn;

    ArrayList<String> manufacturerDataID = new ArrayList<>();
    ArrayList<String> manufacturerDataList = new ArrayList<>();

    ArrayList<String> selectedFilterDataID = new ArrayList<>();
    ArrayList<String> selectedCategoryDataID = new ArrayList<>();
    ArrayList<String> selectedRelatedProductsID = new ArrayList<>();
    ArrayList<String> selectedDownloadDataID = new ArrayList<>();

    ArrayList<String> filterDataID = new ArrayList<>();
    ArrayList<String> filterDataList = new ArrayList<>();

    ArrayList<String> downloadsDataID = new ArrayList<>();
    ArrayList<String> downloadsDataList = new ArrayList<>();

    ArrayList<String> relatedProductsDataID = new ArrayList<>();
    ArrayList<String> relatedProductsDataList = new ArrayList<>();

    //    type = getGeneralDataRelatedToAddProduct
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_product_add_links, container, false);

        manageProductAct = (ManageStoreProductActivity) getActivity();
        generalFunc = manageProductAct.generalFunc;
        productInfoAddBtn = ((MaterialRippleLayout) view.findViewById(R.id.productInfoAddBtn)).getChildView();
        manufacturerSpinner = (AppCompatSpinner) view.findViewById(R.id.manufacturerSpinner);
        categoryTxtView = (MTextView) view.findViewById(R.id.categoryTxtView);
        filterSpinner = (AppCompatSpinner) view.findViewById(R.id.filterSpinner);
        storesTxtView = (MTextView) view.findViewById(R.id.storesTxtView);
        downloadsSpinner = (AppCompatSpinner) view.findViewById(R.id.downloadsSpinner);
        relatedProductsTxtView = (MTextView) view.findViewById(R.id.relatedProductsTxtView);
        categoryContainerView = (LinearLayout) view.findViewById(R.id.categoryContainerView);
        filterContainerView = (LinearLayout) view.findViewById(R.id.filterContainerView);
        downloadsContainerView = (LinearLayout) view.findViewById(R.id.downloadsContainerView);
        relatedProductsContainerView = (LinearLayout) view.findViewById(R.id.relatedProductsContainerView);

        productInfoAddBtn.setId(Utils.generateViewId());
        productInfoAddBtn.setOnClickListener(new setOnClickList());
        categoryTxtView.setOnClickListener(new setOnClickList());
        relatedProductsTxtView.setOnClickListener(new setOnClickList());
        setLabels();

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), manufacturerSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), categoryTxtView);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), filterSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), storesTxtView);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), downloadsSpinner);

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), Color.parseColor("#DEDEDE"), relatedProductsTxtView);
        return view;
    }

    public void setLabels() {
        productInfoAddBtn.setText("Update Information");
    }

    public void continueExecution() {
        if (!manageProductAct.product_id.equals("")) {
            getProductDetails();
        }
    }

    public void getProductDetails() {
        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getStoreProductInfo");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("isLoadGeneralData", "Yes");

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
//                        generalFunc.showGeneralMessage("", generalFunc.getJsonValue(Utils.message_str, responseString));
                    }

                } else {
                    generatePageError();
                }
            }
        });
        exeWebServer.execute();
    }

    public void displayInformation(String responseString) {

        filterContainerView.removeAllViews();
        filterContainerView.setVisibility(View.GONE);
        categoryContainerView.setVisibility(View.GONE);
        categoryContainerView.removeAllViews();
        downloadsContainerView.setVisibility(View.GONE);
        downloadsContainerView.removeAllViews();
        relatedProductsContainerView.setVisibility(View.GONE);
        relatedProductsContainerView.removeAllViews();
        JSONObject productData = generalFunc.getJsonObject("ProductData", responseString);
//        String productTag = generalFunc.getJsonValue("ProductTag", responseString);
        JSONObject productDescriptionData = generalFunc.getJsonObject("ProductDescriptionData", responseString);

        if (productData == null || productDescriptionData == null) {
            generatePageError();
            return;
        }

        JSONArray manufacturerDataArr = generalFunc.getJsonArray("ManufacturerData", generalFunc.getJsonObject("GeneralData", responseString));
        JSONArray filterGroupDescDataArr = generalFunc.getJsonArray("FilterGroupDescData", generalFunc.getJsonObject("GeneralData", responseString));
        JSONArray filterDescDataArr = generalFunc.getJsonArray("FilterDescData", generalFunc.getJsonObject("GeneralData", responseString));
        JSONArray downloadDataArr = generalFunc.getJsonArray("DownloadData", generalFunc.getJsonObject("GeneralData", responseString));
        JSONArray productFilterDataArr = generalFunc.getJsonArray("ProductFilterData", responseString);
        JSONArray productCategoryDataArr = generalFunc.getJsonArray("ProductCategoryData", responseString);
        JSONArray productDownloadData = generalFunc.getJsonArray("ProductDownloadData", responseString);
        JSONArray productRelatedData = generalFunc.getJsonArray("ProductRelatedData", responseString);

        int manufacturer_id = 0;
        if (manufacturerDataArr != null) {
            manufacturerDataList.clear();
            manufacturerDataID.clear();
            for (int i = 0; i < manufacturerDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(manufacturerDataArr, i);
                manufacturerDataList.add(generalFunc.getJsonValue("name", obj_temp));
                manufacturerDataID.add(generalFunc.getJsonValue("manufacturer_id", obj_temp));

                if (generalFunc.getJsonValue("manufacturer_id", productData).equalsIgnoreCase(generalFunc.getJsonValue("manufacturer_id", obj_temp))) {
                    manufacturer_id = i;
                }
            }
        }

        if (productFilterDataArr != null) {
            selectedFilterDataID.clear();
            for (int i = 0; i < productFilterDataArr.length(); i++) {
                JSONObject temp_obj = generalFunc.getJsonObject(productFilterDataArr, i);
                String filter_id = generalFunc.getJsonValue("filter_id", temp_obj);
                selectedFilterDataID.add(filter_id);
            }
        }
        if (productRelatedData != null) {
            selectedRelatedProductsID.clear();
            for (int i = 0; i < productRelatedData.length(); i++) {
                JSONObject temp_obj = generalFunc.getJsonObject(productRelatedData, i);
                String product_id = generalFunc.getJsonValue("product_id", temp_obj);
                String PRODUCT_NAME = generalFunc.getJsonValue("PRODUCT_NAME", temp_obj);
                selectedRelatedProductsID.add(product_id);

                if (selectedRelatedProductsID.contains(product_id)) {
                    addSelectedProducts(product_id, Utils.html2text(PRODUCT_NAME), true);
                }
            }
        }

        if (productDownloadData != null) {
            selectedDownloadDataID.clear();
            for (int i = 0; i < productDownloadData.length(); i++) {
                JSONObject temp_obj = generalFunc.getJsonObject(productDownloadData, i);
                String download_id = generalFunc.getJsonValue("download_id", temp_obj);
                String DOWNLOAD_NAME = generalFunc.getJsonValue("DOWNLOAD_NAME", temp_obj);
                selectedDownloadDataID.add(download_id);

                Utils.printLog("DOWNLOAD_NAME", "::" + Utils.html2text(DOWNLOAD_NAME));
//                if (selectedDownloadDataID.contains(download_id)) {
//                    addSelectedDownload(download_id, Utils.html2text(DOWNLOAD_NAME), true);
//                }
            }
        }
        if (productCategoryDataArr != null) {
            selectedCategoryDataID.clear();
            for (int i = 0; i < productCategoryDataArr.length(); i++) {
                JSONObject temp_obj = generalFunc.getJsonObject(productCategoryDataArr, i);
                String category_id = generalFunc.getJsonValue("category_id", temp_obj);
                String CATEGORY_NAME = generalFunc.getJsonValue("CATEGORY_NAME", temp_obj);
                selectedCategoryDataID.add(category_id);

                Utils.printLog("CATEGORY_NAME", "::" + Utils.html2text(CATEGORY_NAME));
                if (selectedCategoryDataID.contains(category_id)) {
                    addSelectedCategory(category_id, Utils.html2text(CATEGORY_NAME), true);
                }
            }
        }

        Utils.printLog("FIlterDATASELECTED", "::" + selectedFilterDataID.toString());

        if (filterGroupDescDataArr != null) {
            filterDataList.clear();
            filterDataID.clear();

            filterDataList.add("Select Filter");
            filterDataID.add("");

            ArrayList<String> addedFilterList = new ArrayList<>();
            for (int i = 0; i < filterGroupDescDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(filterGroupDescDataArr, i);
                String filter_group_id = generalFunc.getJsonValue("filter_group_id", obj_temp);
                String name_group = generalFunc.getJsonValue("name", obj_temp);

                for (int j = 0; j < filterDescDataArr.length(); j++) {
                    JSONObject obj_temp_1 = generalFunc.getJsonObject(filterDescDataArr, j);
                    String filter_group_id_1 = generalFunc.getJsonValue("filter_group_id", obj_temp_1);
                    String filter_id = generalFunc.getJsonValue("filter_id", obj_temp_1);
                    String name = generalFunc.getJsonValue("name", obj_temp_1);

                    if (filter_group_id_1.equalsIgnoreCase(filter_group_id)) {
                        filterDataList.add(name_group + " > " + name);
                        filterDataID.add(filter_id);
                    }

                    Utils.printLog("FIlterDATASELECTED", "::" + filter_id);
                    if (selectedFilterDataID.contains(filter_id) && !addedFilterList.contains(filter_id)) {
                        addSelectedFilter(filter_id, name_group + " > " + name, true);
                        addedFilterList.add(filter_id);
                    }
                }
            }
        }

        if (downloadDataArr != null) {
            downloadsDataList.clear();
            downloadsDataID.clear();

            downloadsDataList.add("Select Download");
            downloadsDataID.add("");

            for (int i = 0; i < downloadDataArr.length(); i++) {
                JSONObject obj_temp = generalFunc.getJsonObject(downloadDataArr, i);
                String download_id = generalFunc.getJsonValue("download_id", obj_temp);
                String name = generalFunc.getJsonValue("name", obj_temp);
                downloadsDataList.add(name);
                downloadsDataID.add(download_id);
                if (selectedDownloadDataID.contains(download_id)) {
                    addSelectedDownload(download_id, name, true);
                }
            }
        }

        ArrayAdapter<String> manufacturerAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, manufacturerDataList);
        manufacturerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        manufacturerSpinner.setAdapter(manufacturerAdapter);

        if (manufacturer_id != 0) {
            manufacturerSpinner.setSelection(manufacturer_id);
        }


        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, filterDataList);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterAdapter);
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String filter_id = filterDataID.get(position);
                String name = filterDataList.get(position);
                if (!selectedFilterDataID.contains(filter_id) && !filter_id.equals("")) {
                    addSelectedFilter(filter_id, name, false);
                }
                filterSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter<String> downloadsAdapter = new ArrayAdapter<>(getActContext(), R.layout.spinner_item, downloadsDataList);
        downloadsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        downloadsSpinner.setAdapter(downloadsAdapter);
        downloadsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String download_id = downloadsDataID.get(position);
                String name = downloadsDataList.get(position);
                if (!selectedFilterDataID.contains(download_id) && !download_id.equals("")) {
                    addSelectedDownload(download_id, name, false);
                }
                downloadsSpinner.setSelection(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        relatedProductsDataID.clear();
        relatedProductsDataList.clear();

        relatedProductsDataID.add("");
        relatedProductsDataList.add("Select Related Products");


    }

    public void addSelectedCategory(final String categoryId, String categoryName, boolean isAddView) {
        if (selectedCategoryDataID.size() > 0 && isAddView == false) {

            for (int i = 0; i < selectedCategoryDataID.size(); i++) {
                String tempCategoryId = selectedCategoryDataID.get(i);

                if (tempCategoryId.equalsIgnoreCase(categoryId)) {
                    return;
                }
            }
        }
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View categoryView = inflater.inflate(R.layout.add_link_category_item, null);

        MTextView categoryNameTxtView = (MTextView) categoryView.findViewById(R.id.categoryNameTxtView);
        categoryNameTxtView.setText(categoryName);
        ImageView removeImgView = (ImageView) categoryView.findViewById(R.id.removeImgView);
        removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedCategoryDataID.size() > 0) {

                    for (int i = 0; i < selectedCategoryDataID.size(); i++) {
                        String tempCategoryId = selectedCategoryDataID.get(i);
                        if (tempCategoryId.equalsIgnoreCase(categoryId)) {
                            selectedCategoryDataID.remove(i);
                            categoryView.setVisibility(View.GONE);
                        }
                    }
                }


                if (selectedCategoryDataID.size() == 0) {
                    categoryContainerView.setVisibility(View.GONE);
                }
            }
        });
        if (isAddView == false) {
            selectedCategoryDataID.add(categoryId);
        }
        categoryContainerView.addView(categoryView);
        categoryContainerView.setVisibility(View.VISIBLE);
    }

    public void addSelectedProducts(final String categoryId, String categoryName, boolean isAddView) {
        if (selectedRelatedProductsID.size() > 0 && isAddView == false) {

            for (int i = 0; i < selectedRelatedProductsID.size(); i++) {
                String tempCategoryId = selectedRelatedProductsID.get(i);

                if (tempCategoryId.equalsIgnoreCase(categoryId)) {
                    return;
                }
            }
        }
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View categoryView = inflater.inflate(R.layout.add_link_category_item, null);

        MTextView categoryNameTxtView = (MTextView) categoryView.findViewById(R.id.categoryNameTxtView);
        categoryNameTxtView.setText(categoryName);
        ImageView removeImgView = (ImageView) categoryView.findViewById(R.id.removeImgView);
        removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedRelatedProductsID.size() > 0) {

                    for (int i = 0; i < selectedRelatedProductsID.size(); i++) {
                        String tempCategoryId = selectedRelatedProductsID.get(i);
                        if (tempCategoryId.equalsIgnoreCase(categoryId)) {
                            selectedRelatedProductsID.remove(i);
                            categoryView.setVisibility(View.GONE);
                        }
                    }
                }

                if (selectedRelatedProductsID.size() == 0) {
                    relatedProductsContainerView.setVisibility(View.GONE);
                }
            }
        });
        if (isAddView == false) {
            selectedRelatedProductsID.add(categoryId);
        }
        relatedProductsContainerView.addView(categoryView);
        relatedProductsContainerView.setVisibility(View.VISIBLE);
    }

    public void addSelectedDownload(final String categoryId, String categoryName, boolean isAddView) {
        if (selectedDownloadDataID.size() > 0 && isAddView == false) {

            for (int i = 0; i < selectedDownloadDataID.size(); i++) {
                String tempCategoryId = selectedDownloadDataID.get(i);

                if (tempCategoryId.equalsIgnoreCase(categoryId)) {
                    return;
                }
            }
        }
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View categoryView = inflater.inflate(R.layout.add_link_category_item, null);

        MTextView categoryNameTxtView = (MTextView) categoryView.findViewById(R.id.categoryNameTxtView);
        categoryNameTxtView.setText(categoryName);
        ImageView removeImgView = (ImageView) categoryView.findViewById(R.id.removeImgView);
        removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedDownloadDataID.size() > 0) {

                    for (int i = 0; i < selectedDownloadDataID.size(); i++) {
                        String tempCategoryId = selectedDownloadDataID.get(i);
                        if (tempCategoryId.equalsIgnoreCase(categoryId)) {
                            selectedDownloadDataID.remove(i);
                            categoryView.setVisibility(View.GONE);
                        }
                    }
                }


                if (selectedDownloadDataID.size() == 0) {
                    downloadsContainerView.setVisibility(View.GONE);
                }
            }
        });
        if (isAddView == false) {
            selectedDownloadDataID.add(categoryId);
        }
        downloadsContainerView.addView(categoryView);
        downloadsContainerView.setVisibility(View.VISIBLE);
    }

    public void addSelectedFilter(final String filter_id, String filterName, boolean isAddView) {
        if (selectedFilterDataID.size() > 0 && isAddView == false) {

            for (int i = 0; i < selectedFilterDataID.size(); i++) {
                String tempCategoryId = selectedFilterDataID.get(i);

                if (tempCategoryId.equalsIgnoreCase(filter_id)) {
                    return;
                }
            }
        }
        LayoutInflater inflater = (LayoutInflater) getActContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View categoryView = inflater.inflate(R.layout.add_link_category_item, null);

        MTextView categoryNameTxtView = (MTextView) categoryView.findViewById(R.id.categoryNameTxtView);
        categoryNameTxtView.setText(filterName);
        ImageView removeImgView = (ImageView) categoryView.findViewById(R.id.removeImgView);
        removeImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedFilterDataID.size() > 0) {

                    for (int i = 0; i < selectedFilterDataID.size(); i++) {
                        String tempCategoryId = selectedFilterDataID.get(i);
                        if (tempCategoryId.equalsIgnoreCase(filter_id)) {
                            selectedFilterDataID.remove(i);
                            categoryView.setVisibility(View.GONE);
                        }
                    }
                }

                if (selectedFilterDataID.size() == 0) {
                    filterContainerView.setVisibility(View.GONE);
                }
            }
        });
        if (isAddView == false) {
            selectedFilterDataID.add(filter_id);
        }
        filterContainerView.addView(categoryView);
        filterContainerView.setVisibility(View.VISIBLE);
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

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == productInfoAddBtn.getId()) {
                updateLinkData();
            }
            if (view.getId() == categoryTxtView.getId()) {
                (new StartActProcess(getActContext())).startActForResult(getCurrentFragment(), AllCategoriesActivity.class, Utils.ALL_CATEGORIES_REQ_CODE);
            }
            if (view.getId() == relatedProductsTxtView.getId()) {
                Bundle bn = new Bundle();
                bn.putString("manufacturer_id", "" + manufacturerDataID.get(manufacturerSpinner.getSelectedItemPosition()));
                bn.putString("product_id", manageProductAct.product_id);
                (new StartActProcess(getActContext())).startActForResult(getCurrentFragment(), RelatedProductsActivity.class, Utils.RELATED_PRODUCTS_REQ_CODE, bn);
            }
        }
    }

    public void updateLinkData() {
        String categoryIds = "";
        for (int i = 0; i < selectedCategoryDataID.size(); i++) {
            if (!selectedCategoryDataID.get(i).equals("")) {
                categoryIds = categoryIds.equals("") ? selectedCategoryDataID.get(i) : categoryIds + "," + selectedCategoryDataID.get(i);
            }
        }
        String filterIds = "";
        for (int i = 0; i < selectedFilterDataID.size(); i++) {
            if (!selectedFilterDataID.get(i).equals("")) {
                filterIds = filterIds.equals("") ? selectedFilterDataID.get(i) : filterIds + "," + selectedFilterDataID.get(i);
            }
        }
        String downloadsIds = "";
        for (int i = 0; i < selectedDownloadDataID.size(); i++) {
            if (!selectedDownloadDataID.get(i).equals("")) {
                downloadsIds = downloadsIds.equals("") ? selectedDownloadDataID.get(i) : downloadsIds + "," + selectedDownloadDataID.get(i);
            }
        }
        String relatedProductsIds = "";
        for (int i = 0; i < selectedRelatedProductsID.size(); i++) {
            if (!selectedRelatedProductsID.get(i).equals("")) {
                relatedProductsIds = relatedProductsIds.equals("") ? selectedRelatedProductsID.get(i) : relatedProductsIds + "," + selectedRelatedProductsID.get(i);
            }
        }
        Utils.printLog("downloadsIds", "::" + downloadsIds);

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "updateProductLink");
        parameters.put("customer_id", generalFunc.getMemberId());
        parameters.put("product_id", manageProductAct.product_id);
        parameters.put("manufacturerId", manufacturerDataID.get(manufacturerSpinner.getSelectedItemPosition()));
        parameters.put("filterIds", filterIds);
        parameters.put("categoryIds", categoryIds);
        parameters.put("downloadsIds", downloadsIds);
        parameters.put("relatedProductsIds", relatedProductsIds);

        ExecuteWebServerUrl exeWebServer = new ExecuteWebServerUrl(parameters);
        exeWebServer.setLoaderConfig(getActContext(), true, generalFunc);
        exeWebServer.setDataResponseListener(new ExecuteWebServerUrl.SetDataResponse() {
            @Override
            public void setResponse(String responseString) {

                if (responseString != null && !responseString.equals("")) {

                    boolean isDataAvail = GeneralFunctions.checkDataAvail(Utils.action_str, responseString);

                    if (isDataAvail == true) {
                        if (manageProductAct.product_id.equals("")) {
                            String product_id = generalFunc.getJsonValue("product_id", responseString);
                            manageProductAct.product_id = product_id;
                            manageProductAct.setResult(manageProductAct.RESULT_OK);
                        }
                        setLabels();

                        getProductDetails();
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

        if (requestCode == Utils.ALL_CATEGORIES_REQ_CODE && data != null) {

            Utils.printLog("DATAINT", "::" + data.toString());
            addSelectedCategory(data.getStringExtra("category_id"), data.getStringExtra("name"), false);
        } else if (requestCode == Utils.RELATED_PRODUCTS_REQ_CODE && data != null) {

            Utils.printLog("DATAINT", "::" + data.toString());
            addSelectedProducts(data.getStringExtra("product_id"), data.getStringExtra("productName"), false);
        }
    }
}
