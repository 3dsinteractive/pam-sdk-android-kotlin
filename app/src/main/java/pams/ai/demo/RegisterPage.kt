package pams.ai.demo

import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pams.ai.demo.databinding.ActivityRegisterPageBinding
import pams.ai.demo.productsPage.ProductPage
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
        binding?.btnRegister?.setOnClickListener {
            val email = binding?.inputEmail?.text
            MockAPI.getInstance().register(email.toString())

            Pam.track("register", mutableMapOf())

            val intent = Intent(this, ProductPage::class.java)
            startActivity(intent)
            this.finish()
        }

        binding?.consent?.setOnClickListener{
            val intent = Intent(this, ConsentRequestActivity::class.java)
            startActivity(intent)
        }
    }
}