package pams.ai.demo

import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.consent.ConsentMessage
import ai.pams.android.kotlin.consent.ConsentMessageError
import ai.pams.android.kotlin.consent.ConsentPermissionName
import ai.pams.android.kotlin.consent.LocaleText
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import models.AppData
import models.UserModel
import pams.ai.demo.databinding.ActivityLoginPageBinding
import pams.ai.demo.productsPage.ProductPage
import webservices.DemoAPI


class LoginPage : AppCompatActivity() {

    private val emails = listOf("a@a.com", "b@b.com", "c@c.com")
    var binding: ActivityLoginPageBinding? = null
    var emailUseToLogin: String? = null
    var spinnerAdapter: ArrayAdapter<String>? = null

    var trackingConsentMessage: ConsentMessage? = null

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

        Pam.loadConsentDetails("1qDQOyMeBv64LYnXi6dJOcZp2YQ") {
            when (it) {
                is ConsentMessage -> {
                    trackingConsentMessage = it
                    askTrackingPermission()
                }
                is ConsentMessageError -> {
                    Log.e("CONSENT", "Error Code: ${it.errorCode}, Message: ${it.errorMessage}")
                }
            }
        }

        val consentMessageIDs = listOf(
            "consent_message_id_1",
            "consent_message_id_2",
            "consent_message_id_3",
            "consent_message_id_4",
        )
        var consentMessage1: ConsentMessage? = null
        var consentMessage2: ConsentMessage? = null
        var consentMessage3: ConsentMessage? = null
        var consentMessage4: ConsentMessage? = null

        consentMessage1?.allowAll()
        consentMessage2?.allowAll()
        consentMessage3?.allowAll()
        consentMessage4?.allowAll()

        val allConsent = listOf(consentMessage1,
            consentMessage2,
            consentMessage3,
            consentMessage4)

        val payload = mapOf(
            "product_name" to ".......",
            "price" to 999,
            "category" to "food"
        )
        Pam.track("add_to_cart",  payload)

        Pam.submitConsent(allConsent){ result, consentIDs->
            Log.d("CONSENT","$result, $consentIDs")
        }

        Pam.loadConsentDetails(consentMessageIDs){ result ->
            consentMessage1 = result["consent_message_id_1"] as ConsentMessage
            consentMessage1 = result["consent_message_id_2"] as ConsentMessage
            consentMessage1 = result["consent_message_id_3"] as ConsentMessage
            consentMessage1 = result["consent_message_id_4"] as ConsentMessage
        }



        Pam.loadConsentPermissions("1qDQOyMeBv64LYnXi6dJOcZp2YQ"){
            Log.d("PERMS", it.toString())
        }

    }

    fun askTrackingPermission() {
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle(trackingConsentMessage?.name)
        builder.setMessage(trackingConsentMessage?.description)
        builder.setPositiveButton("Accept All") { _, _ ->
            trackingConsentMessage?.allowAll()
            trackingConsentMessage?.let {
                val validationResult = it.validate()
                if (validationResult.isValid) {
                    Pam.submitConsent(it) { result, id ->

                    }
                }
            }
        }
        builder.setNegativeButton("Deny All") { _, _ ->
            trackingConsentMessage?.denyAll()
            trackingConsentMessage?.let {
                val validationResult = it.validate()
                if (validationResult.isValid) {
                    Pam.submitConsent(it) { result, id ->

                    }
                }
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
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
            emailUseToLogin?.let { email ->

                DemoAPI.login(email, password = "1234") {
                    val user = UserModel(it.data.customerID, email)
                    AppData.setUser(user)
                    Pam.userLogin(it.data.customerID)
                    val intent = Intent(this, ProductPage::class.java)
                    startActivity(intent)
                }

//                MockAPI.getInstance().login(email)?.let{ user ->
//                    AppData.setUser(user)
//                    Pam.userLogin(user.CusID)
//                    val intent = Intent(this, ProductPage::class.java)
//                    startActivity(intent)
//                }
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
