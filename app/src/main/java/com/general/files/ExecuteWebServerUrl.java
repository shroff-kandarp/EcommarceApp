package com.general.files;

import android.content.Context;

import com.rest.RestClient;
import com.utils.Utils;
import com.view.MyProgressDialog;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Admin on 22-02-2016.
 */
public class ExecuteWebServerUrl/* extends AsyncTask<String, String, String>*/ {

    SetDataResponse setDataRes;

    HashMap<String, String> parameters;

    GeneralFunctions generalFunc;

    String responseString = "";

    boolean directUrl_value = false;
    String directUrl = "";

    boolean isLoaderShown = false;
    Context mContext;

    MyProgressDialog myPDialog;

    boolean isGenerateDeviceToken = false;
    String key_DeviceToken_param;
    boolean isTaskKilled = false;

    public ExecuteWebServerUrl(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public ExecuteWebServerUrl(String directUrl, boolean directUrl_value) {
        this.directUrl = directUrl;
        this.directUrl_value = directUrl_value;
    }

    public void setLoaderConfig(Context mContext, boolean isLoaderShown, GeneralFunctions generalFunc) {
        this.isLoaderShown = isLoaderShown;
        this.generalFunc = generalFunc;
        this.mContext = mContext;
    }

    public void setIsDeviceTokenGenerate(boolean isGenerateDeviceToken, String key_DeviceToken_param) {
        this.isGenerateDeviceToken = isGenerateDeviceToken;
        this.key_DeviceToken_param = key_DeviceToken_param;
    }

    public void execute() {

        if (isLoaderShown == true) {
            myPDialog = new MyProgressDialog(mContext, false, "Loading");
            myPDialog.show();
        }

        if (directUrl_value == false) {
            performPostCall();
        } else {
            responseString = new ExecuteResponse().getResponse(directUrl);
        }
    }

    public void performPostCall() {
        Call<Object> call = RestClient.getClient().getResponse(parameters);
        call.enqueue(new Callback<Object>() {

            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    // request successful (status code 200, 201)

//                    Utils.printLog("Data", "response = " + new Gson().toJson(response.body()));

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

    public void cancel() {
        isTaskKilled = true;
    }

    public void fireResponse() {
        if (myPDialog != null) {
            myPDialog.close();
        }

        if (setDataRes != null && isTaskKilled == false) {
            setDataRes.setResponse(responseString);
        }
    }

    /*@Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (isLoaderShown == true) {
            myPDialog = new MyProgressDialog(mContext, true, "Loading");
            myPDialog.show();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        if (isGenerateDeviceToken == true && generalFunc != null) {

            String vDeviceToken = generalFunc.generateDeviceToken();

            if (vDeviceToken.equals("")) {
                return "";
            }

            if (parameters != null) {
                parameters.put(key_DeviceToken_param, "" + vDeviceToken);
            }
        }
        if (directUrl_value == false) {
//            responseString = OutputStreamReader.performPostCall(CommonUtilities.SERVER_URL_WEBSERVICE_API, parameters);
            performPostCall();
        } else {
            responseString = new ExecuteResponse().getResponse(directUrl);
        }

        return null;
    }

    public void performPostCall() {
        Call<Object> call = RestClient.getClient().getResponse(parameters);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Response<Object> response) {
                if (response.isSuccess()) {
                    // request successful (status code 200, 201)

                    Utils.printLog("Data", "response = " + new Gson().toJson(response.body()));

                    responseString = new Gson().toJson(response.body());

                } else {
                    responseString = "";
                }
            }

            @Override
            public void onFailure(Throwable t) {
                responseString = "";
            }
        });
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (myPDialog != null) {
            myPDialog.close();
        }

        if (setDataRes != null) {
            setDataRes.setResponse(responseString);
        }
    }*/

    public interface SetDataResponse {
        void setResponse(String responseString);
    }

    public void setDataResponseListener(SetDataResponse setDataRes) {
        this.setDataRes = setDataRes;
    }
}
