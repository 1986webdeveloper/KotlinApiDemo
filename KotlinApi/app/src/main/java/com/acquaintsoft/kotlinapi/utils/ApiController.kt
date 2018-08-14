package com.acquaintsoft.kotlinapi.utils

import android.content.Context
import android.util.Log
import com.acquaintsoft.kotlinapi.utils.Constants
import com.acquaintsoft.kotlinapi.MyApplication
import com.acquaintsoft.kotlinapi.utils.VolleyMultipartRequest
import com.android.volley.*


import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject

import java.util.HashMap

/**
 * Created by agc-android on 18/1/17.
 */

class ApiController {

    var apiResponseListener: ApiResponseListener? = null
    var context: Context? = null
    var stringRequest: StringRequest? = null

    public constructor(context: Context) {
        this.context = context
        this.apiResponseListener = context as ApiResponseListener

    }

    fun actionCallWebService(url: String, params: HashMap<String, String>) {
        stringRequest = object : StringRequest(Request.Method.POST, url,

                Response.Listener<String> { response ->

                    apiResponseListener!!.onSuccessResponse(response, params)
                    Log.e("multiple", "time")
                },
                Response.ErrorListener { error ->

                    apiResponseListener!!.onErrorResponse(error, params)

                }) {
            override fun getParams(): Map<String, String> {
                return params
            }

            override fun getHeaders(): MutableMap<String, String> {
                /*var map = HashMap<String,String>()
                map.put("user","admin")
                map.put("password","admin")*/
                return super.getHeaders()
            }

        }
        stringRequest!!.retryPolicy = DefaultRetryPolicy(
                Constants.MY_API_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        MyApplication.instance.addToRequestQueue(stringRequest!!)


    }

    fun actionCallWebServiceWithFiles(url: String, params: HashMap<String, String>, file: Map<String, VolleyMultipartRequest.DataPart>) {

        val multipartRequest = object : VolleyMultipartRequest(Request.Method.POST, url, Response.Listener<NetworkResponse> { response ->
            val resultResponse = String(response.data)
            try {
                apiResponseListener?.onSuccessResponse(resultResponse, params)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            apiResponseListener?.onErrorResponse(error, params)
        }) {
            override fun getParams(): Map<String, String> {
                return params
            }

            override fun getByteData(): Map<String, DataPart> {
                return file
            }


        }
        multipartRequest.setRetryPolicy(DefaultRetryPolicy(
                Constants.MY_API_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        MyApplication.instance.addToRequestQueue(multipartRequest)
    }

}
