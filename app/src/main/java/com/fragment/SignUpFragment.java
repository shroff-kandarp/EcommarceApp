package com.fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecommarceapp.AppLoginActivity;
import com.ecommarceapp.R;
import com.general.files.GeneralFunctions;
import com.utils.Utils;
import com.view.CreateRoundedView;
import com.view.MButton;
import com.view.MTextView;
import com.view.MaterialRippleLayout;
import com.view.editBox.MaterialEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    View view;
    GeneralFunctions generalFunc;

    MaterialEditText nameBox;
    MaterialEditText countryBox;
    MaterialEditText mobileBox;
    MaterialEditText emailBox;
    MaterialEditText passwordBox;

    MTextView goToSignInTxtView;

    MButton btn_type2;

    AppLoginActivity appLoginAct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        appLoginAct = (AppLoginActivity) getActivity();
        generalFunc = appLoginAct.generalFunc;

        nameBox = view.findViewById(R.id.nameBox);
        countryBox = view.findViewById(R.id.countryBox);
        mobileBox = view.findViewById(R.id.mobileBox);
        emailBox = view.findViewById(R.id.emailBox);
        passwordBox = view.findViewById(R.id.passwordBox);
        goToSignInTxtView = view.findViewById(R.id.goToSignInTxtView);

        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();
        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(new setOnClickList());
        goToSignInTxtView.setOnClickListener(new setOnClickList());

        setLabels();
        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), getResources().getColor(R.color.appThemeColor_1), goToSignInTxtView);

        return view;
    }

    public void setLabels() {
        nameBox.setBothText("Name", "Enter your name");
        emailBox.setBothText("Email", "Enter your email");
        passwordBox.setBothText("Password", "Enter your password");
        mobileBox.setBothText("Mobile No.", "Enter your mobile no.");
        countryBox.setBothText("Country");

        btn_type2.setText("SignUp");
        appLoginAct.headerTxtView.setText("Your details are safe with us. We won't share your details with anyone.");
    }


    public Context getActContext() {
        return appLoginAct;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == btn_type2.getId()) {

            } else if (view.getId() == goToSignInTxtView.getId()) {

                appLoginAct.loadFragment(new SignInFragment());
            }
        }
    }
}
