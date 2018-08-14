package com.acquaintsoft.kotlinapi

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks
import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.TextView

import java.util.ArrayList
import android.Manifest.permission.READ_CONTACTS
import android.content.Context
import android.content.Intent
import android.util.Log
import com.acquaintsoft.kotlinapi.models.UserModel
import com.android.volley.VolleyError
import com.google.gson.Gson
import com.acquaintsoft.kotlinapi.utils.ApiController
import com.acquaintsoft.kotlinapi.utils.ApiResponseListener
import com.acquaintsoft.kotlinapi.utils.Common
import com.acquaintsoft.kotlinapi.utils.Constants

import kotlinx.android.synthetic.main.activity_login.*
import java.util.HashMap

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(), ApiResponseListener {

    private lateinit var apiController: ApiController
    private lateinit var context: Context

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        context = this
        apiController = ApiController(context)
        // Set up the login form.
        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })
        btn_sign_in.setOnClickListener(View.OnClickListener { view ->
            attemptLogin()
        })
        btn_register.setOnClickListener(View.OnClickListener { view ->
            val myIntent = Intent(context, RegisterActivity::class.java)
            startActivity(myIntent)
        })

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        email.error = null
        password.error = null

        // Store values at the time of the login attempt.
        val emailStr = email.text.toString()
        val passwordStr = password.text.toString()


        // Check for a valid password, if the user entered one.
        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        var params = HashMap<String, String>()
        params.put("action", "login")
        params.put("mobile", "" + emailStr)
        params.put("password", "" + passwordStr)
        progress.visibility = View.VISIBLE
        apiController.actionCallWebService(Constants.MAIN_URL, params)


    }

    override fun onSuccessResponse(response: String, hashMap: HashMap<String, String>) {
        progress.visibility = View.GONE
        if (hashMap.get("action") != null && hashMap.get("action").equals("login")) {
            var userModel = Gson().fromJson(response, UserModel::class.java)
            if (userModel?.meta?.status.equals("success") && userModel?.meta?.code.equals("200")) {
                val myIntent = Intent(context, MainActivity::class.java)
                myIntent.putExtra("data", userModel.userDetails)
                startActivity(myIntent)
            } else {
                Common.showToast(context, userModel?.meta?.message!!)
            }
        }

    }

    override fun onErrorResponse(error: VolleyError, hashMap: HashMap<String, String>) {
        progress.visibility = View.GONE
        Log.d("", "")
    }

}
