package pams.ai.demo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import models.Product
import pams.ai.demo.cartPage.CartPage
import pams.ai.demo.databinding.ActivityProductDetailPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
import pamsdk.PamSDK
import webservices.MockAPI

class ProductDetailPage : AppCompatActivity() {
    var binding: ActivityProductDetailPageBinding? = null
    var product: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        product = intent.getParcelableExtra("product") as? Product
        binding?.let {
            it.product = product
            Picasso.get().load(product?.Image).into(it.productImage)
        }

        registerAddToCart()
        registerBuyNow()
        registerFavourite()
        registerCartButton()
        registerNotificationButton()
        registerUserButton()
        registerLogoutButton()
    }

    override fun onResume() {
        super.onResume()

        PamSDK.track(
            "page_view", mutableMapOf(
                "page_title" to this.product!!.Title.toString(),
                "form_fields" to mutableMapOf(
                    "product_id" to this.product!!.Id.toString(),
                    "product_price" to this.product!!.Price.toString()
                )
            )
        )
    }

    private fun registerAddToCart() {
        binding?.let {
            it.btnAddToCart.setOnClickListener {
                MockAPI.getInstance().addToCart(this.product?.Id!!)
                PamSDK.track(
                    "add_to_cart", mutableMapOf(
                        "page_title" to this.product!!.Title.toString(),
                        "form_fields" to mutableMapOf(
                            "product_id" to this.product!!.Id.toString(),
                            "product_price" to this.product!!.Price.toString()
                        )
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
                        "page_title" to this.product!!.Title.toString(),
                        "form_fields" to mutableMapOf(
                            "product_id" to this.product!!.Id.toString(),
                            "product_price" to this.product!!.Price.toString()
                        )
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
                        "page_title" to this.product!!.Title.toString(),
                        "form_fields" to mutableMapOf(
                            "product_id" to this.product!!.Id.toString(),
                            "product_price" to this.product!!.Price.toString()
                        )
                    )
                )
                this.alert("Add To Favourite", "Added to your favourite products")
            }
        }
    }

    private fun registerCartButton() {
        binding?.let {
            it.btnCart.setOnClickListener {
                val intent = Intent(this, CartPage::class.java)
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out)

                startActivity(intent)
            }
        }
    }

    private fun registerNotificationButton() {
        binding?.let {
            it.btnNotification.setOnClickListener {
                val intent = Intent(this, NotificationPage::class.java)
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
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
                this@ProductDetailPage.finish()
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