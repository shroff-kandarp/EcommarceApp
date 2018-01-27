package com.adapter;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.fragment.ProductAddAttributesFragment;
import com.fragment.ProductAddDataFragment;
import com.fragment.ProductAddDiscountsFragment;
import com.fragment.ProductAddGeneralFragment;
import com.fragment.ProductAddImagesFragment;
import com.fragment.ProductAddLinksFragment;
import com.fragment.ProductAddOptionsFragment;
import com.fragment.ProductAddRewardPointsFragment;
import com.fragment.ProductAddSpecialFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

/**
 * Created by tarwindersingh on 26/01/18.
 */

public class AddStoreProductSteppersAdapter extends AbstractFragmentStepAdapter {
    public AddStoreProductSteppersAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Step createStep(int position) {

        if (position == 0) {
            return new ProductAddGeneralFragment();
        } else if (position == 1) {
            return new ProductAddDataFragment();
        } else if (position == 2) {
            return new ProductAddLinksFragment();
        } else if (position == 3) {
            return new ProductAddAttributesFragment();
        } else if (position == 4) {
            return new ProductAddOptionsFragment();
        } else if (position == 5) {
            return new ProductAddDiscountsFragment();
        } else if (position == 6) {
            return new ProductAddSpecialFragment();
        } else if (position == 7) {
            return new ProductAddImagesFragment();
        } else {
            return new ProductAddRewardPointsFragment();
        }
    }

    @Override
    public int getCount() {
        return 9;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        if (position == 0) {
            return new StepViewModel.Builder(context)
                    .setTitle("General") //can be a CharSequence instead
                    .create();
        } else if (position == 1) {
            return new StepViewModel.Builder(context)
                    .setTitle("Data") //can be a CharSequence instead
                    .create();
        } else if (position == 2) {
            return new StepViewModel.Builder(context)
                    .setTitle("Links") //can be a CharSequence instead
                    .create();
        } else if (position == 3) {
            return new StepViewModel.Builder(context)
                    .setTitle("Attribute") //can be a CharSequence instead
                    .create();
        } else if (position == 4) {
            return new StepViewModel.Builder(context)
                    .setTitle("Option") //can be a CharSequence instead
                    .create();
        } else if (position == 5) {
            return new StepViewModel.Builder(context)
                    .setTitle("Discount") //can be a CharSequence instead
                    .create();
        } else if (position == 6) {
            return new StepViewModel.Builder(context)
                    .setTitle("Special") //can be a CharSequence instead
                    .create();
        } else if (position == 7) {
            return new StepViewModel.Builder(context)
                    .setTitle("Image") //can be a CharSequence instead
                    .create();
        } else {
            return new StepViewModel.Builder(context)
                    .setTitle("Reward Points") //can be a CharSequence instead
                    .create();
        }

    }
}
