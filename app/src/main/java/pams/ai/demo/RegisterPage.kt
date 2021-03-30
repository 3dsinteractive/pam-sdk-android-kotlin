package pams.ai.demo

import ai.pams.android.kotlin.ContactConsentManager
import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.AppData
import models.UserModel
import pams.ai.demo.databinding.ActivityRegisterPageBinding
import pams.ai.demo.productsPage.ProductPage
import webservices.DemoAPI
import webservices.MockAPI

class RegisterPage : AppCompatActivity() {
    var binding: ActivityRegisterPageBinding? = null
    private val emails = listOf("a@a.com", "b@b.com", "c@c.com")
    private var contactConsentManager: ContactConsentManager? = null
    var emailUseToRegister: String? = null
    var spinnerAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        registerButtonRegister()
        registerSpinner()
    }

    private fun registerSpinner() {
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, emails)

        emailUseToRegister = emails[0]

        binding?.inputEmail?.adapter = spinnerAdapter
        binding?.inputEmail?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                emailUseToRegister = emails[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                emailUseToRegister = null
            }
        }
    }

    private fun login(email:String){
        DemoAPI.login(email, "1234"){
            val user = UserModel(it.data.customerID, email)
            AppData.setUser(user)
            Pam.userLogin(it.data.customerID)

            val intent = Intent(this, ProductPage::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    private fun registerButtonRegister() {
        binding?.btnRegister?.setOnClickListener {

            contactConsentManager?.applyConsent {
                Log.d("APP", "Consent ID = ${it.consentID}")

                //MockAPI.getInstance().register(email.toString())
                Pam.track("click_register", mutableMapOf())
                DemoAPI.register(
                    email = (emailUseToRegister ?: ""),
                    mobile = (binding?.inputMobile?.text?.toString() ?: ""),
                    password = "1234",
                    consentId = (it.consentID ?: ""))
                { response ->
                    login(response.data.email)
                }
            }
        }

        binding?.checkboxContact?.setOnCheckedChangeListener { _, isChecked ->
            contactConsentManager?.setAcceptAllContactPermissions(isChecked)
        }

        binding?.checkboxTerm?.setOnCheckedChangeListener { _, isChecked ->
            contactConsentManager?.setAcceptAllTermsAndPrivacy(isChecked)
        }

        contactConsentManager = ContactConsentManager(
            consentMessageID = "1qDQgHFygpAhuX0gBxHkYAPiwBN",
            fragmentManager = supportFragmentManager,
            lifecycle
        )

        binding?.openSettingBtn1?.setOnClickListener{
            contactConsentManager?.openConsentRequestDialog()
        }

        binding?.openSettingBtn2?.setOnClickListener{
            contactConsentManager?.openConsentRequestDialog()
        }

        binding?.checkboxTerm?.isEnabled = false
        binding?.checkboxContact?.isEnabled = false

        contactConsentManager?.setOnReadyListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding?.checkboxTerm?.isEnabled = true
                binding?.checkboxContact?.isEnabled = true
            }
        }


        contactConsentManager?.setOnStatusChangedListener { allowList ->
            Log.d("PDPA!", allowList.toString())
            val termAccept = allowList["terms_and_conditions"] ?: false
            val privacyAccept = allowList["privacy_overview"] ?: false
            val emailAccept = allowList["email"] ?: false
            val smsAccept = allowList["sms"] ?: false

            this.binding?.btnRegister?.isEnabled = termAccept && privacyAccept

            this.binding?.checkboxTerm?.isChecked = termAccept && privacyAccept

            if( emailAccept && !smsAccept){
                this.binding?.checkboxContact
                this.binding?.checkboxContact?.isChecked = true
                this.binding?.checkboxContact?.buttonTintList = ContextCompat.getColorStateList(this, R.color.checkbox_apart_bg)
            }else if(!emailAccept && smsAccept) {
                this.binding?.checkboxContact
                this.binding?.checkboxContact?.isChecked = true
                this.binding?.checkboxContact?.buttonTintList = ContextCompat.getColorStateList(this, R.color.checkbox_apart_bg)
            }else if(emailAccept && smsAccept){
                this.binding?.checkboxContact?.isChecked = true
                this.binding?.checkboxContact?.buttonTintList = ContextCompat.getColorStateList(this, R.color.checkbox_full_bg)
            }else{
                this.binding?.checkboxContact?.buttonTintList = ContextCompat.getColorStateList(this, R.color.checkbox_apart_bg)
                this.binding?.checkboxContact?.isChecked = false
            }

            //android:buttonTint="#F18A8A"
        }
    }
}