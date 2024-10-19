package com.sinergi5.kliksewa.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
                "created_at" to FieldValue.serverTimestamp()
            )
            firebaseFirestore.collection("Users").document(userId).set(user).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}