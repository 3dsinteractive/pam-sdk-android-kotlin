package pams.ai.demo

import ai.pams.android.kotlin.ContactConsentManager
import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pams.ai.demo.databinding.ActivityRegisterPageBinding
import pams.ai.demo.productsPage.ProductPage
import webservices.MockAPI

class RegisterPage : AppCompatActivity() {
    var binding: ActivityRegisterPageBinding? = null

    private var contactConsentManager: ContactConsentManager? = null

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

            contactConsentManager?.applyConsent {
                Log.d("APP", "Consent ID = ${it.consentID}")
                val email = binding?.inputEmail?.text
                MockAPI.getInstance().register(email.toString())

                Pam.track("register", mutableMapOf())

                val intent = Intent(this, ProductPage::class.java)
                startActivity(intent)
                this.finish()
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