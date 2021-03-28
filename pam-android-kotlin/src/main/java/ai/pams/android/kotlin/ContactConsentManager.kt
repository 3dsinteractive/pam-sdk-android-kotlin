package ai.pams.android.kotlin

import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.models.consent.contact.ContactConsentModel
import ai.pams.android.kotlin.views.ContactConsentRequestDialog
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import com.google.gson.Gson

class ContactConsentManager(val consentMessageID:String, val fragmentManager: FragmentManager, lifeCycle: Lifecycle) {

    private var onReady: (()->Unit)? = null
    var ready = false
    var consentMessage: ContactConsentModel? = null

    fun setOnReadyListener(onReady: ()->Unit ){
        this.onReady = onReady
        if(ready){
            onReady.invoke()
        }
    }

    init{
        loadConsentMessage()
    }

    private fun loadConsentMessage() {
        val pamServerURL = Pam.shared.options?.pamServer
        Http.getInstance()
            .get("${pamServerURL ?: ""}/consent-message/$consentMessageID") { result, error ->
                if (error == null) {
                    consentMessage = Gson().fromJson(result, ContactConsentModel::class.java)
                    ready = true
                    onReady?.invoke()
                }
            }
    }

    fun openConsentRequestDialog(){
        consentMessage?.let{
            val dialog = ContactConsentRequestDialog(it)
            dialog.onAccept = {

            }
            dialog.show(fragmentManager, "contact_consent_request")
        }
    }

    fun setAcceptAllTermsAndPrivacy(iaAccept: Boolean){

    }

    fun setAcceptAllContactPermissions(isAccept: Boolean){

    }

}