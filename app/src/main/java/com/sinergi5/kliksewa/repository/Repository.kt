package com.sinergi5.kliksewa.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
    suspend fun register(email: String, password: String, name: String, phone: String): Result<Unit> {
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

//    Get Random Recommendation
    private var cachedRecommendations: List<Item>? = null
    private var lastCachedTime: Long = 0
//    private val cacheExpirationTime = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
    private val cacheExpirationTime = 10 * 1000 // 10 seconds in milliseconds
    suspend fun getRandomRecommendation(): Result<List<Item>> {
        return try {
            val currentTime = System.currentTimeMillis()
            if (cachedRecommendations != null && currentTime - lastCachedTime < cacheExpirationTime) {
//                Return cached data
                return Result.success(cachedRecommendations!!)
            }

            val snapshot = firebaseFirestore.collection("Items")
                .whereEqualTo("availability", true) // Only available items
                .get()
                .await()

            val items = snapshot.toObjects(Item::class.java)
            val randomItems = items.shuffled().take(10)

//            Cache the result
            cachedRecommendations = randomItems
            lastCachedTime = currentTime

            Result.success(randomItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}