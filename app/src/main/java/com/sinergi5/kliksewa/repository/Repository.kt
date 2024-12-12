package com.sinergi5.kliksewa.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sinergi5.kliksewa.model.CartItem
import com.sinergi5.kliksewa.model.Product
import com.sinergi5.kliksewa.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class Repository(context: Context) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

//    Login
    /**
     * Suspend function to perform user login with Firebase Authentication
     *
     * @param email User's email address
     * @param password User's password
     * @return Resource sealed class representing login result
     */
    suspend fun login(email: String, password: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Attempt to sign in with Firebase Authentication
                val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()

                // Check if authentication was successful
                if (authResult.user != null) {
                    // Optional: Fetch additional user data from Firestore if needed
                    // val userDoc = firebaseFirestore.collection("users").document(authResult.user!!.uid).get().await()

                    Resource.Success(true)
                } else {
                    Resource.Error("Login failed")
                }
            } catch (e: Exception) {
                // Handle specific Firebase Authentication exceptions
                when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                        Resource.Error("Invalid email or password")

                    is com.google.firebase.auth.FirebaseAuthInvalidUserException ->
                        Resource.Error("User not found")

                    else ->
                        Resource.Error(e.localizedMessage ?: "An unknown error occurred")
                }
            }
        }
    }

    suspend fun register(
        username: String? = "",
        phoneNumber: String? = "",
        email: String,
        password: String
    ): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Create user with Firebase Authentication
                val authResult =
                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()

                // Check if user creation was successful
                if (authResult.user != null) {

                    // Prepare user data to be stored in Firestore
                    val userData = hashMapOf(
                        "username" to username,
                        "fullNameIdCard" to "",
                        "nik" to "",
                        "accountType" to "Personal",
                        "phoneNumber" to phoneNumber,
                        "email" to email,
                        "walletBalance" to 0, // Default balance
                        "voucher" to listOf<String>(), // Default empty list
                        "createdAt" to com.google.firebase.Timestamp.now(), // Current timestamp
                        "updatedAt" to com.google.firebase.Timestamp.now(), // Current timestamp
                        "favorites" to listOf<String>(), // Default empty list
                        "carts" to listOf<String>(),
                        "listing" to listOf<String>(), // Default empty list
                        "profileImage" to "", // Default empty string
                    )

                    // Save user data to Firestore with userId as documentId
                    firebaseFirestore.collection("Users")
                        .document()
                        .set(userData)
                        .await()

                    Resource.Success(true)
                } else {
                    Resource.Error("Registration failed")
                }
            } catch (e: Exception) {
                // Handle specific Firebase Authentication exceptions
                when (e) {
                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException ->
                        Resource.Error("Weak password")

                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException ->
                        Resource.Error("Invalid email format")

                    is com.google.firebase.auth.FirebaseAuthUserCollisionException ->
                        Resource.Error("Email already in use")

                    else ->
                        Resource.Error(e.localizedMessage ?: "An unknown error occurred")
                }
            }
        }
    }

    //    Get List Product
    suspend fun getRandomProduct(): Resource<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firebaseFirestore.collection("Products")
                    .get()
                    .await()

                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                Resource.Success(products)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Terjadi kesalahan saat mengambil data produk")
            }
        }
    }

    //    Toggle Favorite Product
    suspend fun toggleFavorite(productId: String, userId: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val productRef = firebaseFirestore.collection("Products").document(productId)

                firebaseFirestore.runTransaction { transaction ->
                    val snapshot = transaction.get(productRef)
                    val product = snapshot.toObject(Product::class.java)
                        ?: throw Exception("Produk tidak ditemukan")

                    val currentFavorites = product.favoriteBy.toMutableList()

                    if (userId in currentFavorites) {
                        currentFavorites.remove(userId)
                    } else {
                        currentFavorites.add(userId)
                    }

                    transaction.update(productRef, "favoriteBy", currentFavorites)
                }.await()

                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Gagal mengubah status favorit")
            }
        }
    }

    //    Get Detail Product by ID
    suspend fun getProductDetail(productId: String): Resource<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val snapshot = firebaseFirestore.collection("Products")
                    .document(productId)
                    .get()
                    .await()

                val product = snapshot.toObject(Product::class.java)?.copy(id = snapshot.id)
                    ?: throw Exception("Produk tidak ditemukan")

                Resource.Success(product)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Gagal mengambil detail produk")
            }
        }
    }


    suspend fun getUserByEmail(email: String): Resource<String> {
        return withContext(Dispatchers.IO) {
            try {
                val userQuery = firebaseFirestore.collection("Users")
                    .whereEqualTo("email", email)
                    .get()
                    .await()

                if (userQuery.documents.isEmpty()) {
                    Resource.Error("User tidak ditemukan")
                } else {
                    Resource.Success(userQuery.documents.first().id)
                }
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    suspend fun addToCart(productId: String, productName: String, quantity: Int, pricePerItem: Long): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Dapatkan userId dari Firebase Auth
                val authUserId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User tidak terautentikasi")

                // 2. Cari dokumen user berdasarkan email
                val userEmail = FirebaseAuth.getInstance().currentUser?.email
                    ?: throw Exception("Email user tidak ditemukan")

                val userQuery = firebaseFirestore.collection("Users")
                    .whereEqualTo("email", userEmail)
                    .get()
                    .await()

                if (userQuery.documents.isEmpty()) {
                    throw Exception("Data user tidak ditemukan")
                }

                val userDoc = userQuery.documents.first()
                val userId = userDoc.id

                // 3. Cek apakah produk sudah ada di cart user
                val existingCartQuery = firebaseFirestore.collection("CartItems")
                    .whereEqualTo("userId", userId)
                    .whereEqualTo("productId", productId)
                    .get()
                    .await()

                // Jika item sudah ada di cart, update quantity
                if (existingCartQuery.documents.isNotEmpty()) {
                    val existingCartItem = existingCartQuery.documents.first()
                    val currentQuantity = existingCartItem.getLong("quantity")?.toInt() ?: 0
                    val newQuantity = currentQuantity + quantity

                    // Update quantity dan total price
                    firebaseFirestore.collection("CartItems")
                        .document(existingCartItem.id)
                        .update(
                            mapOf(
                                "quantity" to newQuantity,
                                "totalPrice" to (pricePerItem * newQuantity),
                                "updatedAt" to com.google.firebase.Timestamp.now()
                            )
                        )
                        .await()

                    Log.d("Cart", "Updated existing cart item quantity: ${existingCartItem.id}")
                    return@withContext Resource.Success(true)
                }

                // 4. Jika item belum ada di cart, lanjutkan dengan proses penambahan baru
                val cartItemId = UUID.randomUUID().toString()
                val cartItem = CartItem(
                    id = cartItemId,
                    productId = productId,
                    productName = productName,
                    userId = userId,
                    quantity = quantity,
                    pricePerItem = pricePerItem,
                    totalPrice = quantity * pricePerItem,
                    createdAt = com.google.firebase.Timestamp.now(),
                    updatedAt = com.google.firebase.Timestamp.now()
                )

                // 5. Proses transaction untuk item baru
                val userRef = firebaseFirestore.collection("Users").document(userId)

                firebaseFirestore.runTransaction { transaction ->
                    val currentCarts = userDoc.get("carts") as? List<String> ?: listOf()
                    val updatedCarts = currentCarts + cartItemId

                    transaction.update(
                        userRef, mapOf(
                            "carts" to updatedCarts,
                            "updatedAt" to com.google.firebase.Timestamp.now()
                        )
                    )

                    val cartRef = firebaseFirestore.collection("CartItems").document(cartItemId)
                    transaction.set(cartRef, cartItem)
                }.await()

                Log.d("Cart", "Added new item to cart: $cartItemId")
                Resource.Success(true)

            } catch (e: Exception) {
                Log.e("Cart", "Error in cart operation: ${e.message}")
                Resource.Error(e.message ?: "Gagal menambahkan ke keranjang")
            }
        }
    }

    // Fungsi untuk mendapatkan cart items user
    suspend fun getCartItems(userId: String): Resource<List<CartItem>> {
        return withContext(Dispatchers.IO) {
            try {
                val cartItems = firebaseFirestore.collection("CartItems")
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .toObjects(CartItem::class.java)

                Resource.Success(cartItems)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Gagal mengambil data keranjang")
            }
        }
    }

    // Fungsi untuk update quantity
    suspend fun updateCartItemQuantity(cartItemId: String, newQuantity: Int): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val cartRef = firebaseFirestore.collection("CartItems").document(cartItemId)

                firebaseFirestore.runTransaction { transaction ->
                    val cartDoc = transaction.get(cartRef)
                    val cartItem = cartDoc.toObject(CartItem::class.java)
                        ?: throw Exception("Item tidak ditemukan")

                    val newTotalPrice = cartItem.pricePerItem * newQuantity

                    transaction.update(
                        cartRef, mapOf(
                            "quantity" to newQuantity,
                            "totalPrice" to newTotalPrice,
                            "updatedAt" to com.google.firebase.Timestamp.now()
                        )
                    )
                }.await()

                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Gagal mengupdate quantity")
            }
        }
    }

    // Fungsi untuk remove item dari cart
    suspend fun removeFromCart(cartItemId: String): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("User tidak terautentikasi")

                firebaseFirestore.runTransaction { transaction ->
                    // Remove from Users collection
                    val userRef = firebaseFirestore.collection("Users").document(userId)
                    val userDoc = transaction.get(userRef)
                    val currentCarts = userDoc.get("carts") as? List<String> ?: listOf()

                    transaction.update(userRef, "carts", currentCarts - cartItemId)

                    // Remove from CartItems collection
                    val cartRef = firebaseFirestore.collection("CartItems").document(cartItemId)
                    transaction.delete(cartRef)
                }.await()

                Resource.Success(true)
            } catch (e: Exception) {
                Resource.Error(e.message ?: "Gagal menghapus item")
            }
        }
    }
}