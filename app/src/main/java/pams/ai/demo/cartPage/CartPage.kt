package pams.ai.demo.cartPage

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import pams.ai.demo.LoginPage
import pams.ai.demo.R
import pams.ai.demo.databinding.ActivityCartPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
import pams.ai.demo.productsPage.ProductsListAdapter
import pamsdk.PamSDK
import pamsdk.PamSDKName
import webservices.MockAPI

class CartPage : AppCompatActivity() {

    var binding: ActivityCartPageBinding? = null
    private var adapter: CartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCartPageBinding.inflate(layoutInflater)
        binding?.let {
            setContentView(it.root)
        }

        registerCheckoutButton()
        registerCartView()
        registerNotificationButton()
        registerUserButton()
        registerLogoutButton()

        fetchCart()
    }

    private fun registerCheckoutButton() {
        binding?.let {
            it.btnCheckout.let {
                it.setOnClickListener {
                    val cart = MockAPI.getInstance().getCart()

                    cart.Products?.let {
                        val productIds: String = cart.Products?.map { p -> p.Id }!!.joinToString()

                        PamSDK.track(
                            "purchase_success", mutableMapOf(
                                "form_fields" to mutableMapOf(
                                    "product_id" to productIds,
                                    "total_price" to cart.TotalPrice
                                )
                            )
                        )

                        MockAPI.getInstance().checkout()
                        fetchCart()
                    }
                }
            }
        }
    }

    private fun registerCartView() {
        adapter = CartAdapter()
        adapter?.onAddProductClick = { id ->
            MockAPI.getInstance().addToCart(id)
            fetchCart()
        }
        adapter?.onRemoveProductClick = { id ->
            MockAPI.getInstance().removeFromCart(id)
            fetchCart()
        }

        binding?.listView?.adapter = adapter

        val layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding?.listView?.layoutManager = layoutManager
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
                this.finish()
            }
        }
    }

    private fun fetchCart() {
        val cart = MockAPI.getInstance().getCart()

        adapter?.setCart(cart = cart)
        binding?.cart = cart
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