package com.caniplay.caniplay;

import android.app.Application;
import android.content.Context;

/**
 * Created by francesc on 30/09/17.
 */

public class MyApplication extends Application {
    private static MyApplication sInstance;
    private static Boolean isLogged = false;
    private static Boolean isActive = false;
    private static String href_users ="";
    private static String href_eventos ="";
    private static String url_root = "https://admerest.herokuapp.com/api/v1";
    private static String href_self="";
    private static String href_groups="";
    private static String href_self_events="";
    private static String fullName="";
    private static String userName ="default";
    private static double longitud= 41.38;
    private static double latitud = 2.15;
    private static int distance = 5000;


    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
    }

    public static MyApplication getsInstance(){
        return sInstance;
    }

    public static Context getAppContext(){
        return sInstance.getApplicationContext();
    }


    public static Boolean getIsActive() {
        return isActive;
    }

    public static void setIsActive(boolean b) {
        isActive = b;
    }

    public static Boolean getIsLogged() {
        return isLogged;
    }

    public static void setIsLogged(boolean b) {
        isLogged = b;
    }

    public static String getHref_users() {
        return href_users;
    }

    public static void setHref_users(String b) {
        href_users = b;
    }


    public static String getHref_eventos() {
        return href_eventos;
    }

    public static void setHref_eventos(String b) {
        href_eventos = b;
    }


    public static String getUrl_root() {
        return url_root;
    }

    public static String getHref_self() {
        return href_self;
    }

    public static void setHref_self(String b) {
        href_self = b;
    }

    public static String getHref_groups() {
        return href_groups;
    }

    public static void setHref_groups(String b) {
        href_groups = b;
    }

    public static String getHref_self_events() {
        return href_self_events;
    }

    public static void setHref_self_events(String b) {
        href_self_events = b;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String b) {userName = b;}

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName (String b) {fullName = b;}


    public static void setLongitud (double b){

        longitud = b;
    }

    public static double getLongitud(){
        return longitud;

    }


    public static void setLatitud(double b){

        latitud = b;


    }

    public static double getLatitud(){
        return latitud;


    }


    public static void setDistance(int b){

        distance = b;


    }

    public static int getDistance(){
        return distance;


    }
}
