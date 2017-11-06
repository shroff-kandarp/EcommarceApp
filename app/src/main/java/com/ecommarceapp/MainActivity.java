package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;

import com.utils.Utils;
import com.view.CreateRoundedView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new CreateRoundedView(getResources().getColor(android.R.color.transparent), Utils.dipToPixels(getActContext(), 5), Utils.dipToPixels(getActContext(), 1), getResources().getColor(R.color.appThemeColor_TXT_1), findViewById(R.id.searchArea));
    }

    public Context getActContext() {
        return MainActivity.this;
    }
}
