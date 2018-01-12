package com.ecommarceapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.general.files.GeneralFunctions;
import com.general.files.StartActProcess;
import com.view.MTextView;

public class ContactUsActivity extends AppCompatActivity {
    MTextView titleTxt;
    ImageView backImgView;
    GeneralFunctions generalFunc;

    View openContactFormArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        generalFunc = new GeneralFunctions(getActContext());

        titleTxt = (MTextView) findViewById(R.id.titleTxt);
        backImgView = (ImageView) findViewById(R.id.backImgView);
        openContactFormArea = findViewById(R.id.openContactFormArea);

        backImgView.setOnClickListener(new setOnClickList());
        openContactFormArea.setOnClickListener(new setOnClickList());

        titleTxt.setText("Contact Us");

    }

    public Context getActContext() {
        return ContactUsActivity.this;
    }

    public class setOnClickList implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.backImgView:
                    ContactUsActivity.super.onBackPressed();
                    break;
                case R.id.openContactFormArea:
                    new StartActProcess(getActContext()).startAct(ContactFormActivity.class);
                    break;

            }
        }
    }
}
