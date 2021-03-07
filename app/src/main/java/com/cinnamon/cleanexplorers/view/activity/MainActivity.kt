package com.cinnamon.cleanexplorers.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cinnamon.cleanexplorers.R


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_main)
    }


}
