package pams.ai.demo.productsPage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import models.Product
import pams.ai.demo.databinding.ProductListItemBinding

class ProductsListAdapter : RecyclerView.Adapter<ProductViewHolder>() {

    var onClickProduct: ((Product) -> Unit)? = null
    private var products = listOf<Product>()

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ProductListItemBinding.inflate(inflater)

        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.setProduct(product)

        holder.onClickProduct = {
            onClickProduct?.invoke(it)
        }
    }

    fun setProducts(products: List<Product>) {
        this.products = products
        notifyDataSetChanged()
    }
}


class ProductViewHolder(val binding: ProductListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var onClickProduct: ((Product) -> Unit)? = null

    fun setProduct(product: Product) {
        binding.product = product
        Picasso.get().load(product.Image).into(binding.productImage);

        binding.cardView.setOnClickListener {
            onClickProduct?.invoke(product)
        }
    }
}