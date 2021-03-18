package pams.ai.demo.productsPage

import ai.pams.android.kotlin.Pam
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import models.AppData
import pams.ai.demo.LoginPage
import pams.ai.demo.ProductDetailPage
import pams.ai.demo.R
import pams.ai.demo.cartPage.CartPage
import pams.ai.demo.databinding.ActivityProductPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
import webservices.MockAPI

class ProductPage : AppCompatActivity() {

    private var binding: ActivityProductPageBinding? = null
    private var adapter: ProductsListAdapter? = null

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

    private fun registerProductView() {
        adapter = ProductsListAdapter()
        adapter?.onClickProduct = { product ->
            val intent = ProductDetailPage.createIntentWithProduct(this, product)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out)

            binding?.btnLogout?.let {
                it.visibility = View.INVISIBLE
            }
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
            if (AppData.getUser() == null) {
                if (binding?.btnLogin?.visibility == View.INVISIBLE) {
                    binding?.btnLogin?.visibility = View.VISIBLE
                } else {
                    binding?.btnLogin?.visibility = View.INVISIBLE
                }
            } else {
                if (binding?.btnLogout?.visibility == View.INVISIBLE) {
                    binding?.btnLogout?.visibility = View.VISIBLE
                } else {
                    binding?.btnLogout?.visibility = View.INVISIBLE
                }
            }
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
        binding?.btnLogout?.setOnClickListener {
            Pam.userLogout()

            val intent = Intent(this, LoginPage::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

            startActivity(intent)
            this.finish()
        }
    }

    private fun fetchProducts() {
        val products = MockAPI.getInstance().getProducts()
        adapter?.setProducts(products = products)
    }
}