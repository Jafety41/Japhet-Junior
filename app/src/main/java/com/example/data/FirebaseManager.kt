package com.example.data

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    
    lateinit var firestore: FirebaseFirestore
        private set
    lateinit var auth: FirebaseAuth
        private set

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            val options = FirebaseOptions.Builder()
                .setProjectId("japhtech-5bf3d")
                .setApplicationId("1:584179653451:web:47746587610dc9b90e26ce")
                .setApiKey("AIzaSyBcaCfM3hG8ugSt34xbFTlObz-JAGxeRvw")
                .setDatabaseUrl("https://japhtech-5bf3d.firebaseio.com")
                .setStorageBucket("japhtech-5bf3d.firebasestorage.app")
                .build()

            val app = if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context.applicationContext, options)
            } else {
                FirebaseApp.getInstance()
            }

            auth = FirebaseAuth.getInstance(app)
            
            // Connect to named database instance: ai-studio-0255cd82-16d6-424e-bdea-fdd815fb7d67
            firestore = FirebaseFirestore.getInstance(app, "ai-studio-0255cd82-16d6-424e-bdea-fdd815fb7d67")
            firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

            isInitialized = true
            Log.d(TAG, "Firebase successfully initialized with options and custom firestore db.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Manager", e)
        }
    }
}
