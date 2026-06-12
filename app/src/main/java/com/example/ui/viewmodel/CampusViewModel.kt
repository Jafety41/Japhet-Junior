package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("smart_campus_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getDatabase(application)
    val repository = DataRepository(database.campusDao())

    // --- State variables ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _onboardingStep = MutableStateFlow(0)
    val onboardingStep: StateFlow<Int> = _onboardingStep.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    fun clearAuthError() {
        _authError.value = null
    }

    private val _isDarkTheme = MutableStateFlow(sharedPrefs.getBoolean("is_dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun toggleTheme() {
        val next = !_isDarkTheme.value
        _isDarkTheme.value = next
        sharedPrefs.edit().putBoolean("is_dark_theme", next).apply()
    }

    // --- Directory Navigation crumbs for Maktaba ya Kudesa ---
    private val _selectedProgramme = MutableStateFlow<String?>(null)
    val selectedProgramme: StateFlow<String?> = _selectedProgramme.asStateFlow()

    private val _selectedYear = MutableStateFlow<String?>(null)
    val selectedYear: StateFlow<String?> = _selectedYear.asStateFlow()

    private val _selectedSemester = MutableStateFlow<String?>(null)
    val selectedSemester: StateFlow<String?> = _selectedSemester.asStateFlow()

    private val _selectedCourseCode = MutableStateFlow<String?>(null)
    val selectedCourseCode: StateFlow<String?> = _selectedCourseCode.asStateFlow()

    // --- Maktaba Search & Categorization states ---
    private val _maktabaSearch = MutableStateFlow("")
    val maktabaSearch: StateFlow<String> = _maktabaSearch.asStateFlow()

    private val _selectedMaktabaCategory = MutableStateFlow("All")
    val selectedMaktabaCategory: StateFlow<String> = _selectedMaktabaCategory.asStateFlow()

    // --- Study Material Viewer Modal ---
    private val _viewingMaterial = MutableStateFlow<StudyMaterialEntity?>(null)
    val viewingMaterial: StateFlow<StudyMaterialEntity?> = _viewingMaterial.asStateFlow()

    // --- Active Selected Listing for Detail view and Public Discussions Q&A ---
    private val _activeListingId = MutableStateFlow<Int?>(null)
    val activeListingId: StateFlow<Int?> = _activeListingId.asStateFlow()

    // --- Collapsed/Expanded Comment Accordion State (stores parentCommentId to Boolean) ---
    private val _commentAccordionState = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val commentAccordionState: StateFlow<Map<Int, Boolean>> = _commentAccordionState.asStateFlow()

    // --- Active Chat Room ---
    private val _activeChatRoomId = MutableStateFlow<Int?>(null)
    val activeChatRoomId: StateFlow<Int?> = _activeChatRoomId.asStateFlow()

    // --- Timetable filters ---
    private val _selectedTimetableDay = MutableStateFlow("Monday")
    val selectedTimetableDay: StateFlow<String> = _selectedTimetableDay.asStateFlow()

    // --- Search marketplace ---
    private val _marketplaceSearch = MutableStateFlow("")
    val marketplaceSearch: StateFlow<String> = _marketplaceSearch.asStateFlow()

    private val _selectedMarketplaceCategory = MutableStateFlow("All")
    val selectedMarketplaceCategory: StateFlow<String> = _selectedMarketplaceCategory.asStateFlow()

    // --- Flows from db ---
    val routines: StateFlow<List<RoutineEntity>> = _selectedTimetableDay
        .flatMapLatest { day -> repository.getRoutinesByDay(day) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRoutines: StateFlow<List<RoutineEntity>> = repository.getAllRoutines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allListings: StateFlow<List<MarketplaceListingEntity>> = repository.getAllListings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chatRooms: StateFlow<List<ChatRoomEntity>> = repository.getAllChatRooms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = _currentUser
        .filterNotNull()
        .flatMapLatest { user -> repository.getNotificationsForUser(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredListings: StateFlow<List<MarketplaceListingEntity>> = combine(
        allListings, _marketplaceSearch, _selectedMarketplaceCategory
    ) { listings, query, category ->
        listings.filter { listing ->
            if (listing.category == "Discussions") return@filter false
            val matchQuery = listing.title.contains(query, ignoreCase = true) ||
                    listing.description.contains(query, ignoreCase = true)
            val matchCategory = category == "All" || listing.category == category
            matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeListingComments: StateFlow<List<CommentEntity>> = _activeListingId
        .filterNotNull()
        .flatMapLatest { id -> repository.getCommentsForListing(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChatMessages: StateFlow<List<MessageEntity>> = _activeChatRoomId
        .filterNotNull()
        .flatMapLatest { id -> repository.getMessagesForRoom(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val studyMaterials: StateFlow<List<StudyMaterialEntity>> = combine(
        repository.getAllStudyMaterials(),
        _selectedProgramme,
        _selectedYear,
        _selectedSemester,
        _selectedCourseCode,
        _maktabaSearch,
        _selectedMaktabaCategory
    ) { array ->
        val list = array[0] as List<StudyMaterialEntity>
        val prog = array[1] as String?
        val yr = array[2] as String?
        val sem = array[3] as String?
        val course = array[4] as String?
        val query = array[5] as String
        val category = array[6] as String

        list.filter { m ->
            val matchProg = prog == null || m.programme.equals(prog, ignoreCase = true)
            val matchYr = yr == null || m.year.equals(yr, ignoreCase = true)
            val matchSem = sem == null || m.semester.equals(sem, ignoreCase = true)
            val matchCourse = course == null || m.courseCode.equals(course, ignoreCase = true)
            
            val matchQuery = query.isBlank() ||
                    m.title.contains(query, ignoreCase = true) ||
                    m.description.contains(query, ignoreCase = true) ||
                    m.courseCode.contains(query, ignoreCase = true) ||
                    m.fileName.contains(query, ignoreCase = true) ||
                    m.uploadedByName.contains(query, ignoreCase = true)

            val matchCategory = when (category) {
                "All" -> true
                "Notes" -> m.title.contains("Notes", ignoreCase = true) || m.description.contains("summary", ignoreCase = true) || m.description.contains("notes", ignoreCase = true)
                "Cheatsheets" -> m.title.contains("Cheat", ignoreCase = true) || m.title.contains("Sheet", ignoreCase = true) || m.description.contains("formula", ignoreCase = true) || m.description.contains("shortcut", ignoreCase = true)
                "Past Papers" -> m.title.contains("Paper", ignoreCase = true) || m.title.contains("Solved", ignoreCase = true) || m.title.contains("Exercise", ignoreCase = true)
                "PDF Folder" -> m.fileType.equals("pdf", ignoreCase = true)
                "Word Docs" -> m.fileType.equals("docx", ignoreCase = true) || m.fileType.equals("pptx", ignoreCase = true)
                else -> true
            }

            matchProg && matchYr && matchSem && matchCourse && matchQuery && matchCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Initialize programmatic Firebase config and start real-time sync with custom Firestore database
        FirebaseManager.initialize(application)
        repository.startRealtimeSync()

        // Force reset or migrate existing preference to light mode for the user's current session
        if (!sharedPrefs.contains("theme_migrated_to_light_v1")) {
            sharedPrefs.edit()
                .putBoolean("is_dark_theme", false)
                .putBoolean("theme_migrated_to_light_v1", true)
                .apply()
            _isDarkTheme.value = false
        }
        viewModelScope.launch {
            repository.prepopulateSeedDataIfEmpty()
            checkUserLoggedIn()
        }
    }

    private suspend fun checkUserLoggedIn() {
        val firebaseUser = FirebaseManager.auth.currentUser
        if (firebaseUser != null) {
            val uid = firebaseUser.uid
            val user = database.campusDao().getUser(uid).first()
            if (user != null) {
                _currentUser.value = user
                _onboardingStep.value = if (user.onboardingCompleted) 0 else 1
            } else {
                try {
                    FirebaseManager.firestore.collection("users").document(uid).get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful && task.result?.exists() == true) {
                                val doc = task.result
                                val fetchedUser = UserEntity(
                                    id = uid,
                                    displayName = doc?.getString("displayName") ?: "Student",
                                    username = doc?.getString("username") ?: "@student",
                                    email = doc?.getString("email") ?: firebaseUser.email ?: "",
                                    photoURL = doc?.getString("photoURL") ?: "https://api.dicebear.com/7.x/avataaars/svg?seed=$uid",
                                    university = doc?.getString("university") ?: "University of Science & Tech",
                                    onboardingCompleted = doc?.getBoolean("onboardingCompleted") ?: true,
                                    joinedAt = doc?.getLong("joinedAt") ?: System.currentTimeMillis()
                                )
                                viewModelScope.launch {
                                    repository.insertUser(fetchedUser)
                                    _currentUser.value = fetchedUser
                                    if (fetchedUser.onboardingCompleted) {
                                        sharedPrefs.edit()
                                            .putBoolean("onboarding_done", true)
                                            .putString("logged_in_user_id", uid)
                                            .putString("user_display_name", fetchedUser.displayName)
                                            .putString("user_username", fetchedUser.username)
                                            .apply()
                                        _onboardingStep.value = 0
                                    } else {
                                        _onboardingStep.value = 1
                                    }
                                }
                            }
                        }
                } catch (e: Exception) {
                    android.util.Log.e("CampusViewModel", "Failed to check and sync current user", e)
                }
            }
        } else {
            // Dual-guard local items checking if completed onboarding and user set
            val onboardingDone = sharedPrefs.getBoolean("onboarding_done", false)
            val savedUserId = sharedPrefs.getString("logged_in_user_id", null)

            if (onboardingDone && savedUserId != null) {
                val user = database.campusDao().getUser(savedUserId).first()
                if (user != null) {
                    _currentUser.value = user
                } else {
                    // Recover or create a default user profile in room matching stored values
                    val defaultUser = UserEntity(
                        id = savedUserId,
                        displayName = sharedPrefs.getString("user_display_name", "Student") ?: "Student",
                        username = sharedPrefs.getString("user_username", "@student") ?: "@student",
                        email = "student@smartcampus.edu",
                        photoURL = "https://api.dicebear.com/7.x/avataaars/svg?seed=MainUser",
                        university = "University of Science & Tech",
                        onboardingCompleted = true
                    )
                    viewModelScope.launch {
                        repository.insertUser(defaultUser)
                        _currentUser.value = defaultUser
                    }
                }
            }
        }
    }

    // --- Onboarding / Authentication Actions ---
    fun submitLogin(displayName: String, usernameInput: String) {
        // Fallback for legacy calls
        val cleanUsername = if (usernameInput.startsWith("@")) usernameInput else "@$usernameInput"
        val userId = displayName.lowercase().replace(" ", "_") + "_" + (1000..9999).random()

        viewModelScope.launch {
            val newUser = UserEntity(
                id = userId,
                displayName = displayName,
                username = cleanUsername,
                email = "$userId@smartcampus.edu",
                photoURL = "https://api.dicebear.com/7.x/avataaars/svg?seed=$userId",
                university = "University of Science & Tech",
                onboardingCompleted = false
            )
            repository.insertUser(newUser)
            _currentUser.value = newUser
            _onboardingStep.value = 1
        }
    }

    fun handleSignUp(emailStr: String, passwordStr: String, dName: String, uName: String) {
        if (emailStr.isBlank() || passwordStr.isBlank() || dName.isBlank() || uName.isBlank()) {
            _authError.value = "Tafadhali jaza sifa zote kikamilifu."
            return
        }
        _authLoading.value = true
        _authError.value = null
        
        val cleanUsername = if (uName.startsWith("@")) uName else "@$uName"

        FirebaseManager.auth.createUserWithEmailAndPassword(emailStr.trim(), passwordStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    val userId = firebaseUser?.uid ?: (dName.lowercase().replace(" ", "_") + "_" + (1000..9999).random())
                    
                    viewModelScope.launch {
                        val newUser = UserEntity(
                            id = userId,
                            displayName = dName,
                            username = cleanUsername,
                            email = emailStr.trim(),
                            photoURL = "https://api.dicebear.com/7.x/avataaars/svg?seed=$userId",
                            university = "University of Science & Tech",
                            onboardingCompleted = false
                        )
                        repository.insertUser(newUser)
                        _currentUser.value = newUser
                        _authLoading.value = false
                        _onboardingStep.value = 1
                    }
                } else {
                    _authLoading.value = false
                    _authError.value = task.exception?.localizedMessage ?: "Usajili umeshindwa. Tafadhali jaribu tena."
                }
            }
    }

    fun handleSignIn(emailStr: String, passwordStr: String) {
        if (emailStr.isBlank() || passwordStr.isBlank()) {
            _authError.value = "Tafadhali jaza barua pepe na nenosiri."
            return
        }
        _authLoading.value = true
        _authError.value = null

        FirebaseManager.auth.signInWithEmailAndPassword(emailStr.trim(), passwordStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    val uid = firebaseUser?.uid
                    if (uid != null) {
                        FirebaseManager.firestore.collection("users").document(uid).get()
                            .addOnCompleteListener { docTask ->
                                _authLoading.value = false
                                if (docTask.isSuccessful && docTask.result?.exists() == true) {
                                    val doc = docTask.result
                                    val existingUser = UserEntity(
                                        id = uid,
                                        displayName = doc?.getString("displayName") ?: "Student",
                                        username = doc?.getString("username") ?: "@student",
                                        email = doc?.getString("email") ?: emailStr.trim(),
                                        photoURL = doc?.getString("photoURL") ?: "https://api.dicebear.com/7.x/avataaars/svg?seed=$uid",
                                        university = doc?.getString("university") ?: "University of Science & Tech",
                                        onboardingCompleted = doc?.getBoolean("onboardingCompleted") ?: true,
                                        joinedAt = doc?.getLong("joinedAt") ?: System.currentTimeMillis()
                                    )
                                    viewModelScope.launch {
                                        repository.insertUser(existingUser)
                                        _currentUser.value = existingUser
                                        
                                        if (existingUser.onboardingCompleted) {
                                            sharedPrefs.edit()
                                                .putBoolean("onboarding_done", true)
                                                .putString("logged_in_user_id", uid)
                                                .putString("user_display_name", existingUser.displayName)
                                                .putString("user_username", existingUser.username)
                                                .apply()
                                            _onboardingStep.value = 0
                                        } else {
                                            _onboardingStep.value = 1
                                        }
                                    }
                                } else {
                                    // Signed in via Auth but no record in Firestore yet.
                                    val defaultUser = UserEntity(
                                        id = uid,
                                        displayName = firebaseUser.displayName ?: "New Alumnus",
                                        username = "@" + (firebaseUser.email?.substringBefore("@") ?: "alumni"),
                                        email = emailStr.trim(),
                                        photoURL = "https://api.dicebear.com/7.x/avataaars/svg?seed=$uid",
                                        university = "University of Science & Tech",
                                        onboardingCompleted = false
                                    )
                                    viewModelScope.launch {
                                        repository.insertUser(defaultUser)
                                        _currentUser.value = defaultUser
                                        _onboardingStep.value = 1
                                    }
                                }
                            }
                    } else {
                        _authLoading.value = false
                        _authError.value = "Uthibitishaji haujakamilika."
                    }
                } else {
                    _authLoading.value = false
                    _authError.value = task.exception?.localizedMessage ?: "Kuingia kumeshindwa. Angalia barua pepe na nenosiri lako."
                }
            }
    }

    fun proceedOnboardingStep() {
        val currentStep = _onboardingStep.value
        if (currentStep < 2) {
            _onboardingStep.value = currentStep + 1
        } else {
            // Completed onboarding
            _currentUser.value?.let { user ->
                viewModelScope.launch {
                    val updatedUser = user.copy(onboardingCompleted = true)
                    repository.insertUser(updatedUser)
                    _currentUser.value = updatedUser

                    // Dual Guard Persistence
                    sharedPrefs.edit()
                        .putBoolean("onboarding_done", true)
                        .putString("logged_in_user_id", user.id)
                        .putString("user_display_name", user.displayName)
                        .putString("user_username", user.username)
                        .apply()

                    // Add welcome notification
                    repository.insertNotification(
                        NotificationEntity(
                            userId = user.id,
                            title = "Karibu Smart Campus!",
                            message = "Onboarding completed successfully. Check details & enjoy academic resources, timetables and discussions.",
                            type = "system"
                        )
                    )
                }
            }
        }
    }

    fun completeOnboardingDirectly() {
        _currentUser.value?.let { user ->
            viewModelScope.launch {
                val updatedUser = user.copy(onboardingCompleted = true)
                repository.insertUser(updatedUser)
                _currentUser.value = updatedUser
                sharedPrefs.edit().putBoolean("onboarding_done", true).apply()
            }
        }
    }

    fun logout() {
        try {
            FirebaseManager.auth.signOut()
        } catch (e: Exception) {
            android.util.Log.e("CampusViewModel", "Failed to sign out of Firebase", e)
        }
        val currentUserId = _currentUser.value?.id
        sharedPrefs.edit()
            .putBoolean("onboarding_done", false)
            .putString("logged_in_user_id", null)
            .apply()
        _currentUser.value = null
        _onboardingStep.value = 0
    }

    // --- Study Materials Directory Navigation ---
    fun selectProgramme(prog: String?) {
        _selectedProgramme.value = prog
        _selectedYear.value = null
        _selectedSemester.value = null
        _selectedCourseCode.value = null
    }

    fun selectYear(yr: String?) {
        _selectedYear.value = yr
        _selectedSemester.value = null
        _selectedCourseCode.value = null
    }

    fun selectSemester(sem: String?) {
        _selectedSemester.value = sem
        _selectedCourseCode.value = null
    }

    fun selectCourseCode(course: String?) {
        _selectedCourseCode.value = course
    }

    fun setMaktabaSearch(query: String) {
        _maktabaSearch.value = query
    }

    fun selectMaktabaCategory(category: String) {
        _selectedMaktabaCategory.value = category
    }

    fun openMaterialViewer(material: StudyMaterialEntity?) {
        _viewingMaterial.value = material
    }

    fun uploadStudyMaterial(
        title: String,
        description: String,
        programme: String,
        year: String,
        semester: String,
        courseCode: String,
        fileName: String,
        fileType: String,
        sizeText: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val material = StudyMaterialEntity(
                title = title,
                description = description,
                programme = programme,
                year = year,
                semester = semester,
                courseCode = courseCode,
                fileName = fileName,
                fileType = fileType,
                uploadedByName = user.displayName,
                uploadedByPhoto = user.photoURL,
                sizeText = sizeText,
                fileId = "appwrite_file_" + (10000..99999).random() // Mock native Appwrite storage reference
            )
            repository.insertStudyMaterial(material)

            // Trigger notification
            repository.insertNotification(
                NotificationEntity(
                    userId = user.id,
                    title = "File Uploaded Successfully",
                    message = "Material '$title' added to Maktaba ya Kudesa under $courseCode.",
                    type = "system"
                )
            )
        }
    }

    // --- Timetable & Alarms ---
    fun selectTimetableDay(day: String) {
        _selectedTimetableDay.value = day
    }

    fun toggleReminder(routineId: Int, isEnabled: Boolean) {
        viewModelScope.launch {
            // Find in database and update
            val all = repository.getAllRoutines().first()
            val match = all.find { it.id == routineId }
            if (match != null) {
                val updated = match.copy(remindersEnabled = isEnabled)
                repository.insertRoutine(updated)

                val user = _currentUser.value ?: return@launch
                val notifyMessage = if (isEnabled) {
                    "Local notification alarm set for class '${match.courseName}' which runs at ${match.times}."
                } else {
                    "Alarm cancelled for class '${match.courseName}'."
                }
                // Add notification simulating Native Local Notification Alarms
                repository.insertNotification(
                    NotificationEntity(
                        userId = user.id,
                        title = if (isEnabled) "Alarm Set" else "Alarm Cancelled",
                        message = notifyMessage,
                        type = "class"
                    )
                )
            }
        }
    }

    fun forceSyncScraper() {
        viewModelScope.launch {
            repository.clearAllRoutines()
            repository.simulateScraperSync()
            val user = _currentUser.value ?: return@launch
            repository.insertNotification(
                NotificationEntity(
                    userId = user.id,
                    title = "Scraper Sync Complete",
                    message = "Official university scraper proxy fetched 6 class schedule routines and updated local database cache.",
                    type = "class"
                )
            )
        }
    }

    fun addNewManualClass(
        courseName: String,
        courseCode: String,
        lecturer: String,
        times: String,
        location: String,
        dayOfWeek: String,
        classType: String
    ) {
        viewModelScope.launch {
            val newClass = RoutineEntity(
                courseName = courseName,
                courseCode = courseCode,
                lecturer = lecturer,
                times = times,
                location = location,
                dayOfWeek = dayOfWeek,
                isOfficial = false,
                classType = classType
            )
            repository.insertRoutine(newClass)
        }
    }

    fun deleteClass(routineId: Int) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
        }
    }

    // --- Marketplace Listings ---
    fun setSearchQuery(query: String) {
        _marketplaceSearch.value = query
    }

    fun selectMarketplaceCategory(cat: String) {
        _selectedMarketplaceCategory.value = cat
    }

    fun publishListing(
        title: String,
        description: String,
        price: Double,
        category: String,
        condition: String,
        imageSeed: String
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val newListing = MarketplaceListingEntity(
                sellerId = user.id,
                sellerName = user.displayName,
                sellerPhoto = user.photoURL,
                title = title,
                description = description,
                price = price,
                category = category,
                status = "available",
                condition = condition,
                imageSeed = imageSeed
            )
            repository.insertListing(newListing)
            
            // Notification
            repository.insertNotification(
                NotificationEntity(
                    userId = user.id,
                    title = "Item Published!",
                    message = "Your marketplace listing '$title' is now live for all students.",
                    type = "market"
                )
            )
        }
    }

    fun deleteListingItem(listingId: Int) {
        viewModelScope.launch {
            repository.deleteListing(listingId)
            // also delete comments
            val comments = activeListingComments.value
            for (c in comments) {
                repository.deleteComment(c.id)
            }
        }
    }

    fun markListingAsSold(listing: MarketplaceListingEntity) {
        viewModelScope.launch {
            val updated = listing.copy(status = "sold")
            repository.updateListing(updated)
        }
    }

    fun selectActiveListing(listingId: Int?) {
        _activeListingId.value = listingId
        _commentAccordionState.value = emptyMap() // Clear states
    }

    // --- Comments Q&A Nested Discussion Accordion ---
    fun toggleCommentAccordion(parentId: Int) {
        val currentMap = _commentAccordionState.value
        val isExpanded = currentMap[parentId] ?: false
        _commentAccordionState.value = currentMap.toMutableMap().apply {
            put(parentId, !isExpanded)
        }
    }

    fun addListingComment(text: String, parentId: Int? = null) {
        val listingId = _activeListingId.value ?: return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val comment = CommentEntity(
                listingId = listingId,
                userId = user.id,
                userName = user.displayName,
                userPhoto = user.photoURL,
                text = text,
                parentId = parentId
            )
            repository.insertComment(comment)

            // If it's a nested reply, expand the accordion automatically
            if (parentId != null) {
                _commentAccordionState.value = _commentAccordionState.value.toMutableMap().apply {
                    put(parentId, true)
                }
            }

            // If there's a seller, notify them later or simulate interaction (could notify listing owner)
            val listingObj = repository.getListingById(listingId).firstOrNull()
            if (listingObj != null && listingObj.sellerId != user.id) {
                repository.insertNotification(
                    NotificationEntity(
                        userId = listingObj.sellerId,
                        title = "New comment on your listing",
                        message = "${user.displayName} commented on '${listingObj.title}'",
                        type = "market"
                    )
                )
            }
        }
    }

    fun deleteListingComment(commentId: Int) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
        }
    }

    fun reportComment(commentId: Int) {
        viewModelScope.launch {
            repository.reportComment(commentId)
        }
    }

    // --- Private Messenger ---
    fun openChatWithUser(participantId: String, name: String, photo: String) {
        viewModelScope.launch {
            val roomId = repository.getOrCreateChatRoom(participantId, name, photo)
            _activeChatRoomId.value = roomId
            repository.clearUnreadCount(roomId)
        }
    }

    fun selectActiveChatRoom(roomId: Int?) {
        _activeChatRoomId.value = roomId
        if (roomId != null) {
            viewModelScope.launch {
                repository.clearUnreadCount(roomId)
            }
        }
    }

    fun sendChatMessage(text: String) {
        val roomId = _activeChatRoomId.value ?: return
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val newMessage = MessageEntity(
                chatRoomId = roomId,
                senderId = user.id,
                text = text
            )
            repository.insertMessage(newMessage)
            repository.updateChatRoomLastMessage(roomId, text, System.currentTimeMillis())

            // Get target recipient from chatRoom
            val chatRoomsList = repository.getAllChatRooms().first()
            val matchedRoom = chatRoomsList.find { it.id == roomId }
            if (matchedRoom != null) {
                // If chatting with JAPHET Mathias (MODERATOR), simulate a prompt response immediately!
                if (matchedRoom.participantId == "japhet_moderator") {
                    simulateModeratorResponse(roomId, text)
                } else {
                    // Simulate general response after a short delay
                    simulateGeneralResponse(roomId, matchedRoom.participantName, text)
                }
            }
        }
    }

    private fun simulateModeratorResponse(roomId: Int, userText: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1200) // nice dynamic latency
            val randomReplies = listOf(
                "Mambo vipi raw! Keep pushing standard files to Maktaba and listings up in Campus. If you see spam, report it immediately! 🛡️",
                "I review build issues. That looks neat, keep checking routines and assignments. Uni is chaotic but we win! 🚀",
                "Yes @username! Setting reminders triggers actual native local alarms. Let's make sure you never miss that OOP lecture.",
                "Safi kabisa! Marketplace is completely free to list books, chairs, calculators. Good luck with the trade!",
                "Great question. Let's do some study session review in CS-204 this evening. See you in Laboratory room 3!",
                "Got your back, fam! Ask me anything regarding courses or materials. Let's get it. 🔥",
                "Oya, mambo safi! Thanks for sharing study notes. Let me review that pointer PDF right away."
            )
            val replyText = randomReplies.random().replace("@username", _currentUser.value?.username ?: "@student")
            val autoReplyMsg = MessageEntity(
                chatRoomId = roomId,
                senderId = "japhet_moderator",
                text = replyText
            )
            repository.insertMessage(autoReplyMsg)
            repository.updateChatRoomLastMessage(roomId, replyText, System.currentTimeMillis())
        }
    }

    private fun simulateGeneralResponse(roomId: Int, sellerName: String, userText: String) {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1800)
            val replyText = "Yo! Thanks for reaching out. Yes, the item is still available. I'm usually near the Cafeteria or Main Library around 2:00 PM if you want to inspect it. Let me know what time works best for you."
            val replyMsg = MessageEntity(
                chatRoomId = roomId,
                senderId = "seller_bot",
                text = replyText
            )
            repository.insertMessage(replyMsg)
            repository.updateChatRoomLastMessage(roomId, replyText, System.currentTimeMillis())
        }
    }

    fun markNotificationsAsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val list = notifications.value
            for (n in list) {
                if (!n.read) {
                    repository.markNotificationAsRead(n.id)
                }
            }
        }
    }
}
