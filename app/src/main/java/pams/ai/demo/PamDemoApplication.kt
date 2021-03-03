package pams.ai.demo

import android.app.Application
import pamsdk.PamSDK

class PamDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PamSDK.init(application = this)
    }
}