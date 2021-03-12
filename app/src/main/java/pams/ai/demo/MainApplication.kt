package pams.ai.demo

import android.app.Application
import android.content.Context
import android.util.Log
import pamsdk.Pam
import pamsdk.PamSDKName

class MainApplication : Application() {

    companion object{
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()

        appContext = this

        Pam.init(application = this, enableLog = true)

        Pam.listen("onToken") { args ->
            Log.d(PamSDKName, args.toString())
        }

        Pam.listen("onMessage") { args ->
            Log.d(PamSDKName, args.toString())
        }
    }
}