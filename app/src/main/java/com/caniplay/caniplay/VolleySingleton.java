package com.caniplay.caniplay;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by francesc on 30/09/17.
 */

public class VolleySingleton {
    private static final VolleySingleton ourInstance = new VolleySingleton();

    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;

    public static VolleySingleton getInstance() {
        return ourInstance;
    }

    private VolleySingleton() {
        mRequestQueue = Volley.newRequestQueue(MyApplication.getAppContext());

        imageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private LruCache<String, Bitmap> cache =
                    new LruCache<>((int)(Runtime.getRuntime().maxMemory()/1024)/8);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public RequestQueue getmRequestQueue(){
        return mRequestQueue;
    }

    public ImageLoader getImageLoader(){
        return imageLoader;
    }
}
