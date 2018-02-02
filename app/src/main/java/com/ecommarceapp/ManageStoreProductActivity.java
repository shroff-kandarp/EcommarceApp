package com.ecommarceapp;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.adapter.AddStoreProductSteppersAdapter;
import com.fragment.ProductAddAttributesFragment;
import com.fragment.ProductAddDataFragment;
import com.fragment.ProductAddDiscountsFragment;
import com.fragment.ProductAddGeneralFragment;
import com.fragment.ProductAddImagesFragment;
import com.fragment.ProductAddLinksFragment;
import com.fragment.ProductAddRewardPointsFragment;
import com.fragment.ProductAddSpecialFragment;
import com.general.files.GeneralFunctions;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;
import com.utils.Utils;
import com.view.MTextView;

import java.util.ArrayList;

public class ManageStoreProductActivity extends AppCompatActivity implements StepperLayout.StepperListener {

    MTextView titleTxt;
    public ImageView backImgView;

    public GeneralFunctions generalFunc;

    private StepperLayout mStepperLayout;

    public Uri fileUri;

    public String product_id = "";
    AddStoreProductSteppersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_store_product);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        backImgView.setOnClickListener(new setOnClickList());

        product_id = getIntent().getStringExtra("product_id") != null ? getIntent().getStringExtra("product_id") : "";
        mStepperLayout = (StepperLayout) findViewById(R.id.stepperLayout);


        ArrayList<Step> step_list_frag = new ArrayList<>();
        step_list_frag.add(new ProductAddGeneralFragment());
        step_list_frag.add(new ProductAddDataFragment());
        step_list_frag.add(new ProductAddLinksFragment());
        step_list_frag.add(new ProductAddAttributesFragment());
//        step_list_frag.add(new ProductAddOptionsFragment());
        step_list_frag.add(new ProductAddDiscountsFragment());
        step_list_frag.add(new ProductAddSpecialFragment());
        step_list_frag.add(new ProductAddImagesFragment());
        step_list_frag.add(new ProductAddRewardPointsFragment());

        ArrayList<String> stepListTitle = new ArrayList<>();
        stepListTitle.add("General");
        stepListTitle.add("Data");
        stepListTitle.add("Links");
        stepListTitle.add("Attribute");
//        stepListTitle.add("Option");
        stepListTitle.add("Discount");
        stepListTitle.add("Special");
        stepListTitle.add("Image");
        stepListTitle.add("Reward Points");

        adapter = new AddStoreProductSteppersAdapter(getSupportFragmentManager(), getActContext(), step_list_frag, stepListTitle);

        mStepperLayout.setListener(this);
        mStepperLayout.setAdapter(adapter);
        setLabels();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current state
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    public void setLabels() {
        titleTxt.setText(product_id.equals("") ? "Add Product" : "Edit Product");
    }

    public Context getActContext() {
        return ManageStoreProductActivity.this;
    }

    @Override
    public void onCompleted(View completeButton) {

    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if (newStepPosition == 0) {
            ((ProductAddGeneralFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }

        if (newStepPosition == 1) {
            ((ProductAddDataFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }
        if (newStepPosition == 2) {
            ((ProductAddLinksFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }
        if (newStepPosition == 3) {
            ((ProductAddAttributesFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }
//        if (newStepPosition == 4) {
//            ((ProductAddOptionsFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
//        }
        if (newStepPosition == 4) {
            ((ProductAddDiscountsFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }
        if (newStepPosition == 5) {
            ((ProductAddSpecialFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }
        if (newStepPosition == 6) {
            ((ProductAddImagesFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }
        if (newStepPosition == 7) {
            ((ProductAddRewardPointsFragment) adapter.list_step_frag.get(newStepPosition)).continueExecution();
        }
    }

    @Override
    public void onReturn() {

    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            Utils.hideKeyboard((Activity) getActContext());
            if (i == R.id.backImgView) {
                ManageStoreProductActivity.super.onBackPressed();
            }
        }
    }
}
