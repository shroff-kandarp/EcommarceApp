package com.general.files;

import android.content.Context;

import com.rest.RestClient;
import com.utils.Utils;
import com.view.MyProgressDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 08-07-2016.
 */
public class UploadImage /*extends AsyncTask<String, String, String>*/ {

    String selectedImagePath;
    String responseString = "";

    String temp_File_Name = "";
    ArrayList<String[]> paramsList;

    Context mContext;
    MyProgressDialog myPDialog;
    String loading_message = "";
    GeneralFunctions generalFunc;
    String type = "";
    SetResponseListener responseListener;

    public UploadImage(Context mContext, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList, String type) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.type = type;
        this.mContext = mContext;
        generalFunc = new GeneralFunctions(mContext);
    }

    public void setLoadingMessage(String loading_message) {
        this.loading_message = loading_message;
    }

    public void execute() {
        myPDialog = new MyProgressDialog(mContext, false, loading_message);
        try {
            myPDialog.show();
        } catch (Exception e) {

        }

//        String filePath = generalFunc.decodeFile(selectedImagePath, Utils.ImageUpload_DESIREDWIDTH, Utils.ImageUpload_DESIREDHEIGHT, temp_File_Name);


        File file = new File(selectedImagePath);


        MultipartBody.Part filePart = MultipartBody.Part.createFormData("vImage", temp_File_Name, RequestBody.create(MediaType.parse("multipart/form-data"), file));


        HashMap<String, RequestBody> dataParams = new HashMap<>();

        for (int i = 0; i < paramsList.size(); i++) {
            String[] arrData = paramsList.get(i);

            dataParams.put(arrData[0], RequestBody.create(MediaType.parse("text/plain"), arrData[1]));
        }
        Call<Object> call = RestClient.getClient().uploadData(filePart, dataParams);

        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {

                    responseString = RestClient.getGSONBuilder().toJson(response.body());

                    fireResponse();
                } else {
                    responseString = "";
                    fireResponse();
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Utils.printLog("DataError", "::" + t.getMessage());
                responseString = "";
                fireResponse();
            }

        });

    }

    public void fireResponse() {


        try {
            if (myPDialog != null) {
                myPDialog.close();
            }
        } catch (Exception e) {

        }
        if (responseListener != null) {
            responseListener.onFileUploadResponse(responseString, type);
        }
//        myProfileAct.handleImgUploadResponse(responseString);
    }

    public interface SetResponseListener {
        void onFileUploadResponse(String responseString, String type);
    }

    public void setResponseListener(SetResponseListener responseListener) {
        this.responseListener = responseListener;
    }
}
/*public class UploadProfileImage extends AsyncTask<String, String, String> {

    String selectedImagePath;
    String responseString = "";

    String temp_File_Name = "";
    ArrayList<String[]> paramsList;

    MyProfileActivity myProfileAct;
    MyProgressDialog myPDialog;

    public UploadProfileImage(MyProfileActivity myProfileAct, String selectedImagePath, String temp_File_Name, ArrayList<String[]> paramsList) {
        this.selectedImagePath = selectedImagePath;
        this.temp_File_Name = temp_File_Name;
        this.paramsList = paramsList;
        this.myProfileAct = myProfileAct;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        myPDialog = new MyProgressDialog(myProfileAct.getActContext(), false, myProfileAct.generalFunc.retrieveLangLBl("Loading", "LBL_LOADING_TXT"));
        myPDialog.show();
    }


    @Override
    protected String doInBackground(String... strings) {

        String filePath = myProfileAct.generalFunc.decodeFile(selectedImagePath, Utils.ImageUpload_DESIREDWIDTH,
                Utils.ImageUpload_DESIREDHEIGHT, temp_File_Name);
        responseString = new ExecuteResponse().uploadImageAsFile(filePath, temp_File_Name, "vImage", paramsList);

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        myPDialog.close();
        myProfileAct.handleImgUploadResponse(responseString);
    }
}*/
