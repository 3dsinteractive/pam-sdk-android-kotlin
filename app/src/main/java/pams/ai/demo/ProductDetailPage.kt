package pams.ai.demo

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import models.Product
import pams.ai.demo.databinding.ActivityProductDetailPageBinding
import pamsdk.PamSDK


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
        this.registerAddToCart()
        this.registerBuyNow()
        this.registerFavourite()
    }

    override fun onResume() {
        super.onResume()
        PamSDK.track(
            "page_view", mutableMapOf(
                "product_id" to this.product?.Image.toString(),
                "total_price" to this.product?.Price.toString()
            )
        )
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

    private fun registerAddToCart() {
        binding?.let {
            it.btnAddToCart.setOnClickListener {
                PamSDK.track(
                    "add_to_cart", mutableMapOf(
                        "product_id" to this.product?.Image.toString(),
                        "total_price" to this.product?.Price.toString()
                    )
                )
                this.alert("Add To Cart", "Added to your cart")
            }
        }
    }

    private fun registerBuyNow() {
        binding?.let {
            it.btnBuyNow.setOnClickListener {
                PamSDK.track(
                    "purchase_success", mutableMapOf(
                        "product_id" to this.product?.Image.toString(),
                        "total_price" to this.product?.Price.toString()
                    )
                )
                this.alert("Buy Now", "Buy now success")
            }
        }
    }

    private fun registerFavourite() {
        binding?.let {
            it.btnFavourite.setOnClickListener {
                PamSDK.track(
                    "favourite", mutableMapOf(
                        "product_id" to this.product?.Image.toString(),
                        "total_price" to this.product?.Price.toString()
                    )
                )
                this.alert("Add To Favourite", "Added to your favourite products")
            }
        }
    }

    private fun alert(title: String, body: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this@ProductDetailPage).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(body)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }
}