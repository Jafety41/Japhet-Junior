package com.example.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class DataRepository(private val dao: CampusDao) {
    private val TAG = "DataRepository"
    private val scope = CoroutineScope(Dispatchers.IO)

    private fun getSafeLong(doc: DocumentSnapshot, field: String): Long? {
        try {
            val value = doc.get(field) ?: return null
            return when (value) {
                is Number -> value.toLong()
                is Timestamp -> value.toDate().time
                is String -> value.toLongOrNull()
                is Map<*, *> -> {
                    val sec = (value["seconds"] as? Number)?.toLong()
                    if (sec != null) sec * 1000 else null
                }
                else -> null
            }
        } catch (e: Exception) {
            Log.e(TAG, "getSafeLong exception for field $field", e)
            return null
        }
    }

    private fun getSafeDouble(doc: DocumentSnapshot, field: String): Double? {
        try {
            val value = doc.get(field) ?: return null
            return when (value) {
                is Number -> value.toDouble()
                is String -> value.toDoubleOrNull()
                else -> null
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun getSafeBoolean(doc: DocumentSnapshot, field: String): Boolean? {
        try {
            val value = doc.get(field) ?: return null
            return when (value) {
                is Boolean -> value
                is String -> value.toBoolean()
                is Number -> value.toInt() != 0
                else -> null
            }
        } catch (e: Exception) {
            return null
        }
    }

    private fun getSafeString(doc: DocumentSnapshot, field: String): String? {
        try {
            val value = doc.get(field) ?: return null
            return value.toString()
        } catch (e: Exception) {
            return null
        }
    }

    // --- Active Real-Time Pull Synchronization ---
    fun startRealtimeSync() {
        Log.d(TAG, "Starting Active Real-Time Firestore Synchronization...")

        // Sync Users Collection
        try {
            FirebaseManager.firestore.collection("users")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e(TAG, "Users listener failed", e)
                        return@addSnapshotListener
                    }
                    snapshot?.let {
                        for (doc in it.documents) {
                            val user = UserEntity(
                                id = doc.id,
                                displayName = getSafeString(doc, "displayName") ?: "Student",
                                username = getSafeString(doc, "username") ?: "@student",
                                email = getSafeString(doc, "email") ?: "",
                                photoURL = getSafeString(doc, "photoURL") ?: "https://api.dicebear.com/7.x/avataaars/svg?seed=MainUser",
                                university = getSafeString(doc, "university") ?: "Smart Campus",
                                onboardingCompleted = getSafeBoolean(doc, "onboardingCompleted") ?: false,
                                joinedAt = getSafeLong(doc, "joinedAt") ?: System.currentTimeMillis()
                            )
                            scope.launch {
                                dao.insertUser(user)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync users initialization failed", ex) }

        // Sync Routines Collection
        try {
            FirebaseManager.firestore.collection("routines")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.let {
                        for (doc in it.documents) {
                            val routine = RoutineEntity(
                                id = getSafeLong(doc, "id")?.toInt() ?: 0,
                                courseName = getSafeString(doc, "courseName") ?: "",
                                courseCode = getSafeString(doc, "courseCode") ?: "",
                                lecturer = getSafeString(doc, "lecturer") ?: "",
                                times = getSafeString(doc, "times") ?: "",
                                location = getSafeString(doc, "location") ?: "",
                                dayOfWeek = getSafeString(doc, "dayOfWeek") ?: "",
                                isOfficial = getSafeBoolean(doc, "isOfficial") ?: false,
                                remindersEnabled = getSafeBoolean(doc, "remindersEnabled") ?: false,
                                classType = getSafeString(doc, "classType") ?: ""
                            )
                            scope.launch {
                                dao.insertRoutine(routine)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync routines failed", ex) }

        // Sync Marketplace Listings
        try {
            FirebaseManager.firestore.collection("listings")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.let {
                        for (doc in it.documents) {
                            val listing = MarketplaceListingEntity(
                                id = getSafeLong(doc, "id")?.toInt() ?: 0,
                                sellerId = getSafeString(doc, "sellerId") ?: "",
                                sellerName = getSafeString(doc, "sellerName") ?: "",
                                sellerPhoto = getSafeString(doc, "sellerPhoto") ?: "",
                                title = getSafeString(doc, "title") ?: "",
                                description = getSafeString(doc, "description") ?: "",
                                price = getSafeDouble(doc, "price") ?: 0.0,
                                category = getSafeString(doc, "category") ?: "",
                                status = getSafeString(doc, "status") ?: "available",
                                condition = getSafeString(doc, "condition") ?: "",
                                imageSeed = getSafeString(doc, "imageSeed") ?: "",
                                createdAt = getSafeLong(doc, "createdAt") ?: System.currentTimeMillis()
                            )
                            scope.launch {
                                dao.insertListing(listing)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync listings failed", ex) }

        // Sync Comments
        try {
            FirebaseManager.firestore.collection("comments")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.let {
                        for (doc in it.documents) {
                            val comm = CommentEntity(
                                id = getSafeLong(doc, "id")?.toInt() ?: 0,
                                listingId = getSafeLong(doc, "listingId")?.toInt() ?: 0,
                                userId = getSafeString(doc, "userId") ?: "",
                                userName = getSafeString(doc, "userName") ?: "",
                                userPhoto = getSafeString(doc, "userPhoto") ?: "",
                                text = getSafeString(doc, "text") ?: "",
                                parentId = getSafeLong(doc, "parentId")?.toInt(),
                                isReported = getSafeBoolean(doc, "isReported") ?: false,
                                createdAt = getSafeLong(doc, "createdAt") ?: System.currentTimeMillis()
                            )
                            scope.launch {
                                dao.insertComment(comm)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync comments failed", ex) }

        // Sync Study Materials
        try {
            FirebaseManager.firestore.collection("materials")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.let {
                        for (doc in it.documents) {
                            val mat = StudyMaterialEntity(
                                id = getSafeLong(doc, "id")?.toInt() ?: 0,
                                title = getSafeString(doc, "title") ?: "",
                                description = getSafeString(doc, "description") ?: "",
                                programme = getSafeString(doc, "programme") ?: getSafeString(doc, "programmeName") ?: "",
                                year = getSafeString(doc, "year") ?: getSafeString(doc, "academicYear") ?: "",
                                semester = getSafeString(doc, "semester") ?: "",
                                courseCode = getSafeString(doc, "courseCode") ?: getSafeString(doc, "moduleCode") ?: "",
                                fileName = getSafeString(doc, "fileName") ?: "",
                                fileType = getSafeString(doc, "fileType") ?: "pdf",
                                uploadedByName = getSafeString(doc, "uploadedByName") ?: "",
                                uploadedByPhoto = getSafeString(doc, "uploadedByPhoto") ?: "",
                                createdAt = getSafeLong(doc, "createdAt") ?: System.currentTimeMillis(),
                                sizeText = getSafeString(doc, "sizeText") ?: "1.2 MB",
                                fileId = getSafeString(doc, "fileId") ?: ""
                            )
                            scope.launch {
                                dao.insertStudyMaterial(mat)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync materials failed", ex) }

        // Sync Chat Rooms
        try {
            FirebaseManager.firestore.collection("chatRooms")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.let {
                        for (doc in it.documents) {
                            val room = ChatRoomEntity(
                                id = getSafeLong(doc, "id")?.toInt() ?: 0,
                                participantId = getSafeString(doc, "participantId") ?: "",
                                participantName = getSafeString(doc, "participantName") ?: "",
                                participantPhoto = getSafeString(doc, "participantPhoto") ?: "",
                                lastMessage = getSafeString(doc, "lastMessage") ?: "",
                                lastMessageAt = getSafeLong(doc, "lastMessageAt") ?: System.currentTimeMillis(),
                                unreadCount = getSafeLong(doc, "unreadCount")?.toInt() ?: 0
                            )
                            scope.launch {
                                dao.insertChatRoom(room)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync chat rooms failed", ex) }

        // Sync Messages
        try {
            FirebaseManager.firestore.collection("messages")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.let {
                        for (doc in it.documents) {
                            val msg = MessageEntity(
                                id = getSafeLong(doc, "id")?.toInt() ?: 0,
                                chatRoomId = getSafeLong(doc, "chatRoomId")?.toInt() ?: 0,
                                senderId = getSafeString(doc, "senderId") ?: "",
                                text = getSafeString(doc, "text") ?: "",
                                read = getSafeBoolean(doc, "read") ?: false,
                                createdAt = getSafeLong(doc, "createdAt") ?: System.currentTimeMillis()
                            )
                            scope.launch {
                                dao.insertMessage(msg)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync messages failed", ex) }

        // Sync Notifications
        try {
            FirebaseManager.firestore.collection("notifications")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    snapshot?.let {
                        for (doc in it.documents) {
                            val notif = NotificationEntity(
                                id = getSafeLong(doc, "id")?.toInt() ?: 0,
                                userId = getSafeString(doc, "userId") ?: "",
                                title = getSafeString(doc, "title") ?: "",
                                message = getSafeString(doc, "message") ?: "",
                                type = getSafeString(doc, "type") ?: "system",
                                read = getSafeBoolean(doc, "read") ?: false,
                                link = getSafeString(doc, "link") ?: "",
                                createdAt = getSafeLong(doc, "createdAt") ?: System.currentTimeMillis()
                            )
                            scope.launch {
                                dao.insertNotification(notif)
                            }
                        }
                    }
                }
        } catch (ex: Exception) { Log.e(TAG, "Sync notifications failed", ex) }
    }

    // --- Users ---
    fun getUser(userId: String): Flow<UserEntity?> = dao.getUser(userId)

    suspend fun insertUser(user: UserEntity) {
        dao.insertUser(user)
        try {
            val map = hashMapOf(
                "displayName" to user.displayName,
                "username" to user.username,
                "email" to user.email,
                "photoURL" to user.photoURL,
                "university" to user.university,
                "onboardingCompleted" to user.onboardingCompleted,
                "joinedAt" to user.joinedAt
            )
            FirebaseManager.firestore.collection("users").document(user.id).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertUser failed", e)
        }
    }

    // --- Routines ---
    fun getAllRoutines(): Flow<List<RoutineEntity>> = dao.getAllRoutines()
    fun getRoutinesByDay(day: String): Flow<List<RoutineEntity>> = dao.getRoutinesByDay(day)

    suspend fun insertRoutine(routine: RoutineEntity) {
        dao.insertRoutine(routine)
        try {
            val idStr = if (routine.id == 0) System.currentTimeMillis().toString() else routine.id.toString()
            val map = hashMapOf(
                "id" to if (routine.id == 0) idStr.toLong() else routine.id.toLong(),
                "courseName" to routine.courseName,
                "courseCode" to routine.courseCode,
                "lecturer" to routine.lecturer,
                "times" to routine.times,
                "location" to routine.location,
                "dayOfWeek" to routine.dayOfWeek,
                "isOfficial" to routine.isOfficial,
                "remindersEnabled" to routine.remindersEnabled,
                "classType" to routine.classType
            )
            FirebaseManager.firestore.collection("routines").document(idStr).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertRoutine failed", e)
        }
    }

    suspend fun deleteRoutine(routineId: Int) {
        dao.deleteRoutine(routineId)
        try {
            FirebaseManager.firestore.collection("routines").document(routineId.toString()).delete()
        } catch (e: Exception) {
            Log.e(TAG, "Firestore deleteRoutine failed", e)
        }
    }

    suspend fun updateRoutineLocation(routineId: Int, newLocation: String) {
        dao.updateRoutineLocation(routineId, newLocation)
        try {
            val updateMap = mapOf("location" to newLocation)
            FirebaseManager.firestore.collection("routines").document(routineId.toString()).update(updateMap)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore updateRoutineLocation failed", e)
        }
    }

    suspend fun clearAllRoutines() {
        dao.clearAllRoutines()
        // Local only or optionally clear Firestore routines matching specific fields
    }

    // --- Marketplace Listings ---
    fun getAllListings(): Flow<List<MarketplaceListingEntity>> = dao.getAllListings()
    fun getListingById(listingId: Int): Flow<MarketplaceListingEntity?> = dao.getListingById(listingId)

    suspend fun insertListing(listing: MarketplaceListingEntity) {
        dao.insertListing(listing)
        try {
            val idStr = if (listing.id == 0) System.currentTimeMillis().toString() else listing.id.toString()
            val map = hashMapOf(
                "id" to if (listing.id == 0) idStr.toLong() else listing.id.toLong(),
                "sellerId" to listing.sellerId,
                "sellerName" to listing.sellerName,
                "sellerPhoto" to listing.sellerPhoto,
                "title" to listing.title,
                "description" to listing.description,
                "price" to listing.price,
                "category" to listing.category,
                "status" to listing.status,
                "condition" to listing.condition,
                "imageSeed" to listing.imageSeed,
                "createdAt" to listing.createdAt
            )
            FirebaseManager.firestore.collection("listings").document(idStr).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertListing failed", e)
        }
    }

    suspend fun updateListing(listing: MarketplaceListingEntity) {
        dao.updateListing(listing)
        insertListing(listing)
    }

    suspend fun deleteListing(listingId: Int) {
        dao.deleteListing(listingId)
        try {
            FirebaseManager.firestore.collection("listings").document(listingId.toString()).delete()
        } catch (e: Exception) {
            Log.e(TAG, "Firestore deleteListing failed", e)
        }
    }

    // --- Comments & Discussions ---
    fun getCommentsForListing(listingId: Int): Flow<List<CommentEntity>> = dao.getCommentsForListing(listingId)

    suspend fun insertComment(comment: CommentEntity) {
        dao.insertComment(comment)
        try {
            val idStr = if (comment.id == 0) System.currentTimeMillis().toString() else comment.id.toString()
            val map = hashMapOf(
                "id" to if (comment.id == 0) idStr.toLong() else comment.id.toLong(),
                "listingId" to comment.listingId.toLong(),
                "userId" to comment.userId,
                "userName" to comment.userName,
                "userPhoto" to comment.userPhoto,
                "text" to comment.text,
                "parentId" to comment.parentId?.toLong(),
                "isReported" to comment.isReported,
                "createdAt" to comment.createdAt
            )
            FirebaseManager.firestore.collection("comments").document(idStr).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertComment failed", e)
        }
    }

    suspend fun reportComment(commentId: Int) {
        dao.reportComment(commentId)
        try {
            FirebaseManager.firestore.collection("comments").document(commentId.toString())
                .update("isReported", true)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore reportComment failed", e)
        }
    }

    suspend fun deleteComment(commentId: Int) {
        dao.deleteComment(commentId)
        try {
            FirebaseManager.firestore.collection("comments").document(commentId.toString()).delete()
        } catch (e: Exception) {
            Log.e(TAG, "Firestore deleteComment failed", e)
        }
    }

    // --- Chat Rooms ---
    fun getAllChatRooms(): Flow<List<ChatRoomEntity>> = dao.getAllChatRooms()

    suspend fun insertChatRoom(chatRoom: ChatRoomEntity): Long {
        val id = dao.insertChatRoom(chatRoom)
        try {
            val idStr = if (chatRoom.id == 0) id.toString() else chatRoom.id.toString()
            val map = hashMapOf(
                "id" to if (chatRoom.id == 0) id else chatRoom.id.toLong(),
                "participantId" to chatRoom.participantId,
                "participantName" to chatRoom.participantName,
                "participantPhoto" to chatRoom.participantPhoto,
                "lastMessage" to chatRoom.lastMessage,
                "lastMessageAt" to chatRoom.lastMessageAt,
                "unreadCount" to chatRoom.unreadCount.toLong()
            )
            FirebaseManager.firestore.collection("chatRooms").document(idStr).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertChatRoom failed", e)
        }
        return id
    }

    suspend fun getOrCreateChatRoom(participantId: String, name: String, photo: String): Int {
        val existing = dao.getChatRoomForParticipant(participantId)
        if (existing != null) return existing.id
        val newRoom = ChatRoomEntity(
            participantId = participantId,
            participantName = name,
            participantPhoto = photo,
            lastMessage = "Start of chat session",
            unreadCount = 0
        )
        return insertChatRoom(newRoom).toInt()
    }

    suspend fun updateChatRoomLastMessage(roomId: Int, lastMsg: String, time: Long) {
        dao.updateChatRoomLastMessage(roomId, lastMsg, time)
        try {
            val map = hashMapOf<String, Any>(
                "lastMessage" to lastMsg,
                "lastMessageAt" to time
            )
            FirebaseManager.firestore.collection("chatRooms").document(roomId.toString()).update(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore updateChatRoomLastMessage failed", e)
        }
    }

    suspend fun clearUnreadCount(roomId: Int) {
        dao.clearUnreadCount(roomId)
        try {
            FirebaseManager.firestore.collection("chatRooms").document(roomId.toString())
                .update("unreadCount", 0)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore clearUnreadCount failed", e)
        }
    }

    // --- Messages ---
    fun getMessagesForRoom(chatRoomId: Int): Flow<List<MessageEntity>> = dao.getMessagesForRoom(chatRoomId)

    suspend fun insertMessage(message: MessageEntity) {
        dao.insertMessage(message)
        try {
            val idStr = if (message.id == 0) System.currentTimeMillis().toString() else message.id.toString()
            val map = hashMapOf(
                "id" to if (message.id == 0) idStr.toLong() else message.id.toLong(),
                "chatRoomId" to message.chatRoomId.toLong(),
                "senderId" to message.senderId,
                "text" to message.text,
                "read" to message.read,
                "createdAt" to message.createdAt
            )
            FirebaseManager.firestore.collection("messages").document(idStr).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertMessage failed", e)
        }
    }

    // --- Study Materials ---
    fun getAllStudyMaterials(): Flow<List<StudyMaterialEntity>> = dao.getAllStudyMaterials()
    fun getStudyMaterialsFiltered(programme: String, year: String, semester: String): Flow<List<StudyMaterialEntity>> =
        dao.getStudyMaterialsFiltered(programme, year, semester)

    suspend fun insertStudyMaterial(material: StudyMaterialEntity) {
        dao.insertStudyMaterial(material)
        try {
            val idStr = if (material.id == 0) System.currentTimeMillis().toString() else material.id.toString()
            val viewUrl = AppwriteStorageHelper.getFileViewUrl(material.fileId)
            val map = hashMapOf(
                "id" to if (material.id == 0) idStr.toLong() else material.id.toLong(),
                "title" to material.title,
                "description" to material.description,
                "programme" to material.programme,
                "year" to material.year,
                "semester" to material.semester,
                "courseCode" to material.courseCode,
                "fileName" to material.fileName,
                "fileType" to material.fileType,
                "uploadedByName" to material.uploadedByName,
                "uploadedByPhoto" to material.uploadedByPhoto,
                "createdAt" to material.createdAt,
                "sizeText" to material.sizeText,
                "fileId" to material.fileId,
                "fileUrl" to viewUrl
            )
            FirebaseManager.firestore.collection("materials").document(idStr).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertStudyMaterial failed", e)
        }
    }

    // --- Notifications ---
    fun getNotificationsForUser(userId: String): Flow<List<NotificationEntity>> = dao.getNotificationsForUser(userId)

    suspend fun insertNotification(notification: NotificationEntity) {
        dao.insertNotification(notification)
        try {
            val idStr = if (notification.id == 0) System.currentTimeMillis().toString() else notification.id.toString()
            val map = hashMapOf(
                "id" to if (notification.id == 0) idStr.toLong() else notification.id.toLong(),
                "userId" to notification.userId,
                "title" to notification.title,
                "message" to notification.message,
                "type" to notification.type,
                "read" to notification.read,
                "link" to notification.link,
                "createdAt" to notification.createdAt
            )
            FirebaseManager.firestore.collection("notifications").document(idStr).set(map)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore insertNotification failed", e)
        }
    }

    suspend fun markNotificationAsRead(notificationId: Int) {
        dao.markNotificationAsRead(notificationId)
        try {
            FirebaseManager.firestore.collection("notifications").document(notificationId.toString())
                .update("read", true)
        } catch (e: Exception) {
            Log.e(TAG, "Firestore markNotificationAsRead failed", e)
        }
    }

    // --- Scraper Proxy Simulation ---
    suspend fun simulateScraperSync(): List<RoutineEntity> {
        val officialSchedules = listOf(
            RoutineEntity(
                courseName = "Object-Oriented Programming II",
                courseCode = "CS-202",
                lecturer = "Dr. Michael Jackson",
                times = "08:00 AM - 10:00 AM",
                location = "Lecture Hall B",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "CS",
                year = 2
            ),
            RoutineEntity(
                courseName = "Database Systems & Design",
                courseCode = "CS-204",
                lecturer = "Prof. Sophia Smith",
                times = "11:00 AM - 01:00 PM",
                location = "CS Lab 3",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lab",
                program = "CS",
                year = 2
            ),
            RoutineEntity(
                courseName = "Discrete Structures & Logic",
                courseCode = "CS-105",
                lecturer = "Dr. Albert Einstein",
                times = "10:30 AM - 12:30 PM",
                location = "Room A-12",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "CS",
                year = 1
            ),
            RoutineEntity(
                courseName = "Mobile App Design (Kotlin)",
                courseCode = "CS-311",
                lecturer = "Japhet Mathias",
                times = "02:00 PM - 04:00 PM",
                location = "Smart Classroom C",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "CS",
                year = 3
            ),
            RoutineEntity(
                courseName = "Software Engineering Principles",
                courseCode = "CS-308",
                lecturer = "Prof. Margaret Hamilton",
                times = "09:00 AM - 11:00 AM",
                location = "Auditorium Main",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Lecture",
                program = "CS",
                year = 3
            ),
            RoutineEntity(
                courseName = "Artificial Intelligence Basics",
                courseCode = "CS-401",
                lecturer = "Dr. Alan Turing",
                times = "11:00 AM - 01:00 PM",
                location = "Seminar Hall 2",
                dayOfWeek = "Friday",
                isOfficial = true,
                classType = "Seminar",
                program = "CS",
                year = 4
            )
        )

        for (item in officialSchedules) {
            insertRoutine(item)
        }
        seedMathematicsAndStatisticsRoutines()
        return officialSchedules
    }

    suspend fun seedMathematicsAndStatisticsRoutines() {
        val existingRoutines = dao.getAllRoutines().first()
        val mathRoutines = listOf(
            // --- Year 3 (UD089) ---
            RoutineEntity(
                courseName = "Course ST324",
                courseCode = "ST324",
                lecturer = "Mathematics Dept",
                times = "11:00 AM - 12:55 PM",
                location = "YOMBO1",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Course MT346",
                courseCode = "MT346",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 01:55 PM",
                location = "COAF LR1",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Seminar ST318",
                courseCode = "ST318",
                lecturer = "Mathematics Dept",
                times = "02:00 PM - 03:55 PM",
                location = "B2-2",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Tutorial MT310",
                courseCode = "MT310",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 01:55 PM",
                location = "R217",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Tutorial",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Tutorial MT360",
                courseCode = "MT360",
                lecturer = "Mathematics Dept",
                times = "08:00 AM - 08:55 AM",
                location = "A4",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Tutorial",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture MT310",
                courseCode = "MT310",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "A9",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture ST321",
                courseCode = "ST321",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 02:55 PM",
                location = "SB",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Seminar ST312",
                courseCode = "ST312",
                lecturer = "Mathematics Dept",
                times = "05:00 PM - 05:55 PM",
                location = "YOMBO2",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Tutorial MT360",
                courseCode = "MT360",
                lecturer = "Mathematics Dept",
                times = "02:00 PM - 02:55 PM",
                location = "ALRC",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Tutorial",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture MT310",
                courseCode = "MT310",
                lecturer = "Mathematics Dept",
                times = "08:00 AM - 08:55 AM",
                location = "GLY1",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture ST312",
                courseCode = "ST312",
                lecturer = "Mathematics Dept",
                times = "10:00 AM - 11:55 AM",
                location = "YOMBO1",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture MT360",
                courseCode = "MT360",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 02:55 PM",
                location = "A21",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Seminar ST321",
                courseCode = "ST321",
                lecturer = "Mathematics Dept",
                times = "03:00 PM - 03:55 PM",
                location = "SA",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture MT346",
                courseCode = "MT346",
                lecturer = "Mathematics Dept",
                times = "11:00 AM - 12:55 PM",
                location = "SC109",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Tutorial MT360",
                courseCode = "MT360",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "COAF LR8",
                dayOfWeek = "Friday",
                isOfficial = true,
                classType = "Tutorial",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture MT310",
                courseCode = "MT310",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "A206",
                dayOfWeek = "Friday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Lecture ST318",
                courseCode = "ST318",
                lecturer = "Mathematics Dept",
                times = "04:00 PM - 05:55 PM",
                location = "YOMBO1",
                dayOfWeek = "Friday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 3
            ),
            RoutineEntity(
                courseName = "Seminar ST324",
                courseCode = "ST324",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "COAF LR8",
                dayOfWeek = "Friday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 3
            ),

            // --- Year 2 (UD089) ---
            RoutineEntity(
                courseName = "Practical MT274",
                courseCode = "MT274",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "MT_CR1",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Practical",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT274",
                courseCode = "MT274",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 01:55 PM",
                location = "COAF LR10",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT120",
                courseCode = "MT120",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "SC315",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT274",
                courseCode = "MT274",
                lecturer = "Mathematics Dept",
                times = "07:00 AM - 07:55 AM",
                location = "ATB",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Tutorial MT274",
                courseCode = "MT274",
                lecturer = "Mathematics Dept",
                times = "09:00 AM - 09:55 AM",
                location = "MB",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Tutorial",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Practical MT274",
                courseCode = "MT274",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "MT_CR1",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Practical",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT120",
                courseCode = "MT120",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 01:55 PM",
                location = "A21",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture ST219",
                courseCode = "ST219",
                lecturer = "Mathematics Dept",
                times = "03:00 PM - 03:55 PM",
                location = "SC315",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture ST221",
                courseCode = "ST221",
                lecturer = "Mathematics Dept",
                times = "08:00 AM - 08:55 AM",
                location = "SB",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT274",
                courseCode = "MT274",
                lecturer = "Mathematics Dept",
                times = "10:00 AM - 10:55 AM",
                location = "COAF LR1",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT120",
                courseCode = "MT120",
                lecturer = "Mathematics Dept",
                times = "11:00 AM - 11:55 AM",
                location = "A21",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture ST219",
                courseCode = "ST219",
                lecturer = "Mathematics Dept",
                times = "03:00 PM - 03:55 PM",
                location = "YOMBO3",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT278",
                courseCode = "MT278",
                lecturer = "Mathematics Dept",
                times = "04:00 PM - 04:55 PM",
                location = "B2-2",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT274",
                courseCode = "MT274",
                lecturer = "Mathematics Dept",
                times = "08:00 AM - 08:55 AM",
                location = "ATB",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture ST211",
                courseCode = "ST211",
                lecturer = "Mathematics Dept",
                times = "11:00 AM - 12:55 PM",
                location = "NKRUMAH HALL",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT120",
                courseCode = "MT120",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "A21",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Lecture MT278",
                courseCode = "MT278",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 01:55 PM",
                location = "ATB",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Seminar ST211",
                courseCode = "ST211",
                lecturer = "Mathematics Dept",
                times = "02:00 PM - 02:55 PM",
                location = "YOMBO2",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Seminar ST219",
                courseCode = "ST219",
                lecturer = "Mathematics Dept",
                times = "04:00 PM - 04:55 PM",
                location = "SA",
                dayOfWeek = "Thursday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 2
            ),
            RoutineEntity(
                courseName = "Seminar ST221",
                courseCode = "ST221",
                lecturer = "Mathematics Dept",
                times = "09:00 AM - 09:55 AM",
                location = "SA",
                dayOfWeek = "Friday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 2
            ),

            // --- Year 1 (UD089) ---
            RoutineEntity(
                courseName = "Lecture MT147",
                courseCode = "MT147",
                lecturer = "Mathematics Dept",
                times = "10:00 AM - 11:55 AM",
                location = "MB",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture MT136",
                courseCode = "MT136",
                lecturer = "Mathematics Dept",
                times = "10:00 AM - 10:55 AM",
                location = "UDBS3/C520",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Seminar ST118",
                courseCode = "ST118",
                lecturer = "Mathematics Dept",
                times = "11:00 AM - 11:55 AM",
                location = "B2-2",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture MT120",
                courseCode = "MT120",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "SC315",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Seminar FN101",
                courseCode = "FN101",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 01:55 PM",
                location = "ALRA",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Seminar FN101",
                courseCode = "FN101",
                lecturer = "Mathematics Dept",
                times = "04:00 PM - 04:55 PM",
                location = "UDBS2/C124",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Seminar FN101",
                courseCode = "FN101",
                lecturer = "Mathematics Dept",
                times = "07:00 PM - 07:55 PM",
                location = "HEALTH LR 2",
                dayOfWeek = "Monday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture MT120",
                courseCode = "MT120",
                lecturer = "Mathematics Dept",
                times = "01:00 PM - 01:55 PM",
                location = "A21",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture ST118",
                courseCode = "ST118",
                lecturer = "Mathematics Dept",
                times = "05:00 PM - 06:55 PM",
                location = "THEATER1",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Seminar FN101",
                courseCode = "FN101",
                lecturer = "Mathematics Dept",
                times = "07:00 PM - 07:55 PM",
                location = "HEALTH LR 1",
                dayOfWeek = "Tuesday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture MT180",
                courseCode = "MT180",
                lecturer = "Mathematics Dept",
                times = "08:00 AM - 09:55 AM",
                location = "MB",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture MT120",
                courseCode = "MT120",
                lecturer = "Mathematics Dept",
                times = "11:00 AM - 11:55 AM",
                location = "A21",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture FN101",
                courseCode = "FN101",
                lecturer = "Mathematics Dept",
                times = "12:00 PM - 12:55 PM",
                location = "COAF LR8",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture ST114",
                courseCode = "ST114",
                lecturer = "Mathematics Dept",
                times = "03:00 PM - 03:55 PM",
                location = "COAF LR4",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture ST114",
                courseCode = "ST114",
                lecturer = "Mathematics Dept",
                times = "04:00 PM - 04:55 PM",
                location = "COAF LR5",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Seminar FN101",
                courseCode = "FN101",
                lecturer = "Mathematics Dept",
                times = "04:00 PM - 04:55 PM",
                location = "YOMBO3",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Seminar",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture ST114",
                courseCode = "ST114",
                lecturer = "Mathematics Dept",
                times = "05:00 PM - 05:55 PM",
                location = "COAF LR5",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            ),
            RoutineEntity(
                courseName = "Lecture ST114",
                courseCode = "ST114",
                lecturer = "Mathematics Dept",
                times = "06:00 PM - 06:55 PM",
                location = "COAF LR7",
                dayOfWeek = "Wednesday",
                isOfficial = true,
                classType = "Lecture",
                program = "UD089",
                year = 1
            )
        )
        for (item in mathRoutines) {
            val alreadyExists = existingRoutines.any { 
                it.courseCode == item.courseCode && 
                it.dayOfWeek == item.dayOfWeek && 
                it.times == item.times && 
                it.program == item.program && 
                it.year == item.year 
            }
            if (!alreadyExists) {
                insertRoutine(item)
            }
        }
    }

    // --- Prepopulate Initial Seed Data ---
    suspend fun prepopulateSeedDataIfEmpty() {
        val materialsList = dao.getAllStudyMaterials().firstOrNull() ?: emptyList()
        if (materialsList.isEmpty()) {
            val initialMaterials = listOf(
                StudyMaterialEntity(
                    title = "C++ Core Pointers Cheat Sheet",
                    description = "A condensed master guide to dynamic memory allocation containing stack vs heap representations, double pointers, reference tables, and smart pointer syntax.",
                    programme = "Computer Science",
                    year = "Year 1",
                    semester = "Semester 1",
                    courseCode = "CS-101",
                    fileName = "CPP_Pointers_MasterRef.pdf",
                    fileType = "pdf",
                    uploadedByName = "Abbas G.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Abbas",
                    sizeText = "2.4 MB"
                ),
                StudyMaterialEntity(
                    title = "Database Normalization Exercises",
                    description = "Step-by-step resolution of database tables from 1NF to BCNF with transition diagrams, dependencies, primary keys, and index configurations.",
                    programme = "Computer Science",
                    year = "Year 2",
                    semester = "Semester 1",
                    courseCode = "CS-204",
                    fileName = "DBMS_1NF_BCNF_Solved.pdf",
                    fileType = "pdf",
                    uploadedByName = "Sarah K.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Sarah",
                    sizeText = "1.8 MB"
                ),
                StudyMaterialEntity(
                    title = "Calculus II Integral Theorems",
                    description = "Summary list of integration formulas, trigonometry substitutions, and surface area integrations for coordinate systems in 3D.",
                    programme = "Engineering",
                    year = "Year 1",
                    semester = "Semester 2",
                    courseCode = "MTH-102",
                    fileName = "Calc2_CheatSheet.docx",
                    fileType = "docx",
                    uploadedByName = "Mussa J.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Mussa",
                    sizeText = "850 KB"
                ),
                StudyMaterialEntity(
                    title = "Introduction to Computer Architecture",
                    description = "Slides and comprehensive summaries detailing CPU pipeline configurations, hazards, ALU operations, and microprogramming routines.",
                    programme = "Computer Science",
                    year = "Year 3",
                    semester = "Semester 1",
                    courseCode = "CS-301",
                    fileName = "CompArch_FullNotes.pdf",
                    fileType = "pdf",
                    uploadedByName = "Grace O.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Grace",
                    sizeText = "4.2 MB"
                ),
                StudyMaterialEntity(
                    title = "UDBS Financial Accounting Summary (Mambo ya Kudesa)",
                    description = "Quick-revision cheat sheet covering balance sheets, double-entry bookkeeping ledgers, and cash flow formulas. Ideal for business administration tests.",
                    programme = "Business Admin",
                    year = "Year 1",
                    semester = "Semester 1",
                    courseCode = "ACC-101",
                    fileName = "Accounting_Kudesa_Notes.pdf",
                    fileType = "pdf",
                    uploadedByName = "Hassan M.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Hassan",
                    sizeText = "1.1 MB"
                ),
                StudyMaterialEntity(
                    title = "Data Structures & Algorithms Solved Exam Papers",
                    description = "Step-by-step solved examination questions on BST insertion, AVL rotations, Dijkstra's algorithm, and space/time complexity tables.",
                    programme = "Computer Science",
                    year = "Year 2",
                    semester = "Semester 2",
                    courseCode = "CS-201",
                    fileName = "DSA_Solved_Past_Papers.pdf",
                    fileType = "pdf",
                    uploadedByName = "Japhet M.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Japhet",
                    sizeText = "3.5 MB"
                ),
                StudyMaterialEntity(
                    title = "Anatomy Brain Stem Quick-Recall Cards",
                    description = "Detailed cranial nerves routing diagrams, functional summaries of midbrain/pons/medulla, and quick mnemonic tables for OSCE preparations.",
                    programme = "Medicine",
                    year = "Year 2",
                    semester = "Semester 1",
                    courseCode = "MED-202",
                    fileName = "Anatomy_BrainStem_Shortcuts.docx",
                    fileType = "docx",
                    uploadedByName = "Amani S.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Amani",
                    sizeText = "980 KB"
                ),
                StudyMaterialEntity(
                    title = "Linear Algebra Swahili Lecture Companion",
                    description = "Comprehensive translations and explanations of vector spaces, transformation matrices, and eigenvalues/eigenvectors in simple Kiswahili.",
                    programme = "Engineering",
                    year = "Year 1",
                    semester = "Semester 1",
                    courseCode = "MTH-101",
                    fileName = "Linear_Algebra_Swahili_Helper.pdf",
                    fileType = "pdf",
                    uploadedByName = "Mussa J.",
                    uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Mussa",
                    sizeText = "1.5 MB"
                )
            )
            for (material in initialMaterials) {
                insertStudyMaterial(material)
            }
        }

        val listings = dao.getAllListings().firstOrNull() ?: emptyList()
        if (listings.isEmpty()) {
            val initialListings = listOf(
                MarketplaceListingEntity(
                    id = 1,
                    sellerId = "kevin_s",
                    sellerName = "Kevin Shayo",
                    sellerPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Kevin",
                    title = "Scientific Calculator Casio fx-991EX",
                    description = "Perfect condition. Clean and neat, solar-powered, ideal for engineering subjects (Calculus, Stats). Box and manual included.",
                    price = 15.0,
                    category = "Electronics",
                    status = "available",
                    condition = "Like New",
                    imageSeed = "Casiofx991"
                ),
                MarketplaceListingEntity(
                    id = 2,
                    sellerId = "aisha_m",
                    sellerName = "Aisha Mohammed",
                    sellerPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Aisha",
                    title = "Wooden Study Desk / Laptop Table",
                    description = "Study desk with adjustable cup holder and custom cable channels. Lightweight and compact, easy to slide beside bed. Minor scratches on side.",
                    price = 25.0,
                    category = "Books & Furniture",
                    status = "available",
                    condition = "Good",
                    imageSeed = "LaptopDesk"
                ),
                MarketplaceListingEntity(
                    id = 3,
                    sellerId = "japhet_moderator",
                    sellerName = "Japhet Mathias",
                    sellerPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Japhet&eyebrows=flatNatural&mouth=smile&top=shortCurly",
                    title = "Android Mobile Coding BootCamp (Java/Kotlin)",
                    description = "One-on-one custom tutoring lectures. From fundamentals to writing complete apps with Clean Architecture. I'll reviews your project files directly, debug build logs, and setup custom environments.",
                    price = 5.0,
                    category = "Services & Tutoring",
                    status = "available",
                    condition = "Specialty Service",
                    imageSeed = "KotlinBootCamp"
                )
            )

            for (listing in initialListings) {
                insertListing(listing)
            }

            val comments = listOf(
                CommentEntity(
                    id = 1,
                    listingId = 1,
                    userId = "aisha_m",
                    userName = "Aisha Mohammed",
                    userPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Aisha",
                    text = "Is the Casio back-light functional in low light conditions?",
                    parentId = null
                ),
                CommentEntity(
                    id = 2,
                    listingId = 1,
                    userId = "kevin_s",
                    userName = "Kevin Shayo",
                    userPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Kevin",
                    text = "Yes, light is fully functional! I replaced the internal lithium backup battery last week.",
                    parentId = 1
                ),
                CommentEntity(
                    id = 3,
                    listingId = 1,
                    userId = "baraka_b",
                    userName = "Baraka Benson",
                    userPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Baraka",
                    text = "Lao, can you drop it by the Computer Lab on Friday?",
                    parentId = null
                ),
                CommentEntity(
                    id = 4,
                    listingId = 1,
                    userId = "kevin_s",
                    userName = "Kevin Shayo",
                    userPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Kevin",
                    text = "Sure, I have lectures in Lab 3 at 11:00 AM on Friday. We can meet right after that!",
                    parentId = 3
                ),
                CommentEntity(
                    id = 5,
                    listingId = 3,
                    userId = "kevin_s",
                    userName = "Kevin Shayo",
                    userPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Kevin",
                    text = "Bro, this program literally saved my grades last year. Japhet is indeed the real deal! Highly recommended.",
                    parentId = null
                ),
                CommentEntity(
                    id = 6,
                    listingId = 3,
                    userId = "japhet_moderator",
                    userName = "Japhet Mathias",
                    userPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Japhet&eyebrows=flatNatural&mouth=smile&top=shortCurly",
                    text = "Thanks Kevin! Let's get it. Standard schedule is flexible to fit the assignment planner deadlines.",
                    parentId = 5
                )
            )

            for (comment in comments) {
                insertComment(comment)
            }
        }

        val rooms = dao.getAllChatRooms().firstOrNull() ?: emptyList()
        if (rooms.isEmpty()) {
            val roomId = dao.insertChatRoom(
                ChatRoomEntity(
                    participantId = "japhet_moderator",
                    participantName = "Ask Me (Jay)",
                    participantPhoto = "https://api.dicebear.com/7.x/bottts/svg?seed=Jay&size=100&eyes=bulging&mouth=smile",
                    lastMessage = "Hey! Let me help you prep for your academic journey today.",
                    unreadCount = 1
                )
            ).toInt()

            insertMessage(
                MessageEntity(
                    chatRoomId = roomId,
                    senderId = "japhet_moderator",
                    text = "Hey student! Ready to crush your class schedule today? Let me help you coordinate reminders or find materials in Maktaba. Feel free to ask me anything!",
                )
            )
        }

        val routinesList = dao.getAllRoutines().first()
        if (routinesList.isEmpty()) {
            simulateScraperSync()
        } else if (routinesList.none { it.courseCode == "ST324" } || routinesList.none { it.courseCode == "MT147" }) {
            seedMathematicsAndStatisticsRoutines()
        }
    }
}
