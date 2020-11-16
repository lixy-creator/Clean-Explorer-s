package blooom.cinnamon.cleanexplorers.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import blooom.cinnamon.cleanexplorers.R

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slapsh)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }
}