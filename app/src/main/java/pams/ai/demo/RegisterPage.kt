package pams.ai.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pams.ai.demo.databinding.ActivityRegisterPageBinding
import pams.ai.demo.productsPage.ProductPage
import pamsdk.PamSDK
import pamsdk.PamSDKName
import webservices.MockAPI

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
                val response = MockAPI.getInstance().register(email.toString())

                PamSDK.track(
                    "register", mutableMapOf(
                        "form_fields" to mutableMapOf(
                            "email" to response.Email
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