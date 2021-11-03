package pams.ai.demo

import ai.pams.android.kotlin.ContactConsentManager
import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
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

    private var appConsentManager: ContactConsentManager? = null
    private var groupConsentManager: ContactConsentManager? = null

    private val APP_CONSENT_MESSAGE_ID = "1qDQgHFygpAhuX0gBxHkYAPiwBN"
    private val GROUP_CONSENT_MESSAGE_ID = "1qVSxfOdnEZQAu48Ue8xrvjV6JN"

    var appConsentID:String? = null
    var groupConsentID:String? = null

    var emailUseToRegister: String? = null
    var spinnerAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterPageBinding.inflate(layoutInflater)

        binding?.let {
            setContentView(it.root)
        }

        setupConsent()
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

    private fun setupConsent(){
      //  binding?.allowRegister = false
        //Disable button when consent manager is loading
        binding?.openSettingBtn1?.isEnabled = false
        binding?.openSettingBtn2?.isEnabled = false
        binding?.checkboxConsentApp?.isEnabled = false
        binding?.checkboxConsentGroup?.isEnabled = false

        appConsentManager = ContactConsentManager(
            consentMessageID = APP_CONSENT_MESSAGE_ID,
            fragmentManager = supportFragmentManager,
            lifecycle
        )

        groupConsentManager = ContactConsentManager(
            consentMessageID = GROUP_CONSENT_MESSAGE_ID,
            fragmentManager = supportFragmentManager,
            lifecycle
        )

        appConsentManager?.setOnReadyListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding?.checkboxConsentApp?.isEnabled = true
                binding?.openSettingBtn1?.isEnabled = true
            }
        }

        groupConsentManager?.setOnReadyListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding?.checkboxConsentGroup?.isEnabled = true
                binding?.openSettingBtn2?.isEnabled = true
            }
        }

        appConsentManager?.setOnStatusChangedListener { allowList ->
            val termAccept = allowList["terms_and_conditions"] ?: false
            val privacyAccept = allowList["privacy_overview"] ?: false
            this.binding?.checkboxConsentApp?.isChecked = termAccept && privacyAccept
           // binding?.allowRegister = termAccept && privacyAccept
        }

        groupConsentManager?.setOnStatusChangedListener { allowList ->
            val termAccept = allowList["terms_and_conditions"] ?: false
            val privacyAccept = allowList["privacy_overview"] ?: false
            this.binding?.checkboxConsentGroup?.isChecked = termAccept && privacyAccept
        }
    }


    private fun applyConsentApp(){
        appConsentManager?.applyConsent {
            AppData.contactConsent = it.consentID
            appConsentID = it.consentID
            applyConsentGroup()
        }
    }

    private fun applyConsentGroup(){
        groupConsentManager?.applyConsent {
            groupConsentID = it.consentID

            var consentID = appConsentID
            groupConsentID?.let{
                consentID = "$consentID,$it"
            }

            DemoAPI.register(
                email = (emailUseToRegister ?: ""),
                mobile = (binding?.inputMobile?.text?.toString() ?: ""),
                password = "1234",
                consentId = (consentID ?: ""))
            { _ ->
                CoroutineScope(Dispatchers.Main).launch {
                    AlertDialog.Builder(this@RegisterPage)
                        .setTitle("Thank you!")
                        .setMessage("Register was Successful.")
                        .setPositiveButton("Back to Login."){ _,_->
                            finish()
                        }.show()
                }
            }
        }
    }

    private fun registerButtonRegister() {
        binding?.btnRegister?.setOnClickListener {
            Pam.track("click_register", mutableMapOf())
            applyConsentApp()
        }

        binding?.checkboxConsentApp?.setOnCheckedChangeListener { checkbox, isChecked ->
            if(checkbox.isPressed) {
                //Accept to all only when user click the checkbox but not from the code
                appConsentManager?.setAcceptAllPermissions(isChecked)
            }
        }

        binding?.checkboxConsentGroup?.setOnCheckedChangeListener { checkbox, isChecked ->
            if(checkbox.isPressed) {
                //Accept to all only when user click the checkbox but not from the code
                groupConsentManager?.setAcceptAllPermissions(isChecked)
            }
        }

        binding?.openSettingBtn1?.setOnClickListener{
            appConsentManager?.openConsentRequestDialog()
        }

        binding?.openSettingBtn2?.setOnClickListener{
            groupConsentManager?.openConsentRequestDialog()
        }

    }
}