package com.general.files;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.View;

import com.ecommarceapp.LauncherActivity;
import com.ecommarceapp.R;
import com.facebook.login.LoginManager;
import com.utils.ScalingUtilities;
import com.utils.Utils;
import com.view.ErrorView;
import com.view.GenerateAlertBox;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Shroff on 08-Dec-16.
 */
public class GeneralFunctions {
    Context mContext;

    public static final int MY_PERMISSIONS_REQUEST = 51;

    public GeneralFunctions(Context mContext) {
        this.mContext = mContext;
    }

    public void storedata(String key, String data) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(key, data);
        editor.commit();
    }

    public String retriveValue(String key) {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String value_str = mPrefs.getString(key, "");

        return value_str;
    }

    public boolean isFirstLaunchFinished() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String isFirstLaunchFinished_str = mPrefs.getString(Utils.isFirstLaunchFinished, "");

        if (!isFirstLaunchFinished_str.equals("")) {
            return true;
        }

        return false;
    }

    public boolean isRTLmode() {
        return false;
    }

    public void storeUserData(String memberId) {
        storedata(Utils.iMemberId_KEY, memberId);
        storedata(Utils.userLoggedIn_key, "1");
    }

    public String getMemberId() {
        if (isUserLoggedIn() == true) {
            return retriveValue(Utils.iMemberId_KEY);
        } else {
            return "";
        }
    }

    public Typeface getDefaultFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), mContext.getResources().getString(R.string.defaultFont));
    }

    public void signOut() {
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();

        editor.commit();

        restartApp();
    }


    public String wrapHtml(Context context, String html) {
        return context.getString(R.string.html, html);
    }

    public boolean isUserLoggedIn() {

        if (!retriveValue(Utils.userLoggedIn_key).equals("") && retriveValue(Utils.userLoggedIn_key).equals("1")) {
            return true;
        }

        return false;
    }

    public boolean checkLocationPermission(boolean isPermissionDialogShown) {
        int permissionCheck_fine = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck_coarse = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck_fine != PackageManager.PERMISSION_GRANTED || permissionCheck_coarse != PackageManager.PERMISSION_GRANTED) {

            if (isPermissionDialogShown == false) {
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST);
            }


            // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            return false;
        }

        return true;
    }

    public static boolean checkDataAvail(String key, String response) {
        try {
            JSONObject obj_temp = new JSONObject(response);

            String action_str = obj_temp.getString(key);

            if (!action_str.equals("") && !action_str.equals("0") && action_str.equals("1")) {
                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return false;
        }

        return false;
    }

    public String getJsonValue(String key, String response) {

        try {
            JSONObject obj_temp = new JSONObject(response);

            String value_str = obj_temp.getString(key);

            if (value_str != null && !value_str.equals("null") && !value_str.equals("")) {
                return value_str;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return "";
        }

        return "";
    }

    public String getJsonValue(String key, JSONObject obj) {

        try {

            String value_str = obj.getString(key);

            if (value_str != null && !value_str.equals("null") && !value_str.equals("")) {
                return value_str;
            }

        } catch (JSONException e) {
            e.printStackTrace();

            return "";
        }

        return "";
    }

    public JSONArray getJsonArray(String key, String response) {
        try {
            JSONObject obj_temp = new JSONObject(response);
            JSONArray obj_temp_arr = obj_temp.getJSONArray(key);

            return obj_temp_arr;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public JSONArray getJsonArray(String key, JSONObject obj) {
        try {
            JSONArray obj_temp_arr = obj.getJSONArray(key);

            return obj_temp_arr;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }
    }

    public JSONArray getJsonArray(String response) {
        try {
            JSONArray obj_temp_arr = new JSONArray(response);

            return obj_temp_arr;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    public JSONObject getJsonObject(JSONArray arr, int position) {
        try {
            JSONObject obj_temp = arr.getJSONObject(position);

            return obj_temp;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    public JSONObject getJsonObject(String key, String responseString) {
        try {
            JSONObject obj_temp = new JSONObject(responseString);
            JSONObject obj = obj_temp.getJSONObject(key);
            return obj;

        } catch (JSONException e) {
            e.printStackTrace();

            return null;
        }

    }

    public boolean isJSONkeyAvail(String key, String response) {
        try {
            JSONObject json_obj = new JSONObject(response);

            if (json_obj.has(key) && !json_obj.isNull(key)) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean isJSONArrKeyAvail(String key, String response) {
        try {
            JSONObject json_obj = new JSONObject(response);

            if (json_obj.optJSONArray(key) != null) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public void showError() {
        GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setContentMessage("Error Occurred", "Please try again later.");
        generateAlert.setPositiveBtn("OK");
        generateAlert.showAlertBox();
    }

    public void showGeneralMessage(String title, String message) {
        GenerateAlertBox generateAlert = new GenerateAlertBox(mContext);
        generateAlert.setContentMessage(title, message);
        generateAlert.setPositiveBtn("OK");
        generateAlert.showAlertBox();
    }

    public String generateDeviceToken() {
//        if (checkPlayServices() == false) {
//            return "";
//        }
//        InstanceID instanceID = InstanceID.getInstance(mContext);
//        String GCMregistrationId = "";
//        try {
//            GCMregistrationId = instanceID.getToken(retrieveValue(CommonUtilities.APP_GCM_SENDER_ID_KEY), GoogleCloudMessaging.INSTANCE_ID_SCOPE,
//                    null);
//        } catch (IOException e) {
//            e.printStackTrace();
//            GCMregistrationId = "";
//        }
//
//        return GCMregistrationId;

        return "";
    }

    public boolean checkPlayServices() {
//        final GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
//        final int result = googleAPI.isGooglePlayServicesAvailable(mContext);
//        if (result != ConnectionResult.SUCCESS) {
//            if (googleAPI.isUserResolvableError(result)) {
//
//                ((Activity) mContext).runOnUiThread(new Runnable() {
//                    public void run() {
//                        googleAPI.getErrorDialog(((Activity) mContext), result,
//                                Utils.PLAY_SERVICES_RESOLUTION_REQUEST).show();
//                    }
//                });
//
//            }
//
//            return false;
//        }

        return true;
    }


    public static Integer parseInt(int orig, String value) {

        try {
            int value_int = Integer.parseInt(value);
            return value_int;
        } catch (Exception e) {
            return orig;
        }

    }

    public static double parseDouble(double orig, String value) {

        try {
            double value_int = Double.parseDouble(value);
            return value_int;
        } catch (Exception e) {
            return orig;
        }

    }

    public float parseFloat(float orig, String value) {

        try {
            float value_int = Float.parseFloat(value);
            return value_int;
        } catch (Exception e) {
            return orig;
        }

    }

    public void restartApp() {
        (new StartActProcess(mContext)).startAct(LauncherActivity.class);
        ActivityCompat.finishAffinity((Activity) mContext);
    }

    public static View getCurrentView(Activity act) {
        View view = act.findViewById(android.R.id.content);
        return view;
    }

    public void showMessage(View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public boolean isEmailValid(String email) {
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

    public void generateErrorView(ErrorView errorView, String title, String subTitle) {
        errorView.setConfig(ErrorView.Config.create()
                .title(title)
                .titleColor(mContext.getResources().getColor(android.R.color.black))
                .subtitle(subTitle)
                .retryText("Retry")
                .retryTextColor(mContext.getResources().getColor(R.color.error_view_retry_btn_txt_color))
                .build());
    }


    public static void printHashKey(Context mContext) {
        try {
            PackageInfo info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Utils.printLog("hashKey", ":" + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Utils.printLog("hashKey", ":Exception:" + e.getMessage());
        } catch (Exception e) {
            Utils.printLog("hashKey", ":Exception:" + e.getMessage());
        }
    }


    public void logOUTFrmFB() {
        LoginManager.getInstance().logOut();
    }


    public boolean isCameraPermissionGranted() {
        int permissionCheck_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

        if (permissionCheck_storage == PackageManager.PERMISSION_GRANTED && permissionCheck_camera == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            return false;
        }
    }

    public boolean isCameraStoragePermissionGranted() {

        int permissionCheck_storage = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionCheck_camera = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);

        if (permissionCheck_storage != PackageManager.PERMISSION_GRANTED || permissionCheck_camera != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    MY_PERMISSIONS_REQUEST);

            return false;
        }

        return true;
    }

    public String decodeFile(String path, int DESIREDWIDTH, int DESIREDHEIGHT, String tempImgName) {
        String strMyImagePath = null;
        Bitmap scaledBitmap = null;

        try {
            // Part 1: Decode image
//            scaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT);
            int rotation = Utils.getExifRotation(path);
            Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.CROP);
            } else {
//                unscaledBitmap.recycle();
//                return path;

                if (unscaledBitmap.getWidth() > unscaledBitmap.getHeight()) {
                    scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, unscaledBitmap.getHeight(), unscaledBitmap.getHeight(), ScalingUtilities.ScalingLogic.CROP);
                } else {
                    scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, unscaledBitmap.getWidth(), unscaledBitmap.getWidth(), ScalingUtilities.ScalingLogic.CROP);
                }
            }

            // Store to tmp file
            scaledBitmap = rotateBitmap(scaledBitmap, rotation);
            String extr = Environment.getExternalStorageDirectory().toString();
            File mFolder = new File(extr + "/" + Utils.TempImageFolderPath);
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

//            String s = "tmp.png";

            File f = new File(mFolder.getAbsolutePath(), tempImgName);

            strMyImagePath = f.getAbsolutePath();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {

                e.printStackTrace();
            } catch (Exception e) {

                e.printStackTrace();
            }

            scaledBitmap.recycle();
        } catch (Throwable e) {
        }

        if (strMyImagePath == null) {
            return path;
        }
        return strMyImagePath;

    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap;
        }
    }

}
