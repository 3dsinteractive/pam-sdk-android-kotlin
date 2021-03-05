package pams.ai.demo.productsPage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import pams.ai.demo.LoginPage
import pams.ai.demo.ProductDetailPage
import pams.ai.demo.R
import pams.ai.demo.databinding.ActivityProductPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
import pamsdk.PamSDK
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

        PamSDK.askNotificationPermission()

        registerProductView()
        registerNotificationButton()
        registerUserButton()
        registerLogoutButton()

        fetchProducts()
    }

    private fun registerProductView() {
        adapter = ProductsListAdapter()
        adapter?.onClickProduct = { product ->
            val intent = Intent(this, ProductDetailPage::class.java).also {
                it.putExtra("product", product)
            }

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

    private fun registerNotificationButton() {
        binding?.let {
            it.btnNotification.setOnClickListener {
                val intent = Intent(this, NotificationPage::class.java)
                startActivity(intent)
            }
        }
    }

    private fun registerUserButton() {
        binding?.let {
            it.btnUser.setOnClickListener {
                binding?.let { b ->
                    if (b.btnLogout.visibility == View.INVISIBLE) {
                        b.btnLogout.visibility = View.VISIBLE
                    } else {
                        b.btnLogout.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }

    private fun registerLogoutButton() {
        binding?.let {
            it.btnLogout.setOnClickListener {
                PamSDK.userLogout()

                val intent = Intent(this, LoginPage::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

                startActivity(intent)
                this@ProductPage.finish()
            }
        }
    }

    private fun fetchProducts() {
        val products = MockAPI.getInstance().getProducts()
        adapter?.setProducts(products = products)
    }
}