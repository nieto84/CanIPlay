package com.caniplay.caniplay;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;


import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;

import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
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

import android.widget.Button;
import android.widget.TextView;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.caniplay.caniplay.StatusConnection.checkConnection;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{



    private String url = "http://192.168.1.133:8080/api/v1";

    //private String url = "https://admerest.herokuapp.com/api/v1";
    private TextView control;
    private Button logout;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected ListAdapter mAdapter;
    protected RecyclerView mRecyclerView;
    protected  LinearLayoutManager mLayoutManager;
    private ArrayList<Evento> arrayEventos = new ArrayList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Instanciamos el RecyclerView del activity_main layout y lo conectamos con la MainActivity
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // Si se sabe que la cantidad de items de la lista es siempre la misma y esta no cambiará
        // entonces podemos hacer uso de la sigiente propidad para mejorar el
        // Performance del RecyclerView


        // Instanciamos un linear layout manager para setearlo en el RecyclerView
        mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        buildGoogleApiClient();
        cargaMenu();


        control = (TextView) findViewById(R.id.control);
        logout = (Button) findViewById(R.id.logout);

        if (checkConnection()) {
           firstCallServer();

        }else{
           //Error en la conexión a internet
            Intent LoadScreenError = new Intent(getApplicationContext(), ErrorActivity.class);
            startActivity(LoadScreenError);
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

        } else {
            Toast.makeText(this, "Para una mejor experiencia de usuario activa la localización", Toast.LENGTH_LONG).show();

        }
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


    public void loginOrRegister(View view){

        if(!MyApplication.getIsLogged()){
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
            //fragmentManager.beginTransaction().replace(R.id.co, new HelpFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
****************************************************************************************************************************************
* ***************************************************ENVIO REGISTRO USUARIO******************************************************************
* **************************************************************************************************************************************
 */

    private void envioDatosServidor(final User usuario){

        try {

            RequestQueue requestQueue = VolleySingleton.getInstance().getmRequestQueue();
            //RequestQueue requestQueue = Volley.newRequestQueue(this);

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("fullName", usuario.getFullName()); //Add the data you'd like to send to the server.
            jsonBody.put("userName", usuario.getUserName());
            jsonBody.put("password", usuario.getPassword());


            final String mRequestBody = jsonBody.toString();

            JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, MyApplication.getHref_users(), jsonBody, new Response.Listener<JSONObject>() {
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

       if(MyApplication.getIsLogged()) {
           FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
           fab.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                           .setAction("Action", null).show();
               }
           });
       }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

   }



    public boolean isUserRegisterd(){

        SharedPreferences prefs =
                getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        String userName = prefs.getString("userName", "default");

        if (userName.equals("default") ){

            return false;
        }else{

            return true;
        }

    }

    public boolean checkData(){

        if(MyApplication.getHref_users().isEmpty()){

            return false;

        }else{
            return true;
        }
    }

    public  boolean userPreviouslyLogged(){

        //En sharedpreferences vamos a dejar guardado si el usuario tiene la sesión iniciada o no
        // por lo tanto vamos a recuperar el valor de la ultima sesion del usuario y hacerla efectiva si estaba logueado

        SharedPreferences prefs =
                getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

        String userName = prefs.getString("logged", "off");

        if (userName.equals("offs") ){

            return false;

        }else{

            return true;
        }

    }

    public void automaticLoginUser(){

        SharedPreferences prefs = getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

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
            Toast toast2 =
                    Toast.makeText(getApplicationContext(),
                            "Error al intentar acceder a los datos de tu cuenta.", Toast.LENGTH_SHORT);
            toast2.show();

            getEvents();

        }

    }

   public boolean isGPSEnabled(){

       LocationManager mlocManager = (LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);;
       return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

   }

   public boolean isNetworkProviderEnabled(){

       LocationManager lm = (LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);
       return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

   }
/*
****************************************************************************************************************************************
* ***************************************************FIRST CALL SERVER******************************************************************
* **************************************************************************************************************************************
 */
public void firstCallServer(){

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


                    //Como hemos obtenido respuesta OK del servidor empezamos a validar conexiones, gestionar usuario y eventos
                    if(!MyApplication.getHref_users().isEmpty() && ! MyApplication.getHref_eventos().isEmpty()){

                        if(userPreviouslyLogged()){

                            automaticLoginUser();

                        } else{

                            //El usuario no estaba logueado y por lo tanto no se loguea pero se le muestran los eventos
                            getEvents();
                        }

                    }else{
                        //Error en la llamada al servidor no tenemos las urls necesarias para mostrar mas info
                        // Se muestra pantalla de error
                        Intent LoadScreenError = new Intent(getApplicationContext(), ErrorActivity.class);
                        startActivity(LoadScreenError);
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // If there a HTTP error then add a note to our repo list.
                    Toast toast2 =
                            Toast.makeText(getApplicationContext(),
                                    error.toString(), Toast.LENGTH_LONG);
                    toast2.show();

                    Log.e("Volley", error.toString());
                   //control.setText(error.toString());
                }
            }
    );
    // Add the request to the RequestQueue.
    queue.add(request);
}

