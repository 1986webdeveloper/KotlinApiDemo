package com.acquaintsoft.kotlinapi

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.acquaintsoft.kotlinapi.models.UserModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var userDetails: UserModel.UserDetails

    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this;
        userDetails = intent.extras.getParcelable<UserModel.UserDetails>("data")
        Log.d("", "")
        Glide.with(context!!)?.load(userDetails.userImage).apply(RequestOptions.circleCropTransform()).into(imageView)

        val data = "id     : " + userDetails.id + "\n" + "name   : " + userDetails.name + "\n" + "mobile : " + userDetails.mobile + "\n" + "email  : " + userDetails.email
        textView.setText(data)

    }
}
