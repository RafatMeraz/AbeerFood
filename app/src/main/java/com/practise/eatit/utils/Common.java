package com.practise.eatit.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.practise.eatit.model.User;
import com.practise.eatit.remote.APISerivice;
import com.practise.eatit.remote.IGoogleService;
import com.practise.eatit.remote.RetrofitClient;

public class Common {

    public static User currentUser;
    public static final String  BASE_URL = "https://fcm.googleapis.com/";
    public static final String  GOOGLE_API_URL = "https://maps.googleapis.com/";

    public static APISerivice getFCMService(){
        return RetrofitClient.getclient(BASE_URL).create(APISerivice.class);
    }

    public static IGoogleService getGoogleMapAPI(){
        return RetrofitClient.getclient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){
                for (int i=0; i<info.length; i++){
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

}
