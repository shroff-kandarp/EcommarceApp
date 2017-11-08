package com.rest;

import com.general.files.LoggingInterceptor;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;
import com.utils.CommonUtilities;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Ashiq Uz Zoha on 9/13/15.
 * Dhrubok Infotech Services Ltd.
 * ashiq.ayon@gmail.com
 */
public class RestClient {

    private static ApiInterface apiInterface;
    private static String baseUrl = CommonUtilities.SERVER_API;

    public static ApiInterface getClient() {
//        if (apiInterface == null) {

        OkHttpClient okClient = new OkHttpClient();
        okClient.setConnectTimeout(55, TimeUnit.SECONDS);
        okClient.setReadTimeout(55, TimeUnit.SECONDS);
        okClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response;
            }
        });

        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverter(String.class, new ToStringConverter())
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        client.client().interceptors().add(new LoggingInterceptor());
        apiInterface = client.create(ApiInterface.class);
//        }
        return apiInterface;
    }

    public static ApiInterface getClient(String baseUrl) {


        OkHttpClient okClient = new OkHttpClient();
        okClient.setConnectTimeout(25, TimeUnit.SECONDS);
        okClient.setReadTimeout(30, TimeUnit.SECONDS);

        okClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Response response = chain.proceed(chain.request());
                return response;
            }
        });

        Retrofit client = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverter(String.class, new ToStringConverter())
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        client.client().interceptors().add(new LoggingInterceptor());
        apiInterface = client.create(ApiInterface.class);

        return apiInterface;
    }

    public interface ApiInterface {

//        @Headers("User-Agent: Retrofit2.0Tutorial-App")
//        @GET("/search/users")
//        Call<LoginResult> getUsersNamedTom(@Query("q") String name);


//        @POST("/api/api.php")
//        @FormUrlEncoded
//        Call<LoginResult> loginUser(@Header("Authorization") String Authorization, @Field("type") String type, @Field("device_type") String device_type, @Field("fcm") String fcm, @Field("latitude") String latitude, @Field("longitude") String longitude);
//
//
//        @GET("/maps/api/geocode/json")
//        Call<Object> getAddressResult(@Query("latlng") String latlng, @Query("sensor") String sensor);
//
//        @GET("/reverse")
//        Call<Object> getAddressResult(@Query("format") String format, @Query("lat") String lat, @Query("lon") String lon, @Query("zoom") String zoom, @Query("addressdetails") String addressdetails);

        @FormUrlEncoded
        @POST("/ecommarce/webservice/webservices.php")
        Call<Object> getResponse(@FieldMap Map<String, String> params);
    }

}
