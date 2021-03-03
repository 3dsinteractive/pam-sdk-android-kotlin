package pams.ai.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pams.ai.demo.databinding.ActivityRegisterPageBinding
import pamsdk.PamSDK

class RegisterPage : AppCompatActivity() {
    var binding: ActivityRegisterPageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        this.registerButtonRegister()
    }

    private fun registerButtonRegister() {
        binding?.btnRegister?.let { btn ->
            btn.setOnClickListener {
                val email = binding?.inputEmail?.text
                val task = CoroutineScope(Dispatchers.IO)

                task.async {
                    PamSDK.track(
                        "register", mutableMapOf(
                            "form_fields" to mutableMapOf(
                                "email" to email
                            )
                        )
                    )

                    val intent = Intent(this@RegisterPage, ProductPage::class.java)
                    startActivity(intent)
                    this@RegisterPage.finish()
                }
            }
        }
    }
}