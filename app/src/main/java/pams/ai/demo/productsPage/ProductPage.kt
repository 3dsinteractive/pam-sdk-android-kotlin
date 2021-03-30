package pams.ai.demo.productsPage

import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.TrackingConsentManager
import ai.pams.android.kotlin.events.PamStandardEvent
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import models.AppData
import pams.ai.demo.LoginPage
import pams.ai.demo.ProductDetailPage
import pams.ai.demo.R
import pams.ai.demo.UserProfileActivity
import pams.ai.demo.cartPage.CartPage
import pams.ai.demo.databinding.ActivityProductPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
import webservices.MockAPI

class ProductPage : AppCompatActivity() {

    private var binding: ActivityProductPageBinding? = null
    private var adapter: ProductsListAdapter? = null

    private var trackingConsentManager: TrackingConsentManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        Pam.askNotificationPermission()

        registerProductView()
        registerCartButton()
        registerNotificationButton()
        registerUserButton()
        registerLoginButton()
        registerLogoutButton()

        fetchProducts()


    }

    override fun onResume() {
        super.onResume()

        PamStandardEvent.PageView(pageTitle = "Product list",
            pageURL = "digits3://products-list",
            payload = null).track()

        trackingConsentManager = TrackingConsentManager(supportFragmentManager, lifecycle)
        trackingConsentManager?.onAcceptConsent = {consentID , _ ->
            AppData.trackingConsent = consentID
        }
    }

    private fun registerProductView() {
        adapter = ProductsListAdapter()
        adapter?.onClickProduct = { product ->
            val intent = ProductDetailPage.createIntentWithProduct(this, product)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
        }

        binding?.listView?.adapter = adapter

        val layoutManager = GridLayoutManager(this, 2)
        binding?.listView?.layoutManager = layoutManager
    }

    private fun registerCartButton() {
        binding?.btnCart?.setOnClickListener {
            val intent = Intent(this, CartPage::class.java)
            startActivity(intent)
        }
    }

    private fun registerNotificationButton() {
        binding?.btnNotification?.setOnClickListener {
            val intent = Intent(this, NotificationPage::class.java)
            startActivity(intent)
        }
    }

    private fun registerUserButton() {
        binding?.btnUser?.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun registerLoginButton() {
        binding?.btnLogin?.setOnClickListener {
            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
            this.finish()
        }
    }

    private fun registerLogoutButton() {
//        binding?.btnLogout?.setOnClickListener {
//            Pam.userLogout()
//
//            val intent = Intent(this, LoginPage::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//
//            startActivity(intent)
//            this.finish()
//        }
    }

    private fun fetchProducts() {
        val products = MockAPI.getInstance().getProducts()
        adapter?.setProducts(products = products)
    }
}