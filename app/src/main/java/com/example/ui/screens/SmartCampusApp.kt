package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.*
import com.example.ui.viewmodel.CampusViewModel

// Central Theme Tokens
object CosmicTheme {
    var isDark: Boolean = true

    // Primary Brand Theme: Vibrant Orange
    val AccentTeal get() = Color(0xFFF97316)
    val NeonOrange get() = Color(0xFFF97316)
    val PrimaryAccent get() = Color(0xFFF97316)
    val PrimaryAccentPress get() = Color(0xFFEA580C)

    // Subtle Accent Light: #ffedd5 / rgba(249,115,22,0.15)
    val SubtleAccent get() = if (isDark) Color(0x26F97316) else Color(0xFFFEDD5)

    // Base Background: #fafafa / #09090b
    val DarkSlate get() = if (isDark) Color(0xFF09090B) else Color(0xFFFAFAFA)

    // Card Background: #ffffff / #18181b
    val MidnightGradStart get() = if (isDark) Color(0xFF18181B) else Color(0xFFFFFFFF)

    // Borders: #f4f4f5 / rgba(255,255,255,0.05)
    val Slate700 get() = if (isDark) Color(0x0DFFFFFF) else Color(0xFFF4F4F5)
    val Slate800 get() = if (isDark) Color(0x0DFFFFFF) else Color(0xFFE4E4E7)
    val GlassBorder get() = if (isDark) Color(0x0DFFFFFF) else Color(0xFFF4F4F5)
    val Zinc800 get() = if (isDark) Color(0xFF27272A) else Color(0xFFF4F4F5)

    // Headline Text: #18181b / #f4f4f5
    val TextLight get() = if (isDark) Color(0xFFF4F4F5) else Color(0xFF18181B)

    // Body/Subtext: #52525b / #a1a1aa
    val TextMuted get() = if (isDark) Color(0xFFA1A1AA) else Color(0xFF52525B)

    // Supporting Accents
    val EmeraldAccent = Color(0xFF10B981) // emerald-500
    val WarnOrange = Color(0xFFEAB308)    // yellow-500 / amber-500
    val SunnyYellow = Color(0xFFEAB308)
    val EnergeticBlue = Color(0xFF3B82F6) // blue-500
    val DevPurple = Color(0xFFA855F7)     // Moderator purple
    val AccentPurple = Color(0xFFA855F7)

    // Theme constants
    val Zinc950 = Color(0xFF09090B)
    val Zinc900 = Color(0xFF18181B)
    val GlassBg get() = if (isDark) Color(0x1AFFFFFF) else Color(0x0DF97316)
}

fun Modifier.cosmicCard(isDark: Boolean): Modifier {
    return this.then(
        if (isDark) {
            Modifier.border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(24.dp))
        } else {
            Modifier.graphicsLayer {
                shadowElevation = 8f
                shape = RoundedCornerShape(24.dp)
                clip = true
            }.border(1.dp, Color(0xFFF4F4F5), RoundedCornerShape(24.dp))
        }
    )
}

fun Modifier.cosmicHero(isDark: Boolean): Modifier {
    return this.then(
        if (isDark) {
            Modifier.border(1.dp, Color(0x0DFFFFFF), RoundedCornerShape(32.dp))
        } else {
            Modifier.graphicsLayer {
                shadowElevation = 12f
                shape = RoundedCornerShape(32.dp)
                clip = true
            }
        }
    )
}

fun Modifier.cosmicButton(isDark: Boolean): Modifier {
    return this.then(
        if (isDark) {
            Modifier
        } else {
            Modifier.graphicsLayer {
                shadowElevation = 6f
                shape = RoundedCornerShape(16.dp)
                clip = true
            }
        }
    )
}

sealed class AppTab(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : AppTab("home", "Home", Icons.Default.Home)
    object Timetable : AppTab("timetable", "Timetable", Icons.Default.Today)
    object Maktaba : AppTab("maktaba", "Library", Icons.Default.FolderOpen)
    object Marketplace : AppTab("marketplace", "Market", Icons.Default.Storefront)
    object Talk : AppTab("talk", "Talks", Icons.Default.Forum)
    object Profile : AppTab("profile", "Profile", Icons.Default.AccountCircle)
}

@Composable
fun GlassmorphicBottomBar(
    activeTab: AppTab,
    onTabSelect: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        AppTab.Home,
        AppTab.Timetable,
        AppTab.Maktaba,
        AppTab.Marketplace,
        AppTab.Talk,
        AppTab.Profile
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .graphicsLayer {
                shadowElevation = 16f
                shape = RoundedCornerShape(32.dp)
                clip = true
            }
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        if (CosmicTheme.isDark) Color(0xD9121214) else Color(0xE6FFFFFF),
                        if (CosmicTheme.isDark) Color(0xD91C1C1F) else Color(0xE6F8F9FA)
                    )
                )
            )
            .border(
                border = BorderStroke(
                    1.dp, 
                    if (CosmicTheme.isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
                ), 
                shape = RoundedCornerShape(32.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = activeTab == tab
                
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.08f else 1.0f,
                    animationSpec = spring(dampingRatio = 0.5f, stiffness = 200f),
                    label = "tab_bounce"
                )
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelect(tab) }
                        )
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .testTag("nav_tab_${tab.route}"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) {
                                    CosmicTheme.PrimaryAccent.copy(alpha = 0.15f)
                                } else {
                                    Color.Transparent
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) {
                                CosmicTheme.PrimaryAccent
                            } else {
                                CosmicTheme.TextMuted
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(1.dp))
                    
                    Text(
                        text = tab.title,
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) CosmicTheme.TextLight else CosmicTheme.TextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun TalkScreen(viewModel: CampusViewModel, onBackToHome: (() -> Unit)? = null) {
    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Chats, 1 = Discussions
    
    val activeChatRoomId by viewModel.activeChatRoomId.collectAsStateWithLifecycle()
    val activeListingId by viewModel.activeListingId.collectAsStateWithLifecycle()
    
    val showTopSelector = (activeChatRoomId == null) && (activeListingId == null)
    
    Column(modifier = Modifier.fillMaxSize()) {
        if (showTopSelector) {
            Surface(
                color = Color.Transparent,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (CosmicTheme.isDark) Color(0xFF1C1C1E) else Color(0xFFE5E5EA))
                        .padding(2.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    val subTabs = listOf("Direct Chats", "Campus Forums")
                    subTabs.forEachIndexed { index, title ->
                        val isSel = selectedSubTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSel) {
                                        if (CosmicTheme.isDark) Color(0xFF2C2C2E) else Color.White
                                    } else {
                                        Color.Transparent
                                    }
                                )
                                .clickable { selectedSubTab = index }
                                .graphicsLayer {
                                    shadowElevation = if (isSel) 2f else 0f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) {
                                    CosmicTheme.TextLight
                                } else {
                                    CosmicTheme.TextMuted
                                }
                            )
                        }
                    }
                }
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            if (selectedSubTab == 0) {
                ChatsScreen(viewModel = viewModel, onBackToHome = onBackToHome)
            } else {
                DiscussionsScreen(viewModel = viewModel, onBackToHome = onBackToHome)
            }
        }
    }
}

