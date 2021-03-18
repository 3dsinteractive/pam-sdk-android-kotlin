package pams.ai.demo.cartPage

import ai.pams.android.kotlin.Pam
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import models.AppData
import pams.ai.demo.LoginPage
import pams.ai.demo.R
import pams.ai.demo.databinding.ActivityCartPageBinding
import pams.ai.demo.notificationsPage.NotificationPage
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
        registerLoginButton()
        registerLogoutButton()

        fetchCart()
    }

    private fun registerCheckoutButton() {
        binding?.btnCheckout?.setOnClickListener {
            val cart = MockAPI.getInstance().getCart()

            cart.Products?.let {
                val productIds: String =
                    cart.Products?.map { p -> p.Id }?.joinToString() ?: ""
                val categoryIds: String =
                    cart.Products?.map { p -> p.CategoryId }?.joinToString() ?: ""

                Pam.track(
                    "purchase_success", mapOf(
                        "product_id" to productIds,
                        "product_cat" to categoryIds,
                        "total_price" to (cart.TotalPrice ?: "")
                    )
                )

                MockAPI.getInstance().checkout()
                fetchCart()
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
        binding?.btnUser?.setOnClickListener {


            if (AppData.getUser() == null) {
                if (binding?.btnLogin?.visibility == View.INVISIBLE) {
                    binding?.btnLogin?.visibility = View.VISIBLE
                } else {
                    binding?.btnLogin?.visibility = View.INVISIBLE
                }
            } else {
                if (binding?.btnLogout?.visibility == View.INVISIBLE) {
                    binding?.btnLogout?.visibility = View.VISIBLE
                } else {
                    binding?.btnLogout?.visibility = View.INVISIBLE
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