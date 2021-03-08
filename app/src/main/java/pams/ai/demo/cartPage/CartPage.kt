package pams.ai.demo.cartPage

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import pams.ai.demo.databinding.ActivityCartPageBinding
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
            LinearLayoutManager(this@CartPage, LinearLayoutManager.VERTICAL, false)
        binding?.listView?.layoutManager = layoutManager
    }

    private fun fetchCart() {
        val cart = MockAPI.getInstance().getCart()
        Log.d(PamSDKName, "cart ${cart.toString()}")

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