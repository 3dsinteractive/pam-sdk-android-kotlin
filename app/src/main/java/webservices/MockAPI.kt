package webservices

import android.util.Log
import models.Notification
import models.Product
import models.UserModel
import pamsdk.PamSDKName

class MockAPI {
    companion object {
        private val instance = MockAPI()
        fun getInstance(): MockAPI {
            return instance
        }
    }

    private var cart = mutableListOf<Product>()
    private var notifications = mutableListOf<Notification>()

    fun addToCart(productID: String) {
        val product = getProductFromID(productID)
        product?.let {
            cart.add(it)
        }
    }

    fun addToNotification(image: String, title: String, message: String, date: String) {
        notifications.add(
            Notification(
                Image = image,
                Title = title,
                Message = message,
                Date = date
            )
        )
    }

    fun getCart() = cart

    fun getProducts() = mockProducts

    fun getNotifications() = notifications

    fun getProductFromID(productID: String): Product? {
        for (p in mockProducts) {
            if (productID == p.Id) {
                return p
            }
        }
        return null
    }

    fun login(email: String): UserModel? {
        return mockUsers[email]
    }

    fun register(email: String): UserModel {
        return UserModel(
            CusID = email,
            Email = email
        )
    }
}