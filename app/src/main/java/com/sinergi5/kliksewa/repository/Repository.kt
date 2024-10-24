package com.sinergi5.kliksewa.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sinergi5.kliksewa.data.model.CartItem
import com.sinergi5.kliksewa.data.model.CategoryItem
import com.sinergi5.kliksewa.data.model.Item
import kotlinx.coroutines.tasks.await

class Repository {


    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register user
    suspend fun register(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<Unit> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("Failed to get user ID")

            // Save user data to Firestore
            val user = hashMapOf(
                "name" to name,
                "email" to email,
                "phone_number" to phone,
                "address" to "",
                "profile_image_url" to "",
                "created_at" to FieldValue.serverTimestamp(),
                "rented_items" to arrayListOf<String>()
            )
            firebaseFirestore.collection("Users").document(userId).set(user).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private var cachedRecommendations: List<Item>? = null
    private var lastCachedTime: Long = 0

    // Cache expiration set to 10 seconds for testing purposes
    private val cacheExpirationTime = 10 * 1000 // 10 seconds in milliseconds

    suspend fun getRandomRecommendation(): Result<List<Item>> {
        return try {
            val currentTime = System.currentTimeMillis()

            // Check if cache is valid
            if (!isCacheExpired(currentTime)) {
                cachedRecommendations?.let {
                    return Result.success(it)
                }
            }

            // Fetch data from Firestore
            val snapshot = firebaseFirestore.collection("Items")
                .whereEqualTo("availability", true) // Only available items
                .get()
                .await()

            val items = snapshot.documents.mapNotNull { document ->
                val item = document.toObject(Item::class.java)
                item?.itemId = document.id
                Log.d("Repository", "Fetched Item: $item")
                item
            }
            val randomItems = items.shuffled().take(10)
            // Cache the result
            cacheRecommendations(randomItems, currentTime)
            Result.success(randomItems)
            cachedRecommendations = randomItems
            lastCachedTime = currentTime
            Log.d("Repository", "Cached items: $cachedRecommendations")
            Result.success(randomItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Helper function to check if the cache has expired
    private fun isCacheExpired(currentTime: Long): Boolean {
        return cachedRecommendations == null || currentTime - lastCachedTime > cacheExpirationTime
    }

    // Helper function to cache the recommendations
    private fun cacheRecommendations(recommendations: List<Item>, currentTime: Long) {
        cachedRecommendations = recommendations
        lastCachedTime = currentTime
    }


    //    Get Detail Items
    suspend fun getDetailItem(itemId: String): Result<Item> {
        return try {
            val document = firebaseFirestore.collection("Items")
                .document(itemId)
                .get()
                .await()

            val item = document.toObject(Item::class.java)
            if (item != null) {
                Result.success(item)
            } else {
                Result.failure(Exception("Item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //    Get Category Items
    suspend fun getCategoryItem(): Result<List<CategoryItem>> {
        return try {
            val snapshot = firebaseFirestore.collection("Categories")
                .get()
                .await()

            val categories = snapshot.documents.mapNotNull { document ->
                val category = document.toObject(CategoryItem::class.java)
                category?.id = document.id
                category
            }

            Result.success(categories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemsByCategoryId(categoryId: String? = null): Result<List<Item>> {
        return try {
            var query = firebaseFirestore.collection("Items")
                .whereEqualTo("availability", true) // Only available items

            // Filter by categoryId only if it's not null
            if (categoryId != null) {
                query = query.whereEqualTo("categoryId", categoryId)
            }

            val snapshot = query.get().await()

            val items = snapshot.documents.mapNotNull {
                val item = it.toObject(Item::class.java)
                item?.itemId = it.id
                item
            }

            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /**
     * Menambahkan item ke keranjang
     */
    suspend fun addToCart(itemId: String, quantity: Int = 1): Result<Boolean> = try {

        val cartsCollection = firebaseFirestore.collection("Users")
            .document(firebaseAuth.currentUser?.uid ?: "")
            .collection("Cart")

        val cartItem = hashMapOf(
            "userId" to firebaseAuth.currentUser?.uid,
            "itemId" to itemId,
            "quantity" to quantity,
            "addedAt" to FieldValue.serverTimestamp()
        )

        // Cek apakah item sudah ada di cart
        val existingItem = cartsCollection
            .whereEqualTo("itemId", itemId)
            .get()
            .await()

        if (existingItem.isEmpty) {
            // Jika belum ada, tambahkan item baru
            cartsCollection.add(cartItem).await()
        } else {
            // Jika sudah ada, update quantity
            val currentQuantity = existingItem.documents[0].getLong("quantity")?.toInt() ?: 0
            cartsCollection.document(existingItem.documents[0].id)
                .update("quantity", currentQuantity + quantity)
                .await()
        }
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Mengambil list item dalam keranjang
     */
    suspend fun getCartItems(): Result<List<CartItem>> = try {
        val cartsCollection = firebaseFirestore.collection("Users")
            .document(firebaseAuth.currentUser?.uid ?: "")
            .collection("Cart")

        val cartSnapshot = cartsCollection.get().await()
        val cartItems = mutableListOf<CartItem>()

        for (document in cartSnapshot.documents) {
            val itemId = document.getString("itemId") ?: continue
            val quantity = document.getLong("quantity")?.toInt() ?: 1

            // Mengambil detail item dari collection Items
            val itemSnapshot = firebaseFirestore.collection("Items")
                .document(itemId)
                .get()
                .await()

            if (itemSnapshot.exists()) {
                val item = itemSnapshot.toObject(Item::class.java)?.copy(itemId = itemSnapshot.id)
                item?.let {
                    cartItems.add(
                        CartItem(
                            id = document.id,
                            item = it,
                            quantity = quantity
                        )
                    )
                }
            }
        }
        Result.success(cartItems)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Mengupdate quantity item di keranjang
     */
    suspend fun updateCartItemQuantity(cartId: String, quantity: Int): Result<Boolean> = try {
        val cartsCollection = firebaseFirestore.collection("Users")
            .document(firebaseAuth.currentUser?.uid ?: "")
            .collection("Cart")
        if (quantity <= 0) {
            removeCartItem(cartId)
        } else {
            cartsCollection.document(cartId)
                .update("quantity", quantity)
                .await()
        }
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Menghapus item dari keranjang
     */
    suspend fun removeCartItem(cartId: String): Result<Boolean> = try {
        val cartsCollection = firebaseFirestore.collection("Users")
            .document(firebaseAuth.currentUser?.uid ?: "")
            .collection("Cart")
        cartsCollection.document(cartId)
            .delete()
            .await()
        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }


    /**
     *
     * --------------------------------------------------------------------------------------------------------
     *
     * **/

//    suspend fun registerAsOwner(
//        name: String,
//        description: String,
//        phoneNumber: String,
//        address: StoreAddress,
//        whatsappNumber: String? = null // Optional, default menggunakan phoneNumber jika null
//    ): Result<String> {
//        return try {
//            val userId = firebaseAuth.currentUser?.uid
//                ?: return Result.failure(Exception("User not authenticated"))
//
//            // Generate unique store ID
//            val storeId = firebaseFirestore.collection("stores").document().id
//
//            // Create store document
//            val store = hashMapOf(
//                "storeId" to storeId,
//                "ownerId" to userId,
//                "name" to name,
//                "description" to description,
//                "address" to hashMapOf(
//                    "street" to address.street,
//                    "city" to address.city,
//                    "province" to address.province,
//                    "postalCode" to address.postalCode,
//                    "detail" to address.detail,
//                    "coordinates" to address.coordinates?.let {
//                        hashMapOf(
//                            "latitude" to it.latitude,
//                            "longitude" to it.longitude
//                        )
//                    }
//                ),
//                "phoneNumber" to phoneNumber,
//                "whatsappNumber" to (whatsappNumber ?: phoneNumber),
//                "operationalHours" to hashMapOf(
//                    "monday" to hashMapOf("open" to "08:00", "close" to "17:00"),
//                    "tuesday" to hashMapOf("open" to "08:00", "close" to "17:00"),
//                    "wednesday" to hashMapOf("open" to "08:00", "close" to "17:00"),
//                    "thursday" to hashMapOf("open" to "08:00", "close" to "17:00"),
//                    "friday" to hashMapOf("open" to "08:00", "close" to "17:00"),
//                    "saturday" to hashMapOf("open" to "08:00", "close" to "17:00"),
//                    "sunday" to hashMapOf("open" to "08:00", "close" to "17:00")
//                ),
//                "rating" to 0.0,
//                "totalReviews" to 0,
//                "storeImages" to listOf<String>(),
//                "status" to "pending", // Store perlu diverifikasi admin sebelum aktif
//                "createdAt" to FieldValue.serverTimestamp(),
//                "updatedAt" to FieldValue.serverTimestamp()
//            )
//
//            // Start a batch write
//            firebaseFirestore.runBatch { batch ->
//                // Create new store document
//                batch.set(
//                    firebaseFirestore.collection("stores").document(storeId),
//                    store
//                )
//
//                // Update user's role and add storeId reference
//                batch.update(
//                    firebaseFirestore.collection("users").document(userId),
//                    mapOf(
//                        "role" to "owner",
//                        "storeId" to storeId,
//                        "updatedAt" to FieldValue.serverTimestamp()
//                    )
//                )
//            }.await()
//
//            Result.success(storeId)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    // Extension function untuk validasi input
//    fun validateStoreInput(
//        name: String,
//        description: String,
//        phoneNumber: String,
//        address: StoreAddress
//    ): Result<Unit> {
//        return try {
//            require(name.length >= 3) { "Nama toko minimal 3 karakter" }
//            require(description.length >= 10) { "Deskripsi toko minimal 10 karakter" }
//            require(phoneNumber.matches(Regex("^\\+?[0-9]{10,13}$"))) { "Format nomor telepon tidak valid" }
//            require(address.street.isNotBlank()) { "Alamat jalan harus diisi" }
//            require(address.city.isNotBlank()) { "Kota harus diisi" }
//            require(address.province.isNotBlank()) { "Provinsi harus diisi" }
//            require(address.postalCode.matches(Regex("^[0-9]{5}$"))) { "Kode pos tidak valid" }
//
//            Result.success(Unit)
//        } catch (e: IllegalArgumentException) {
//            Result.failure(e)
//        }
//    }
//
//    // Fungsi untuk mengecek status verifikasi toko
//    suspend fun checkStoreStatus(storeId: String): Result<String> {
//        return try {
//            val store = firebaseFirestore.collection("stores")
//                .document(storeId)
//                .get()
//                .await()
//
//            val status =
//                store.getString("status") ?: return Result.failure(Exception("Status not found"))
//            Result.success(status)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//
//    // Fungsi untuk mengecek apakah user sudah memiliki toko
//    suspend fun checkIfUserHasStore(): Result<Boolean> {
//        return try {
//            val userId = firebaseAuth.currentUser?.uid
//                ?: return Result.failure(Exception("User not authenticated"))
//
//            val user = firebaseFirestore.collection("users")
//                .document(userId)
//                .get()
//                .await()
//
//            val hasStore = !user.getString("storeId").isNullOrEmpty()
//            Result.success(hasStore)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }


}