package webservices

import models.*

class MockAPI {
    companion object {
        private val instance = MockAPI()
        fun getInstance(): MockAPI {
            return instance
        }
    }

    private var cart = CartModel(TotalPrice = 0.0)
    private var favourite = mutableListOf<String>()

    fun addToFavourite(productID: String) {
        favourite.add(productID)
    }

    fun removeFromFavourite(productID: String) {
        favourite.remove(productID)
    }

    fun isProductFavourite(productID: String): Boolean {
        var bool = false

        favourite.forEach { id ->
            if (productID == id) {
                bool = true
            }
        }

        return bool
    }

    fun checkout() {
        this.cart = CartModel(TotalPrice = 0.0)
    }

    fun addToCart(productID: String) {
        val product = getProductFromID(productID)

        product?.let {
            val oldCartProduct = getProductFromCartById(productID)
            val cartProduct = CartProductModel(
                Id = product.Id,
                CategoryId = product.CategoryId,
                Image = product.Image,
                Title = product.Title,
                UnitPrice = product.Price,
                Quantity = 1,
                TotalPrice = product.Price
            )

            if (oldCartProduct != null) {
                cartProduct.Quantity = oldCartProduct.Quantity?.plus(1)
                cart.Products?.remove(oldCartProduct)
            }

            cartProduct.TotalPrice = (cartProduct.Quantity?.times(cartProduct.UnitPrice!!))

            if (cart.Products == null) {
                cart.Products = mutableListOf(
                    cartProduct
                )
            } else {
                cart.Products?.add(cartProduct)
            }
        }
    }

    fun removeFromCart(productID: String) {
        val product = getProductFromID(productID)

        product?.let {
            val oldCartProduct = getProductFromCartById(productID)
            val cartProduct = CartProductModel(
                Id = product.Id,
                Image = product.Image,
                Title = product.Title,
                UnitPrice = product.Price,
                Quantity = 1,
                TotalPrice = product.Price
            )

            cartProduct.Quantity = oldCartProduct!!.Quantity?.minus(1)
            cart.Products?.remove(oldCartProduct)
            cartProduct.TotalPrice = (cartProduct.Quantity?.times(cartProduct.UnitPrice!!))

            if (cartProduct.Quantity!! <= 0) {
                cart.Products?.remove(cartProduct)
            } else {
                cart.Products?.add(cartProduct)
            }
        }
    }

    fun deleteFromCart(productID: String) {
        val product = getProductFromID(productID)

        product?.let {
            val oldCartProduct = getProductFromCartById(productID)
            cart.Products?.remove(oldCartProduct)
        }
    }

    private fun getProductFromCartById(productID: String): CartProductModel? {
        cart.Products?.let {
            it.forEach { p ->
                if (p.Id == productID) {
                    return p
                }
            }
        }

        return null
    }

    fun getCart(): CartModel {
        cart.Products?.let { ps ->
            var totalPrice = 0.0
            for (product in ps) {
                product.TotalPrice?.let {
                    totalPrice += it
                }
            }
            cart.TotalPrice = totalPrice
        }

        return cart
    }

    fun getProducts() = mockProducts

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