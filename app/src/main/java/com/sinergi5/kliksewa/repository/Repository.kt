package com.sinergi5.kliksewa.repository

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
                    val userId = UUID.randomUUID().toString() // Generate a unique userId

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
                        "userId" to userId // Store the generated UUID
                    )

                    // Save user data to Firestore with userId as documentId
                    firebaseFirestore.collection("Users")
                        .document(userId) // Use UUID as documentId
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
}