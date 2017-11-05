package com.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ecommarceapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppIntroFragment extends Fragment {

    View view;
    ProgressBar loading;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_app_intro, container, false);

        Bundle args = getArguments();
        String headerLBL = args.getString("HeaderLBL");
        int defaultImgId = args.getInt("DefaultImgId");

        ImageView imgView = (ImageView) view.findViewById(R.id.imgView);
        TextView txtView = (TextView) view.findViewById(R.id.txtView);

        txtView.setText(headerLBL);
        imgView.setImageResource(defaultImgId);
        return view;
    }

}
