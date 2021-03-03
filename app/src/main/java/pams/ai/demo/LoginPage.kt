package pams.ai.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import models.UserModel
import pams.ai.demo.databinding.ActivityLoginPageBinding
import pamsdk.PamSDK
import pamsdk.PamSDKName

val users: MutableMap<String, UserModel> = mutableMapOf(
    "choengchai@3dsinteractive.com" to UserModel(
        CusID = "1ZYqsLkwMLuo3RIaXo4EwAZlhVP",
        Email = "choengchai@3dsinteractive.com"
    )
)

class LoginPage : AppCompatActivity() {
    var binding: ActivityLoginPageBinding? = null
    var user: UserModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        this.registerButtonLogin()
        this.registerButtonRegister()
        this.registerSpinner()
    }

    private fun registerButtonLogin() {
        val buttonLogin = binding?.btnLogin
        buttonLogin?.let { btn ->
            btn.setOnClickListener {
                user?.let {
                    PamSDK.userLogin(user!!.CusID)
                    val intent = Intent(this@LoginPage, ProductPage::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun registerButtonRegister() {
        val buttonRegister = binding?.btnRegister
        buttonRegister?.let { btn ->
            btn.setOnClickListener {
                val intent = Intent(this@LoginPage, RegisterPage::class.java)
                startActivity(intent)
            }
        }
    }

    private fun registerSpinner() {
        val spinner = binding?.spinnerUser
        this@LoginPage.user = users[resources.getStringArray(R.array.users)[0]]

        ArrayAdapter.createFromResource(
            this,
            R.array.users,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = it
        }

        spinner?.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                val array = resources.getStringArray(R.array.users)
                this@LoginPage.user = users[array[position]]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                this@LoginPage.user = null
            }
        }
    }
}