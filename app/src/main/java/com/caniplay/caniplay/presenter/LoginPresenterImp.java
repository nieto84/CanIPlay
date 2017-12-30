package com.caniplay.caniplay.presenter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.caniplay.caniplay.AuthRequest;
import com.caniplay.caniplay.ErrorActivity;
import com.caniplay.caniplay.MyApplication;
import com.caniplay.caniplay.Profile;
import com.caniplay.caniplay.User;
import com.caniplay.caniplay.VolleySingleton;
import com.caniplay.caniplay.ui.LoginView;
import com.caniplay.caniplay.ui.MainView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by A on 19/12/2017.
 */

public class LoginPresenterImp implements LoginPresenter {


    private LoginView view;
    private boolean created = false;


    public LoginPresenterImp(LoginView view) {
        this.view = view;
    }

    public void userRegistration(final User usuario){

        try {

            RequestQueue requestQueue = VolleySingleton.getInstance().getmRequestQueue();
            //RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("fullName", usuario.getFullName()); //Add the data you'd like to send to the server.
            jsonBody.put("userName", usuario.getUserName());
            jsonBody.put("password", usuario.getPassword());


            final String mRequestBody = jsonBody.toString();

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST,  MyApplication.getHref_users(), jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Log.i("LOG_VOLLEY", response);
                    Toast toast2 =
                            Toast.makeText(getView().getContext(),
                                    "Usuario creado correctamente", Toast.LENGTH_SHORT);
                    toast2.show();

                    created = true;

                    // Si el usuario se crea correctamente automaticamente nos logueamos

                    if(created){

                        //guardar shared preferences

                        SharedPreferences prefs = getView().getContext().getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("userName", usuario.getUserName());
                        editor.putString("fullName", usuario.getFullName());
                        editor.putString("email", usuario.getEmail());
                        editor.putString("password", usuario.getPassword());
                        editor.putString("logged","on");
                        editor.commit();


                        loginUser(usuario);

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    Log.e("LOG_VOLLEY", error.toString());
                   /* Toast toast2 =
                            Toast.makeText(getApplicationContext(),
                                    "Error creando usuario", Toast.LENGTH_SHORT);
                    toast2.show();

                    Toast toast3 =
                            Toast.makeText(getApplicationContext(),
                                    error.toString(), Toast.LENGTH_SHORT);
                    toast3.show();*/
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }


                @Override
                public byte[] getBody() {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        //VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        return null;
                    }
                }
            };

            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loginUser(final User usuario){


        SharedPreferences prefs = getView().getContext().getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", usuario.getUserName());
        editor.putString("fullName", usuario.getFullName());
        editor.putString("email", usuario.getEmail());
        editor.putString("password", usuario.getPassword());
        editor.putString("logged","on");
        editor.commit();

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
                    Intent perfil = new Intent(getView().getContext(), Profile.class);
                    getView().getContext().startActivity(perfil);

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

          /*  @Override
            Map<String, String> createBasicAuthHeader(String username, String password) {
                Map<String, String> headerMap = new HashMap<String, String>();

                String credentials = usuario.getUserName() + ":" + usuario.getPassword();
                String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headerMap.put("Authorization", "Basic " + encodedCredentials);
                headerMap.put("userName", usuario.getUserName());

                return headerMap;
            }*/

        };

        request.setRetryPolicy(new DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private LoginView getView(){

        return this.view;

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


}
