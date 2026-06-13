package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUser(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // --- Routines ---
    @Query("SELECT * FROM routines ORDER BY id ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE dayOfWeek = :day ORDER BY id ASC")
    fun getRoutinesByDay(day: String): Flow<List<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Query("DELETE FROM routines WHERE id = :routineId")
    suspend fun deleteRoutine(routineId: Int)

    @Query("UPDATE routines SET location = :newLocation WHERE id = :routineId")
    suspend fun updateRoutineLocation(routineId: Int, newLocation: String)

    @Query("DELETE FROM routines")
    suspend fun clearAllRoutines()

    // --- Marketplace Listings ---
    @Query("SELECT * FROM marketplace_listings ORDER BY createdAt DESC")
    fun getAllListings(): Flow<List<MarketplaceListingEntity>>

    @Query("SELECT * FROM marketplace_listings WHERE id = :listingId LIMIT 1")
    fun getListingById(listingId: Int): Flow<MarketplaceListingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: MarketplaceListingEntity)

    @Update
    suspend fun updateListing(listing: MarketplaceListingEntity)

    @Query("DELETE FROM marketplace_listings WHERE id = :listingId")
    suspend fun deleteListing(listingId: Int)

    // --- Comments & Discussions (Facebook Marketplace Style) ---
    @Query("SELECT * FROM comments WHERE listingId = :listingId ORDER BY createdAt ASC")
    fun getCommentsForListing(listingId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("UPDATE comments SET isReported = 1 WHERE id = :commentId")
    suspend fun reportComment(commentId: Int)

    @Query("DELETE FROM comments WHERE id = :commentId OR parentId = :commentId")
    suspend fun deleteComment(commentId: Int)

    // --- Chat Rooms ---
    @Query("SELECT * FROM chat_rooms ORDER BY lastMessageAt DESC")
    fun getAllChatRooms(): Flow<List<ChatRoomEntity>>

    @Query("SELECT * FROM chat_rooms WHERE participantId = :participantId LIMIT 1")
    suspend fun getChatRoomForParticipant(participantId: String): ChatRoomEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatRoom(chatRoom: ChatRoomEntity): Long

    @Query("UPDATE chat_rooms SET lastMessage = :lastMsg, lastMessageAt = :time, unreadCount = unreadCount + 1 WHERE id = :roomId")
    suspend fun updateChatRoomLastMessage(roomId: Int, lastMsg: String, time: Long)

    @Query("UPDATE chat_rooms SET unreadCount = 0 WHERE id = :roomId")
    suspend fun clearUnreadCount(roomId: Int)

    // --- Messages ---
    @Query("SELECT * FROM messages WHERE chatRoomId = :chatRoomId ORDER BY createdAt ASC")
    fun getMessagesForRoom(chatRoomId: Int): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    // --- Study Materials ---
    @Query("SELECT * FROM study_materials ORDER BY createdAt DESC")
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>>

    @Query("SELECT * FROM study_materials WHERE programme = :programme AND year = :year AND semester = :semester ORDER BY createdAt DESC")
    fun getStudyMaterialsFiltered(programme: String, year: String, semester: String): Flow<List<StudyMaterialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyMaterial(material: StudyMaterialEntity)

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET read = 1 WHERE id = :notificationId")
    suspend fun markNotificationAsRead(notificationId: Int)
}

@Database(
    entities = [
        UserEntity::class,
        RoutineEntity::class,
        MarketplaceListingEntity::class,
        CommentEntity::class,
        ChatRoomEntity::class,
        MessageEntity::class,
        StudyMaterialEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun campusDao(): CampusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_campus_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
