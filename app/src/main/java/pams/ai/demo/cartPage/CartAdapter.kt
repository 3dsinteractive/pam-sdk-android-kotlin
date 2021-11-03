package pams.ai.demo.cartPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import models.CartModel
import models.CartProductModel
import pams.ai.demo.databinding.CartListItemBinding

class CartAdapter : RecyclerView.Adapter<CartViewHolder>() {

    private var cart = CartModel()
    var onAddProductClick: ((productID: String) -> Unit)? = null
    var onRemoveProductClick: ((productID: String) -> Unit)? = null

    override fun getItemCount(): Int {
        cart.Products?.size?.let {
            return it
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CartListItemBinding.inflate(inflater)

        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart = this.cart.Products?.get(position)
        holder.setCartProduct(cart)

        holder.onAddProductClick = {
            onAddProductClick?.invoke(it)
        }

        holder.onRemoveProductClick = {
            onRemoveProductClick?.invoke(it)
        }
    }

    fun setCart(cart: CartModel) {
        this.cart = cart
        notifyDataSetChanged()
    }
}

class CartViewHolder(val binding: CartListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var onAddProductClick: ((productID: String) -> Unit)? = null
    var onRemoveProductClick: ((productID: String) -> Unit)? = null

    fun setCartProduct(product: CartProductModel?) {
//        binding.cartProduct = product

        Glide.with(this.itemView.context).load(product?.Image).into(binding.productImage)

        binding.iconAdd.setOnClickListener {
            onAddProductClick?.invoke(product?.Id!!)
        }

        binding.iconRemove.setOnClickListener {
            onRemoveProductClick?.invoke(product?.Id!!)
        }
    }
}
