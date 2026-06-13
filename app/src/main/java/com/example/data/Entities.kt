package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val username: String,
    val email: String,
    val photoURL: String,
    val university: String,
    val onboardingCompleted: Boolean,
    val joinedAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val courseName: String,
    val courseCode: String,
    val lecturer: String,
    val times: String, // e.g., "08:00 AM - 10:00 AM"
    val location: String, // e.g., "Lecture Hall B"
    val dayOfWeek: String, // e.g., "Monday", "Tuesday", etc.
    val isOfficial: Boolean,
    val remindersEnabled: Boolean = false,
    val classType: String, // e.g., "Lecture", "Tutorial", "Lab"
    val program: String = "UD089",
    val year: Int = 3
) : Serializable

@Entity(tableName = "marketplace_listings")
data class MarketplaceListingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sellerId: String,
    val sellerName: String,
    val sellerPhoto: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String, // e.g., "Electronics", "Books", "Services"
    val status: String, // "available" or "sold"
    val condition: String, // "Like New", "Good", "Fair", "Heavily Used"
    val imageSeed: String, // seed for dicebear avatar or image helper
    val imageUrls: String = "", // Delimited image URLs or image seeds, e.g. "url1||url2||url3"
    val createdAt: Long = System.currentTimeMillis()
) : Serializable {
    fun getImageList(): List<String> {
        if (imageUrls.isBlank()) {
            if (imageSeed.isNotBlank()) {
                return listOf(imageSeed)
            }
            return emptyList()
        }
        return imageUrls.split("||").filter { it.isNotBlank() }
    }
}

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listingId: Int,
    val userId: String,
    val userName: String,
    val userPhoto: String,
    val text: String,
    val parentId: Int? = null, // if not null, it's a nested reply to comment matching parentId
    val isReported: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "chat_rooms")
data class ChatRoomEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val participantId: String,
    val participantName: String,
    val participantPhoto: String,
    val lastMessage: String,
    val lastMessageAt: Long = System.currentTimeMillis(),
    val unreadCount: Int = 0
) : Serializable

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chatRoomId: Int,
    val senderId: String,
    val text: String,
    val read: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Serializable

@Entity(tableName = "study_materials")
data class StudyMaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val programme: String, // e.g., "Computer Science", "Engineering"
    val year: String, // e.g., "Year 1", "Year 2"
    val semester: String, // e.g., "Semester 1", "Semester 2"
    val courseCode: String, // e.g., "CS-101"
    val fileName: String,
    val fileType: String, // e.g., "pdf", "docx"
    val uploadedByName: String,
    val uploadedByPhoto: String,
    val createdAt: Long = System.currentTimeMillis(),
    val sizeText: String = "1.2 MB",
    val fileId: String = "" // Appwrite File ID reference
) : Serializable

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // "class", "market", "system", "chat"
    val read: Boolean = false,
    val link: String = "",
    val createdAt: Long = System.currentTimeMillis()
) : Serializable
