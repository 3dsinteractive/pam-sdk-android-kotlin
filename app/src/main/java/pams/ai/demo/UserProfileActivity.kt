package pams.ai.demo

import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import models.AppData
import pams.ai.demo.databinding.ActivityUserProfileBinding

interface UserProfilePresenter {
    fun clickLogout()
    fun clickClean()
}

class UserProfileActivity : AppCompatActivity(), UserProfilePresenter {

    var binding: ActivityUserProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

    }

    override fun onResume() {
        super.onResume()

        binding?.presenter = this
        binding?.dbAlias = Pam.shared.getDatabaseAlias()
        binding?.email = AppData.getUser()?.Email
        binding?.custID = Pam.shared.getCustomerID()
        binding?.contactConsent = AppData.contactConsent
        binding?.trackingConsent = AppData.trackingConsent
        binding?.contactID = Pam.shared.getContactID()

    }

    override fun clickLogout() {
        Pam.userLogout(){
            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            this.finish()
        }
    }

    override fun clickClean() {
        Pam.userLogout(){
            AppData.clean()
            Pam.cleanEverything()
            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            this.finish()
        }
    }


}

