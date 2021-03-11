package pams.ai.demo

import android.app.Application
import android.util.Log
import pamsdk.PamSDK
import pamsdk.PamSDKName

class PamDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PamSDK.init(application = this, enableLog = true)

        PamSDK.listen("onToken") { args ->
            Log.d(PamSDKName, args.toString())
        }

        PamSDK.listen("onMessage") { args ->
            Log.d(PamSDKName, args.toString())
        }
    }
}