public void callServerLogin(final User usuario){

    RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();

    AuthRequest request = new AuthRequest(Request.Method.GET, MyApplication.getHref_users(), null, new Response.Listener<JSONObject>() {


        @Override
        public void onResponse(JSONObject response) {
            // Check the length of our response (to see if the user has any repos)
            if (response.length() > 0) {


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

                MyApplication.setIsActive(true);
                //guardar shared preferences
                if(MyApplication.getIsActive()){
                    MyApplication.setIsLogged(true);
                    SharedPreferences prefs = getSharedPreferences("UserLogin", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("logged", "on");
                    editor.commit();
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


        @Override
        Map<String, String> createBasicAuthHeader(String username, String password) {
            Map<String, String> headerMap = new HashMap<String, String>();

            String credentials = usuario.getUserName() + ":" + usuario.getPassword();
            String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
            headerMap.put("Authorization", "Basic " + encodedCredentials);
            headerMap.put("userName", usuario.getUserName());

            return headerMap;
        }

    };

    queue.add(request);

}

public  void Logout(View view){


    MyApplication.setIsLogged(false);
    MyApplication.setIsActive(false);

    SharedPreferences prefs =
            getSharedPreferences("UserLogin", Context.MODE_PRIVATE);

    SharedPreferences.Editor editor = prefs.edit();
    editor.putString("userName", "default");
    editor.putString("fullName", "default");
    editor.putString("email", "default");
    editor.putString("password", "default");
    editor.putString("logged","off");
    editor.commit();

}

public void IsLogged(View view){

    Toast.makeText(getApplicationContext(),MyApplication.getIsLogged().toString(),
            Toast.LENGTH_LONG).show();

}


    private void getEvents() {
// Instantiate the RequestQueue.
        RequestQueue queue = VolleySingleton.getInstance().getmRequestQueue();


        AuthRequest request = new AuthRequest(Request.Method.GET,MyApplication.getHref_eventos(), null, new Response.Listener<JSONObject>() {
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
                    refreshDataset();

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

                }) {


            @Override
            Map<String, String> createBasicAuthHeader(String username, String password) {
                Map<String, String> headerMap = new HashMap<String, String>();

                String credentials = "nieto84" + ":" + "abc123";
                String encodedCredentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                //headerMap.put("Authorization", "Basic " + encodedCredentials);
                headerMap.put("userName", "");
                headerMap.put("lon", "40.1");
                headerMap.put("lat", "24.1");
                headerMap.put("dis", "5000");


                return headerMap;
            }

        };

        // Add the request to the RequestQueue.
        queue.add(request);

    }

    private void refreshDataset() {
        if (mRecyclerView == null)
            return;

        if (mAdapter == null) {
            mAdapter = new ListAdapter(this, arrayEventos);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

public void newEvent(View view){

    // Verificar si el usuario está logueado

    //Si lo está se le permite crear un nuevo evento


    //Si no lo está se le indica que debe estar registrado y logueado para poder crear un evento
    Toast.makeText(getApplicationContext(),"Crear evento",
            Toast.LENGTH_LONG).show();

}
}
