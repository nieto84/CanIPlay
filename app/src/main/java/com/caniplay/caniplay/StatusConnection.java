package com.caniplay.caniplay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.caniplay.caniplay.MyApplication.getAppContext;

/**
 * Created by A on 23/11/2017.
 */

public class StatusConnection {


    public static boolean checkConnection() {

        //Se verifica si el dispositivo dispone de conexión a internet
        boolean a = false;

        ConnectivityManager connectivity = (ConnectivityManager) getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo wifi = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo datos = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifi != null || datos != null) {
                if (wifi.isConnected() || datos.isConnected()) {
                    a = true;
                }
            }
        } else {
            a = false;
        }
        return a;
    }

}

  /*  //Instanciamos una referencia al Contexto
    Context context = this.getApplicationContext();
    //Instanciamos el objeto SharedPreferences y creamos un fichero Privado bajo el
    //nombre definido con la clave preference_file_key en el fichero string.xml
    sharedPref = context.getSharedPreferences(
    getString(R.string.preference_file_key), Context.MODE_PRIVATE);

    //Tambien es posible obtener una instancia del objeto SharedPreferences con la siguiente
    // sentencia, esta opcin es usada cuando se sabe que en la aplicacion no se usara mas
    // de un fichero de preferencias:
    //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

    getProfile();
}

   private void saveProfile() {
        //Capturamos en una variable de tipo String
        String nombre = etNombre.getText().toString();
        String apellido = etApellido.getText().toString();
        String email = etEmail.getText().toString();
        String cargo = etCargo.getText().toString();

        //Instanciamos un objeto del SharedPreferences.Editor
        //el cual nos permite almacenar con su metodo putString
        //los 4 valores del perfil profesional asociandolos a una
        // clave la cual definimos como un string en el fichero strings.xml
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.nombre_key), nombre);
        editor.putString(getString(R.string.apellido_key), apellido);
        editor.putString(getString(R.string.email_key), email);
        editor.putString(getString(R.string.cargo_key), cargo);

        //NOTA: En el caso de que necesitemos gauirdar un valor numerico podeis usar
        //el metodo putInt en vez del putString.

        //Con el método commit logramos guardar los datos en el fichero
        //de preferncias compartidas de nombre cuyo nombre se defini en
        // el String preference_file_key
        editor.commit();

        //Notificamos la usuario de que se han guardado los datos del perfil correctamente.
        Toast.makeText(getApplicationContext(),getString(R.string.msg_save), Toast.LENGTH_SHORT).show();
    }

    private void getProfile() {
        //Para obtener los datos previamente guardados
        // simplemente empleamos el método getString()
        // del objeto SharedPreferences
        String nombre = sharedPref.getString((getString(R.string.nombre_key)), "");
        etNombre.setText(nombre);
        String apellido = sharedPref.getString((getString(R.string.apellido_key)), "");
        etApellido.setText(apellido);
        String email = sharedPref.getString((getString(R.string.email_key)), "");
        etEmail.setText(email);
        String cargo = sharedPref.getString((getString(R.string.cargo_key)), "");
        etCargo.setText(cargo);
    }

    private void clearProfile() {
        //Para borrar el registro de algun dato en elfichero compartido
        // sencillamente empleamos el metodo remove(key)
        // del objeto SharedPreferences.Editor
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(getString(R.string.nombre_key));
        editor.remove(getString(R.string.apellido_key));
        editor.remove(getString(R.string.email_key));
        editor.remove(getString(R.string.cargo_key));
        editor.commit();

        //Limpiamos los EditText de la pantalla
        etNombre.setText("");
        etApellido.setText("");
        etEmail.setText("");
        etCargo.setText("");
    }*/


