package pams.ai.demo

import android.app.Application
import android.content.Context
import android.util.Log
import pamsdk.Pam

class MainApplication : Application() {

    companion object{
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()

        appContext = this

        Pam.initialize(application = this, enableLog = true)

        Pam.listen("onToken") { args ->
            Log.d("Pam", args.toString())
        }

        Pam.listen("onMessage") { args ->
            Log.d("Pam", args.toString())
        }
    }
}