package models

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pams.ai.demo.R
import java.lang.Thread.sleep

val mockProducts: MutableList<Product> = mutableListOf(
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1582535404566.jpg",
        Title = "Infrastructure as Code (IaC) with Terraform"
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1579526262154.jpg",
        Title = "Elasticsearch Ninja Workshop 2"
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1573725997947.png",
        Title = "Workshop : สร้าง iOS App ด้วย SwiftUI"
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1570174385559.jpg",
        Title = "Workshop: Advanced Docker with Kubernetes รุ่น 6"
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1570174412610.jpg",
        Title = "Workshop: Docker From Zero to Hero รุ่น 6"
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1570173005235.jpg",
        Title = "Elasticsearch Ninja Workshop"
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1561550880736.jpg",
        Title = "Workshop: Jmeter for Production Zero to Hero Scale"
    ),
    Product(
        Image = "https://3digitsacademy.com/image/imageIcon-1560243149609.jpg",
        Title = "Workshop: Marketing Masterclass for Non-Marketer (1-Day Class)"
    )
)

data class Product(
    var Image: String,
    var Title: String
)

class ProductModel(var context: Context, var datas: MutableList<Product>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.activity_product_item, null)
        val image: ImageView = view.findViewById(R.id.product_image)
        val title: TextView = view.findViewById(R.id.product_title)
        val product: Product = datas[position]

        Picasso.get().load(product.Image).into(image)
        title.text = product.Title

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