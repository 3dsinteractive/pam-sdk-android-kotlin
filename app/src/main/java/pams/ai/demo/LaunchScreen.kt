package pams.ai.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.*
import pams.ai.demo.databinding.ActivityLaunchScreenBinding
import pams.ai.demo.productsPage.ProductPage
import pamsdk.Pam
import pamsdk.PamStandardEvent
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

        CoroutineScope(Dispatchers.Default).launch {
            sleep(1000)
            withContext(Dispatchers.Main) {
                val customerID = Pam.getCustomerID()

                if (customerID == null) {
                    val intent = Intent(this@LaunchScreen, LoginPage::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@LaunchScreen, ProductPage::class.java)
                    startActivity(intent)
                }

                this@LaunchScreen.finish()
            }
        }




//        val isNotPamIntent = Pam.isNotPamIntent(intent) {
//            val productRegex = """(product_id=)((?!\&).)+""".toRegex()
//            val cartRegex = """cart""".toRegex()
//
//            it.url?.let { url ->
//                if (productRegex.matches(url)) {
//                    val result = productRegex.find(url)
//                    result?.value?.let { rs ->
//                        var productID = ""
//                        rs.split("=").let { strs ->
//                            strs[1].let { id ->
//                                productID = id
//                            }
//                        }
//
//                        if (productID != "") {
//                            gotoProductPage(productID)
//                        } else {
//                            defaultNavigator()
//                        }
//                    }
//                } else if (cartRegex.matches(url)) {
//                    gotoCartPage()
//                } else {
//                    defaultNavigator()
//                }
//            }
//        }

//        if (isNotPamIntent) {
//            defaultNavigator()
//        }
    }

//    private fun gotoProductPage(productID: String) {
//        val productsIntent = Intent(this, ProductPage::class.java)
//        productsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        startActivity(productsIntent)
//
//        val product = MockAPI.getInstance().getProductFromID(productID)
//        product?.let {
//            val productDetailIntent = Intent(this, ProductDetailPage::class.java)
//
//            productDetailIntent.putExtra("product", product)
//            startActivity(productDetailIntent)
//
//            Pam.track(
//                PamStandardEvent.openPush, mapOf(
//                    "product_id" to (product.Id ?: ""),
//                    "product_title" to (product.Title ?: ""),
//                    "product_price" to (product.Price ?: "")
//                )
//            )
//        }
//
//        this@LaunchScreen.finish()
//    }

//    private fun gotoCartPage() {
//        val productsIntent = Intent(this, ProductPage::class.java)
//        productsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        startActivity(productsIntent)
//
//        val cartIndent = Intent(this, ProductDetailPage::class.java)
//        startActivity(cartIndent)
//
//        this@LaunchScreen.finish()
//    }

//    private fun defaultNavigator() {
//        val task = CoroutineScope(Dispatchers.Default)
//        task.launch {
//            sleep(1000)
//            withContext(Dispatchers.Main) {
//                val customerID = Pam.getCustomerID()
//
//                if (customerID == null) {
//                    val intent = Intent(this@LaunchScreen, LoginPage::class.java)
//                    startActivity(intent)
//                } else {
//                    val intent = Intent(this@LaunchScreen, ProductPage::class.java)
//                    startActivity(intent)
//                }
//
//                this@LaunchScreen.finish()
//            }
//        }
//    }

}