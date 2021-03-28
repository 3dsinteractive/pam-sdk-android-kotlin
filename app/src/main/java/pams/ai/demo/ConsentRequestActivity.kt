package pams.ai.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import pams.ai.demo.databinding.ActivityConsentRequestBinding

class ConsentRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConsentRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConsentRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}