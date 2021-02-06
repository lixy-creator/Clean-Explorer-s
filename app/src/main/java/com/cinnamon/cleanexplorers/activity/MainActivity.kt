package com.cinnamon.cleanexplorers.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cinnamon.cleanexplorers.R
import com.cinnamon.cleanexplorers.manager.CleanFragmentManager
import com.cinnamon.cleanexplorers.model.User
import com.facebook.CallbackManager
import com.google.gson.Gson





class MainActivity : AppCompatActivity() {

    lateinit var callbackManager:CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedpreferences :SharedPreferences= getSharedPreferences("data", Context.MODE_PRIVATE)
       val userJson : String? =sharedpreferences.getString("user", null)
        if (userJson==null) {
            CleanFragmentManager.displayLogin(this)
        }
        else {
            val gson = Gson()
            val user: User = gson.fromJson(userJson, User::class.java)
            CleanFragmentManager.displayHome(this, user)

        }

        callbackManager = CallbackManager.Factory.create()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }
}