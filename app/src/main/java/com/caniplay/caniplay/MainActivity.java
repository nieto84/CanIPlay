package com.caniplay.caniplay;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;


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
import com.caniplay.caniplay.presenter.MainPresenter;
import com.caniplay.caniplay.presenter.MainPresenterImp;
import com.caniplay.caniplay.ui.MainView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.caniplay.caniplay.StatusConnection.checkConnection;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainView{


    private String url = "http://192.168.1.131:8080/api/v1";

    //private String url = "https://admerest.herokuapp.com/api/v1";
    private TextView control;
    private Button logout;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private ListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private  LinearLayoutManager mLayoutManager;
    private List<Evento> arrayEventos = new ArrayList();
    private MainPresenter presenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cargaMenu();
        //Instanciamos el RecyclerView del activity_main layout y lo conectamos con la MainActivity
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // Si se sabe que la cantidad de items de la lista es siempre la misma y esta no cambiará
        // entonces podemos hacer uso de la sigiente propidad para mejorar el
        // Performance del RecyclerView

        // Instanciamos un linear layout manager para setearlo en el RecyclerView
        mLayoutManager = new LinearLayoutManager(MyApplication.getAppContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        control = (TextView) findViewById(R.id.control);
        logout = (Button) findViewById(R.id.logout);

        presenter = new MainPresenterImp(this);

        getPresenter().getLocation();

       if (checkConnection()) {
            getPresenter().firstCallServer();

        }else{
           //Error en la conexión a internet
            Intent LoadScreenError = new Intent(getApplicationContext(), ErrorActivity.class);
            startActivity(LoadScreenError);
        }
    }

    private MainPresenter getPresenter(){

       return this.presenter;
    }

    /**
     * Builds a GoogleApiClient. Uses the addApi() method to request the LocationServices API.
     */

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().getGoogleApiClient().connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if ( getPresenter().getGoogleApiClient().isConnected()) {
            getPresenter().getGoogleApiClient().disconnect();
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

   public boolean isGPSEnabled(){

       LocationManager mlocManager = (LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);;
       return mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

   }

   public boolean isNetworkProviderEnabled(){

       LocationManager lm = (LocationManager) MyApplication.getAppContext().getSystemService(Context.LOCATION_SERVICE);
       return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

   }

public void Logout(View view){

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


  @Override
    public void refreshDataset(List<Evento> arrayEventos) {
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

    @Override
    public void noGPSfound() {
        Toast.makeText(this, "Para una mejor experiencia de usuario activa la localización", Toast.LENGTH_LONG).show();
    }

    @Override
    public void noDataFound() {
        Toast.makeText(this, "No se han importado datos", Toast.LENGTH_LONG).show();
    }

    @Override
    public void noLogin() {
        Toast.makeText(this, "Error al intentar acceder a los datos de tu cuenta", Toast.LENGTH_LONG).show();
    }
    @Override
    public Context getContext(){

    return this;
    }

}
