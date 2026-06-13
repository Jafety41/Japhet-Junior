package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class CampusViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("smart_campus_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getDatabase(application)
    val repository = DataRepository(database.campusDao())

    // --- State variables ---
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // --- Persisted Academic Affiliation settings ---
    private val _userProgram = MutableStateFlow(sharedPrefs.getString("user_program", "UD089") ?: "UD089")
    val userProgram: StateFlow<String> = _userProgram.asStateFlow()

    private val _userYear = MutableStateFlow(sharedPrefs.getString("user_year", "Year 3") ?: "Year 3")
    val userYear: StateFlow<String> = _userYear.asStateFlow()

    fun updateProfileSettings(program: String, year: String) {
        _userProgram.value = program
        _userYear.value = year
        sharedPrefs.edit()
            .putString("user_program", program)
            .putString("user_year", year)
            .apply()
    }

    // --- Direct course filtering state ---
    private val _activeCourseFilter = MutableStateFlow<String?>(null)
    val activeCourseFilter: StateFlow<String?> = _activeCourseFilter.asStateFlow()

    fun setActiveCourseFilter(courseCode: String?) {
        _activeCourseFilter.value = courseCode
    }

    // --- Tab Navigation request bus ---
    private val _tabNavigationRequest = MutableSharedFlow<com.example.ui.screens.AppTab>()
    val tabNavigationRequest = _tabNavigationRequest.asSharedFlow()

    fun requestTabNavigation(tab: com.example.ui.screens.AppTab) {
        viewModelScope.launch {
            _tabNavigationRequest.emit(tab)
        }
    }

    // --- Jay Assistant typing state ---
    private val _isJayTyping = MutableStateFlow(false)
    val isJayTyping: StateFlow<Boolean> = _isJayTyping.asStateFlow()

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
    private val _selectedTimetableDay = MutableStateFlow(
        run {
            val calendar = java.util.Calendar.getInstance()
            when (calendar.get(java.util.Calendar.DAY_OF_WEEK)) {
                java.util.Calendar.MONDAY -> "Monday"
                java.util.Calendar.TUESDAY -> "Tuesday"
                java.util.Calendar.WEDNESDAY -> "Wednesday"
                java.util.Calendar.THURSDAY -> "Thursday"
                java.util.Calendar.FRIDAY -> "Friday"
                java.util.Calendar.SATURDAY -> "Saturday"
                java.util.Calendar.SUNDAY -> "Sunday"
                else -> "Monday"
            }
        }
    )
    val selectedTimetableDay: StateFlow<String> = _selectedTimetableDay.asStateFlow()

    // --- Search marketplace ---
    private val _marketplaceSearch = MutableStateFlow("")
    val marketplaceSearch: StateFlow<String> = _marketplaceSearch.asStateFlow()

    private val _selectedMarketplaceCategory = MutableStateFlow("All")
    val selectedMarketplaceCategory: StateFlow<String> = _selectedMarketplaceCategory.asStateFlow()

    // --- Flows from db ---
    val routines: StateFlow<List<RoutineEntity>> = combine(
        _selectedTimetableDay,
        _userProgram,
        _userYear,
        repository.getAllRoutines()
    ) { day, program, yearStr, allList ->
        val yearInt = try {
            yearStr.replace("Year ", "").trim().toInt()
        } catch (e: Exception) {
            3
        }
        allList.filter { 
            it.dayOfWeek.equals(day, ignoreCase = true) &&
            (it.program == program || program == "ALL") &&
            (it.year == yearInt || yearStr == "ALL")
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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

    fun updateClassLocation(routineId: Int, newLocation: String) {
        viewModelScope.launch {
            repository.updateRoutineLocation(routineId, newLocation)
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
        imageSeed: String,
        imageUrls: String = ""
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
                imageSeed = imageSeed,
                imageUrls = imageUrls
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
                // If chatting with "Ask Me (Jay)", simulate a prompt-based or Gemini-based response immediately!
                if (matchedRoom.participantId == "japhet_moderator") {
                    queryJayAssistant(roomId, text)
                } else {
                    // Simulate general response after a short delay
                    simulateGeneralResponse(roomId, matchedRoom.participantName, text)
                }
            }
        }
    }

    private fun queryJayAssistant(roomId: Int, userText: String) {
        _isJayTyping.value = true
        viewModelScope.launch {
            try {
                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                
                val currentProg = userProgram.value
                val currentYr = userYear.value
                val userName = _currentUser.value?.displayName ?: "Student"

                val systemInstruction = """
                    You are 'Jay', a hyper-intelligent, proactive campus assistant. Your persona is brief, warm, and highly personalized.
                    Knowledge Base: You have full access to the user's timetable and marketplace activity.
                    The current user's name is $userName, they are registered in program $currentProg and academic year $currentYr.
                    Interaction Rules:
                    Proactive Assistance: Speak to the user using their details. Answer in 1-2 short sentences. Use a style similar to Meta AI on WhatsApp.
                    Deep Linking: ALWAYS include exactly one of the following clickable action tags in brackets at the end of your message if relevant to the request:
                    - [View Timetable] (if referencing classes, timings, schedules, or calendars)
                    - [Open Library] (if referencing study materials, slides, books, past exams, or notes)
                    - [Check Market] (if referencing selling, buying, products, or goods)
                    Tone: You are not a 'moderator'; you are a personal assistant. You know the user's program ($currentProg) and year level ($currentYr). Treat every conversation as a continuation of their academic journey. Keep replies strictly under 3 sentences.
                """.trimIndent().replace("\n", "\\n").replace("\"", "\\\"")

                if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
                    kotlinx.coroutines.delay(1200)
                    val replyText = generateSimulatedJayResponse(userText, userName, currentProg, currentYr)
                    val autoReplyMsg = MessageEntity(
                        chatRoomId = roomId,
                        senderId = "japhet_moderator",
                        text = replyText
                    )
                    repository.insertMessage(autoReplyMsg)
                    repository.updateChatRoomLastMessage(roomId, replyText, System.currentTimeMillis())
                    _isJayTyping.value = false
                    return@launch
                }

                val jsonBodyText = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {"text": "$userText"}
                          ]
                        }
                      ],
                      "systemInstruction": {
                        "parts": [
                          {"text": "$systemInstruction"}
                        ]
                      },
                      "generationConfig": {
                        "temperature": 0.5,
                        "maxOutputTokens": 120
                      }
                    }
                """.trimIndent()

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = okhttp3.RequestBody.Companion.create(mediaType, jsonBodyText)

                val request = okhttp3.Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            val resBody = response.body?.string() ?: ""
                            val parsedText = extractTextFromGeminiResponse(resBody)
                            val autoReplyMsg = MessageEntity(
                                chatRoomId = roomId,
                                senderId = "japhet_moderator",
                                text = parsedText
                            )
                            repository.insertMessage(autoReplyMsg)
                            repository.updateChatRoomLastMessage(roomId, parsedText, System.currentTimeMillis())
                        } else {
                            val replyText = generateSimulatedJayResponse(userText, userName, currentProg, currentYr)
                            val autoReplyMsg = MessageEntity(
                                chatRoomId = roomId,
                                senderId = "japhet_moderator",
                                text = replyText
                            )
                            repository.insertMessage(autoReplyMsg)
                            repository.updateChatRoomLastMessage(roomId, replyText, System.currentTimeMillis())
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CampusViewModel", "Gemini chat failed, fallback used", e)
                val replyText = "Hey! Let me double-check that with the campus server. Check back in a moment or try navigating. [View Timetable]"
                val autoReplyMsg = MessageEntity(
                    chatRoomId = roomId,
                    senderId = "japhet_moderator",
                    text = replyText
                )
                repository.insertMessage(autoReplyMsg)
                repository.updateChatRoomLastMessage(roomId, replyText, System.currentTimeMillis())
            } finally {
                _isJayTyping.value = false
            }
        }
    }

    private fun generateSimulatedJayResponse(userText: String, userName: String, program: String, year: String): String {
        val q = userText.lowercase()
        val firstName = userName.split(" ").firstOrNull() ?: "there"
        return when {
            q.contains("schedule") || q.contains("timetable") || q.contains("class") || q.contains("routine") || q.contains("saa") || q.contains("kipindi") || q.contains("ratiba") -> {
                "Hey $firstName! Ready to crush your $year $program schedule today? I see you have Tutorial MT360 later. Need help prep or checking room? [View Timetable]"
            }
            q.contains("library") || q.contains("book") || q.contains("notes") || q.contains("material") || q.contains("pdf") || q.contains("study") || q.contains("soma") || q.contains("kitabu") || q.contains("desa") -> {
                "Absolutely! We have complete courses and past notes updated for $program in the library. Go ahead and grab the study files! [Open Library]"
            }
            q.contains("market") || q.contains("soko") || q.contains("buy") || q.contains("sell") || q.contains("calculator") || q.contains("phone") || q.contains("price") -> {
                "Awesome! Check out our Campus Marketplace. You can find essential books, laptops, or even place your own listing in a few taps. [Check Market]"
            }
            else -> {
                "Hey $firstName! Ready to crush your $year $program schedule? Let me know how I can help you coordinate classes or study guides today! [View Timetable]"
            }
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

    // --- AI SCHEDULER (RATIBA YANGU) INTELLIGENCE LAYER ---
    private val _aiSchedulerResponse = MutableStateFlow<String?>(null)
    val aiSchedulerResponse: StateFlow<String?> = _aiSchedulerResponse.asStateFlow()

    private val _isAiSchedulerLoading = MutableStateFlow(false)
    val isAiSchedulerLoading: StateFlow<Boolean> = _isAiSchedulerLoading.asStateFlow()

    fun queryAiScheduler(program: String, year: String) {
        _isAiSchedulerLoading.value = true
        _aiSchedulerResponse.value = null
        viewModelScope.launch {
            try {
                val promptText = if (program.uppercase() != "UD089" || year == "Year 4" || year == "ALL") {
                    "The user selected '$program' for '$year'. We do not have this data yet. Respond with a polite 'Coming Soon' message in Kiswahili/English style."
                } else {
                    "The user selected program '$program' and year '$year'. Provide a polite academic summary or details of matching course routines from UD089."
                }

                val apiKey = com.example.BuildConfig.GEMINI_API_KEY
                if (apiKey.isBlank() || apiKey == "YOUR_GEMINI_API_KEY") {
                    kotlinx.coroutines.delay(1000)
                    if (program.uppercase() != "UD089" || year == "Year 4" || year == "ALL") {
                        _aiSchedulerResponse.value = "{\"status\": \"coming_soon\", \"message\": \"Ratiba ya $program ($year) haijafikiwa katika hifadhi yetu ya sasa. Coming soon!\"}"
                    } else {
                        _aiSchedulerResponse.value = "Kipindi chako cha UD089 ($year) kimeandaliwa vyema kwenye orodha!"
                    }
                    _isAiSchedulerLoading.value = false
                    return@launch
                }

                val systemInstruction = """
                    You are the Intelligence Engine for the 'Ratiba Yangu' application. Your source of truth is the provided JSON data for UD089.

                    Core Logic Rules:

                    Data Hierarchy: Always prioritize the selected Program (e.g., UD089) and Year (1-3).

                    Course Code Intelligence: You must automatically deduce the academic year from the course code. If a course code starts with '1' (e.g., MT1xx ,or ST1xx or FN1xx), it belongs to Year 1; '2' (MT2xx) to Year 2; '3' (MT3xx) to Year 3.

                    Search/Filter Behavior: When a user selects a Year or Program:

                    If the user selects a program/year where data exists, return the JSON routines filtered by the logic above.

                    If the user selects a program/year that has no data (e.g., 'Year 4' or 'CS (CompSci)'), respond with a structured JSON flag: {"status": "coming_soon", "message": "Timetable for this category is coming soon."}.

                    UI/UX Intelligence: When generating responses for the app UI, format the data into clear, actionable objects for the frontend (Title, Time, Location, Type, Color Tag).

                    Source of Truth: Never invent data. If a requested course is not in the JSON, state that it is not available in the current official timetable.
                """.trimIndent().replace("\n", "\\n").replace("\"", "\\\"")

                val jsonBodyText = """
                    {
                      "contents": [
                        {
                          "parts": [
                            {"text": "$promptText"}
                          ]
                        }
                      ],
                      "systemInstruction": {
                        "parts": [
                          {"text": "$systemInstruction"}
                        ]
                      },
                      "generationConfig": {
                        "temperature": 0.5
                      }
                    }
                """.trimIndent()

                val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                val requestBody = okhttp3.RequestBody.Companion.create(mediaType, jsonBodyText)

                val request = okhttp3.Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(requestBody)
                    .build()

                val client = okhttp3.OkHttpClient.Builder()
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build()

                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            val errBody = response.body?.string() ?: ""
                            android.util.Log.e("CampusViewModel", "Gemini HTTP error ${response.code}: $errBody")
                            _aiSchedulerResponse.value = "{\"status\": \"error\", \"message\": \"Imeshindwa kuunganisha akili ya bandia ya ratiba.\"}"
                        } else {
                            val resBody = response.body?.string() ?: ""
                            val parsedText = extractTextFromGeminiResponse(resBody)
                            _aiSchedulerResponse.value = parsedText
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("CampusViewModel", "Gemini query exception", e)
                _aiSchedulerResponse.value = "{\"status\": \"coming_soon\", \"message\": \"Ratiba ya $program ($year) haijafikiwa katika hifadhi yetu ya sasa. Coming soon!\"}"
            } finally {
                _isAiSchedulerLoading.value = false
            }
        }
    }

    private fun extractTextFromGeminiResponse(rawJson: String): String {
        try {
            val partsStart = rawJson.indexOf("\"text\":")
            if (partsStart != -1) {
                val startQuote = rawJson.indexOf("\"", partsStart + 7)
                if (startQuote != -1) {
                    var endQuote = startQuote + 1
                    var isEscaped = false
                    val length = rawJson.length
                    val sb = StringBuilder()
                    while (endQuote < length) {
                        val c = rawJson[endQuote]
                        if (isEscaped) {
                            sb.append(c)
                            isEscaped = false
                        } else if (c == '\\') {
                            isEscaped = true
                        } else if (c == '"') {
                            break
                        } else {
                            sb.append(c)
                        }
                        endQuote++
                    }
                    return sb.toString().replace("\\n", "\n").replace("\\t", "\t").trim()
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("CampusViewModel", "Error parsing gemini response manually", e)
        }
        return rawJson
    }
}
