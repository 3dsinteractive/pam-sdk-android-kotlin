package pams.ai.demo

import ai.pams.android.kotlin.Pam
import ai.pams.android.kotlin.TrackingConsentManager
import ai.pams.android.kotlin.events.PamStandardEvent
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import models.AppData
import models.Product
import pams.ai.demo.cartPage.CartPage
import pams.ai.demo.databinding.ActivityProductDetailPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
import webservices.MockAPI

class ProductDetailPage : AppCompatActivity() {

    var trackingConsentManager: TrackingConsentManager? = null

    companion object{
        fun createIntentWithProduct(context: Context, product: Product): Intent{
            val intent = Intent(context, ProductDetailPage::class.java)
            intent.putExtra("product", product)
            return intent
        }

        fun createIntentWithProductID(context: Context, productID: String): Intent{
            val intent = Intent(context, ProductDetailPage::class.java)
            intent.putExtra("product_id", productID)
            return intent
        }
    }

    var binding: ActivityProductDetailPageBinding? = null
    var product: Product? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        product = intent.getParcelableExtra("product")
        if(product == null){
            intent.getStringExtra("product_id")?.let{ productId ->
                product = MockAPI.getInstance().getProductFromID(productId)
            }
        }

        binding?.product = product

        binding?.productImage?.let{
            Glide.with(this).load(product?.Image).into(it)
        }

        PamStandardEvent.PageView(
            this.product?.Title ?: "",
            "digits3://product?id=${this.product?.Id ?: ""}",
            mapOf(
                "product_id" to (this.product?.Id ?: "")
            )
        ).track()

        registerAddToCart()
        registerBuyNow()
        registerFavourite()
        registerCartButton()
        registerNotificationButton()
        registerUserButton()
        registerLoginButton()
        registerLogoutButton()

        fetchFavourite()
    }

    private fun registerAddToCart() {
        binding?.btnAddToCart?.setOnClickListener {
            MockAPI.getInstance().addToCart(this.product?.Id!!)
            Pam.track(
                "add_to_cart",
                mapOf(
                    "page_title" to (this.product?.Title ?: ""),
                    "product_id" to (this.product?.Id ?: ""),
                    "product_price" to (this.product?.Price ?: "")
                )
            )
            this.alert("Add To Cart", "Added to your cart")
        }
    }

    private fun registerBuyNow() {
        binding?.btnBuyNow?.setOnClickListener {
            Pam.track(
                "purchase_success",
                mapOf(
                    "page_title" to (this.product?.Title ?: ""),
                    "product_id" to (this.product?.Id ?: ""),
                    "product_price" to (this.product?.Price ?: "")
                )
            )
            this.alert("Buy Now", "Buy now success")
        }
    }

    private fun registerFavourite() {
        binding?.btnFavourite?.setOnClickListener {
            val isAddedToFavourite =
                MockAPI.getInstance().isProductFavourite(this.product?.Id ?: "")
            if (!isAddedToFavourite) {
                MockAPI.getInstance().addToFavourite(this.product?.Id ?: "")
                Pam.track(
                    "favourite", mapOf(
                        "page_title" to (this.product?.Title ?: ""),
                        "product_id" to (this.product?.Id ?: ""),
                        "product_price" to (this.product?.Price ?: "")
                    )
                )
                this.alert("Add To Favourite", "Added to your favourite products")
            } else {
                MockAPI.getInstance().removeFromFavourite(this.product?.Id ?: "")
                Pam.track(
                    "remove_favourite", mapOf(
                        "page_title" to (this.product?.Title ?: ""),
                        "product_id" to (this.product?.Id ?: ""),
                        "product_price" to (this.product?.Price ?: "")
                    )
                )
                this.alert("Remove From Favourite", "Removed from your favourite products")
            }

            fetchFavourite()
        }
    }

    private fun registerCartButton() {
        binding?.btnCart?.setOnClickListener {
            val intent = Intent(this, CartPage::class.java)
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
            startActivity(intent)
        }
    }

    private fun registerNotificationButton() {
        binding?.btnNotification?.setOnClickListener {
            val intent = Intent(this, NotificationPage::class.java)
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
            startActivity(intent)
        }
    }

    private fun registerUserButton() {
        binding?.btnUser?.setOnClickListener {
            binding?.let { b ->
                val user = AppData.getUser()
                if (user == null) {
                    if (b.btnLogin.visibility == View.INVISIBLE) {
                        b.btnLogin.visibility = View.VISIBLE
                    } else {
                        b.btnLogin.visibility = View.INVISIBLE
                    }
                } else {
                    if (b.btnLogout.visibility == View.INVISIBLE) {
                        b.btnLogout.visibility = View.VISIBLE
                    } else {
                        b.btnLogout.visibility = View.INVISIBLE
                    }
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

    private fun fetchFavourite() {
        product?.Id.let { id ->
            val isAddedToFavourite = MockAPI.getInstance().isProductFavourite(id ?: "")
            binding?.btnFavourite?.imageAlpha = when (isAddedToFavourite) {
                true-> 255
                false -> 100
            }
        }
    }

    private fun alert(title: String, body: String) {
        val alertDialog: AlertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(body)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, _ -> dialog.dismiss() }
        alertDialog.show()
    }
}