package pams.ai.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.*
import pams.ai.demo.databinding.ActivityLaunchScreenBinding
import pams.ai.demo.productsPage.ProductPage
import pamsdk.PamSDK
import pamsdk.PamSDKName
import webservices.MockAPI
import java.lang.Thread.sleep

class LaunchScreen : AppCompatActivity() {
    var binding: ActivityLaunchScreenBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLaunchScreenBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        navigatorHandler(intent?.extras?.keySet())
    }

    private fun navigatorHandler(bundle: Set<String>?) {
        if (bundle == null) {
            val task = CoroutineScope(Dispatchers.Default)
            task.launch {
                sleep(1500)
                withContext(Dispatchers.Main) {
                    val contactID = PamSDK.getContactID()

                    if (contactID == null) {
                        val intent = Intent(this@LaunchScreen, LoginPage::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this@LaunchScreen, ProductPage::class.java)
                        startActivity(intent)
                    }

                    this@LaunchScreen.finish()
                }
            }

            return
        }

        intent.extras?.keySet()?.forEach { key ->
            if (key == "product_id") {
                val productsIntent = Intent(this, ProductPage::class.java)
                productsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                startActivity(productsIntent)

                val value = intent.extras?.get(key)
                val product = MockAPI.getInstance().getProductFromID(value.toString())

                product?.let {
                    val productDetailIntent = Intent(this, ProductDetailPage::class.java)

                    productDetailIntent.putExtra("product", product)
                    startActivity(productDetailIntent)

                    PamSDK.track(
                        "open_push", mutableMapOf(
                            "form_fields" to mutableMapOf(
                                "product_id" to product.Id,
                                "product_title" to product.Title,
                                "product_price" to product.Price
                            )
                        )
                    )
                }

                this@LaunchScreen.finish()
            }
        }
    }
}