@Composable
fun SidebarNavigation(
    activeTab: AppTab,
    onTabSelect: (AppTab) -> Unit,
    currentUser: UserEntity?
) {
    Column(
        modifier = Modifier
            .width(220.dp)
            .fillMaxHeight()
            .background(CosmicTheme.MidnightGradStart)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Logo / Branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(CosmicTheme.NeonOrange, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SMART CAMPUS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = CosmicTheme.NeonOrange,
                    letterSpacing = 1.5.sp
                )
            }

            // Navigation Items
            val tabs = listOf(
                AppTab.Home,
                AppTab.Timetable,
                AppTab.Maktaba,
                AppTab.Marketplace,
                AppTab.Talk,
                AppTab.Profile
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tabs.forEach { tab ->
                    val isSelected = activeTab == tab
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) CosmicTheme.SubtleAccent else Color.Transparent)
                            .clickable { onTabSelect(tab) }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) CosmicTheme.AccentTeal else CosmicTheme.TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tab.title,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) CosmicTheme.TextLight else CosmicTheme.TextMuted
                        )
                    }
                }
            }
        }

        // Bottom profile card
        if (currentUser != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onTabSelect(AppTab.Profile) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(CosmicTheme.DevPurple)
                        .border(1.5.dp, CosmicTheme.NeonOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = currentUser.displayName.substring(0, 1).uppercase()
                    Text(initial, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = currentUser.displayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTheme.TextLight,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentUser.username,
                        fontSize = 10.sp,
                        color = CosmicTheme.TextMuted,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun SmartCampusApp(viewModel: CampusViewModel) {
    val context = LocalContext.current
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    
    // Bind active theme dynamically
    CosmicTheme.isDark = isDarkTheme
    
    val view = androidx.compose.ui.platform.LocalView.current
    if (!view.isInEditMode) {
        androidx.compose.runtime.SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = if (isDarkTheme) android.graphics.Color.parseColor("#09090b") else android.graphics.Color.parseColor("#fafafa")
            window.navigationBarColor = if (isDarkTheme) android.graphics.Color.parseColor("#09090b") else android.graphics.Color.parseColor("#fafafa")
            
            val insetsController = androidx.core.view.WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !isDarkTheme
            insetsController.isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val onboardingStep by viewModel.onboardingStep.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf<AppTab>(AppTab.Home) }

    // Double-back click to exit app controller
    var lastBackPressTime by remember { mutableStateOf(0L) }

    // If active modal/sheets exist, intercept back press
    val viewingMaterial by viewModel.viewingMaterial.collectAsStateWithLifecycle()
    val activeListingId by viewModel.activeListingId.collectAsStateWithLifecycle()
    val activeChatRoomId by viewModel.activeChatRoomId.collectAsStateWithLifecycle()

    BackHandler {
        when {
            viewingMaterial != null -> {
                viewModel.openMaterialViewer(null)
            }
            activeListingId != null -> {
                viewModel.selectActiveListing(null)
            }
            activeChatRoomId != null -> {
                viewModel.selectActiveChatRoom(null)
            }
            activeTab != AppTab.Home -> {
                activeTab = AppTab.Home
            }
            else -> {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackPressTime < 2000) {
                    // Quit application manually
                    (context as? android.app.Activity)?.finish()
                } else {
                    lastBackPressTime = currentTime
                    Toast.makeText(context, "Bonyeza mara mbili ili kutoka!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isExpanded = configuration.screenWidthDp >= 600

    // Material standard visual layout
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = CosmicTheme.DarkSlate
    ) {
        if (currentUser == null || !currentUser!!.onboardingCompleted) {
            LoginOnboardingLayout(viewModel, currentUser, onboardingStep)
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                if (isExpanded) {
                    SidebarNavigation(
                        activeTab = activeTab,
                        onTabSelect = { activeTab = it },
                        currentUser = currentUser
                    )
                    VerticalDivider(color = CosmicTheme.Slate700, modifier = Modifier.fillMaxHeight().width(1.dp))
                }

                val shouldHideBottomBar = (viewingMaterial != null) || (activeListingId != null) || (activeChatRoomId != null)

                Scaffold(
                    bottomBar = {
                        if (!isExpanded && !shouldHideBottomBar) {
                            GlassmorphicBottomBar(
                                activeTab = activeTab,
                                onTabSelect = { activeTab = it }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CosmicTheme.DarkSlate)
                            .padding(innerPadding)
                    ) {
                        Crossfade(targetState = activeTab, label = "tabTransition") { tab ->
                            when (tab) {
                                AppTab.Home -> ScheduleScreen(viewModel = viewModel, isPlannerMode = false, onTabSelect = { activeTab = it })
                                AppTab.Timetable -> ScheduleScreen(viewModel = viewModel, isPlannerMode = true, onTabSelect = { activeTab = it })
                                AppTab.Maktaba -> MaktabaScreen(viewModel, onBackToHome = { activeTab = AppTab.Home })
                                AppTab.Marketplace -> MarketplaceScreen(viewModel, onBackToHome = { activeTab = AppTab.Home })
                                AppTab.Talk -> TalkScreen(viewModel, onBackToHome = { activeTab = AppTab.Home })
                                AppTab.Profile -> ProfileScreen(viewModel, onBackToHome = { activeTab = AppTab.Home })
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. LOGIN & SINGLE-TIME ONBOARDING PAGE
// ==========================================
@Composable
fun LoginOnboardingLayout(viewModel: CampusViewModel, user: UserEntity?, step: Int) {
    var displayName by remember { mutableStateOf("") }
    var usernameSec by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isSignUpMode by remember { mutableStateOf(false) }

    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()

    val cardBrush = Brush.verticalGradient(listOf(CosmicTheme.MidnightGradStart, CosmicTheme.DarkSlate))

    LaunchedEffect(isSignUpMode) {
        viewModel.clearAuthError()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    listOf(CosmicTheme.DarkSlate, Color(0xFF1E1E38), CosmicTheme.DarkSlate)
                )
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .wrapContentHeight()
                .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (step == 0) {
                    // Login Setup Profile
                    Text(
                        text = "Smart Campus",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        color = CosmicTheme.TextLight,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Ukurasa wa Elimu na Biashara",
                        fontSize = 14.sp,
                        color = CosmicTheme.AccentTeal,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Tab selector row for Sign In vs Sign Up
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .background(CosmicTheme.Slate800, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (!isSignUpMode) CosmicTheme.AccentTeal else Color.Transparent)
                                .clickable(enabled = !authLoading) { isSignUpMode = false }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ingia",
                                color = if (!isSignUpMode) Color.White else CosmicTheme.TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSignUpMode) CosmicTheme.AccentTeal else Color.Transparent)
                                .clickable(enabled = !authLoading) { isSignUpMode = true }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Jisajili",
                                color = if (isSignUpMode) Color.White else CosmicTheme.TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Display errors beautifully
                    authError?.let { err ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicTheme.WarnOrange.copy(alpha = 0.15f)),
                            border = BorderStroke(1.dp, CosmicTheme.WarnOrange.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Error info",
                                    tint = CosmicTheme.WarnOrange,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = err,
                                    color = CosmicTheme.TextLight,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Funga arifa",
                                    tint = CosmicTheme.TextMuted,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { viewModel.clearAuthError() }
                                )
                            }
                        }
                    }

                    // Fields
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Display Name (Your Real Name)") },
                            singleLine = true,
                            enabled = !authLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CosmicTheme.TextLight,
                                unfocusedTextColor = CosmicTheme.TextLight,
                                focusedBorderColor = CosmicTheme.AccentTeal,
                                unfocusedBorderColor = CosmicTheme.Slate700
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_name_input"),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CosmicTheme.AccentTeal) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = usernameSec,
                            onValueChange = { usernameSec = it },
                            label = { Text("Username (e.g. @japhet)") },
                            singleLine = true,
                            enabled = !authLoading,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CosmicTheme.TextLight,
                                unfocusedTextColor = CosmicTheme.TextLight,
                                focusedBorderColor = CosmicTheme.AccentTeal,
                                unfocusedBorderColor = CosmicTheme.Slate700
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_input"),
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = CosmicTheme.AccentTeal) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("College Email (Barua Pepe)") },
                        singleLine = true,
                        enabled = !authLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicTheme.TextLight,
                            unfocusedTextColor = CosmicTheme.TextLight,
                            focusedBorderColor = CosmicTheme.AccentTeal,
                            unfocusedBorderColor = CosmicTheme.Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CosmicTheme.AccentTeal) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password (Nenosiri)") },
                        singleLine = true,
                        enabled = !authLoading,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicTheme.TextLight,
                            unfocusedTextColor = CosmicTheme.TextLight,
                            focusedBorderColor = CosmicTheme.AccentTeal,
                            unfocusedBorderColor = CosmicTheme.Slate700
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CosmicTheme.AccentTeal) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(image, contentDescription = if (passwordVisible) "Toggle Password" else "Toggle Password", tint = CosmicTheme.TextMuted)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    if (authLoading) {
                        CircularProgressIndicator(color = CosmicTheme.AccentTeal, modifier = Modifier.size(36.dp))
                    } else {
                        Button(
                            onClick = {
                                if (isSignUpMode) {
                                    viewModel.handleSignUp(emailInput, passwordInput, displayName, usernameSec)
                                } else {
                                    viewModel.handleSignIn(emailInput, passwordInput)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(12.dp),
                            enabled = emailInput.isNotBlank() && passwordInput.isNotBlank() && (!isSignUpMode || (displayName.isNotBlank() && usernameSec.isNotBlank()))
                        ) {
                            Text(
                                text = if (isSignUpMode) "Jisajili Sasa ➔" else "Ingia Sasa ➔",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (step == 1) {
                    // Preferences Select Onboarding Setup (Skip ProgressBar as requested: "Omit top progress bar entirely so it feels visually clean")
                    Text(
                        text = "Customize Academic Path",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = CosmicTheme.TextLight,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Select programme details to personalize your class routines & Maktaba archive filters.",
                        fontSize = 13.sp,
                        color = CosmicTheme.TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    var progSec by remember { mutableStateOf("Computer Science") }
                    var yrSec by remember { mutableStateOf("Year 2") }
                    var semSec by remember { mutableStateOf("Semester 1") }

                    val programmes = listOf("Computer Science", "Engineering", "Business Admin", "Medicine")
                    val years = listOf("Year 1", "Year 2", "Year 3", "Year 4")
                    val semesters = listOf("Semester 1", "Semester 2")

                    Text("Selected Programme:", color = CosmicTheme.AccentTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                    LazyRow(modifier = Modifier.padding(vertical = 6.dp)) {
                        items(programmes) { p ->
                            val isSel = p == progSec
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate800)
                                    .clickable { progSec = p }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(p, color = CosmicTheme.TextLight, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Current Academic Year:", color = CosmicTheme.AccentTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(vertical = 6.dp)) {
                        years.forEach { y ->
                            val isSel = y == yrSec
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate800)
                                    .clickable { yrSec = y }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(y, color = CosmicTheme.TextLight, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Semester Term:", color = CosmicTheme.AccentTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                    Row(modifier = Modifier.padding(vertical = 6.dp)) {
                        semesters.forEach { s ->
                            val isSel = s == semSec
                            Box(
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate800)
                                    .clickable { semSec = s }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(s, color = CosmicTheme.TextLight, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.selectProgramme(progSec)
                            viewModel.selectYear(yrSec)
                            viewModel.selectSemester(semSec)
                            viewModel.proceedOnboardingStep()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("onboarding_save_preferences"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save & Next ➔", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Final Onboarding slide: Beautiful Greeting Card from Moderator Japhet Mathias with correct avatar, text, name.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF3B82F6).copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Moderator",
                            tint = CosmicTheme.DevPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ujumbe kutoka kwa Moderator! 🛡️",
                            fontWeight = FontWeight.Bold,
                            color = CosmicTheme.TextLight,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Male avatar graphic circle representing Dicebear URL
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(CosmicTheme.DevPurple.copy(alpha = 0.2f))
                            .border(2.dp, CosmicTheme.DevPurple, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing minimalist male avatar matching Dicebear seed seed=Japhet
                        Canvas(modifier = Modifier.size(60.dp)) {
                            // Hair / curls details
                            drawCircle(color = Color(0xFF6B4527), radius = 25f, center = center.copy(y = center.y - 15f))
                            // Face skin
                            drawCircle(color = Color(0xFFE0A96D), radius = 22f, center = center)
                            // Smile / mouth
                            drawArc(
                                color = Color(0xFF1E293B),
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = false,
                                size = size.copy(width = 16f, height = 10f),
                                topLeft = center.copy(x = center.x - 8f, y = center.y + 4f)
                            )
                            // Eyes
                            drawCircle(color = Color(0xFF1E293B), radius = 3f, center = center.copy(x = center.x - 8f, y = center.y - 4f))
                            drawCircle(color = Color(0xFF1E293B), radius = 3f, center = center.copy(x = center.x + 8f, y = center.y - 4f))
                            // flatNatural eyebrows
                            drawLine(
                                color = Color(0xFF3E2723),
                                start = center.copy(x = center.x - 13f, y = center.y - 12f),
                                end = center.copy(x = center.x - 3f, y = center.y - 12f),
                                strokeWidth = 3f
                            )
                            drawLine(
                                color = Color(0xFF3E2723),
                                start = center.copy(x = center.x + 3f, y = center.y - 12f),
                                end = center.copy(x = center.x + 13f, y = center.y - 12f),
                                strokeWidth = 3f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Japhet Mathias",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CosmicTheme.TextLight
                    )

                    Text(
                        text = "Community Moderator",
                        fontSize = 12.sp,
                        color = CosmicTheme.TextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Message box
                    Surface(
                        color = CosmicTheme.Slate800,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Yo ${user?.username ?: "@username"}! I’m Japhet. Let’s be real, uni life can be a mess of deadlines and zero sleep. I built Smart Campus to handle the chaos (schedules, materials, products, all of it, you name it, it's there) so you can actually breathe. Welcome to the fam. I've got your back, let’s get it! 🚀",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            lineHeight = 19.sp,
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier.padding(14.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.proceedOnboardingStep() },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.EmeraldAccent),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("onboarding_complete_button")
                    ) {
                        Text("Let's Get It! 🚀", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. TIMETABLE & SCHEDULE MODULE (ROUTINE)
// ==========================================
@Composable
fun Modifier.pressScaleClickable(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "pressScale"
    )
    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = androidx.compose.foundation.LocalIndication.current,
            onClick = onClick
        )
}

@Composable
fun ScheduleScreen(viewModel: CampusViewModel, isPlannerMode: Boolean, onTabSelect: (AppTab) -> Unit) {
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val allRoutinesList by viewModel.allRoutines.collectAsStateWithLifecycle(initialValue = emptyList())
    val activeDay by viewModel.selectedTimetableDay.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    
    var showAddDialog by remember { mutableStateOf(false) }
    val viewingDetailedRoutine = isPlannerMode
    
    // Back handler for sub-screen
    if (viewingDetailedRoutine) {
        BackHandler {
            onTabSelect(AppTab.Home)
        }
    }
    
    val notificationList by viewModel.notifications.collectAsStateWithLifecycle(initialValue = emptyList())
    val unreadNotifs = notificationList.count { !it.read }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    // Dynamic Swahili/local colloquial greeting array tailored to the time of day
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greetings = remember(currentHour) {
        when {
            currentHour in 5..11 -> listOf("Amka piga msuli", "Oya msomi", "Mambo vipi", "Niaje")
            currentHour in 12..16 -> listOf("Oya msomi", "Mambo vipi", "Niaje", "Piga kitabu")
            currentHour in 17..21 -> listOf("Mambo vipi", "Oya msomi", "Niaje", "Time ya kurelax")
            else -> listOf("Niaje", "Amka piga msuli", "Oya msomi", "Mambo vipi")
        }
    }
    var greetingIndex by remember { mutableStateOf(0) }
    LaunchedEffect(greetings) {
        while (true) {
            kotlinx.coroutines.delay(2500)
            greetingIndex = (greetingIndex + 1) % greetings.size
        }
    }

    // Avatar entrance animation
    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startAnimation = true
    }
    val animScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )
    val animRotate by animateFloatAsState(
        targetValue = if (startAnimation) 0f else -20f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "rotate"
    )

    // Mascot bouncing portal animations
    val infiniteTransition = rememberInfiniteTransition(label = "mascot")
    val bounceY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotBounce"
    )
    val rotationZValue by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascotRotation"
    )

    val iconScale1 by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconScale1"
    )
    val iconRotation1 by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconRotation1"
    )
    val iconBounceY1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "iconBounceY1"
    )
    val bookRotation by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bookRotation"
    )

    // Shadow gravity math sync'd with bounce
    val fraction = (bounceY - (-12f)) / (0f - (-12f)) // 0 peak, 1 ground
    val shadowScaleX = 0.8f + (fraction * 0.4f)  // shrinks at peak, expands at ground
    val shadowOpacity = 0.08f + (fraction * 0.16f) // lower at peak, higher at ground

    // Cycling mascot icon swaps every 2s
    var mascotIconIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2000)
            mascotIconIndex = (mascotIconIndex + 1) % 4
        }
    }
    val enteringRotation = remember { androidx.compose.animation.core.Animatable(-90f) }
    LaunchedEffect(mascotIconIndex) {
        enteringRotation.snapTo(-90f)
        enteringRotation.animateTo(0f, animationSpec = spring(dampingRatio = 0.5f, stiffness = 300f))
    }

    // Time calculations for routines today
    val calendar = java.util.Calendar.getInstance()
    val dayOfWeekNum = calendar.get(java.util.Calendar.DAY_OF_WEEK)
    val currentDayName = when (dayOfWeekNum) {
        java.util.Calendar.MONDAY -> "Monday"
        java.util.Calendar.TUESDAY -> "Tuesday"
        java.util.Calendar.WEDNESDAY -> "Wednesday"
        java.util.Calendar.THURSDAY -> "Thursday"
        java.util.Calendar.FRIDAY -> "Friday"
        java.util.Calendar.SATURDAY -> "Saturday"
        java.util.Calendar.SUNDAY -> "Sunday"
        else -> "Monday"
    }

    // Routines for current day
    val routinesToday = allRoutinesList.filter { it.dayOfWeek == currentDayName }
    val classTimeStatus = checkClassStatus(routinesToday)

    if (viewingDetailedRoutine) {
        // DETAILED CALENDAR ROUTINE MANAGER (Image 5)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onTabSelect(AppTab.Home) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CosmicTheme.TextLight)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Ratiba Yangu",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTheme.TextLight
                    )
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(containerColor = CosmicTheme.AccentTeal),
                    modifier = Modifier.size(36.dp).clip(CircleShape).testTag("schedule_add_class_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Class", tint = CosmicTheme.TextLight, modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Daily Progress completion card
            val completedCount = routines.count { r ->
                val range = parseClassTimes(r.times)
                if (range == null) true
                else {
                    val currMinutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
                    range.second < currMinutes
                }
            }
            val completionPercentage = if (routines.isEmpty()) 100 else (completedCount * 100) / routines.size

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("DAILY PROGRESS", fontSize = 10.sp, color = CosmicTheme.WarnOrange, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (completionPercentage == 100) "Mission Complete, Mwanangu! 🏆" else "Unasoma nini leo? 📚",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CosmicTheme.TextLight
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .clickable { viewModel.forceSyncScraper() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SYNCHRONIZE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.AccentTeal)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(72.dp)) {
                        CircularProgressIndicator(
                            progress = { completionPercentage / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = CosmicTheme.NeonOrange,
                            strokeWidth = 6.dp,
                            trackColor = Color.White.copy(alpha = 0.08f)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("$completionPercentage%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                            Text("DONE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextMuted)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Academic Calendar
            Text("ACADEMIC CALENDAR", fontSize = 10.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
            val currentMonthName = months[calendar.get(java.util.Calendar.MONTH)]
            val currentDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
            Text(
                text = "${currentDayName}, ${currentMonthName} ${currentDayOfMonth}th",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.TextLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontal Days layout
            val currentWeekDays = remember {
                val list = mutableListOf<Pair<String, Int>>()
                val tempCal = java.util.Calendar.getInstance()
                tempCal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
                for (i in 0 until 7) {
                    val dName = when (tempCal.get(java.util.Calendar.DAY_OF_WEEK)) {
                        java.util.Calendar.MONDAY -> "Monday"
                        java.util.Calendar.TUESDAY -> "Tuesday"
                        java.util.Calendar.WEDNESDAY -> "Wednesday"
                        java.util.Calendar.THURSDAY -> "Thursday"
                        java.util.Calendar.FRIDAY -> "Friday"
                        java.util.Calendar.SATURDAY -> "Saturday"
                        java.util.Calendar.SUNDAY -> "Sunday"
                        else -> "Monday"
                    }
                    val dVal = tempCal.get(java.util.Calendar.DAY_OF_MONTH)
                    list.add(Pair(dName, dVal))
                    tempCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                }
                list
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currentWeekDays) { pair ->
                    val isSelected = activeDay == pair.first
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CosmicTheme.NeonOrange else CosmicTheme.MidnightGradStart.copy(alpha = 0.5f))
                            .border(1.dp, if (isSelected) Color.Transparent else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectTimetableDay(pair.first) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = pair.first.substring(0, 3).uppercase(),
                                color = if (isSelected) CosmicTheme.TextLight else CosmicTheme.TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = pair.second.toString(),
                                color = CosmicTheme.TextLight,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pills filters ALL / UPCOMING / COMPLETED
            var selectedFilter by remember { mutableStateOf("ALL") }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("ALL", "UPCOMING", "COMPLETED").forEach { f ->
                    val isSel = selectedFilter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color.White.copy(alpha = 0.15f) else Color.Transparent)
                            .border(1.dp, if (isSel) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                            .clickable { selectedFilter = f }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = f,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) CosmicTheme.TextLight else CosmicTheme.TextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Routines (filtered)
            val filteredRoutines = remember(routines, selectedFilter) {
                val currMinutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
                routines.filter { r ->
                    val range = parseClassTimes(r.times)
                    if (range == null) true
                    else {
                        val startTime = range.first
                        val endTime = range.second
                        when (selectedFilter) {
                            "COMPLETED" -> endTime < currMinutes
                            "UPCOMING" -> startTime >= currMinutes || (currMinutes in startTime until endTime)
                            else -> true
                        }
                    }
                }
            }

            if (filteredRoutines.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Empty",
                            tint = CosmicTheme.EmeraldAccent,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Hakuna vipindi hapa, msomi!",
                            color = CosmicTheme.TextMuted,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredRoutines, key = { it.id }) { routine ->
                        RoutineItemCard(routine, onToggleAlarm = { valVal ->
                            viewModel.toggleReminder(routine.id, valVal)
                        }, onDelete = {
                            viewModel.deleteClass(routine.id)
                        })
                    }
                }
            }
        }
    } else {
        // MAIN CAMPUS HUB DASHBOARD (Image 6)
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Background blur blobs (gradient purple to orange blur-3xl)
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF8B5CF6).copy(alpha = 0.12f), Color.Transparent),
                        radius = 350.dp.toPx()
                    ),
                    center = androidx.compose.ui.geometry.Offset(x = size.width * 0.15f, y = size.height * 0.2f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFF97316).copy(alpha = 0.12f), Color.Transparent),
                        radius = 400.dp.toPx()
                    ),
                    center = androidx.compose.ui.geometry.Offset(x = size.width * 0.85f, y = size.height * 0.65f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. HEADER ROW CONTROLS (Right-aligned, square compact pill buttons, unread orange notification dot, circular avatar with thick border)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left Brand Badge / Accent Name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(CosmicTheme.NeonOrange, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SMART CAMPUS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = CosmicTheme.NeonOrange,
                            letterSpacing = 1.2.sp
                        )
                    }

                    // Right drawer UI Controls
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Chats link (40x40px, 20% border-radius = 8.dp)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = if (CosmicTheme.isDark) 0.05f else 0.40f))
                                .border(1.dp, Color.White.copy(alpha = if (CosmicTheme.isDark) 0.10f else 0.50f), RoundedCornerShape(8.dp))
                                .clickable { onTabSelect(AppTab.Talk) },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubbleOutline,
                                    contentDescription = "Chats",
                                    tint = CosmicTheme.TextLight,
                                    modifier = Modifier.size(18.dp)
                                )
                                // Pulsing badge representing real messages
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(CosmicTheme.NeonOrange, CircleShape)
                                        .border(1.dp, CosmicTheme.DarkSlate, CircleShape)
                                )
                            }
                        }

                        // Notifications bell link with glowing badge (40x40px, 20% border-radius = 8.dp)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = if (CosmicTheme.isDark) 0.05f else 0.40f))
                                .border(1.dp, Color.White.copy(alpha = if (CosmicTheme.isDark) 0.10f else 0.50f), RoundedCornerShape(8.dp))
                                .clickable { showNotificationsDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsNone,
                                    contentDescription = "Notifications",
                                    tint = CosmicTheme.TextLight,
                                    modifier = Modifier.size(19.dp)
                                )
                                if (unreadNotifs > 0) {
                                    // Notification dot with glowing outer glow shadow emulation
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(CosmicTheme.NeonOrange, CircleShape)
                                            .border(1.dp, CosmicTheme.DarkSlate, CircleShape)
                                    )
                                }
                            }
                        }

                        // Elegant circular Custom Avatar link framed in thick orange accent border
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    scaleX = animScale
                                    scaleY = animScale
                                    rotationZ = animRotate
                                }
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(CosmicTheme.DevPurple)
                                .border(2.dp, CosmicTheme.NeonOrange, CircleShape) // thick 2dp orange border
                                .clickable { onTabSelect(AppTab.Profile) },
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = (currentUser?.displayName ?: "Thomas").substring(0, 1).uppercase()
                            Text(
                                text = initial,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. COLONY GREETING SYSTEM & GRAPHIC PANEL (MIDDLE ROW)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val pulseAlpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "pulseAlpha"
                            )
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .graphicsLayer { alpha = pulseAlpha }
                                    .background(CosmicTheme.NeonOrange, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            
                            // Dynamic options based on the time of day, cycling smoothly
                            val currentGreetingStr = greetings[greetingIndex % greetings.size]
                            
                            // Animated vertical slide roll transition mimicking Framer Motion AnimatePresence
                            AnimatedContent(
                                targetState = currentGreetingStr,
                                transitionSpec = {
                                    (slideInVertically(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow)) { height -> height } 
                                            + fadeIn(animationSpec = tween(300))).togetherWith(
                                        slideOutVertically(animationSpec = tween(250)) { height -> -height } 
                                                + fadeOut(animationSpec = tween(200))
                                    )
                                },
                                label = "greetAnim"
                            ) { txt ->
                                Text(
                                    text = txt,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmicTheme.NeonOrange
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = (currentUser?.displayName?.split(" ")?.firstOrNull() ?: "Thomas"),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTheme.TextLight,
                                letterSpacing = (-0.5).sp
                            )
                            Text(
                                text = "!",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTheme.NeonOrange
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School, // Graduation Cap vector icon
                                contentDescription = null,
                                tint = CosmicTheme.NeonOrange,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            
                            val activeProg by viewModel.selectedProgramme.collectAsStateWithLifecycle()
                            val activeYear by viewModel.selectedYear.collectAsStateWithLifecycle()
                            val metadataLabel = "${activeProg ?: "COET"} • ${activeYear ?: "Yr 2"}"
                            Text(
                                text = metadataLabel,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CosmicTheme.TextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Bouncing Portal Container & Mascot (105.dp)
                    Box(
                        modifier = Modifier
                            .size(105.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFFF97316).copy(alpha = 0.15f), Color.Transparent)
                                )
                            )
                            .background(Color.White.copy(alpha = if (CosmicTheme.isDark) 0.04f else 0.35f))
                            .border(1.dp, Color.White.copy(alpha = if (CosmicTheme.isDark) 0.08f else 0.40f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // Floating bouncing portal element
                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        translationY = bounceY.dp.toPx()
                                        rotationZ = rotationZValue
                                    }
                                    .size(46.dp)
                                    .background(CosmicTheme.NeonOrange.copy(alpha = 0.15f), CircleShape)
                                    .border(1.5.dp, CosmicTheme.NeonOrange, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // Specs request: User Profile (Person), Book (Book), Shopping Bag, Yellow Sparkles (Star) rotating
                                val currentMascotIcon = when (mascotIconIndex) {
                                    0 -> Icons.Default.Person
                                    1 -> Icons.Default.Book
                                    2 -> Icons.Default.Star
                                    else -> Icons.Default.LocalMall
                                }
                                Icon(
                                    imageVector = currentMascotIcon,
                                    contentDescription = null,
                                    tint = CosmicTheme.NeonOrange,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .graphicsLayer {
                                            rotationZ = enteringRotation.value
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Gravity shadow indicator
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(3.dp)
                                    .graphicsLayer {
                                        scaleX = shadowScaleX
                                    }
                                    .background(Color.Black.copy(alpha = shadowOpacity), CircleShape)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section title
                Text(
                    text = "KIPINDI KIJACHO",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // REAL-TIME CLASS TIMER CARD (HERO ELEMENT)
                val status = classTimeStatus
                when {
                    status.activeClass != null -> {
                        // Condition A: Active Class Glassmorphic Card (rounded-[32px])
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .cosmicCard(CosmicTheme.isDark)
                                .border(1.5.dp, CosmicTheme.NeonOrange.copy(alpha = 0.4f), RoundedCornerShape(32.dp))
                                .pressScaleClickable { onTabSelect(AppTab.Timetable) },
                            colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = if (CosmicTheme.isDark) 0.4f else 0.95f)),
                            shape = RoundedCornerShape(32.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.04f),
                                    modifier = Modifier
                                        .size(110.dp)
                                        .align(Alignment.BottomEnd)
                                        .graphicsLayer {
                                            rotationZ = 12f
                                            translationX = 20f
                                            translationY = 20f
                                        }
                                )

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val infinitePulsing = rememberInfiniteTransition(label = "pulse")
                                        val pulseAlpha by infinitePulsing.animateFloat(
                                            initialValue = 0.4f,
                                            targetValue = 1f,
                                            animationSpec = infiniteRepeatable(
                                                animation = tween(1000, easing = LinearEasing),
                                                repeatMode = RepeatMode.Reverse
                                            ),
                                            label = "alpha"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .graphicsLayer { alpha = pulseAlpha }
                                                .background(CosmicTheme.NeonOrange, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "INAPOENDELEA (Inaisha baada ya ${status.minutesLeft}m)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicTheme.NeonOrange
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = status.activeClass.courseName.uppercase(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CosmicTheme.TextLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = CosmicTheme.TextMuted, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Lect: ${status.activeClass.lecturer} (${status.activeClass.courseCode})",
                                            fontSize = 12.sp,
                                            color = CosmicTheme.TextMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Perfect two-column split footer grid (Mini high-density containers)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Column 1: Time
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color.White.copy(alpha = if (CosmicTheme.isDark) 0.05f else 0.45f), RoundedCornerShape(12.dp))
                                                .border(1.dp, Color.White.copy(alpha = if (CosmicTheme.isDark) 0.10f else 0.60f), RoundedCornerShape(12.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = CosmicTheme.EnergeticBlue, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text("MUDA", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
                                                    Text(status.activeClass.times, fontSize = 11.sp, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        // Column 2: Venue
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color.White.copy(alpha = if (CosmicTheme.isDark) 0.05f else 0.45f), RoundedCornerShape(12.dp))
                                                .border(1.dp, Color.White.copy(alpha = if (CosmicTheme.isDark) 0.10f else 0.60f), RoundedCornerShape(12.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Place, contentDescription = null, tint = CosmicTheme.NeonOrange, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text("UKUMBI", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
                                                    Text(status.activeClass.location, fontSize = 11.sp, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    // slide up "Up Next" inside 30 mins
                                    val nextClass = routinesToday.filter {
                                        val range = parseClassTimes(it.times)
                                        range != null && range.first >= parseClassTimes(status.activeClass.times)!!.second
                                    }.minByOrNull { parseClassTimes(it.times)!!.first }

                                    if (status.minutesLeft <= 45 && nextClass != null) {
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(
                                                        modifier = Modifier
                                                            .background(CosmicTheme.NeonOrange, RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text("UP NEXT", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                    }
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(nextClass.courseName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                }
                                                Text(nextClass.times.split("-").first().trim(), fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    status.upcomingClass != null -> {
                        // Condition B: Upcoming Class Card (rounded-[32px], yellow outline theme)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .cosmicCard(CosmicTheme.isDark)
                                .border(1.5.dp, CosmicTheme.SunnyYellow, RoundedCornerShape(32.dp))
                                .pressScaleClickable { onTabSelect(AppTab.Timetable) },
                            colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = if (CosmicTheme.isDark) 0.4f else 0.95f)),
                            shape = RoundedCornerShape(32.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.03f),
                                    modifier = Modifier
                                        .size(110.dp)
                                        .align(Alignment.BottomEnd)
                                        .graphicsLayer {
                                            rotationZ = 12f
                                            translationX = 20f
                                            translationY = 20f
                                        }
                                )

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = CosmicTheme.SunnyYellow, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        val hrUnit = if (status.minutesUntil >= 60) "${status.minutesUntil / 60}h ${status.minutesUntil % 60}m" else "${status.minutesUntil}m"
                                        Text(
                                            text = "INAKUJA (Inaanza baada ya $hrUnit)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicTheme.SunnyYellow
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = status.upcomingClass.courseName.uppercase(),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = CosmicTheme.TextLight
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = CosmicTheme.TextMuted, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Lect: ${status.upcomingClass.lecturer} (${status.upcomingClass.courseCode})",
                                            fontSize = 12.sp,
                                            color = CosmicTheme.TextMuted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Perfect two-column split footer grid (Mini high-density containers)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        // Column 1: Time
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color.White.copy(alpha = if (CosmicTheme.isDark) 0.05f else 0.45f), RoundedCornerShape(12.dp))
                                                .border(1.dp, Color.White.copy(alpha = if (CosmicTheme.isDark) 0.10f else 0.60f), RoundedCornerShape(12.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.AccessTime, contentDescription = null, tint = CosmicTheme.EnergeticBlue, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text("MUDA", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
                                                    Text(status.upcomingClass.times, fontSize = 11.sp, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        // Column 2: Venue
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .background(Color.White.copy(alpha = if (CosmicTheme.isDark) 0.05f else 0.45f), RoundedCornerShape(12.dp))
                                                .border(1.dp, Color.White.copy(alpha = if (CosmicTheme.isDark) 0.10f else 0.60f), RoundedCornerShape(12.dp))
                                                .padding(10.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Place, contentDescription = null, tint = CosmicTheme.SunnyYellow, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Column {
                                                    Text("UKUMBI", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
                                                    Text(status.upcomingClass.location, fontSize = 11.sp, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // Condition C: Weekend / End of Day Celebratory "Ratiba Nyeupe!" Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .cosmicCard(CosmicTheme.isDark)
                                .pressScaleClickable { onTabSelect(AppTab.Timetable) },
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = RoundedCornerShape(32.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(
                                                Color(0xFF8B5CF6).copy(alpha = 0.85f),
                                                Color(0xFFF97316).copy(alpha = 0.85f)
                                            )
                                        )
                                    )
                                    .padding(20.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.15f),
                                    modifier = Modifier
                                        .size(120.dp)
                                        .align(Alignment.BottomEnd)
                                        .graphicsLayer {
                                            rotationZ = -12f
                                            translationX = 20f
                                            translationY = 20f
                                        }
                                )

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.20f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Ratiba Nyeupe!",
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = "Beautiful day! You've completed 100% of today's classes.",
                                                fontSize = 11.sp,
                                                color = Color.White.copy(alpha = 0.82f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // High contrast info tray
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(modifier = Modifier.size(6.dp).background(Color.White, CircleShape))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Kazi Leo", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Kipindi chochote cha leo kimekamilika kwa asilimia 100%! Unaweza kupitia nondo (notes, past papers) mpya zilizowekwa leo maktaba wetu.",
                                                fontSize = 11.sp,
                                                color = Color.White,
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section title
                Text(
                    text = "COLLABORATIVE HUB",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 4. RESPONSIVE INTERACTION GRID LINKS (RATIBA YANGU, UZA NA NUNUA, NONDO & MATERIALS with spring scaler and active transitions)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // "RATIBA YANGU" (Blue theme, Calendar icon with size/rotation animation)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .border(1.5.dp, CosmicTheme.EnergeticBlue.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                            .pressScaleClickable { onTabSelect(AppTab.Timetable) },
                        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = if (CosmicTheme.isDark) 0.2f else 0.90f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .align(Alignment.BottomEnd)
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(CosmicTheme.EnergeticBlue.copy(alpha = 0.25f), Color.Transparent)
                                        ),
                                        shape = CircleShape
                                    )
                            )

                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CosmicTheme.EnergeticBlue.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = CosmicTheme.EnergeticBlue,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .graphicsLayer {
                                                scaleX = iconScale1
                                                scaleY = iconScale1
                                                rotationZ = iconRotation1
                                            }
                                    )
                                }

                                Column {
                                    Text("RATIBA YANGU", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CosmicTheme.TextLight)
                                    Text("Planner Hub", fontSize = 9.sp, color = CosmicTheme.TextMuted)
                                }
                            }
                        }
                    }

                    // "UZA NA NUNUA" (Green theme, Custom shopping bag vector icon with bouncing animation)
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(115.dp)
                            .border(1.5.dp, CosmicTheme.EmeraldAccent.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                            .pressScaleClickable { onTabSelect(AppTab.Marketplace) },
                        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = if (CosmicTheme.isDark) 0.2f else 0.90f)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                            // Custom Floating Orange alert dot
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(CosmicTheme.NeonOrange, CircleShape)
                                    .align(Alignment.TopEnd)
                            )

                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CosmicTheme.EmeraldAccent.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocalMall, // Shopping Bag icon
                                        contentDescription = null,
                                        tint = CosmicTheme.EmeraldAccent,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .graphicsLayer {
                                                translationY = iconBounceY1
                                            }
                                    )
                                }

                                Column {
                                    Text("UZA NA NUNUA", fontSize = 11.sp, fontWeight = FontWeight.Black, color = CosmicTheme.TextLight)
                                    Text("Campus Hub", fontSize = 9.sp, color = CosmicTheme.TextMuted)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // "NONDO & MATERIALS" (Purple theme, full-width row span, animated book, arrow forward)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(76.dp)
                        .border(1.5.dp, CosmicTheme.DevPurple.copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                        .pressScaleClickable { onTabSelect(AppTab.Maktaba) },
                    colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = if (CosmicTheme.isDark) 0.2f else 0.90f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(CosmicTheme.DevPurple.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Book, // Book icon
                                    contentDescription = null,
                                    tint = CosmicTheme.DevPurple,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer {
                                            rotationZ = bookRotation
                                        }
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("NONDO & MATERIALS", fontSize = 12.sp, fontWeight = FontWeight.Black, color = CosmicTheme.TextLight)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(modifier = Modifier.size(5.dp).background(CosmicTheme.NeonOrange, CircleShape))
                                }
                                Text("Notes & past papers", fontSize = 10.sp, color = CosmicTheme.TextMuted)
                            }
                        }

                        Icon(Icons.Default.ArrowForward, contentDescription = "Forward link", tint = CosmicTheme.TextMuted, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 5. SOKO LA CHUO SHOWCASE WIDGET (Translucent bottom banner framed in orange border with clean status indicators & bold titles)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, CosmicTheme.NeonOrange.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                        .pressScaleClickable { onTabSelect(AppTab.Marketplace) },
                    colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = if (CosmicTheme.isDark) 0.4f else 0.95f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        Icon(
                            imageVector = Icons.Default.LocalMall, // Shopping Bag logo background
                            contentDescription = null,
                            tint = CosmicTheme.NeonOrange.copy(alpha = 0.05f),
                            modifier = Modifier
                                .size(110.dp)
                                .align(Alignment.BottomEnd)
                                .graphicsLayer {
                                    rotationZ = 12f
                                    translationX = 30f
                                    translationY = 30f
                                }
                        )

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF22C55E), CircleShape) // Green dot representing Online
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Soko la Chuo (Live) 🟢", fontSize = 10.sp, color = Color(0xFF22C55E), fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Dili za Geto", fontSize = 20.sp, fontWeight = FontWeight.Black, color = CosmicTheme.TextLight)
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Gundua ", fontSize = 12.sp, color = CosmicTheme.TextMuted)
                                Box(
                                    modifier = Modifier
                                        .background(CosmicTheme.NeonOrange.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("50+ bidhaa mpya", fontSize = 10.sp, color = CosmicTheme.NeonOrange, fontWeight = FontWeight.Bold)
                                }
                                Text(" zilizowekwa leo.", fontSize = 12.sp, color = CosmicTheme.TextMuted)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { onTabSelect(AppTab.Marketplace) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF09090B)),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text("Browse Market", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showNotificationsDialog) {
        Dialog(onDismissRequest = { showNotificationsDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Campus Alerts & Logs 🔔", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                        IconButton(onClick = { showNotificationsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = CosmicTheme.TextMuted)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    if (notificationList.isEmpty()) {
                        Text("Hakuna arifa mpya sasa hivi.", fontSize = 12.sp, color = CosmicTheme.TextMuted)
                    } else {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 250.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(notificationList) { notify ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text(notify.title, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight, fontSize = 12.sp)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(notify.message, color = CosmicTheme.TextMuted, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddClassDialog(onDismiss = { showAddDialog = false }) { name, code, lect, time, loc, day, type ->
            viewModel.addNewManualClass(name, code, lect, time, loc, day, type)
            showAddDialog = false
        }
    }
}

@Composable
fun RoutineItemCard(
    routine: RoutineEntity,
    onToggleAlarm: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
        modifier = Modifier
            .fillMaxWidth()
            .cosmicCard(CosmicTheme.isDark)
            .testTag("routine_card_${routine.id}")
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val typeColor = when (routine.classType.lowercase()) {
                            "lab" -> CosmicTheme.EmeraldAccent
                            "seminar" -> CosmicTheme.DevPurple
                            else -> CosmicTheme.AccentTeal
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(typeColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = routine.classType,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = typeColor
                            )
                        }

                        if (routine.isOfficial) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF3B82F6).copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "OFFICIAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF60A5FA)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = routine.courseName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CosmicTheme.TextLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = routine.courseCode + " • Lect: " + routine.lecturer,
                        color = CosmicTheme.TextMuted,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete class", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Divider(color = Color(0xFF334155).copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(14.dp))
                    Text(
                        text = " " + routine.times,
                        fontSize = 12.sp,
                        color = CosmicTheme.TextLight,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(Icons.Default.Place, contentDescription = null, tint = CosmicTheme.WarnOrange, modifier = Modifier.size(14.dp))
                    Text(
                        text = " " + routine.location,
                        fontSize = 12.sp,
                        color = CosmicTheme.TextMuted,
                        maxLines = 1
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (routine.remindersEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                        contentDescription = "Notification Alarm status",
                        tint = if (routine.remindersEnabled) CosmicTheme.WarnOrange else CosmicTheme.TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = routine.remindersEnabled,
                        onCheckedChange = { onToggleAlarm(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CosmicTheme.TextLight,
                            checkedTrackColor = CosmicTheme.WarnOrange,
                            uncheckedThumbColor = CosmicTheme.TextLight,
                            uncheckedTrackColor = CosmicTheme.Slate700
                        ),
                        modifier = Modifier.scale(0.7f).testTag("reminder_switch_${routine.id}")
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClassDialog(onDismiss: () -> Unit, onSave: (String, String, String, String, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var lect by remember { mutableStateOf("") }
    var times by remember { mutableStateOf("09:00 AM - 11:00 AM") }
    var loc by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("Monday") }
    var type by remember { mutableStateOf("Lecture") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Manual Class Slot", color = CosmicTheme.TextLight) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Course Name") })
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. CS-204)") })
                OutlinedTextField(value = lect, onValueChange = { lect = it }, label = { Text("Lecturer Name") })
                OutlinedTextField(value = times, onValueChange = { times = it }, label = { Text("Time (e.g. 02:00 PM - 04:00 PM)") })
                OutlinedTextField(value = loc, onValueChange = { loc = it }, label = { Text("Location Hall") })
                OutlinedTextField(value = day, onValueChange = { day = it }, label = { Text("Day of Week (e.g. Monday)") })
                OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Class Type (Lecture/Lab/Seminar)") })
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, code, lect, times, loc, day, type) },
                enabled = name.isNotBlank() && code.isNotBlank()
            ) {
                Text("Add Slot")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ==========================================
// 3. STUDY MATERIALS ARCHIVE ("MAKTABA YA KUDESA")
// ==========================================
@Composable
fun MaktabaScreen(viewModel: CampusViewModel, onBackToHome: (() -> Unit)? = null) {
    val materials by viewModel.studyMaterials.collectAsStateWithLifecycle()
    val selectedProg by viewModel.selectedProgramme.collectAsStateWithLifecycle()
    val selectedYr by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedSem by viewModel.selectedSemester.collectAsStateWithLifecycle()
    val selectedCourse by viewModel.selectedCourseCode.collectAsStateWithLifecycle()

    val searchQuery by viewModel.maktabaSearch.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedMaktabaCategory.collectAsStateWithLifecycle()
    val viewingMaterial by viewModel.viewingMaterial.collectAsStateWithLifecycle()

    var showUploadModal by remember { mutableStateOf(false) }

    val categories = listOf(
        "All" to "📚 All Kudesa",
        "Notes" to "📝 Lecture Notes",
        "Cheatsheets" to "⚡ Cheatsheets",
        "Past Papers" to "📄 Past Papers",
        "PDF Folder" to "📕 PDFs",
        "Word Docs" to "📁 Word Docs"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Header Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBackToHome != null) {
                IconButton(onClick = onBackToHome, modifier = Modifier.padding(end = 4.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Home",
                        tint = CosmicTheme.TextLight
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Maktaba ya Kudesa",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.TextLight
                )
                Text(
                    text = "Academic cheat sheets, solved past papers & summaries",
                    fontSize = 12.sp,
                    color = CosmicTheme.TextMuted
                )
            }

            IconButton(
                onClick = { showUploadModal = true },
                colors = IconButtonDefaults.iconButtonColors(containerColor = CosmicTheme.AccentTeal),
                modifier = Modifier
                    .size(44.dp)
                    .testTag("maktaba_upload_icon_btn")
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = "Upload Material", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setMaktabaSearch(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("maktaba_search_bar"),
            placeholder = {
                Text(
                    text = "Tafuta nakala (e.g. Calculus, NORMAL, PPT...)",
                    fontSize = 13.sp,
                    color = CosmicTheme.TextMuted
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = CosmicTheme.AccentTeal
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setMaktabaSearch("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = CosmicTheme.TextMuted
                        )
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CosmicTheme.MidnightGradStart,
                unfocusedContainerColor = CosmicTheme.MidnightGradStart,
                focusedBorderColor = CosmicTheme.AccentTeal,
                unfocusedBorderColor = CosmicTheme.Slate700,
                focusedTextColor = CosmicTheme.TextLight,
                unfocusedTextColor = CosmicTheme.TextLight
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Category Horizontal Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("maktaba_category_pills"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(categories.size) { index ->
                val (key, label) = categories[index]
                val isSelected = selectedCategory == key
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) CosmicTheme.AccentTeal else CosmicTheme.MidnightGradStart
                        )
                        .border(
                            1.dp,
                            if (isSelected) CosmicTheme.AccentTeal else CosmicTheme.Slate700,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { viewModel.selectMaktabaCategory(key) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else CosmicTheme.TextLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Directory Breadcrumbs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (CosmicTheme.isDark) Color(0x13FFFFFF) else Color(0x08F97316),
                    RoundedCornerShape(12.dp)
                )
                .border(1.dp, CosmicTheme.Slate700, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                tint = CosmicTheme.AccentTeal,
                modifier = Modifier.size(14.dp)
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selectedProg == null) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.Transparent)
                    .clickable { viewModel.selectProgramme(null) }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "Maktaba",
                    color = if (selectedProg == null) CosmicTheme.AccentTeal else CosmicTheme.TextLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (selectedProg != null) {
                Text("➔", color = CosmicTheme.TextMuted, fontSize = 10.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedYr == null) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { viewModel.selectYear(null) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(selectedProg!!, color = if (selectedYr == null) CosmicTheme.AccentTeal else CosmicTheme.TextLight, fontSize = 11.sp)
                }
            }

            if (selectedYr != null) {
                Text("➔", color = CosmicTheme.TextMuted, fontSize = 10.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedSem == null) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { viewModel.selectSemester(null) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(selectedYr!!, color = if (selectedSem == null) CosmicTheme.AccentTeal else CosmicTheme.TextLight, fontSize = 11.sp)
                }
            }

            if (selectedSem != null) {
                Text("➔", color = CosmicTheme.TextMuted, fontSize = 10.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedCourse == null) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { viewModel.selectCourseCode(null) }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(selectedSem!!, color = if (selectedCourse == null) CosmicTheme.AccentTeal else CosmicTheme.TextLight, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Active View: Folder Browser vs Global Search Results List
        val isGlobalSearchMode = searchQuery.isNotBlank() || selectedCategory != "All"

        if (!isGlobalSearchMode && selectedProg == null) {
            // Root View: List Programmes as beautiful graphical folder cards
            Text(
                text = "Academic Programmes:",
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.TextLight,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            val programmes = listOf(
                "Computer Science" to Pair("💻", "Software, DBMS, Comp Architecture & Math"),
                "Engineering" to Pair("⚙️", "Calculus, Electronics & Linear Systems"),
                "Business Admin" to Pair("📊", "Accounting, Finance & Operations Management"),
                "Medicine" to Pair("🩺", "Anatomy, Physiology & OSCE Guides")
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(programmes.size) { idx ->
                    val (p, descInfo) = programmes[idx]
                    val (emoji, tagline) = descInfo
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                        modifier = Modifier
                            .fillMaxWidth()
                            .cosmicCard(CosmicTheme.isDark)
                            .clickable { viewModel.selectProgramme(p) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(emoji, fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(tagline, color = CosmicTheme.TextMuted, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CosmicTheme.AccentTeal)
                        }
                    }
                }
            }
        } else if (!isGlobalSearchMode && selectedYr == null) {
            // Year Browser View
            Text(
                text = "Select Study Year for $selectedProg:",
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.TextLight,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val years = listOf("Year 1", "Year 2", "Year 3", "Year 4")
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(years.size) { idx ->
                    val yr = years[idx]
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                        modifier = Modifier
                            .fillMaxWidth()
                            .cosmicCard(CosmicTheme.isDark)
                            .clickable { viewModel.selectYear(yr) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = CosmicTheme.AccentTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(yr, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CosmicTheme.AccentTeal)
                        }
                    }
                }
            }
        } else if (!isGlobalSearchMode && selectedSem == null) {
            // Semester Browser View
            Text(
                text = "Select Semester Term ($selectedYr):",
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.TextLight,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val semesters = listOf("Semester 1", "Semester 2")

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(semesters.size) { idx ->
                    val sem = semesters[idx]
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                        modifier = Modifier
                            .fillMaxWidth()
                            .cosmicCard(CosmicTheme.isDark)
                            .clickable { viewModel.selectSemester(sem) }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = null,
                                tint = CosmicTheme.AccentTeal,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(sem, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = CosmicTheme.AccentTeal)
                        }
                    }
                }
            }
        } else {
            // List Matching Study Resources / Notes files
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isGlobalSearchMode) {
                        "Found ${materials.size} documents in Repository"
                    } else {
                        "Notes in $selectedProg • $selectedYr • $selectedSem"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CosmicTheme.TextMuted,
                    modifier = Modifier.weight(1f)
                )

                TextButton(
                    onClick = {
                        viewModel.selectProgramme(null)
                        viewModel.setMaktabaSearch("")
                        viewModel.selectMaktabaCategory("All")
                    }
                ) {
                    Text("Clear All Filters", color = CosmicTheme.AccentTeal, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (materials.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = CosmicTheme.TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Hakuna study documents yaliyowekwa hapa.",
                            color = CosmicTheme.TextMuted,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gonga kitufe cha upload hapo juu kuchangia notes zako!",
                            color = CosmicTheme.TextMuted,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(materials.size) { index ->
                        val mat = materials[index]
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .cosmicCard(CosmicTheme.isDark)
                                .clickable { viewModel.openMaterialViewer(mat) }
                                .testTag("study_material_card_${mat.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mat.title,
                                            color = CosmicTheme.TextLight,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = mat.description,
                                            color = CosmicTheme.TextMuted,
                                            fontSize = 11.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (mat.fileType == "pdf") Color(0xFFEF4444) else Color(0xFF3B82F6)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = mat.fileType.uppercase(),
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Tags
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                CosmicTheme.AccentTeal.copy(alpha = 0.1f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = mat.courseCode,
                                            color = CosmicTheme.AccentTeal,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .background(
                                                CosmicTheme.Slate700,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = mat.programme,
                                            color = CosmicTheme.TextLight,
                                            fontSize = 9.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.weight(1f))

                                    Text(
                                        text = mat.sizeText,
                                        color = CosmicTheme.TextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                HorizontalDivider(color = CosmicTheme.Slate700)

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(CosmicTheme.Slate700)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "By ${mat.uploadedByName}",
                                            color = CosmicTheme.TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Text(
                                        text = "Tap to read",
                                        color = CosmicTheme.AccentTeal,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Material Dialog uploader overlay
    if (showUploadModal) {
        UploadMaterialDialog(
            activeProg = selectedProg ?: "Computer Science",
            activeYr = selectedYr ?: "Year 2",
            activeSem = selectedSem ?: "Semester 1",
            onDismiss = { showUploadModal = false }
        ) { title, desc, prog, yr, sem, code, fName, fType ->
            viewModel.uploadStudyMaterial(title, desc, prog, yr, sem, code, fName, fType, "1.4 MB")
            showUploadModal = false
        }
    }

    // ⛔ Integrated Storage Viewer Rule: CLICKING VIEW ON STUDY MATERIALS MUST RENDER IN INTERNAL VIEWER PDF PREVIEW MODAL
    // close, download simulation, copy material links
    if (viewingMaterial != null) {
        InternalMaterialViewerModal(material = viewingMaterial!!, onDismiss = { viewModel.openMaterialViewer(null) })
    }
}

@Composable
fun InternalMaterialViewerModal(material: StudyMaterialEntity, onDismiss: () -> Unit) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val customLink = "https://smartcampus.hustle.edu/maktaba/document/appwrite/file_bucket/" + material.fileId

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .border(1.dp, CosmicTheme.AccentTeal, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Red.copy(alpha = 0.2f))
                            .padding(4.dp)
                    ) {
                        Text(material.fileType.uppercase(), color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close viewer", tint = CosmicTheme.TextLight)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Icon(
                    imageVector = Icons.Default.PictureAsPdf,
                    contentDescription = null,
                    tint = CosmicTheme.AccentTeal,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = material.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = CosmicTheme.TextLight,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = material.courseCode + " • " + material.sizeText,
                    fontSize = 12.sp,
                    color = CosmicTheme.TextMuted,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Simulated PDF canvas / text reader preview page to display integrated file content viewer
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CosmicTheme.DarkSlate)
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CosmicTheme.AccentTeal))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("[INTEGRATED ACADEMIC DOC VIEW - PAGE 1]", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = CosmicTheme.AccentTeal, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "PREVIEW CONTENT:\n" + material.description + "\n\n" +
                                    "1. Key Definitions & Class Core Formulas.\n" +
                                    "2. Verified Syllabus matching University Lecture schedule.\n" +
                                    "3. Code Snippets/Solved exercise questions.\n\n" +
                                    "--- File Securely verified by Japhet Mathias (Community Moderator) ---",
                            fontSize = 10.sp,
                            color = CosmicTheme.TextLight,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy material link btn
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(customLink))
                            Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.Slate700),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy Link", fontSize = 11.sp)
                    }

                    // Download simulator btn
                    Button(
                        onClick = {
                            Toast.makeText(context, "Downloading simulated study file: ${material.fileName}", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Download", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun UploadMaterialDialog(activeProg: String, activeYr: String, activeSem: String, onDismiss: () -> Unit, onSave: (String, String, String, String, String, String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var prog by remember { mutableStateOf(activeProg) }
    var yr by remember { mutableStateOf(activeYr) }
    var sem by remember { mutableStateOf(activeSem) }
    var code by remember { mutableStateOf("") }
    var fName by remember { mutableStateOf("") }
    var fType by remember { mutableStateOf("pdf") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Study Resource to Appwrite Bucket", color = CosmicTheme.TextLight, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Document Title") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Short Description") })
                OutlinedTextField(value = code, onValueChange = { code = it }, label = { Text("Course Code (e.g. CS-204)") })
                OutlinedTextField(value = fName, onValueChange = { fName = it }, label = { Text("File Name (e.g. database_notes.pdf)") })

                Text("File Type:", color = CosmicTheme.AccentTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row {
                    listOf("pdf", "docx", "pptx").forEach { t ->
                        val isSel = t == fType
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate700)
                                .clickable { fType = t }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(t.uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(title, desc, prog, yr, sem, code, fName, fType) },
                enabled = title.isNotBlank() && code.isNotBlank() && fName.isNotBlank()
            ) {
                Text("Confirm Upload", fontSize = 12.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ==========================================
// 4. MARKETPLACE & PUBLIC DISCUSSIONS (FACEBOOK STYLE)
// ==========================================
@Composable
fun MarketplaceScreen(viewModel: CampusViewModel, onBackToHome: (() -> Unit)? = null) {
    val listings by viewModel.filteredListings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.marketplaceSearch.collectAsStateWithLifecycle()
    val activeCategory by viewModel.selectedMarketplaceCategory.collectAsStateWithLifecycle()

    val activeListingId by viewModel.activeListingId.collectAsStateWithLifecycle()
    val listingsAll = viewModel.allListings.collectAsStateWithLifecycle().value
    val activeListing = listingsAll.find { it.id == activeListingId }

    var showSellModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (activeListing == null) {
            // Main Marketplace Listing Grid Visuals
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackToHome != null) {
                    IconButton(onClick = onBackToHome, modifier = Modifier.padding(end = 4.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = CosmicTheme.TextLight
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Campus Soko",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTheme.TextLight
                    )
                    Text(
                        text = "Trade items locally directly on campus",
                        fontSize = 12.sp,
                        color = CosmicTheme.TextMuted
                    )
                }

                Button(
                    onClick = { showSellModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.EmeraldAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("marketplace_sell_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sell Item", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Header Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search casio calculators, study desks, tutors...", color = CosmicTheme.TextMuted, fontSize = 12.sp) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CosmicTheme.TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CosmicTheme.TextLight,
                    unfocusedTextColor = CosmicTheme.TextLight,
                    focusedBorderColor = CosmicTheme.AccentTeal,
                    unfocusedBorderColor = CosmicTheme.Slate700
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("marketplace_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Categories horizontal scroller list
            val categories = listOf("All", "Electronics", "Books & Furniture", "Services & Tutoring", "Others")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSel = activeCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate800)
                            .clickable { viewModel.selectMarketplaceCategory(cat) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSel) CosmicTheme.TextLight else CosmicTheme.TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Grid items
            if (listings.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Hakuna bidhaa zilizopatikana kwenye soko.", color = CosmicTheme.TextMuted, fontSize = 13.sp)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(listings, key = { it.id }) { listing ->
                        MarketplaceItemCard(listing) {
                            viewModel.selectActiveListing(listing.id)
                        }
                    }
                }
            }
        } else {
            // Detailed Single Listing detail sheet containing Facebook Marketplace Q&A nesting Q&A accordion replies.
            ListingDetailsView(listing = activeListing, viewModel = viewModel, onClose = { viewModel.selectActiveListing(null) })
        }
    }

    if (showSellModal) {
        SellItemDialog(onDismiss = { showSellModal = false }) { title, desc, price, cat, cond ->
            viewModel.publishListing(title, desc, price, cat, cond, "item_seed_" + (1000..9999).random())
            showSellModal = false
        }
    }
}

@Composable
fun MarketplaceItemCard(listing: MarketplaceListingEntity, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
        modifier = Modifier
            .fillMaxWidth()
            .cosmicCard(CosmicTheme.isDark)
            .clickable { onClick() }
            .testTag("listing_card_${listing.id}")
    ) {
        Column {
            // Thumbnail container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Brush.radialGradient(listOf(Color(0xFF334155), CosmicTheme.DarkSlate))),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val fallbackIcon = when (listing.category.lowercase()) {
                        "electronics" -> Icons.Default.Computer
                        "books & furniture" -> Icons.Default.FolderOpen
                        else -> Icons.Default.DesignServices
                    }
                    Icon(fallbackIcon, contentDescription = null, tint = CosmicTheme.AccentTeal.copy(alpha = 0.6f), modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CosmicTheme.DarkSlate.copy(alpha = 0.8f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(listing.condition, color = CosmicTheme.TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Price Tag highlight top corner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(CosmicTheme.EmeraldAccent)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("$" + String.format("%.1f", listing.price), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }

                // If Sold overlay indicator
                if (listing.status.lowercase() == "sold") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SOLD OUT", color = Color.Red, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = listing.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = CosmicTheme.TextLight
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(10.dp))
                        Text(" Campus", color = CosmicTheme.TextMuted, fontSize = 10.sp)
                    }

                    Text("Active", color = CosmicTheme.EmeraldAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// DETAILED VIEW & PUBLIC DISCUSSIONS Q&A (FACEBOOK MARKTEPLACE STYLE)
// ==========================================
@Composable
fun ListingDetailsView(listing: MarketplaceListingEntity, viewModel: CampusViewModel, onClose: () -> Unit) {
    val comments by viewModel.activeListingComments.collectAsStateWithLifecycle()
    val commentsAccordionState by viewModel.commentAccordionState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var commentText by remember { mutableStateOf("") }
    var replyingToId by remember { mutableStateOf<Int?>(null) }
    var replyText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("listing_details_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Close header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to listings", tint = CosmicTheme.TextLight)
                }
                Text(if (listing.category == "Discussions") "Discussion Thread" else "Item Details", fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight, fontSize = 16.sp)

                // Seller Actions or Delete listing
                if (currentUser?.id == listing.sellerId) {
                    TextButton(onClick = { viewModel.markListingAsSold(listing) }) {
                        Text("Mark Sold", color = CosmicTheme.WarnOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Box(modifier = Modifier.size(36.dp))
                }
            }
        }

        // Listing Hero Details
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(listing.title, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = CosmicTheme.TextLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (listing.category == "Discussions") "Lebo/Label: ${listing.condition}" else "Category: ${listing.category}  |  Condition: ${listing.condition}", 
                        fontSize = 12.sp, 
                        color = CosmicTheme.TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (listing.category != "Discussions") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$" + String.format("%.2f", listing.price),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 22.sp,
                                color = CosmicTheme.EmeraldAccent
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (listing.status == "available") CosmicTheme.EmeraldAccent.copy(alpha = 0.2f) else Color.Red.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = listing.status.uppercase(),
                                    color = if (listing.status == "available") CosmicTheme.EmeraldAccent else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(CosmicTheme.AccentTeal, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Mada iko wazi kwa majadiliano", fontSize = 12.sp, color = CosmicTheme.AccentTeal, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(listing.description, fontSize = 13.sp, color = Color(0xFFE2E8F0), lineHeight = 19.sp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(color = Color(0xFF334155).copy(alpha = 0.5f))

                    Spacer(modifier = Modifier.height(12.dp))

                    // Seller Card line
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CosmicTheme.AccentTeal.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(listing.sellerName.substring(0, 1), color = CosmicTheme.AccentTeal, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(listing.sellerName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CosmicTheme.TextLight)
                                Text(if (listing.category == "Discussions") "Msemaji wa Mada" else "Campus Seller Verified", fontSize = 10.sp, color = CosmicTheme.TextMuted)
                            }
                        }

                        // Compact action button for starting private chats with seller
                        if (currentUser?.id != listing.sellerId && listing.category != "Discussions") {
                            Button(
                                onClick = {
                                    viewModel.openChatWithUser(listing.sellerId, listing.sellerName, listing.sellerPhoto)
                                    // Trigger tab switch automatically to Chats tab (handled outside or by simply activating chat Room)
                                    Toast.makeText(context, "Opening direct inbox thread with ${listing.sellerName}!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("chat_with_seller_btn")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Message, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Chat Seller", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            // If is native seller, allow delete listing
                            Button(
                                onClick = {
                                    viewModel.deleteListingItem(listing.id)
                                    onClose()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Delete Post", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Marketplace discussions Header (Public Q&A)
        item {
            Text(
                text = "Discussions & Public Q&A",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CosmicTheme.AccentTeal,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Input field for writing comments/questions
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Ask seller a public question...", fontSize = 12.sp, color = CosmicTheme.TextMuted) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("market_comment_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicTheme.TextLight,
                            unfocusedTextColor = CosmicTheme.TextLight,
                            focusedBorderColor = CosmicTheme.AccentTeal,
                            unfocusedBorderColor = CosmicTheme.Slate700
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (commentText.isNotBlank()) {
                                viewModel.addListingComment(commentText, null)
                                commentText = ""
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = CosmicTheme.AccentTeal),
                        modifier = Modifier.testTag("submit_comment_btn")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Submit question", tint = Color.White)
                    }
                }
            }
        }

        // Render parent comments list
        val parentComments = comments.filter { it.parentId == null }
        if (parentComments.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No public questions asked yet. Be the first to inquire!", color = CosmicTheme.TextMuted, fontSize = 12.sp)
                }
            }
        } else {
            items(parentComments) { parent ->
                val nestedReplies = comments.filter { it.parentId == parent.id }
                val isExpanded = commentsAccordionState[parent.id] ?: false

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color(0xFF334155).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        // User info line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(CosmicTheme.Slate700),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(parent.userName.substring(0, 1), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(parent.userName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CosmicTheme.TextLight)
                                        if (parent.userId == listing.sellerId) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(CosmicTheme.WarnOrange.copy(alpha = 0.2f))
                                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                                            ) {
                                                Text("Seller", color = CosmicTheme.WarnOrange, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                    Text("Post Question", color = CosmicTheme.TextMuted, fontSize = 9.sp)
                                }
                            }

                            // Report/Delete comment
                            Row {
                                if (parent.isReported) {
                                    Icon(Icons.Default.Report, contentDescription = "Reported comment", tint = Color.Red, modifier = Modifier.size(16.dp))
                                } else {
                                    IconButton(onClick = { viewModel.reportComment(parent.id) }, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Default.Report, contentDescription = "Report", tint = CosmicTheme.TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }

                                if (parent.userId == currentUser?.id || currentUser?.id == "japhet_moderator") {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(onClick = { viewModel.deleteListingComment(parent.id) }, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(parent.text, fontSize = 13.sp, color = CosmicTheme.TextLight)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Controls: Reply Trigger & Collapsed/Expanded Comment Accordion toggle button showing "View X Replies" and rotating chevrons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    replyingToId = if (replyingToId == parent.id) null else parent.id
                                    replyText = ""
                                },
                                modifier = Modifier.height(28.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Reply, contentDescription = null, modifier = Modifier.size(12.dp), tint = CosmicTheme.AccentTeal)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Reply", fontSize = 11.sp, color = CosmicTheme.AccentTeal, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (nestedReplies.isNotEmpty()) {
                                TextButton(
                                    onClick = { viewModel.toggleCommentAccordion(parent.id) },
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (isExpanded) "Hide Replies" else "View ${nestedReplies.size} Replies",
                                            fontSize = 11.sp,
                                            color = CosmicTheme.AccentTeal,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = CosmicTheme.AccentTeal,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Inline Reply Input Display directly matching "replyingToId" selection
                        if (replyingToId == parent.id) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = replyText,
                                    onValueChange = { replyText = it },
                                    placeholder = { Text("Write inline reply...", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 40.dp),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = CosmicTheme.TextLight,
                                        unfocusedTextColor = CosmicTheme.TextLight,
                                        focusedBorderColor = CosmicTheme.AccentTeal,
                                        unfocusedBorderColor = CosmicTheme.Slate700
                                    )
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = {
                                        if (replyText.isNotBlank()) {
                                            viewModel.addListingComment(replyText, parent.id)
                                            replyingToId = null
                                            replyText = ""
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(containerColor = CosmicTheme.AccentTeal),
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send reply", tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        // COLLAPSIBLE NESTED ACCORDION VIEW OF REPLIES
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 14.dp)
                                    .background(Color.Black.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                nestedReplies.forEach { reply ->
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(CosmicTheme.Slate700),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(reply.userName.substring(0, 1), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                }

                                                Spacer(modifier = Modifier.width(6.dp))

                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(reply.userName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = CosmicTheme.TextLight)
                                                    if (reply.userId == listing.sellerId) {
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(3.dp))
                                                                .background(CosmicTheme.WarnOrange.copy(alpha = 0.2f))
                                                                .padding(horizontal = 3.dp, vertical = 1.dp)
                                                        ) {
                                                            Text("Seller", color = CosmicTheme.WarnOrange, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                                        }
                                                    }
                                                }
                                            }

                                            if (reply.userId == currentUser?.id || currentUser?.id == "japhet_moderator") {
                                                IconButton(onClick = { viewModel.deleteListingComment(reply.id) }, modifier = Modifier.size(16.dp)) {
                                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.5f), modifier = Modifier.size(11.dp))
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(reply.text, fontSize = 12.sp, color = Color(0xFFCBD5E1))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Dialog helper to sell item
@Composable
fun SellItemDialog(onDismiss: () -> Unit, onSave: (String, String, Double, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }
    var condition by remember { mutableStateOf("Like New") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish Market Listing", color = CosmicTheme.TextLight) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Listing Title (e.g. CASIO FX)") })
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Full Item Description") })
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price (USD $)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Text("Category:", color = CosmicTheme.AccentTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    listOf("Electronics", "Books & Furniture", "Services & Tutoring").forEach { cat ->
                        val isSel = cat == category
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate700)
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(cat, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text("Condition:", color = CosmicTheme.AccentTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row {
                    listOf("Like New", "Good", "Used").forEach { cond ->
                        val isSel = cond == condition
                        Box(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate700)
                                .clickable { condition = cond }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(cond, color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: 0.0
                    onSave(title, desc, p, category, condition)
                },
                enabled = title.isNotBlank() && priceStr.isNotBlank()
            ) {
                Text("Publish Listing")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DiscussionsScreen(viewModel: CampusViewModel, onBackToHome: (() -> Unit)? = null) {
    val allListings by viewModel.allListings.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeListingId by viewModel.activeListingId.collectAsStateWithLifecycle()
    
    // Filter discussions
    val discussions = remember(allListings) {
        allListings.filter { it.category == "Discussions" }
    }
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("All") }
    var showCreateDialog by remember { mutableStateOf(false) }
    
    val activeListing = allListings.find { it.id == activeListingId }
    
    // Filtering logic
    val filteredDiscussions = remember(discussions, searchQuery, selectedTag) {
        discussions.filter { thread ->
            val matchesQuery = thread.title.contains(searchQuery, ignoreCase = true) ||
                    thread.description.contains(searchQuery, ignoreCase = true)
            val matchesTag = selectedTag == "All" || thread.condition == selectedTag
            matchesQuery && matchesTag
        }
    }
    
    if (activeListing != null) {
        // Detailed Q&A view (using our ListingDetailsView which handles Comments & Discussions!)
        ListingDetailsView(
            listing = activeListing,
            viewModel = viewModel,
            onClose = { viewModel.selectActiveListing(null) }
        )
    } else {
        // Discussions Landing Page
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackToHome != null) {
                    IconButton(onClick = onBackToHome, modifier = Modifier.padding(end = 4.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = CosmicTheme.TextLight
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Campus Discussions",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTheme.TextLight
                    )
                    Text(
                        text = "Ask questions, share advice, and talk with fellow students",
                        fontSize = 12.sp,
                        color = CosmicTheme.TextMuted
                    )
                }
                
                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("create_thread_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ask", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search discussions, assignments, physics...", color = CosmicTheme.TextMuted, fontSize = 12.sp) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CosmicTheme.TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = CosmicTheme.TextLight,
                    unfocusedTextColor = CosmicTheme.TextLight,
                    focusedBorderColor = CosmicTheme.AccentTeal,
                    unfocusedBorderColor = CosmicTheme.Slate700
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("discussion_search_input")
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tags selector
            val tags = listOf("All", "Academics", "Exams", "Campus Life", "Events", "General")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tags) { tag ->
                    val isSel = selectedTag == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.MidnightGradStart)
                            .border(1.dp, if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate700, RoundedCornerShape(12.dp))
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else CosmicTheme.TextLight
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (filteredDiscussions.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = CosmicTheme.TextMuted.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Hakuna majadiliano bado!",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = CosmicTheme.TextLight
                    )
                    Text(
                        text = "Kuwa wa kwanza kuanzisha mada au kuuliza swali!",
                        fontSize = 12.sp,
                        color = CosmicTheme.TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredDiscussions) { thread ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CosmicTheme.Slate700, RoundedCornerShape(16.dp))
                                .clickable { viewModel.selectActiveListing(thread.id) },
                            colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(CosmicTheme.DevPurple.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = thread.sellerName.take(1).uppercase(),
                                                color = CosmicTheme.DevPurple,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = thread.sellerName,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicTheme.TextLight
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CosmicTheme.SubtleAccent)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = thread.condition, // our tag
                                            color = CosmicTheme.AccentTeal,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                    }
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = thread.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CosmicTheme.TextLight
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = thread.description,
                                    fontSize = 12.sp,
                                    color = CosmicTheme.TextMuted,
                                    maxLines = 2,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Divider(color = CosmicTheme.Zinc800, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Tap to participate & reply",
                                        fontSize = 11.sp,
                                        color = CosmicTheme.AccentTeal,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = null,
                                        tint = CosmicTheme.TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (showCreateDialog) {
        var threadTitle by remember { mutableStateOf("") }
        var threadDesc by remember { mutableStateOf("") }
        var threadTag by remember { mutableStateOf("General") }
        
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text("Anzisha Majadiliano", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = threadTitle,
                        onValueChange = { threadTitle = it },
                        label = { Text("Mada / Title") },
                        placeholder = { Text("E.g., Physics exam study guide") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicTheme.AccentTeal,
                            unfocusedBorderColor = CosmicTheme.Slate700,
                            focusedLabelColor = CosmicTheme.AccentTeal
                        )
                    )
                    
                    OutlinedTextField(
                        value = threadDesc,
                        onValueChange = { threadDesc = it },
                        label = { Text("Maelezo / Details") },
                        placeholder = { Text("E.g., Anyone want to team up to study chapter 4?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CosmicTheme.AccentTeal,
                            unfocusedBorderColor = CosmicTheme.Slate700,
                            focusedLabelColor = CosmicTheme.AccentTeal
                        )
                    )
                    
                    Text("Chagua Lebo / Select Tag:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextMuted)
                    
                    val tagsList = listOf("Academics", "Exams", "Campus Life", "Events", "General")
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        tagsList.forEach { tag ->
                            val isSelected = threadTag == tag
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CosmicTheme.AccentTeal.copy(alpha = 0.2f) else Color.Transparent)
                                    .border(1.dp, if (isSelected) CosmicTheme.AccentTeal else CosmicTheme.Slate800, RoundedCornerShape(8.dp))
                                    .clickable { threadTag = tag }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(tag, fontSize = 10.sp, color = if (isSelected) CosmicTheme.AccentTeal else CosmicTheme.TextLight)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (threadTitle.isNotBlank() && threadDesc.isNotBlank()) {
                            viewModel.publishListing(
                                title = threadTitle,
                                description = threadDesc,
                                price = 0.0,
                                category = "Discussions",
                                condition = threadTag,
                                imageSeed = "forum"
                            )
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                    enabled = threadTitle.isNotBlank() && threadDesc.isNotBlank()
                ) {
                    Text("Anzisha")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Ghairi", color = CosmicTheme.TextMuted)
                }
            },
            containerColor = CosmicTheme.MidnightGradStart
        )
    }
}

// ==========================================
// 5. PRIVATE MESSENGER (CHAT ROOM & INSTANT INBOX)
// ==========================================
@Composable
fun ChatsScreen(viewModel: CampusViewModel, onBackToHome: (() -> Unit)? = null) {
    val chatRooms by viewModel.chatRooms.collectAsStateWithLifecycle()
    val activeRoomId by viewModel.activeChatRoomId.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var searchFieldText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (activeRoomId == null) {
            // Render Inbox list of conversations with iOS inspired Title and Search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackToHome != null) {
                    IconButton(onClick = onBackToHome, modifier = Modifier.padding(end = 4.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = CosmicTheme.TextLight,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Text(
                    text = "Conversations",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = CosmicTheme.TextLight,
                    letterSpacing = (-0.5).sp
                )
            }

            // iOS Inspired subtle Search input field
            OutlinedTextField(
                value = searchFieldText,
                onValueChange = { searchFieldText = it },
                placeholder = { Text("Search messages", color = CosmicTheme.TextMuted, fontSize = 14.sp) },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CosmicTheme.TextMuted, modifier = Modifier.size(18.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (CosmicTheme.isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7),
                    unfocusedContainerColor = if (CosmicTheme.isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = CosmicTheme.TextLight,
                    unfocusedTextColor = CosmicTheme.TextLight
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("chats_search_field")
            )

            Spacer(modifier = Modifier.height(10.dp))

            val filteredRooms = remember(chatRooms, searchFieldText) {
                chatRooms.filter { it.participantName.contains(searchFieldText, ignoreCase = true) || it.lastMessage.contains(searchFieldText, ignoreCase = true) }
            }

            if (filteredRooms.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("No active conversations.", color = CosmicTheme.TextMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(filteredRooms) { room ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.selectActiveChatRoom(room.id) }
                                .padding(vertical = 12.dp)
                                .testTag("chatroom_item_${room.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Avatar circle of recipient
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (room.participantId == "japhet_moderator") {
                                                CosmicTheme.DevPurple.copy(alpha = 0.15f)
                                            } else {
                                                CosmicTheme.AccentTeal.copy(alpha = 0.15f)
                                            }
                                        )
                                        .border(
                                            width = 1.5.dp,
                                            color = if (room.participantId == "japhet_moderator") CosmicTheme.DevPurple else CosmicTheme.AccentTeal,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = room.participantName.substring(0, 1).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicTheme.TextLight,
                                        fontSize = 16.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = room.participantName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = CosmicTheme.TextLight
                                            )
                                            if (room.participantId == "japhet_moderator") {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(CosmicTheme.DevPurple.copy(alpha = 0.2f))
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text("Moderator", color = CosmicTheme.DevPurple, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Active",
                                                fontSize = 11.sp,
                                                color = CosmicTheme.TextMuted
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = CosmicTheme.TextMuted.copy(alpha = 0.6f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = room.lastMessage,
                                            fontSize = 13.sp,
                                            color = CosmicTheme.TextMuted,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )

                                        if (room.unreadCount > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .padding(start = 8.dp)
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF007AFF)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(room.unreadCount.toString(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Divider(
                                color = if (CosmicTheme.isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA),
                                thickness = 0.5.dp,
                                modifier = Modifier.padding(start = 60.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Detailed Chat Screen View for direct exchanges
            val activeRoom = chatRooms.find { it.id == activeRoomId }
            if (activeRoom != null) {
                ActiveChatView(room = activeRoom, viewModel = viewModel) {
                    viewModel.selectActiveChatRoom(null)
                }
            }
        }
    }
}

@Composable
fun ActiveChatView(room: ChatRoomEntity, viewModel: CampusViewModel, onBack: () -> Unit) {
    val messages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("active_chat_layout")
    ) {
        // Chat Header banner (iOS styling)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(if (CosmicTheme.isDark) Color(0xCC1C1C1E) else Color(0xCCE5E5EA))
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                    contentDescription = "Back", 
                    tint = Color(0xFF007AFF) // standard iOS blue back
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f))
                    .border(1.dp, CosmicTheme.AccentTeal, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(room.participantName.substring(0, 1).uppercase(), color = CosmicTheme.AccentTeal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(room.participantName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CosmicTheme.TextLight)
                Text(
                    text = if (room.participantId == "japhet_moderator") "Community Moderator" else "Campus Member • Active",
                    fontSize = 11.sp,
                    color = CosmicTheme.TextMuted
                )
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(CosmicTheme.DarkSlate)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUser?.id
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMe) Color(0xFF007AFF) else (if (CosmicTheme.isDark) Color(0xFF262629) else Color(0xFFE5E5EA))
                        ),
                        modifier = Modifier.widthIn(max = 290.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                            Text(
                                text = msg.text, 
                                color = if (isMe) Color.White else CosmicTheme.TextLight, 
                                fontSize = 14.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isMe) "Wewe" else room.participantName,
                                color = if (isMe) Color.White.copy(alpha = 0.7f) else CosmicTheme.TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = if (isMe) TextAlign.End else TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Message input row mimicking iMessage
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("iMessage", fontSize = 14.sp, color = CosmicTheme.TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_message_input"),
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (CosmicTheme.isDark) Color(0xFF1E1E1E) else Color(0xFFF2F2F7),
                    unfocusedContainerColor = if (CosmicTheme.isDark) Color(0xFF1E1E1E) else Color(0xFFF2F2F7),
                    focusedTextColor = CosmicTheme.TextLight,
                    unfocusedTextColor = CosmicTheme.TextLight,
                    focusedBorderColor = if (CosmicTheme.isDark) Color(0xFF3A3A3C) else Color(0xFFC7C7CC),
                    unfocusedBorderColor = if (CosmicTheme.isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendChatMessage(messageText)
                        messageText = ""
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFF007AFF)),
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send, 
                    contentDescription = "Send", 
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ==========================================
// 6. PROFILE & SYSTEM NOTIFICATIONS
// ==========================================
@Composable
fun ProfileScreen(viewModel: CampusViewModel, onBackToHome: (() -> Unit)? = null) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()

    var showNotifBoard by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!showNotifBoard) {
            // Master profile details layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onBackToHome != null) {
                    IconButton(onClick = onBackToHome, modifier = Modifier.padding(end = 4.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Home",
                            tint = CosmicTheme.TextLight
                        )
                    }
                }

                Text(
                    text = "My Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.TextLight,
                    modifier = Modifier.weight(1f)
                )

                // Compact trigger button for notifications inbox board
                val unreadNotifs = notifications.count { !it.read }
                Box(
                    modifier = Modifier.wrapContentSize()
                ) {
                    IconButton(
                        onClick = {
                            viewModel.markNotificationsAsRead()
                            showNotifBoard = true
                        }
                    ) {
                        Icon(
                            imageVector = if (unreadNotifs > 0) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Show Alerts Board",
                            tint = if (unreadNotifs > 0) CosmicTheme.WarnOrange else CosmicTheme.TextLight
                        )
                    }

                    if (unreadNotifs > 0) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(unreadNotifs.toString(), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // User Card Info representation
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                modifier = Modifier
                    .fillMaxWidth()
                    .cosmicCard(CosmicTheme.isDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(74.dp)
                            .clip(CircleShape)
                            .background(CosmicTheme.AccentTeal.copy(alpha = 0.2f))
                            .border(2.dp, CosmicTheme.AccentTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (currentUser?.displayName ?: "S").substring(0, 1).uppercase(),
                            color = CosmicTheme.AccentTeal,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentUser?.displayName ?: "Toleo Mozo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = CosmicTheme.TextLight
                    )

                    Text(
                        text = currentUser?.username ?: "@student",
                        fontSize = 13.sp,
                        color = CosmicTheme.AccentTeal,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = currentUser?.university ?: "University of Science & Tech",
                        fontSize = 11.sp,
                        color = CosmicTheme.TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Stats details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                    modifier = Modifier
                        .weight(1f)
                        .cosmicCard(CosmicTheme.isDark)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Reminders Set", color = CosmicTheme.TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.routines.collectAsStateWithLifecycle().value.count { it.remindersEnabled }.toString(),
                            color = CosmicTheme.WarnOrange,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                    modifier = Modifier
                        .weight(1f)
                        .cosmicCard(CosmicTheme.isDark)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Active Soko Items", color = CosmicTheme.TextMuted, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = viewModel.allListings.collectAsStateWithLifecycle().value.count { it.sellerId == currentUser?.id }.toString(),
                            color = CosmicTheme.EmeraldAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // About block
            Surface(
                color = CosmicTheme.MidnightGradStart,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .cosmicCard(CosmicTheme.isDark)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Msaada wa Mfumo", fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Smart Campus is an offline-ready helper app built to handle classes schedules, peer materials, student deals and private messaging instantly without lags.",
                        color = CosmicTheme.TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Theme Toggle setting block
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            Surface(
                color = CosmicTheme.MidnightGradStart,
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (CosmicTheme.isDark) Color(0x0DFFFFFF) else Color(0xFFF4F4F5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme Icon",
                                tint = CosmicTheme.AccentTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Muonekano wa App",
                                fontWeight = FontWeight.Bold,
                                color = CosmicTheme.TextLight,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isDarkTheme) "Toleo la Kiza (Dark Mode)" else "Toleo la Mwanga (Light Mode)",
                                color = CosmicTheme.TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { viewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CosmicTheme.AccentTeal,
                            checkedTrackColor = CosmicTheme.AccentTeal.copy(alpha = 0.3f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("logout_button")
            ) {
                Text("Logout Account", color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            // Notifications Board Overlay view
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showNotifBoard = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close board", tint = CosmicTheme.TextLight)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text("Campus Alerts & Logs", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = CosmicTheme.TextLight)
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (notifications.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Hakuna arifa au kumbukumbu zilizotumwa kwako sasa hivi.", color = CosmicTheme.TextMuted, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(notifications) { n ->
                        val alertColor = when (n.type.lowercase()) {
                            "class" -> CosmicTheme.WarnOrange
                            "market" -> CosmicTheme.EmeraldAccent
                            else -> CosmicTheme.AccentTeal
                        }
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(alertColor)
                                        .padding(top = 4.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(n.title, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(n.message, color = CosmicTheme.TextMuted, fontSize = 11.sp, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Time calculations and helpers
data class ClassTimeStatus(
    val activeClass: RoutineEntity?,
    val upcomingClass: RoutineEntity?,
    val minutesLeft: Int,
    val minutesUntil: Int,
    val isEndOfDay: Boolean
)

fun parseClassTimes(timeStr: String): Pair<Int, Int>? {
    try {
        val parts = timeStr.split("-").map { it.trim() }
        if (parts.size != 2) return null
        
        fun timeToMinutes(t: String): Int {
            val clean = t.uppercase().replace(" ", "")
            val isPM = clean.contains("PM")
            val isAM = clean.contains("AM")
            val timePart = clean.replace("PM", "").replace("AM", "")
            val hourMin = timePart.split(":")
            if (hourMin.size != 2) return 0
            var hr = hourMin[0].toIntOrNull() ?: 0
            val min = hourMin[1].toIntOrNull() ?: 0
            
            if (isPM && hr < 12) hr += 12
            if (isAM && hr == 12) hr = 0
            return hr * 60 + min
        }
        
        return Pair(timeToMinutes(parts[0]), timeToMinutes(parts[1]))
    } catch (e: Exception) {
        return null
    }
}

fun checkClassStatus(routinesToday: List<RoutineEntity>): ClassTimeStatus {
    val cal = java.util.Calendar.getInstance()
    val currHour = cal.get(java.util.Calendar.HOUR_OF_DAY)
    val currMin = cal.get(java.util.Calendar.MINUTE)
    val currMinutes = currHour * 60 + currMin
    
    var activeClass: RoutineEntity? = null
    var upcomingClass: RoutineEntity? = null
    var minLeft = 0
    var minUntil = 999999
    
    for (r in routinesToday) {
        val range = parseClassTimes(r.times) ?: continue
        val startTime = range.first
        val endTime = range.second
        
        if (currMinutes in startTime until endTime) {
            activeClass = r
            minLeft = endTime - currMinutes
            break
        } else if (startTime > currMinutes) {
            val diff = startTime - currMinutes
            if (diff < minUntil) {
                minUntil = diff
                upcomingClass = r
            }
        }
    }
    
    return ClassTimeStatus(
        activeClass = activeClass,
        upcomingClass = upcomingClass,
        minutesLeft = minLeft,
        minutesUntil = minUntil,
        isEndOfDay = activeClass == null && upcomingClass == null
    )
}
