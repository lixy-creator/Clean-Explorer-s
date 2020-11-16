package blooom.cinnamon.cleanexplorers.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import blooom.cinnamon.cleanexplorers.R
import blooom.cinnamon.cleanexplorers.manager.CleanFragmentManager
import com.facebook.CallbackManager


class MainActivity : AppCompatActivity() {

    lateinit var callbackManager:CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CleanFragmentManager.displayLogin(this)

        callbackManager = CallbackManager.Factory.create()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }
}