package com.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.ecommarceapp.R;


/**
 * Created by Admin on 05-05-2016.
 */
public class MTextView extends AppCompatTextView {
    private static Typeface mTypeface;

    public MTextView(final Context context) {
        this(context, null);
    }

    public MTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            String defaultFont_path = getResources().getString(R.string.defaultFont);
            mTypeface = Typeface.createFromAsset(context.getAssets(), defaultFont_path);
        }
        setTypeface(mTypeface);

//     this.setTextSize(22);
    }
}
