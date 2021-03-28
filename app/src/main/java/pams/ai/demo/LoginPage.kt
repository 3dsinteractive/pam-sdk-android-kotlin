package pams.ai.demo

import ai.pams.android.kotlin.TrackingConsentManager
import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import models.AppData
import pams.ai.demo.databinding.ActivityLoginPageBinding
import pams.ai.demo.productsPage.ProductPage
import webservices.MockAPI


class LoginPage : AppCompatActivity() {

    private val emails = listOf("a@a.com", "b@b.com", "c@c.com")
    var binding: ActivityLoginPageBinding? = null
    var emailUseToLogin: String? = null
    var spinnerAdapter: ArrayAdapter<String>? = null

    var trackingConsentManager:TrackingConsentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        registerButtonLogin()
        registerButtonRegister()
        registerButtonSkip()
        registerSpinner()

        trackingConsentManager = TrackingConsentManager(supportFragmentManager, lifecycle)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        Pam.appReady()
    }

    private fun registerButtonLogin() {
        binding?.btnLogin?.setOnClickListener {
            emailUseToLogin?.let{ email ->
                MockAPI.getInstance().login(email)?.let{ user ->
                    AppData.setUser(user)
                    Pam.userLogin(user.CusID)
                    val intent = Intent(this, ProductPage::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun registerButtonRegister() {
        val buttonRegister = binding?.btnRegister
        buttonRegister?.let { btn ->
            btn.setOnClickListener {
                val intent = Intent(this, RegisterPage::class.java)
                startActivity(intent)
            }
        }
    }

    private fun registerButtonSkip() {
        binding?.btnSkip?.setOnClickListener {
            val intent = Intent(this, ProductPage::class.java)
            startActivity(intent)
        }
    }

    private fun registerSpinner() {
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emails)

        emailUseToLogin = emails[0]

        binding?.spinnerUser?.adapter = spinnerAdapter
        binding?.spinnerUser?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                emailUseToLogin = emails[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                emailUseToLogin = null
            }
        }
    }
}
