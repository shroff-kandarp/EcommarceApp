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
public class SignInFragment extends Fragment {

    View view;


    GeneralFunctions generalFunc;

    MaterialEditText emailBox;
    MaterialEditText passwordBox;
    MButton btn_type2;

    MTextView forgetPassTxt;
    MTextView goToRegisterTxtView;

    AppLoginActivity appLoginAct;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        appLoginAct = (AppLoginActivity) getActivity();

        generalFunc = appLoginAct.generalFunc;

        emailBox = (MaterialEditText) view.findViewById(R.id.emailBox);
        passwordBox = (MaterialEditText) view.findViewById(R.id.passwordBox);
        forgetPassTxt = (MTextView) view.findViewById(R.id.forgetPassTxt);
        goToRegisterTxtView = (MTextView) view.findViewById(R.id.goToRegisterTxtView);

        btn_type2 = ((MaterialRippleLayout) view.findViewById(R.id.btn_type2)).getChildView();

        setLabels();

        btn_type2.setId(Utils.generateViewId());

        btn_type2.setOnClickListener(new setOnClickList());
        forgetPassTxt.setOnClickListener(new setOnClickList());
        goToRegisterTxtView.setOnClickListener(new setOnClickList());

        new CreateRoundedView(Color.parseColor("#FFFFFF"), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), getResources().getColor(R.color.appThemeColor_1), goToRegisterTxtView);

        return view;
    }

    public void setLabels() {
        emailBox.setBothText("Email", "Enter your email");
        passwordBox.setBothText("Password", "Enter your password");

        btn_type2.setText("SignIn");
        appLoginAct.headerTxtView.setText("Sign in to access your orders, wishlist and your account.");
    }

    public Context getActContext() {
        return appLoginAct;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.goToRegisterTxtView:
                    appLoginAct.loadFragment(new SignUpFragment());
                    break;
            }
        }
    }
}
