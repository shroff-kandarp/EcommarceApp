package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;

import com.adapter.AllCategoriesAdapter;
import com.general.files.AddDrawer;
import com.general.files.ExecuteWebServerUrl;
import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.models.AllCategoriesParentItem;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.MTextView;
import com.view.bottombar.BottomBar;
import com.view.bottombar.OnTabSelectListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AllCategoriesActivity extends AppCompatActivity implements OnTabSelectListener, ExpandableListView.OnGroupClickListener, ExpandableListView.OnChildClickListener {
    MTextView titleTxt;
//    ImageView backImgView;

    GeneralFunctions generalFunc;
    ProgressBar loading;
    ErrorView errorView;

    ExpandableListView lvExp;
    AllCategoriesAdapter adapter;

    ArrayList<AllCategoriesParentItem> dataList = new ArrayList<>();

    AddDrawer addDrawer;

    BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_categories);

        generalFunc = new GeneralFunctions(getActContext());

        addDrawer = new AddDrawer(getActContext());

        lvExp = (ExpandableListView) findViewById(R.id.lvExp);
        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        loading = (ProgressBar) findViewById(R.id.loading);
        errorView = (ErrorView) findViewById(R.id.errorView);
        bottomBar = findViewById(R.id.bottomBar);

        adapter = new AllCategoriesAdapter(getActContext(), dataList);
        lvExp.setAdapter(adapter);

//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(dataRecyclerView.getContext(),
//                LinearLayout.VERTICAL);
//        dataRecyclerView.addItemDecoration(dividerItemDecoration);

//        adapter.setOnItemClickListener(this);

        lvExp.setOnChildClickListener(this);
        lvExp.setOnGroupClickListener(this);
        setLabels();

        loadAllCategories();

        bottomBar.setDefaultTab(R.id.tab_category);
        bottomBar.setOnTabSelectListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (addDrawer != null) {
            addDrawer.findUserCartCount();
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long l) {
        AllCategoriesParentItem parentItem = dataList.get(position);
        if (parentItem.getSubCategoryList().size() == 0) {
            Bundle bn = new Bundle();
            bn.putString("category_id", parentItem.getCategory_id());
            bn.putString("name", parentItem.getName());
            (new StartActProcess(getActContext())).startActWithData(ListAllProductsActivity.class, bn);
        }
        return false;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
        AllCategoriesParentItem parentItem = dataList.get(groupPosition);
        HashMap<String, String> childItem = parentItem.getSubCategoryList().get(childPosition);
        Bundle bn = new Bundle();
        bn.putString("category_id", childItem.get("category_id"));
        bn.putString("name", childItem.get("name"));
        (new StartActProcess(getActContext())).startActWithData(ListAllProductsActivity.class, bn);
        return false;
    }

//    @Override
//    public void onItemClickList(View v, int position) {
////        HashMap<String, String> data = dataList.get(position);
////        if (data.get("TYPE").equals("" + adapter.TYPE_HEADER)) {
//            Bundle bn = new Bundle();
//            bn.putString("category_id", data.get("category_id"));
//            bn.putString("name", data.get("name"));
//            (new StartActProcess(getActContext())).startActWithData(ListAllProductsActivity.class, bn);
////        }
//    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    AllCategoriesActivity.super.onBackPressed();
                    break;

            }
        }
    }

    public void setLabels() {
        titleTxt.setText("All Categories");
    }

    public void loadAllCategories() {
        if (errorView.getVisibility() == View.VISIBLE) {
            errorView.setVisibility(View.GONE);
        }
        if (loading.getVisibility() != View.VISIBLE) {
            loading.setVisibility(View.VISIBLE);
        }

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put("type", "getAllCategories");

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
                        JSONObject obj_cat = generalFunc.getJsonObject(msgArr, i);
                        JSONArray subCategoriesArr = generalFunc.getJsonArray("SubCategories", obj_cat);

                        String name = generalFunc.getJsonValue("name", obj_cat);
                        String category_id = generalFunc.getJsonValue("category_id", obj_cat);

                        AllCategoriesParentItem parentItem = new AllCategoriesParentItem();
//                                Utils.printLog("CatName","::name::"+name);

                        parentItem.setCategory_id(category_id);
                        parentItem.setName(name);

                        ArrayList<HashMap<String, String>> subCatList = new ArrayList<>();
                        for (int j = 0; j < subCategoriesArr.length(); j++) {
                            JSONObject obj_sub = generalFunc.getJsonObject(subCategoriesArr, j);

                            String subCatName = generalFunc.getJsonValue("name", obj_sub);
                            String subCategoryId = generalFunc.getJsonValue("category_id", obj_sub);

                            HashMap<String, String> dataMap_cat = new HashMap<>();
                            dataMap_cat.put("name", subCatName);
                            dataMap_cat.put("category_id", subCategoryId);

                            subCatList.add(dataMap_cat);
                        }
                        parentItem.setSubCategoryList(subCatList);
//                        dataMap_cat.put("TYPE", "" + adapter.TYPE_HEADER);
                        dataList.add(parentItem);
                    }

                    adapter.notifyDataSetChanged();
                }
            } else {

            }
        } else {

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
                loadAllCategories();
            }
        });
    }

    public Context getActContext() {
        return AllCategoriesActivity.this;
    }

    @Override
    public void onTabSelected(int tabId) {

        switch (tabId) {
            case R.id.tab_home:
                addDrawer.goToHome();
                break;
            case R.id.tab_category:
                break;
            case R.id.tab_deals:
                break;
            case R.id.tab_my_acc:
                break;
        }
    }
}
