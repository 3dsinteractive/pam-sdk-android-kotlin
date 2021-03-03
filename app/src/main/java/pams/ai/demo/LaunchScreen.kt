package pams.ai.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import pams.ai.demo.databinding.ActivityLaunchScreenBinding
import pamsdk.PamSDK
import java.lang.Thread.sleep

class LaunchScreen : AppCompatActivity() {
    var binding: ActivityLaunchScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PamSDK.userLogout()
        binding = ActivityLaunchScreenBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }
    }

    override fun onResume() {
        super.onResume()

        val task = CoroutineScope(Dispatchers.Default)
        task.async {
            sleep(0)
            withContext(Dispatchers.Main) {
                val intent = Intent(this@LaunchScreen, LoginPage::class.java)
                startActivity(intent)
                this@LaunchScreen.finish()
            }
        }
    }
}