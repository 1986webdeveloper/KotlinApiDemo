package com.acquaintsoft.kotlinapi.utils

import com.android.volley.VolleyError

import java.util.HashMap

/**
 * Created by agc-android on 18/1/17.
 */

interface ApiResponseListener {

    fun onSuccessResponse(response: String, hashMap: HashMap<String, String>)
    fun onErrorResponse(error: VolleyError, hashMap: HashMap<String, String>)

}
