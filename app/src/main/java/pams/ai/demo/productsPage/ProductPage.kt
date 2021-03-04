package pams.ai.demo.productsPage

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import pams.ai.demo.ProductDetailPage
import pams.ai.demo.R
import pams.ai.demo.databinding.ActivityProductPageBinding
import webservices.MockAPI

class ProductPage : AppCompatActivity() {

    var binding: ActivityProductPageBinding? = null
    var adapter: ProductsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        registerProductView()
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

        val layout = GridLayoutManager(this, 2)
        binding?.listView?.layoutManager = layout
    }

    private fun fetchProducts() {
        val products = MockAPI.getInstance().getProducts()
        adapter?.setProducts(products = products)
    }
}