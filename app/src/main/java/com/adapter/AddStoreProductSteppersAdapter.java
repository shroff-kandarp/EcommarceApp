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
 * Created by tarwindersingh on 26/01/18.
 */

public class AddStoreProductSteppersAdapter extends AbstractFragmentStepAdapter {
    public ArrayList<Step> list_step_frag;
    public ArrayList<String> list_step_frag_title;

    public AddStoreProductSteppersAdapter(@NonNull FragmentManager fm, @NonNull Context context, ArrayList<Step> list_step_frag, ArrayList<String> list_step_frag_title) {
        super(fm, context);
        this.list_step_frag = list_step_frag;
        this.list_step_frag_title = list_step_frag_title;
    }

    @Override
    public Step createStep(int position) {

        return list_step_frag.get(position);
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

    }
}
