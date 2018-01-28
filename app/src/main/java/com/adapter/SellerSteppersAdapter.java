package com.adapter;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;
import com.stepstone.stepper.viewmodel.StepViewModel;

import java.util.ArrayList;

/**
 * Created by Shroff on 24-Jan-18.
 */

public class SellerSteppersAdapter extends AbstractFragmentStepAdapter {
//    public SellerSteppersAdapter(FragmentManager fm, Context context) {
//        super(fm, context);
//    }

    public ArrayList<Step> list_step_frag;
    public ArrayList<String> list_step_frag_title;

    public SellerSteppersAdapter(@NonNull FragmentManager fm, @NonNull Context context, ArrayList<Step> list_step_frag, ArrayList<String> list_step_frag_title) {
        super(fm, context);
        this.list_step_frag = list_step_frag;
        this.list_step_frag_title = list_step_frag_title;
    }

    @Override
    public Step createStep(int position) {

        return list_step_frag.get(position);
//        if (position == 0) {
//            return new StoreInfoFragment();
//        } else if (position == 1) {
//            return new StoreDetailFragment();
//        } else {
//            return new SellerBankInfoFragment();
//        }
    }

    @Override
    public int getCount() {
        return list_step_frag.size();
    }

    @NonNull
    @Override
    public StepViewModel getViewModel(@IntRange(from = 0) int position) {
        //Override this method to set Step title for the Tabs, not necessary for other stepper types


        return new StepViewModel.Builder(context)
                .setTitle(list_step_frag_title.get(position)) //can be a CharSequence instead
                .create();
//        if (position == 0) {
//            return new StepViewModel.Builder(context)
//                    .setTitle("Store Info") //can be a CharSequence instead
//                    .create();
//        } else if (position == 1) {
//            return new StepViewModel.Builder(context)
//                    .setTitle("Store Detail") //can be a CharSequence instead
//                    .create();
//        } else {
//            return new StepViewModel.Builder(context)
//                    .setTitle("Bank Details") //can be a CharSequence instead
//                    .create();
//        }

    }
}
