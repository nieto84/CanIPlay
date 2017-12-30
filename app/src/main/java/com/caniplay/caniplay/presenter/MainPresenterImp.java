package com.caniplay.caniplay.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.caniplay.caniplay.AuthRequest;
import com.caniplay.caniplay.ErrorActivity;
import com.caniplay.caniplay.Evento;
import com.caniplay.caniplay.MyApplication;
import com.caniplay.caniplay.User;
import com.caniplay.caniplay.VolleySingleton;
import com.caniplay.caniplay.ui.MainView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by A on 19/12/2017.
 */

public class MainPresenterImp implements MainPresenter,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MainView view;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private ArrayList<Evento> arrayEventos = new ArrayList();


    public MainPresenterImp(MainView view) {
        this.view = view;
    }

    @Override
    public void getLocation() {
        buildGoogleApiClient();
    }

    public void getEvents(){

        Map<String, String> headerMap = new HashMap<String, String>();


        RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();

        if (getBestLocationUser() == 0){

            headerMap.put("lon", String.valueOf(MyApplication.getLongitudDefault()));
            headerMap.put("lat", String.valueOf(MyApplication.getLatitudDefault()));
            headerMap.put("dis", String.valueOf(MyApplication.getDistanceDefault()));

        }else if (getBestLocationUser() == 1){

            headerMap.put("lon",  String.valueOf(MyApplication.getLongitudDefault()));
            headerMap.put("lat", String.valueOf(MyApplication.getLatitudDefault()));
            headerMap.put("dis",  String.valueOf(MyApplication.getDistanceDefault()));
        }else{

            headerMap.put("lon", String.valueOf(MyApplication.getLongitudDefault()));
            headerMap.put("lat", String.valueOf(MyApplication.getLatitudDefault()));
            headerMap.put("dis", String.valueOf(MyApplication.getDistanceDefault()));
        }


        AuthRequest request = new AuthRequest(Request.Method.GET, "http://192.168.1.131:8443/api/v1/distance",
                headerMap,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Check the length of our response (to see if the user has any repos)
                if (response.length() > 0) {

                    String[] names = new String[20];
                    arrayEventos = new ArrayList<>();
                    // The user does have repos, so let's loop through them all.
                    for (int i = 0; i < response.length()-1; i++) {
                        try {
                            JSONArray links = response.getJSONArray("data");
                            //Aqui recogemos toda la lista de eventos

                            for (int j = 0; j < links.length(); j++) {

                                Evento e = new Evento();

                                JSONObject r = links.getJSONObject(j);

                                e.setEventName(r.getString("name"));
                                // Crearemos un array de la clase Evento y cada Evento que se cree se añade al
                                // e.setEventName(names[j]);
                                arrayEventos.add(e);

                            }

                        } catch (JSONException e) {
                            // If there is an error then output this to the logs.
                            Log.e("Volley", "Invalid JSON Object.");
                        }
                    }
                    getView().refreshDataset(arrayEventos);

                } else {
                    // The user didn't have any repos.
                    // listText("No repos found.");
                    Log.e("Volley", "No repos found");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        if (error instanceof TimeoutError || error instanceof NoConnectionError){
                            Log.e("Volley", error.toString());
                        }

                        else if (error instanceof AuthFailureError){
                            Log.e("Volley", error.toString());
                        }else if (error instanceof ServerError){
                            Log.e("Volley", error.toString());
                        }else if (error instanceof NetworkError){
                            Log.e("Volley", error.toString());
                        }else if (error instanceof ParseError){
                            Log.e("Volley", error.toString());
                        }

                    }

                }) {

        };
        request.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Add the request to the RequestQueue.
        queue.add(request);
    }

    public  boolean userPreviouslyLogged(){

        //En sharedpreferences vamos a dejar guardado si el usuario tiene la sesión iniciada o no
        // por lo tanto vamos a recuperar el valor de la ultima sesion del usuario y hacerla efectiva si estaba logueado

        SharedPreferences prefs =
                getView().getContext().getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        String userName = prefs.getString("logged", "off");

        if (userName.equals("off") ){


            return false;

        }else{

            return true;
        }

    }
    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getView().getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        if (ActivityCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getView().getContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {

            // Toast.makeText(this,  mLastLocation.getLatitude(), Toast.LENGTH_LONG).show();

            MyApplication.setLongitud(mLastLocation.getLongitude());
            MyApplication.setLatitud(mLastLocation.getLatitude());

            SharedPreferences prefs = getView().getContext().getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("lat", String.valueOf(mLastLocation.getLatitude()));
            editor.putString("lon", String.valueOf(mLastLocation.getLongitude()));
            editor.commit();

        } else {
            getView().noGPSfound();
        }
    }

    private MainView getView(){

        return this.view;

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        //Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        // Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    public void callServerLogin(final User usuario){


        Map<String, String> headerMap = new HashMap<String, String>();

        String credentials = usuario.getUserName() + ":" + usuario.getPassword();
        String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headerMap.put("Authorization", "Basic " + encodedCredentials);
        headerMap.put("userName", usuario.getUserName());

        RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();

        AuthRequest request = new AuthRequest(Request.Method.GET, MyApplication.getHref_users(), headerMap,null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Check the length of our response (to see if the user has any repos)
                if (response.length() > 0) {

                    setActivePreferences();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONArray links = response.getJSONArray("links");

                            for (int j = 0; j < links.length(); j++) {
                                JSONObject r = links.getJSONObject(j);
                                switch (j) {
                                    case 0:
                                        MyApplication.setHref_self(r.getString("href"));
                                        break;
                                    case 1:
                                        MyApplication.setHref_groups(r.getString("href"));
                                        break;
                                    case 2:
                                        MyApplication.setHref_self_events(r.getString("href"));
                                        break;
                                }
                            }

                            MyApplication.setFullName(response.getString("fullName"));
                            MyApplication.setUserName(response.getString("userName"));

                        } catch (JSONException e) {
                            // If there is an error then output this to the logs.
                            Log.e("Volley", "Invalid JSON Object.");
                        }
                    }

                    //cambioCabeceraMenu();

                    getEvents();

                } else {
                    // The user didn't have any repos.
                    // listText("No repos found.");
                    Log.e("Volley", "No repos found");
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.

                        Log.e("Volley", error.toString());
                    }

                }){


        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void firstCallServer(){

        RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();

        //Llamada al servidor para obtener links y eventos
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, MyApplication.getUrl_root(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the length of our response (to see if the user has any repos)
                        if (response.length() > 0) {

                            // The user does have repos, so let's loop through them all.
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONArray links = response.getJSONArray("links");

                                    for (int j = 0; j < links.length(); j++) {
                                        JSONObject r = links.getJSONObject(j);
                                        switch (j){
                                            case 0:
                                                MyApplication.setHref_users(r.getString("href"));
                                                //control.setText(r.getString("href"));
                                                break;
                                            case 1:
                                                MyApplication.setHref_eventos(r.getString("href"));
                                                break;
                                        }
                                    }

                                } catch (JSONException e) {
                                    // If there is an error then output this to the logs.
                                    getView().noDataFound();

                                }
                            }
                        } else {
                            // The user didn't have any repos.
                            getView().noDataFound();
                        }

                        //Como hemos obtenido respuesta OK del servidor empezamos a validar conexiones, gestionar usuario y eventos
                        if(!MyApplication.getHref_users().isEmpty() && ! MyApplication.getHref_eventos().isEmpty()){

                            if(MyApplication.getIsLogged()){

                                getEvents();

                            }else {

                                if (userPreviouslyLogged()) {

                                    automaticLoginUser();

                                    //Aqui no hay que loguearse el usario ya aesta logueado por lo que todas las llamadas ya se realizan

                                } else {

                                    //El usuario no estaba logueado y por lo tanto no se loguea pero se le muestran los eventos
                                    getEvents();
                                }
                            }
                        }else{
                            //Error en la llamada al servidor no tenemos las urls necesarias para mostrar mas info
                            // Se muestra pantalla de error
                            Intent LoadScreenError = new Intent(getView().getContext(), ErrorActivity.class);
                            getView().getContext().startActivity(LoadScreenError);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // If there a HTTP error then add a note to our repo list.
                        Toast toast2 =
                                Toast.makeText(getView().getContext(),
                                        error.toString(), Toast.LENGTH_LONG);
                        toast2.show();

                        Log.e("Volley", error.toString());
                        //control.setText(error.toString());
                    }
                }
        );
        // Add the request to the RequestQueue.

        request.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void automaticLoginUser(){

        SharedPreferences prefs = getView().getContext().getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        String userName = prefs.getString("userName", "default");
        String fullName = prefs.getString("fullName", "default");
        String password = prefs.getString("password", "default");
        String email = prefs.getString("email", "default");

        if (!userName.equals("default")) {//&& !fullName.equals("default") && !password.equals("default") ){

            //Si hay datos de usuario lo logueamos y marcamos en la shared que está logueado y actualizamos la variable global

            User usuario = new User(password,userName,fullName,email);

            callServerLogin(usuario);

        }else{
            //Se muestra mensaje de error
            getView().noLogin();
            getEvents();

        }
    }

    private void setActivePreferences(){

        MyApplication.setIsActive(true);
        //guardar shared preferences
        if(MyApplication.getIsActive()){
            MyApplication.setIsLogged(true);
            SharedPreferences prefs = getView().getContext().getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("logged", "on");
            editor.commit();
        }
    }

    private int getBestLocationUser(){

        SharedPreferences prefs = getView().getContext().getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        String lat = prefs.getString("lat", "default");

        if(lat.equals("default")){

                return 2;
        }else if(MyApplication.getLatitud() != 0 && MyApplication.getLongitud() != 0){

            return 1;
        }
        else return 0;

    }

}
