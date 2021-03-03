package pams.ai.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import models.ProductModel
import models.mockProducts
import pams.ai.demo.databinding.ActivityProductPageBinding


class ProductPage : AppCompatActivity() {
    var binding: ActivityProductPageBinding? = null

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
        val adapter = ProductModel(applicationContext, mockProducts)
        products?.let {
            it.adapter = adapter
        }
    }
}