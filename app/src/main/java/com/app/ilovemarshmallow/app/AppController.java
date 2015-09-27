package com.app.ilovemarshmallow.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.app.ilovemarshmallow.utils.LruBitmapCache;


/**
 * AppController.java - a simple application controller class for maintain singleton instances.
 * these instances are used to maintain http request queues and image loader stuff.
 *
 * @author Saurabh Patel
 *         skpatel@syr.edu
 * @version 1.0
 */
public class AppController extends Application {

    private static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * Retrieve the singleton instance of AppController class
     *
     * @return instance of AppController class.
     */
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    /**
     * Instantiate the RequestQueue if it is not initialized,Volley provides a convenience method Volley.newRequestQueue that sets up a RequestQueue for you, using default values, and starts the queue
     *
     * @return return object of RequestQueue
     */
    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    /**
     * Instantiate the ImageLoader object if it is not initialized,
     * ImageLoader object will be created which also takes care of LruBitmapCache.
     *
     * @return return instance of ImageLoader
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    /**
     * Add application requests for network data in request queue object.
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }
}
