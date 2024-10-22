package com.sinergi5.kliksewa.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
            val query = firebaseFirestore.collection("Items")
                .whereEqualTo("availability", true) // Only available items

            // Filter by categoryId only if it's not null
            if (categoryId != null) {
                query.whereEqualTo("categoryId", categoryId)
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



}