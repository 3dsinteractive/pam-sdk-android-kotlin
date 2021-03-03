package pams.ai.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import models.Product
import models.ProductModel
import models.mockProducts
import pams.ai.demo.databinding.ActivityProductPageBinding


class ProductPage : AppCompatActivity(), AdapterView.OnItemClickListener {
    var binding: ActivityProductPageBinding? = null
    val mocks: MutableList<Product> = mockProducts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        this.registerProductView()
    }

    private fun registerProductView() {
        val products = binding?.gridViewProduct
        val adapter = ProductModel(applicationContext, mocks)
        products?.let {
            it.adapter = adapter
            it.onItemClickListener = this
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val product: Product = mocks[position]
        val intent = Intent(this, ProductDetailPage::class.java)

        intent.putExtra("product", product)
        startActivity(intent)
    }
}