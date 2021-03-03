package models

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text
import pams.ai.demo.R
import java.io.Serializable;

val mockProducts: MutableList<Product> = mutableListOf(
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1582535404566.jpg",
        Title = "Infrastructure as Code (IaC) with Terraform",
        Price = 1999.00
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1579526262154.jpg",
        Title = "Elasticsearch Ninja Workshop 2",
        Price = 1690.00
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1573725997947.png",
        Title = "Workshop : สร้าง iOS App ด้วย SwiftUI",
        Price = 1999.00
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1570174385559.jpg",
        Title = "Workshop: Advanced Docker with Kubernetes รุ่น 6",
        Price = 899.00
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1570174412610.jpg",
        Title = "Workshop: Docker From Zero to Hero รุ่น 6",
        Price = 899.00
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1570173005235.jpg",
        Title = "Elasticsearch Ninja Workshop",
        Price = 2999.00
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1561550880736.jpg",
        Title = "Workshop: Jmeter for Production Zero to Hero Scale",
        Price = 1999.00
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1560243149609.jpg",
        Title = "Workshop: Marketing Masterclass for Non-Marketer (1-Day Class)",
        Price = 599.00
    )
)

data class Product(
    val Image: String? = null,
    val Title: String? = null,
    val Price: Double? = null
) : Serializable

class ProductModel(var context: Context, var datas: MutableList<Product>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.activity_product_item, null)
        val image: ImageView = view.findViewById(R.id.product_image)
        val title: TextView = view.findViewById(R.id.product_title)
        val price: TextView = view.findViewById(R.id.product_price)
        val product: Product = datas[position]

        Picasso.get().load(product.Image).into(image)
        title.text = product.Title
        price.text = "฿ ${product.Price.toString()}"

        return view
    }

    override fun getItem(position: Int): Any {
        return datas[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return datas.size
    }
}