package com.caniplay.caniplay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.caniplay.caniplay.StatusConnection.checkConnection;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

private static  int SPLASH_TIME_OUT = 4000;
    //private String url = "http://10.0.2.2:8080/api/v1";
    private String url = "http://10.0.2.2:8080/api/v1";
    private String href_users = "";
    private String href_eventos = "";
    public Boolean userExist = false;
    public Boolean isLogged = false;

    private static final String LOGTAG = "android-localizacion";

    private static final int PETICION_PERMISO_LOCALIZACION = 101;

    private GoogleApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent home = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(home); // Cambio de aplicación
            }
        },SPLASH_TIME_OUT);

        setContentView(R.layout.activity_main);
        cargaMenu();

        if (checkConnection()) {
            if(callServer()){
                userExist = isUserRegisterd();
                if (userExist) {
                    //loginUser();
                    //cambioCabeceraMenu();
                    //callServerForEvents(href_eventos);
                    //callServerForUserActions(href_users);
                }else{
                    //callServerForEvents(href_eventos);
                }

            }else{
                Intent LoadScreenError = new Intent(getApplicationContext(), ErrorActivity.class);
                startActivity(LoadScreenError); // Cambio de aplicación

            }
        }else{
            Intent LoadScreenError = new Intent(getApplicationContext(), ErrorActivity.class);
            startActivity(LoadScreenError); // Cambio de aplicación


        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void login(View view){

        if(!userExist){
            Intent newLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(newLogin); // Cambio de aplicación

        }else{
            Intent profile = new Intent(getApplicationContext(), Profile.class);
            startActivity(profile); // Cambio de aplicación
        }

    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.ayuda) {
            fragmentManager.beginTransaction().replace(R.id.co, new HelpFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private void envioDatosServidor(final User usuario){

        try {

            RequestQueue requestQueue = VolleySingleton.getInstance().getmRequestQueue();
            //RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("fullName", usuario.getFullName()); //Add the data you'd like to send to the server.
            jsonBody.put("userName", usuario.getUserName());
            jsonBody.put("password", usuario.getPassword());


            final String mRequestBody = jsonBody.toString();

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, href_users, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // Log.i("LOG_VOLLEY", response);
                    Toast toast2 =
                            Toast.makeText(getApplicationContext(),
                                    "Usuario creado correctamente", Toast.LENGTH_SHORT);
                    toast2.show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("LOG_VOLLEY", error.toString());
                    Toast toast2 =
                            Toast.makeText(getApplicationContext(),
                                    "Error creando usuario", Toast.LENGTH_SHORT);
                    toast2.show();
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

   public void cargaMenu(){

       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

   }

   public boolean callServer(){

       RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();

       //Llamada al servidor para obtener links y eventos
       JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                               href_users = r.getString("href");
                                               break;
                                           case 1:
                                               href_eventos = r.getString("href");
                                               break;
                                       }
                                   }
                               } catch (JSONException e) {
                                   // If there is an error then output this to the logs.
                                   Toast toast2 =
                                           Toast.makeText(getApplicationContext(),
                                                   "No se han importado datos", Toast.LENGTH_SHORT);
                                   toast2.show();
                               }
                           }
                       } else {
                           // The user didn't have any repos.
                           Toast toast2 =
                                   Toast.makeText(getApplicationContext(),
                                           "No se han importado datos", Toast.LENGTH_SHORT);
                           toast2.show();
                       }
                   }
               },
               new Response.ErrorListener() {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       // If there a HTTP error then add a note to our repo list.
                       Toast toast2 =
                               Toast.makeText(getApplicationContext(),
                                       "Error en la conexión", Toast.LENGTH_SHORT);
                       toast2.show();

                       Log.e("Volley", error.toString());
                   }
               }
       );
       // Add the request to the RequestQueue.
       queue.add(request);

       if(href_users.isEmpty()){

           return false;

       }else{
           return true;
       }


   }

    public boolean isUserRegisterd(){

        SharedPreferences prefs =
                getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        String userName = prefs.getString("userName", "default");

        if (userName.equals("default") ){

            return false;
        }else{

            return false;
        }

    }
}
