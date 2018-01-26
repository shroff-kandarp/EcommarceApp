package com.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.view.MyProgressDialog;
import com.view.editBox.MaterialEditText;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shroff on 7/3/2016.
 */
public class Utils {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);


    public static final int ImageUpload_DESIREDWIDTH = 1024;
    public static final int ImageUpload_DESIREDHEIGHT = 1024;
    public static final int ImageUpload_MINIMUM_WIDTH = 256;
    public static final int ImageUpload_MINIMUM_HEIGHT = 256;

    public static int minPasswordLength = 2;
    public static String isFirstLaunchFinished = "isFirstLaunchFinished";
    public static String iMemberId_KEY = "iMemberId";
    public static String userLoggedIn_key = "isUserLoggedIn";

    public static final String TempProfileImageName = "temp_pic_img.png";
    public static final String TempImageFolderPath = "TempImages";

    public static String device_type = "android";
    public static String action_str = "Action";
    public static String message_str = "message";

    public static final int MENU_ABOUT_US = 0;
    public static final int MENU_BLOG = 1;
    public static final int MENU_VIDEOS = 2;
    public static final int MENU_SIGN_OUT = 3;
    public static final int MENU_HELP = 4;

    public static final int GOOGLE_SIGN_IN_REQ_CODE = 112;
    public static final int SELECT_COUNTRY_REQ_CODE = 124;
    public static final int ADD_ADDRESS_REQ_CODE = 125;
    public static final int CHOOSE_ADDRESS_REQ_CODE = 126;
    public static final int PLACE_ORDER_REQ_CODE = 127;
    public static final int CHOOSE_PAY_OPTION_REQ_CODE = 128;
    public static final int BECOME_SELLER_REQ_CODE = 129;
    public static final int SELECT_STATE_REQ_CODE = 130;
    public static final int ADD_PRODUCT_STORE_REQ_CODE = 131;

    static MyProgressDialog myPDialog;
    //Single Instance object
    private static Utils instance = null;

    //
    private Utils() {
    }

    //Single Instance get
    public static Utils getInstance() {
        if (instance == null)
            instance = new Utils();

        return instance;
    }

    public static void printLog(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static void hideKeyboard(Activity act) {
//        ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE))
//                .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);

        if (act.getCurrentFocus() == null) {
            return;
        }

        InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(act.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics));
    }

    public static boolean checkText(MaterialEditText editBox) {
        if (getText(editBox).trim().equals("")) {
            return false;
        }
        return true;
    }

    public static boolean checkText(EditText editBox) {
        if (getText(editBox).trim().equals("")) {
            return false;
        }
        return true;
    }

    public static String getText(MaterialEditText editBox) {
        return editBox.getText().toString();
    }

    public static String getText(EditText editBox) {
        return editBox.getText().toString();
    }

    public static boolean setErrorFields(MaterialEditText editBox, String error) {
        editBox.setError(error);
        return false;
    }

    public static void removeInput(EditText editBox) {
        editBox.setInputType(InputType.TYPE_NULL);
        editBox.setFocusableInTouchMode(false);
        editBox.setFocusable(false);

        editBox.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return true;
            }
        });
    }

    public static int generateViewId() {
        /**
         * Generate a value suitable for use in {@link #setId(int)}.
         * This value will not collide with ID values generated at build time by aapt for R.id.
         *
         * @return a generated ID value
         */

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            for (; ; ) {
                final int result = sNextGeneratedId.get();
                // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
                int newValue = result + 1;
                if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
                if (sNextGeneratedId.compareAndSet(result, newValue)) {
                    return result;
                }
            }

        } else {
            return View.generateViewId();
        }

    }

    public static void runGC() {
        System.gc();
    }


    public static void closeLoader() {
        if (myPDialog != null) {
            myPDialog.close();
            myPDialog = null;
        }
    }

    public static MyProgressDialog showLoader(Context mContext) {
        myPDialog = new MyProgressDialog(mContext, true, "Loading");
        myPDialog.show();

        return myPDialog;
    }

    public static void animateMarker(GoogleMap gMap, final Marker marker, final LatLng toPosition, final boolean hideMarker, float rotation, final long duration) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = gMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
//        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });

        rotateMarker(marker, rotation, gMap, duration);
    }

    public static void rotateMarker(final Marker marker, final float toRotation, GoogleMap map, final long duration) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final float startRotation = marker.getRotation();
//        final long duration = 1000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                float rot = t * toRotation + (1 - t) * startRotation;

                marker.setRotation(-rot > 180 ? rot / 2 : rot);
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }

    public static int getSDKInt() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }


    public static int getExifRotation(String path) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        return orientation;

    }


    public static String[] generateImageParams(String key, String content) {
        String[] tempArr = new String[2];
        tempArr[0] = key;
        tempArr[1] = content;

        return tempArr;
    }

    public static boolean isValidImageResolution(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(path, options);
        int width = options.outWidth;
        int height = options.outHeight;

        if (width >= Utils.ImageUpload_MINIMUM_WIDTH && height >= Utils.ImageUpload_MINIMUM_HEIGHT) {
            return true;
        }
        return false;
    }


    public static void setMultiLineEditBox(MaterialEditText editBox) {
        editBox.setSingleLine(false);
        editBox.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editBox.setGravity(Gravity.TOP);
    }
}
