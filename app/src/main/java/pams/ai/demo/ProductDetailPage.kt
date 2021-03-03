package pams.ai.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import models.Product
import pams.ai.demo.databinding.ActivityProductDetailPageBinding

class ProductDetailPage : AppCompatActivity() {
    var binding: ActivityProductDetailPageBinding? = null
    var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        val product: Product? = intent.getSerializableExtra("product") as Product
        product?.let {
            this.product = it
        }

        this.registerImage()
        this.registerTitle()
        this.registerPrice()
    }

    private fun registerImage() {
        binding?.let {
            it.productImage.let { image ->
                this.product?.Image?.let {
                    Picasso.get().load(this.product?.Image).into(image)
                }
            }
        }
    }

    private fun registerTitle() {
        binding?.let {
            it.productTitle.let { title ->
                this.product?.Title?.let {
                    title.text = this.product!!.Title
                }
            }
        }
    }

    private fun registerPrice() {
        binding?.let {
            it.productPrice.let { title ->
                this.product?.Price?.let {
                    title.text = "฿ ${this.product!!.Price.toString()}"
                }
            }
        }
    }
}