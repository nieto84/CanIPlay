package com.caniplay.caniplay.presenter;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by A on 19/12/2017.
 */

public interface MainPresenter {

    void getLocation();
    void getEvents();
    void firstCallServer();
    GoogleApiClient getGoogleApiClient();
}
