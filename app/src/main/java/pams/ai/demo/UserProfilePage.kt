package pams.ai.demo

import ai.pams.android.kotlin.Pam
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import models.AppData
import pams.ai.demo.databinding.ActivityUserProfilePageBinding


interface UserProfilePresenter {
    fun clickLogout()
    fun clickClean()
    fun clickBackToHome()
}

class UserProfilePage : AppCompatActivity(), UserProfilePresenter {

    var binding: ActivityUserProfilePageBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfilePageBinding.inflate(layoutInflater)
        setContentView(binding?.root)
    }

    override fun onResume() {
        super.onResume()

//        binding?.isLogin = AppData.getUser() != null
//        binding?.presenter = this
//        binding?.dbAlias = Pam.shared.getDatabaseAlias()
//        binding?.email = AppData.getUser()?.Email
//        binding?.custID = Pam.shared.getCustomerID()
//        binding?.contactConsent = AppData.contactConsent
//        binding?.trackingConsent = AppData.trackingConsent
//        binding?.contactID = Pam.shared.getContactID()

    }


    override fun clickLogout() {
        Pam.userLogout{
            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            this.finish()
        }
    }

    override fun clickClean() {
        Pam.userLogout{
            AppData.clean()
            Pam.cleanEverything()
            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            this.finish()
        }
    }

    override fun clickBackToHome(){
        val intent = Intent(this, LoginPage::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        this.finish()
    }
}