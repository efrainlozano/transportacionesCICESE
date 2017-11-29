package com.transporte.cicese.transportaciones_cicese;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/**
 * Created by blanca on 15/11/2017.
 */

public class MiFirebaseInstanceIdService extends FirebaseInstanceIdService {

    public static final String TAG = "Noticias";

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String token = FirebaseInstanceId.getInstance().getToken();

        Log.d(TAG, "token: " + token);
    }
}
