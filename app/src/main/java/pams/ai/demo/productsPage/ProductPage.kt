package pams.ai.demo.productsPage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import pams.ai.demo.ProductDetailPage
import pams.ai.demo.R
import pams.ai.demo.databinding.ActivityProductPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
import pamsdk.PamSDK
import pamsdk.PamSDKName
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

    private fun fetchProducts() {
        val products = MockAPI.getInstance().getProducts()
        adapter?.setProducts(products = products)
    }
}