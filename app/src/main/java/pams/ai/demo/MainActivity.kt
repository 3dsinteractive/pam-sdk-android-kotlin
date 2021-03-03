package pams.ai.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pams.ai.demo.databinding.ActivityMainBinding
import pamsdk.PamSDK

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }
    }

    override fun onStart() {
        super.onStart()

        binding?.btnLogin?.setOnClickListener {
            PamSDK.userLogin("1pEKNhfP8XNKhjMWtLAG3W95C6T")
        }
        binding?.btnPageView?.setOnClickListener {
            PamSDK.track(
                "page_view", mutableMapOf(
                    "page_url" to "pam sdk android",
                    "page_title" to "Pam SDK Android",
                    "form_fields" to mutableMapOf<String, String>(
                        "firstname" to "Choengchai",
                        "lastname" to "Phachonyut"
                    )
                )
            )
        }
        binding?.btnLogout?.setOnClickListener {
            PamSDK.userLogout()
        }
    }
}