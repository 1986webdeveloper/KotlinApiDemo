package com.acquaintsoft.kotlinapi

import android.content.Context
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import android.text.TextUtils
import android.util.Log


import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

import java.util.HashMap

/**
 * Created by agc-android on 18/1/17.
 */

class MyApplication : MultiDexApplication() {
    private var queue: RequestQueue? = null
    private var queueSuggestion: RequestQueue? = null
    val TAG = MyApplication::class.java
            .simpleName
    private var context: Context? = null
    private val params: HashMap<String, String>? = null

    override fun onCreate() {
        super.onCreate()
        context = this
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    val requestQueue: RequestQueue
        get() {


            if (queue == null) {
                queue = Volley.newRequestQueue(applicationContext)
            }

            return queue!!
        }

    val requestQueueSuggestion: RequestQueue
        get() {


            if (queueSuggestion == null) {
                queueSuggestion = Volley.newRequestQueue(applicationContext)
            }

            return queueSuggestion!!
        }

    fun <T> addToRequestQueue(req: com.android.volley.Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        requestQueue.add(req)
    }

    fun <T> addToRequestQueue(req: com.android.volley.Request<T>) {
        req.tag = TAG
        requestQueue.add(req)
    }

    fun <T> addToRequestQueueSuggestion(req: com.android.volley.Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        requestQueueSuggestion.add(req)
    }

    fun <T> addToRequestQueueSuggestion(req: com.android.volley.Request<T>) {
        req.tag = TAG
        requestQueueSuggestion.add(req)
    }

    fun cancelPendingRequests(tag: Any) {
        if (queue != null) {
            queue!!.cancelAll(tag)
        }
    }

    fun cancelPendingRequestsSuggestion(tag: Any) {
        if (queueSuggestion != null) {
            queueSuggestion!!.cancelAll(tag)
        }
    }

    fun stopQueue() {
        if (queue != null) {
            queue!!.stop()
        }
    }


}
