package webservices

import models.Product
import models.UserModel

class MockAPI {
    companion object {
        private val instance = MockAPI()
        fun getInstance(): MockAPI {
            return instance
        }
    }

    private var cart = mutableListOf<Product>()

    fun addToCart(productID: String) {
        val product = getProductFromID(productID)
        product?.let {
            cart.add(it)
        }
    }

    fun getCart() = cart
    fun getProducts() = mockProducts
    private fun getProductFromID(productID: String): Product? = mockProducts.find {
        if (it.Id === productID) {
            return it
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