package pams.ai.demo

import ai.pams.android.kotlin.ContactConsentManager
import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pams.ai.demo.databinding.ActivityRegisterPageBinding
import pams.ai.demo.productsPage.ProductPage
import webservices.MockAPI

class RegisterPage : AppCompatActivity() {
    var binding: ActivityRegisterPageBinding? = null

    var contactConsentManager: ContactConsentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        this.registerButtonRegister()

        contactConsentManager = ContactConsentManager(
            consentMessageID = "1qHksFD60L3Nekkt45Jjbdisp1Z",
            fragmentManager = supportFragmentManager,
            lifecycle
        )

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

    }
}