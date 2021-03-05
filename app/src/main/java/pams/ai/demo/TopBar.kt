package pams.ai.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pams.ai.demo.databinding.TopBarBinding

class TopBar : AppCompatActivity() {

    private var binding: TopBarBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TopBarBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        registerNotificationButton()
    }

    private fun registerNotificationButton() {
        binding?.let {

        }
    }
}