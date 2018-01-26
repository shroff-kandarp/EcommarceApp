package com.adapter;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.fragment.ProductAddGeneralFragment;
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
            return new StoreDetailFragment();
        } else {
            return new SellerBankInfoFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types
        if (position == 0) {
            return new StepViewModel.Builder(context)
                    .setTitle("Store Info") //can be a CharSequence instead
                    .create();
        } else if (position == 1) {
            return new StepViewModel.Builder(context)
                    .setTitle("Store Detail") //can be a CharSequence instead
                    .create();
        } else {
            return new StepViewModel.Builder(context)
                    .setTitle("Bank Details") //can be a CharSequence instead
                    .create();
        }

    }
}
