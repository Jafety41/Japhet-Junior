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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.provider.OpenableColumns
import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.data.*
import com.example.ui.viewmodel.CampusViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

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
    object Academic : AppTab("academic", "Academic", Icons.Default.School)
    object Campus : AppTab("campus", "Campus", Icons.Default.Groups)
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
        AppTab.Academic,
        AppTab.Campus,
        AppTab.Profile
    )
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        shape = RoundedCornerShape(24.dp),
        color = (if (CosmicTheme.isDark) Color(0xFF09090B) else Color(0xFFFFFFFF)).copy(alpha = 0.7f),
        border = BorderStroke(
            1.dp, 
            if (CosmicTheme.isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)
        ),
        shadowElevation = 16.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
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
fun NavigationOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (CosmicTheme.isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.TextLight
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = CosmicTheme.TextMuted,
                    fontWeight = FontWeight.Normal
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = CosmicTheme.TextMuted,
                modifier = Modifier.size(20.dp)
            )
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
fun AcademicScreen(
    viewModel: CampusViewModel,
    selectedSubTab: Int,
    onSubTabChanged: (Int) -> Unit,
    onBackToHome: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                val subTabs = listOf("Schedule Timetable 📅", "Library Archive 📚")
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
                            .clickable { onSubTabChanged(index) }
                            .graphicsLayer {
                                shadowElevation = if (isSel) 2f else 0f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) CosmicTheme.TextLight else CosmicTheme.TextMuted
                        )
                    }
                }
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            if (selectedSubTab == 0) {
                ScheduleScreen(viewModel = viewModel, isPlannerMode = true, onTabSelect = { onBackToHome() })
            } else {
                MaktabaScreen(viewModel = viewModel, onBackToHome = onBackToHome)
            }
        }
    }
}

@Composable
fun CampusScreen(
    viewModel: CampusViewModel,
    selectedSubTab: Int,
    onSubTabChanged: (Int) -> Unit,
    onBackToHome: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
                val subTabs = listOf("Soko Marketplace 🛒", "Chats Forum 💬")
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
                            .clickable { onSubTabChanged(index) }
                            .graphicsLayer {
                                shadowElevation = if (isSel) 2f else 0f
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) CosmicTheme.TextLight else CosmicTheme.TextMuted
                        )
                    }
                }
            }
        }
        
        Box(modifier = Modifier.weight(1f)) {
            if (selectedSubTab == 0) {
                MarketplaceScreen(viewModel = viewModel, onBackToHome = onBackToHome)
            } else {
                TalkScreen(viewModel = viewModel, onBackToHome = onBackToHome)
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
                AppTab.Academic,
                AppTab.Campus,
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
@OptIn(ExperimentalMaterial3Api::class)
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
    var academicSubTab by remember { mutableStateOf(0) }
    var campusSubTab by remember { mutableStateOf(0) }
    var bottomBarVisible by remember { mutableStateOf(true) }
    var showAcademicSheet by remember { mutableStateOf(false) }
    var showCampusSheet by remember { mutableStateOf(false) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = available.y
                if (delta < -12f) {
                    bottomBarVisible = false
                } else if (delta > 12f) {
                    bottomBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val handleTabSelect: (AppTab) -> Unit = { tab ->
        when (tab) {
            AppTab.Timetable -> {
                activeTab = AppTab.Academic
                academicSubTab = 0
            }
            AppTab.Maktaba -> {
                activeTab = AppTab.Academic
                academicSubTab = 1
            }
            AppTab.Marketplace -> {
                activeTab = AppTab.Campus
                campusSubTab = 0
            }
            AppTab.Talk -> {
                activeTab = AppTab.Campus
                campusSubTab = 1
            }
            else -> {
                activeTab = tab
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.tabNavigationRequest.collect { tab ->
            handleTabSelect(tab)
        }
    }

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
                        onTabSelect = handleTabSelect,
                        currentUser = currentUser
                    )
                    VerticalDivider(color = CosmicTheme.Slate700, modifier = Modifier.fillMaxHeight().width(1.dp))
                }

                val shouldHideBottomBar = (viewingMaterial != null) || (activeListingId != null) || (activeChatRoomId != null)

                Scaffold(
                    bottomBar = {
                        if (!isExpanded && !shouldHideBottomBar) {
                            AnimatedVisibility(
                                visible = (activeTab == AppTab.Home) && bottomBarVisible,
                                enter = slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                ) + fadeIn(animationSpec = tween(150)),
                                exit = slideOutVertically(
                                    targetOffsetY = { it },
                                    animationSpec = spring(stiffness = Spring.StiffnessMedium)
                                ) + fadeOut(animationSpec = tween(150))
                            ) {
                                GlassmorphicBottomBar(
                                    activeTab = activeTab,
                                    onTabSelect = { tab ->
                                        if (tab == AppTab.Academic) {
                                            showAcademicSheet = true
                                        } else if (tab == AppTab.Campus) {
                                            showCampusSheet = true
                                        } else {
                                            handleTabSelect(tab)
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(CosmicTheme.DarkSlate)
                            .nestedScroll(nestedScrollConnection)
                            .padding(innerPadding)
                    ) {
                        Crossfade(targetState = activeTab, label = "tabTransition") { tab ->
                            when (tab) {
                                AppTab.Home -> ScheduleScreen(viewModel = viewModel, isPlannerMode = false, onTabSelect = handleTabSelect)
                                AppTab.Academic -> AcademicScreen(viewModel = viewModel, selectedSubTab = academicSubTab, onSubTabChanged = { academicSubTab = it }, onBackToHome = { handleTabSelect(AppTab.Home) })
                                AppTab.Campus -> CampusScreen(viewModel = viewModel, selectedSubTab = campusSubTab, onSubTabChanged = { campusSubTab = it }, onBackToHome = { handleTabSelect(AppTab.Home) })
                                AppTab.Profile -> ProfileScreen(viewModel, onBackToHome = { handleTabSelect(AppTab.Home) })
                                // Fallbacks for legacy routing safety
                                AppTab.Timetable -> AcademicScreen(viewModel = viewModel, selectedSubTab = 0, onSubTabChanged = { academicSubTab = it }, onBackToHome = { handleTabSelect(AppTab.Home) })
                                AppTab.Maktaba -> AcademicScreen(viewModel = viewModel, selectedSubTab = 1, onSubTabChanged = { academicSubTab = it }, onBackToHome = { handleTabSelect(AppTab.Home) })
                                AppTab.Marketplace -> CampusScreen(viewModel = viewModel, selectedSubTab = 0, onSubTabChanged = { campusSubTab = it }, onBackToHome = { handleTabSelect(AppTab.Home) })
                                AppTab.Talk -> CampusScreen(viewModel = viewModel, selectedSubTab = 1, onSubTabChanged = { campusSubTab = it }, onBackToHome = { handleTabSelect(AppTab.Home) })
                            }
                        }
                    }
                }

                // Modern iOS-Style "Half-Screen" Bottom Sheets for Academic and Campus consolidation
                if (showAcademicSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showAcademicSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        containerColor = CosmicTheme.MidnightGradStart,
                        contentColor = CosmicTheme.TextLight,
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth().testTag("academic_bottom_sheet"),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 24.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Academic Hub 🎓",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTheme.TextLight,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            NavigationOption(
                                title = "Schedule Timetable",
                                subtitle = "Ratiba ya masomo, vipindi na mitihani yako ya chuo",
                                icon = Icons.Default.Today,
                                iconColor = CosmicTheme.PrimaryAccent,
                                onClick = {
                                    showAcademicSheet = false
                                    handleTabSelect(AppTab.Timetable)
                                }
                            )

                            NavigationOption(
                                title = "Library Archive (Maktaba)",
                                subtitle = "Vitabu, past papers na material yote ya masomo",
                                icon = Icons.Default.FolderOpen,
                                iconColor = CosmicTheme.EmeraldAccent,
                                onClick = {
                                    showAcademicSheet = false
                                    handleTabSelect(AppTab.Maktaba)
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                if (showCampusSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showCampusSheet = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                        containerColor = CosmicTheme.MidnightGradStart,
                        contentColor = CosmicTheme.TextLight,
                        tonalElevation = 8.dp,
                        modifier = Modifier.fillMaxWidth().testTag("campus_bottom_sheet"),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 24.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Campus Community 🤝",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTheme.TextLight,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            NavigationOption(
                                title = "Soko Marketplace",
                                subtitle = "Nunua na uza vifaa vya chuo kwa bei nafuu",
                                icon = Icons.Default.Storefront,
                                iconColor = CosmicTheme.WarnOrange,
                                onClick = {
                                    showCampusSheet = false
                                    handleTabSelect(AppTab.Marketplace)
                                }
                            )

                            NavigationOption(
                                title = "Chats Forum (Campus Talks)",
                                subtitle = "Sogoa mada na group discussions za chuo chenu",
                                icon = Icons.Default.Forum,
                                iconColor = CosmicTheme.AccentTeal,
                                onClick = {
                                    showCampusSheet = false
                                    handleTabSelect(AppTab.Talk)
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
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

    // Dialog state for support & forgot password help
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Onboarding carousel slides visible for guests only
    var showCarousel by remember(user) { mutableStateOf(user == null) }
    var carouselPage by remember { mutableStateOf(0) }

    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(isSignUpMode) {
        viewModel.clearAuthError()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CosmicTheme.DarkSlate)
            .drawBehind {
                // Soft elegant orange/teal radial glow blobs to avoid generic plain looks
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(CosmicTheme.AccentTeal.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    radius = size.minDimension * 0.8f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.25f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(CosmicTheme.DevPurple.copy(alpha = 0.10f), Color.Transparent)
                    ),
                    radius = size.minDimension * 0.9f,
                    center = androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.75f)
                )
            }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Subtle floating theme toggle in the top-right corner to allow PRE-LOGIN theme switching
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(12.dp)
                .clip(CircleShape)
                .background(if (isDarkTheme) Color(0x1AFFFFFF) else Color(0x0F000000))
                .clickable { viewModel.toggleTheme() }
                .padding(10.dp)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = "Switch Theme",
                tint = CosmicTheme.AccentTeal,
                modifier = Modifier.size(20.dp)
            )
        }

        // Support Dialogs
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Rudisha Nenosiri 🔑", fontWeight = FontWeight.Black, fontSize = 18.sp, color = CosmicTheme.TextLight) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Kama umesahau nenosiri lako la Smart Campus, andika barua pepe yako ya chuo chini ili utumiwe maelekezo ya kulibadili.",
                            fontSize = 13.sp,
                            color = CosmicTheme.TextMuted
                        )
                        var resetEmail by remember { mutableStateOf("") }
                        OutlinedTextField(
                            value = resetEmail,
                            onValueChange = { resetEmail = it },
                            label = { Text("Barua Pepe (Email)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showForgotPasswordDialog = false
                            Toast.makeText(context, "Tumekutumia barua pepe ya kurudisha nenosiri! Kagua inbox au spam folder.", Toast.LENGTH_LONG).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal)
                    ) {
                        Text("Tuma Maelekezo")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForgotPasswordDialog = false }) {
                        Text("Ghairi", color = CosmicTheme.TextMuted)
                    }
                },
                containerColor = CosmicTheme.MidnightGradStart,
                shape = RoundedCornerShape(24.dp)
            )
        }

        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text("Msaada & Support 🤝", fontWeight = FontWeight.Black, fontSize = 18.sp, color = CosmicTheme.TextLight) },
                text = {
                    Text(
                        "Smart Campus ni mfumo unaokurahisishia maisha yako ya chuo kwa kukubali ratiba za masomo, Notes kudesa, na soko huru.\n\n" +
                        "Endapo umekwama kujisajili au kuingia, wasiliana na Lead Architect Japhet Mathias kupitia:\n" +
                        "📧 dev@smartcampus.com\n" +
                        "📞 +255 712 345 678\n\n" +
                        "Tutatatua tatizo lako mara moja. Karibu sana!",
                        fontSize = 13.sp,
                        color = CosmicTheme.TextMuted,
                        lineHeight = 19.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showHelpDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal)
                    ) {
                        Text("Nimeelewa")
                    }
                },
                containerColor = CosmicTheme.MidnightGradStart,
                shape = RoundedCornerShape(24.dp)
            )
        }

        if (showCarousel && user == null) {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .wrapContentHeight()
                    .border(
                        BorderStroke(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f)))),
                        RoundedCornerShape(28.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("🎓", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Campus",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = CosmicTheme.TextLight,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Text(
                        text = "Kusaidia elimu na biashara za wanafunzi",
                        fontSize = 11.sp,
                        color = CosmicTheme.AccentTeal,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedContent(
                        targetState = carouselPage,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(200))
                        },
                        label = "carousel_transition"
                    ) { page ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            when (page) {
                                0 -> {
                                    Canvas(modifier = Modifier.size(110.dp)) {
                                        drawRoundRect(
                                            color = CosmicTheme.AccentTeal.copy(alpha = 0.12f),
                                            size = size,
                                            cornerRadius = CornerRadius(20f, 20f)
                                        )
                                        drawRoundRect(
                                            color = CosmicTheme.AccentTeal,
                                            size = size.copy(height = size.height * 0.28f),
                                            cornerRadius = CornerRadius(20f, 20f)
                                        )
                                        drawCircle(color = Color.White.copy(alpha = 0.5f), radius = 5f, center = Offset(size.width * 0.25f, size.height * 0.14f))
                                        drawCircle(color = Color.White.copy(alpha = 0.5f), radius = 5f, center = Offset(size.width * 0.5f, size.height * 0.14f))
                                        drawCircle(color = Color.White.copy(alpha = 0.5f), radius = 5f, center = Offset(size.width * 0.75f, size.height * 0.14f))

                                        drawRoundRect(
                                            color = CosmicTheme.AccentTeal.copy(alpha = 0.35f),
                                            topLeft = Offset(15f, size.height * 0.38f),
                                            size = size.copy(width = size.width - 30f, height = size.height * 0.20f),
                                            cornerRadius = CornerRadius(8f, 8f)
                                        )
                                        drawRoundRect(
                                            color = CosmicTheme.DevPurple.copy(alpha = 0.25f),
                                            topLeft = Offset(15f, size.height * 0.66f),
                                            size = size.copy(width = size.width - 30f, height = size.height * 0.20f),
                                            cornerRadius = CornerRadius(8f, 8f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Text(
                                        text = "Ratiba Imara za Masomo 🗓️",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CosmicTheme.TextLight,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Panga na ufuate vipindi vya chuo, ratiba za masomo na ukumbusho kwa wakati mmoja bila fujo kiganjani mwako.",
                                        fontSize = 12.sp,
                                        color = CosmicTheme.TextMuted,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                                1 -> {
                                    Canvas(modifier = Modifier.size(110.dp)) {
                                        drawRoundRect(
                                            color = CosmicTheme.DevPurple,
                                            topLeft = Offset(12f, size.height * 0.65f),
                                            size = size.copy(width = size.width - 24f, height = size.height * 0.22f),
                                            cornerRadius = CornerRadius(8f, 8f)
                                        )
                                        drawRoundRect(
                                            color = Color.White.copy(alpha = 0.25f),
                                            topLeft = Offset(24f, size.height * 0.69f),
                                            size = size.copy(width = 30f, height = size.height * 0.14f),
                                            cornerRadius = CornerRadius(4f, 4f)
                                        )

                                        drawRoundRect(
                                            color = CosmicTheme.AccentTeal,
                                            topLeft = Offset(22f, size.height * 0.40f),
                                            size = size.copy(width = size.width - 44f, height = size.height * 0.22f),
                                            cornerRadius = CornerRadius(8f, 8f)
                                        )
                                        drawRoundRect(
                                            color = Color.White.copy(alpha = 0.25f),
                                            topLeft = Offset(32f, size.height * 0.44f),
                                            size = size.copy(width = 30f, height = size.height * 0.14f),
                                            cornerRadius = CornerRadius(4f, 4f)
                                        )

                                        drawRoundRect(
                                            color = CosmicTheme.EmeraldAccent,
                                            topLeft = Offset(32f, size.height * 0.16f),
                                            size = size.copy(width = size.width - 64f, height = size.height * 0.22f),
                                            cornerRadius = CornerRadius(8f, 8f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Text(
                                        text = "Maktaba ya Kudesa Notes 📖",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CosmicTheme.TextLight,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Pata muhtasari mzuri (notes), mitihani iliyopita (past papers), na vitabu vilivyowekwa na wanafunzi wenzako kurahisisha kujiandaa.",
                                        fontSize = 12.sp,
                                        color = CosmicTheme.TextMuted,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                                2 -> {
                                    Canvas(modifier = Modifier.size(110.dp)) {
                                        drawRoundRect(
                                            color = CosmicTheme.AccentTeal.copy(alpha = 0.15f),
                                            topLeft = Offset(10f, size.height * 0.28f),
                                            size = size.copy(width = size.width - 20f, height = size.height * 0.62f),
                                            cornerRadius = CornerRadius(10f, 10f)
                                        )
                                        drawRoundRect(
                                            color = CosmicTheme.AccentTeal,
                                            topLeft = Offset(5f, size.height * 0.16f),
                                            size = size.copy(width = size.width - 10f, height = size.height * 0.20f),
                                            cornerRadius = CornerRadius(6f, 6f)
                                        )
                                        drawRoundRect(
                                            color = CosmicTheme.DarkSlate,
                                            topLeft = Offset(28f, size.height * 0.52f),
                                            size = size.copy(width = size.width - 56f, height = size.height * 0.38f),
                                            cornerRadius = CornerRadius(4f, 4f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(18.dp))
                                    Text(
                                        text = "Soko la Wanafunzi (Soko) 🛍️",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = CosmicTheme.TextLight,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Nunua au uza vitabu, simu, laptop na bidhaa nyingine poa kwa usalama kamili kutoka kwa mwanafunzi mwenzako.",
                                        fontSize = 12.sp,
                                        color = CosmicTheme.TextMuted,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { index ->
                            val active = index == carouselPage
                            val widthVal by animateDpAsState(targetValue = if (active) 16.dp else 6.dp, label = "dot")
                            Box(
                                modifier = Modifier
                                    .size(height = 6.dp, width = widthVal)
                                    .clip(CircleShape)
                                    .background(if (active) CosmicTheme.AccentTeal else CosmicTheme.TextMuted.copy(alpha = 0.4f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showCarousel = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = CosmicTheme.TextMuted)
                        ) {
                            Text("Ruka (Skip)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                if (carouselPage < 2) {
                                    carouselPage += 1
                                } else {
                                    showCarousel = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (carouselPage < 2) "Endelea ➔" else "Tuanze Sasa 🚀",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .wrapContentHeight()
                    .border(
                        BorderStroke(1.dp, Brush.linearGradient(listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.02f)))),
                        RoundedCornerShape(24.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = 0.92f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                if (step == 0) {
                    // Login / Signup Header
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text(
                            text = "🎓",
                            fontSize = 32.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Smart Campus",
                            fontWeight = FontWeight.Black,
                            fontSize = 30.sp,
                            color = CosmicTheme.TextLight,
                            letterSpacing = (-0.5).sp
                        )
                    }
                    Text(
                        text = "Ukurasa wa Elimu na Biashara ya Wanafunzi",
                        fontSize = 12.sp,
                        color = CosmicTheme.AccentTeal,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Tab selector row for Sign In vs Sign Up
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .background(if (CosmicTheme.isDark) Color(0x13FFFFFF) else Color(0xFFF1F5F9), RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isSignUpMode) CosmicTheme.AccentTeal else Color.Transparent)
                                .clickable(enabled = !authLoading) { isSignUpMode = false }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Ingia Sasa",
                                color = if (!isSignUpMode) Color.White else CosmicTheme.TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSignUpMode) CosmicTheme.AccentTeal else Color.Transparent)
                                .clickable(enabled = !authLoading) { isSignUpMode = true }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Jisajili",
                                color = if (isSignUpMode) Color.White else CosmicTheme.TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Display errors beautifully
                    authError?.let { err ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicTheme.WarnOrange.copy(alpha = 0.12f)),
                            border = BorderStroke(1.dp, CosmicTheme.WarnOrange.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            shape = RoundedCornerShape(12.dp)
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
                                    modifier = Modifier.size(18.dp)
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
                                    contentDescription = "Funga-arifa",
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
                            label = { Text("Display Name (E.g. Kevin S.)") },
                            singleLine = true,
                            enabled = !authLoading,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CosmicTheme.TextLight,
                                unfocusedTextColor = CosmicTheme.TextLight,
                                focusedBorderColor = CosmicTheme.AccentTeal,
                                unfocusedBorderColor = CosmicTheme.Slate700,
                                focusedContainerColor = if (CosmicTheme.isDark) Color(0x0AFFFFFF) else Color(0x05000000),
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_name_input"),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(20.dp)) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = usernameSec,
                            onValueChange = { usernameSec = it },
                            label = { Text("Username (E.g. @kevin)") },
                            singleLine = true,
                            enabled = !authLoading,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = CosmicTheme.TextLight,
                                unfocusedTextColor = CosmicTheme.TextLight,
                                focusedBorderColor = CosmicTheme.AccentTeal,
                                unfocusedBorderColor = CosmicTheme.Slate700,
                                focusedContainerColor = if (CosmicTheme.isDark) Color(0x0AFFFFFF) else Color(0x05000000),
                                unfocusedContainerColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_username_input"),
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(20.dp)) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("College Email (E.g. student@uni.edu)") },
                        singleLine = true,
                        enabled = !authLoading,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicTheme.TextLight,
                            unfocusedTextColor = CosmicTheme.TextLight,
                            focusedBorderColor = CosmicTheme.AccentTeal,
                            unfocusedBorderColor = CosmicTheme.Slate700,
                            focusedContainerColor = if (CosmicTheme.isDark) Color(0x0AFFFFFF) else Color(0x05000000),
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_email_input"),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(20.dp)) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password (Nenosiri)") },
                        singleLine = true,
                        enabled = !authLoading,
                        shape = RoundedCornerShape(16.dp),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = CosmicTheme.TextLight,
                            unfocusedTextColor = CosmicTheme.TextLight,
                            focusedBorderColor = CosmicTheme.AccentTeal,
                            unfocusedBorderColor = CosmicTheme.Slate700,
                            focusedContainerColor = if (CosmicTheme.isDark) Color(0x0AFFFFFF) else Color(0x05000000),
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(20.dp)) },
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(image, contentDescription = "Toggle Visibility", tint = CosmicTheme.TextMuted, modifier = Modifier.size(20.dp))
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
                                .height(52.dp)
                                .testTag("login_submit_button"),
                            shape = RoundedCornerShape(16.dp),
                            enabled = emailInput.isNotBlank() && passwordInput.isNotBlank() && (!isSignUpMode || (displayName.isNotBlank() && usernameSec.isNotBlank()))
                        ) {
                            Text(
                                text = if (isSignUpMode) "Unda Akaunti Sasa ➔" else "Ingia Kwenye Smart Campus ➔",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Support and password help link triggers at bottom of form
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { showForgotPasswordDialog = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = CosmicTheme.AccentTeal)
                            ) {
                                Text("Umesahau Nenosiri?", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            TextButton(
                                onClick = { showHelpDialog = true },
                                colors = ButtonDefaults.textButtonColors(contentColor = CosmicTheme.TextMuted)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.HelpOutline, contentDescription = "Msaada", modifier = Modifier.size(13.dp), tint = CosmicTheme.TextMuted)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Msaada / Help", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else if (step == 1) {
                    // STEP 1: Customize Academic Path (COMPLETELY POLISHED WITH PROGRESSIVE DISCLOSURE & VIBRANT GLOW)
                    Text(
                        text = "Customize Academic Path",
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = CosmicTheme.TextLight,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Customize your class schedule routines and local Maktaba filters automatically based on your programme.",
                        fontSize = 12.sp,
                        color = CosmicTheme.TextMuted,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(top = 6.dp, bottom = 22.dp)
                    )

                    var progSec by remember { mutableStateOf("") }
                    var yrSec by remember { mutableStateOf("") }
                    var semSec by remember { mutableStateOf("") }

                    val years = listOf("Year 1", "Year 2", "Year 3", "Year 4")
                    val semesters = listOf("Semester 1", "Semester 2")

                    // Active Programme Selection Panel with vibrant border and soft glow
                    Text(
                        text = "Chagua Mtaala unaotumika (Active Programme):",
                        color = CosmicTheme.AccentTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp)
                    )

                    val isSelUD089 = progSec == "UD089"
                    val borderThicknessUD089 by animateDpAsState(targetValue = if (isSelUD089) 2.dp else 1.dp, label = "borderUD089")
                    val scaleFactorUD089 by animateFloatAsState(targetValue = if (isSelUD089) 1.02f else 1.0f, label = "scaleUD089")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .scale(scaleFactorUD089)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelUD089) CosmicTheme.AccentTeal.copy(alpha = 0.12f)
                                else if (isDarkTheme) Color(0x0AFFFFFF)
                                else Color(0xFFF8FAFC)
                            )
                            .border(
                                borderThicknessUD089,
                                if (isSelUD089) CosmicTheme.AccentTeal else CosmicTheme.Slate800,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { progSec = "UD089" }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isSelUD089) CosmicTheme.AccentTeal else if (isDarkTheme) Color(0x20FFFFFF) else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelUD089) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            } else {
                                Icon(Icons.Default.School, contentDescription = null, tint = CosmicTheme.TextMuted, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "UD089",
                                color = CosmicTheme.TextLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "BSc in Mathematics & Statistics",
                                color = CosmicTheme.AccentTeal,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Coming soon programmes listed cleanly
                    Text(
                        text = "Mitaala Inayoandaliwa (Coming Soon):",
                        color = CosmicTheme.TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "CS" to "Computer Science",
                            "ENG" to "Engineering",
                            "BIZ" to "Business Admin"
                        ).forEach { (pKey, label) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .graphicsLayer { alpha = 0.5f }
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDarkTheme) Color(0x06FFFFFF) else Color(0xFFF9FAFB))
                                    .border(
                                        1.dp,
                                        CosmicTheme.Slate800,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        Toast.makeText(context, "$label inakuja hivi karibuni! Chagua UD089 kwa sasa kufurahia kila kitu.", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isDarkTheme) Color(0x15FFFFFF) else Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = "Locked", tint = CosmicTheme.TextMuted, modifier = Modifier.size(12.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = pKey,
                                    color = CosmicTheme.TextLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    color = CosmicTheme.TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Progressive Disclosure: Disclose Year selection only after UD089 is selected
                    AnimatedVisibility(
                        visible = progSec == "UD089",
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Current Study Year:",
                                color = CosmicTheme.AccentTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                years.forEach { y ->
                                    val isSel = y == yrSec
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSel) CosmicTheme.AccentTeal else if (isDarkTheme) Color(0x0AFFFFFF) else Color(0xFFF8FAFC))
                                            .border(1.dp, if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate800, RoundedCornerShape(10.dp))
                                            .clickable { yrSec = y }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = y,
                                            color = if (isSel) Color.White else CosmicTheme.TextLight,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Progressive Disclosure: Disclose Semester selection only after Year is picked
                    AnimatedVisibility(
                        visible = progSec == "UD089" && yrSec.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Semester Term:",
                                color = CosmicTheme.AccentTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start).padding(bottom = 6.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                semesters.forEach { s ->
                                    val isSel = s == semSec
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSel) CosmicTheme.AccentTeal else if (isDarkTheme) Color(0x0AFFFFFF) else Color(0xFFF8FAFC))
                                            .border(1.dp, if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate800, RoundedCornerShape(12.dp))
                                            .clickable { semSec = s }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = s,
                                            color = if (isSel) Color.White else CosmicTheme.TextLight,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Show save preferences button only when path is completely customized
                    AnimatedVisibility(
                        visible = progSec == "UD089" && yrSec.isNotEmpty() && semSec.isNotEmpty()
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Spacer(modifier = Modifier.height(26.dp))
                            Button(
                                onClick = {
                                    viewModel.updateProfileSettings(progSec, yrSec) // Dual Guard Persistence & Timetable sync
                                    viewModel.selectProgramme(progSec)
                                    viewModel.selectYear(yrSec)
                                    viewModel.selectSemester(semSec)
                                    viewModel.proceedOnboardingStep()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("onboarding_save_preferences"),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Sanikisha & Sawa ➔", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                } else {
                    // STEP 2: POLISHED MODERATOR CARD GREETING VIEW
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CosmicTheme.AccentTeal.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                            .border(1.dp, CosmicTheme.AccentTeal.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Moderator",
                            tint = CosmicTheme.AccentTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ujumbe kutoka kwa Moderator! 🛡️",
                            fontWeight = FontWeight.ExtraBold,
                            color = CosmicTheme.AccentTeal,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Breath pulse animation loop for moderator avatar
                    val infiniteTransition = rememberInfiniteTransition(label = "modAvatarPulse")
                    val avatarScale by infiniteTransition.animateFloat(
                        initialValue = 0.96f,
                        targetValue = 1.04f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "avatarScale"
                    )

                    // Male portrait avatar graphic representing Lead Architect
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .scale(avatarScale)
                            .clip(CircleShape)
                            .background(CosmicTheme.AccentPurple.copy(alpha = 0.15f))
                            .border(2.dp, CosmicTheme.AccentTeal, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(64.dp)) {
                            // Hair / curls details
                            drawCircle(color = Color(0xFF332011), radius = 26f, center = center.copy(y = center.y - 18f))
                            // Face skin
                            drawCircle(color = Color(0xFFC68642), radius = 23f, center = center)
                            // Smile / mouth curly arc
                            drawArc(
                                color = Color(0xFF111111),
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = false,
                                size = size.copy(width = 16f, height = 11f),
                                topLeft = center.copy(x = center.x - 8f, y = center.y + 4f)
                            )
                            // Eyes
                            drawCircle(color = Color(0xFF111111), radius = 3f, center = center.copy(x = center.x - 8f, y = center.y - 4f))
                            drawCircle(color = Color(0xFF111111), radius = 3f, center = center.copy(x = center.x + 8f, y = center.y - 4f))
                            // flatNatural eyebrows
                            drawLine(
                                color = Color(0xFF221100),
                                start = center.copy(x = center.x - 13f, y = center.y - 12f),
                                end = center.copy(x = center.x - 3f, y = center.y - 12f),
                                strokeWidth = 3f
                            )
                            drawLine(
                                color = Color(0xFF221100),
                                start = center.copy(x = center.x + 3f, y = center.y - 12f),
                                end = center.copy(x = center.x + 13f, y = center.y - 12f),
                                strokeWidth = 3f
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Japhet Mathias",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = CosmicTheme.TextLight
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Staff",
                            tint = CosmicTheme.EmeraldAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lead Platform Architect",
                            fontSize = 11.sp,
                            color = CosmicTheme.TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Message Speech bubble pulling displaying student name dynamically
                    val userName = user?.displayName ?: "Mwanafunzi"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDarkTheme) Color(0x13FFFFFF) else Color(0xFFF1F5F9))
                            .border(1.dp, CosmicTheme.Slate800, RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = "Mambo vipi, $userName! Mimi ni Japhet Mathias. Najua vizuri stress za chuo za kupambana na ratiba, kudesa mitihani na kupata vifaa vya masomo. Nilitengeneza Smart Campus ili kurahisisha maisha yako - hapa kuna kila kitu (ratiba za vipindi, library kudesa notes, soko la kununua/kuuza vitu, na group chats). Karibu sana kwenye familia yetu, niko hapa kusaidia! 🚀",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp,
                            color = CosmicTheme.TextLight.copy(alpha = 0.90f),
                            modifier = Modifier.padding(18.dp),
                            textAlign = TextAlign.Start
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.proceedOnboardingStep() },
                        colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.EmeraldAccent),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("onboarding_complete_button")
                    ) {
                        Text("Tuanze Sasa Hivi! 🚀", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
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

fun getYearFromCourseCode(courseCode: String): Int {
    val firstDigit = courseCode.firstOrNull { it.isDigit() }
    return if (firstDigit != null) {
        firstDigit.toString().toIntOrNull() ?: 3
    } else {
        3
    }
}

@Composable
fun ScheduleScreen(viewModel: CampusViewModel, isPlannerMode: Boolean, onTabSelect: (AppTab) -> Unit) {
    val routines by viewModel.routines.collectAsStateWithLifecycle()
    val aiResponse by viewModel.aiSchedulerResponse.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiSchedulerLoading.collectAsStateWithLifecycle()
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

    // Time calculations for routines today using java.time.LocalDate
    val calendar = java.util.Calendar.getInstance()
    val currentDayName = java.time.LocalDate.now().dayOfWeek.getDisplayName(
        java.time.format.TextStyle.FULL,
        java.util.Locale.ENGLISH
    )

    // Routines for current day synchronized with registered academic profile
    val userProgramByProfile by viewModel.userProgram.collectAsStateWithLifecycle()
    val userYearByProfile by viewModel.userYear.collectAsStateWithLifecycle()

    val profileYearInt = try {
        userYearByProfile.replace("Year ", "").trim().toInt()
    } catch (e: Exception) {
        3
    }

    val routinesToday = allRoutinesList.filter { 
        it.dayOfWeek == currentDayName &&
        (it.program == userProgramByProfile || userProgramByProfile == "ALL") &&
        (it.year == profileYearInt || userYearByProfile == "ALL")
    }
    val classTimeStatus = checkClassStatus(routinesToday)

    if (viewingDetailedRoutine) {
        var showFilterSettings by remember { mutableStateOf(false) }
        val currentWeekDays = remember {
            val list = mutableListOf<Triple<String, Int, String>>() // DayName, DayOfMonth, MonthName
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
                val mVal = when(tempCal.get(java.util.Calendar.MONTH)) {
                    java.util.Calendar.JANUARY -> "Jan"
                    java.util.Calendar.FEBRUARY -> "Feb"
                    java.util.Calendar.MARCH -> "Mar"
                    java.util.Calendar.APRIL -> "Apr"
                    java.util.Calendar.MAY -> "May"
                    java.util.Calendar.JUNE -> "Jun"
                    java.util.Calendar.JULY -> "Jul"
                    java.util.Calendar.AUGUST -> "Aug"
                    java.util.Calendar.SEPTEMBER -> "Sep"
                    java.util.Calendar.OCTOBER -> "Oct"
                    java.util.Calendar.NOVEMBER -> "Nov"
                    java.util.Calendar.DECEMBER -> "Dec"
                    else -> "Jan"
                }
                list.add(Triple(dName, dVal, mVal))
                tempCal.add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            list
        }

        val persistedProgram by viewModel.userProgram.collectAsStateWithLifecycle()
        val persistedYear by viewModel.userYear.collectAsStateWithLifecycle()

        var syncWithProfile by remember { mutableStateOf(true) }
        var selectedProgramFilter by remember { mutableStateOf("UD089") }
        var selectedYearFilter by remember { mutableStateOf("Year 3") }

        // React when persisted program/year changes (if sync is turned on)
        LaunchedEffect(persistedProgram, persistedYear, syncWithProfile) {
            if (syncWithProfile) {
                if (!persistedProgram.isNullOrEmpty() && persistedProgram != "ALL") {
                    selectedProgramFilter = persistedProgram
                }
                if (!persistedYear.isNullOrEmpty() && persistedYear != "ALL") {
                    selectedYearFilter = persistedYear
                }
            }
        }

        // Auto-query AI Scheduler on filters update for context-aware intelligence
        LaunchedEffect(selectedProgramFilter, selectedYearFilter) {
            viewModel.queryAiScheduler(selectedProgramFilter, selectedYearFilter)
        }

        // Daily Progress completion computation
        val completedCount = routines.count { r ->
            val range = parseClassTimes(r.times)
            if (range == null) true
            else {
                val currMinutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
                range.second < currMinutes
            }
        }
        val completionPercentage = if (routines.isEmpty()) 100 else (completedCount * 100) / routines.size

        // DETAILED CALENDAR ROUTINE MANAGER (Image 5)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onTabSelect(AppTab.Home) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = CosmicTheme.TextLight)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Ratiba Yangu",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CosmicTheme.TextLight
                        )
                        Text(
                            text = if (selectedProgramFilter == "UD089") "BSc Mathematics & Statistics" else "Selected Programme",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = CosmicTheme.AccentTeal
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Sync icon toggle
                    IconButton(
                        onClick = { showFilterSettings = !showFilterSettings },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (showFilterSettings) CosmicTheme.AccentTeal.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                        ),
                        modifier = Modifier.size(36.dp).clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune, 
                            contentDescription = "Filters & Settings", 
                            tint = if (showFilterSettings) CosmicTheme.AccentTeal else CosmicTheme.TextLight,
                            modifier = Modifier.size(18.dp)
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
            }

            // Beautiful Collapsible Filter Settings & Progress Area
            AnimatedVisibility(
                visible = showFilterSettings,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Daily Progress completion card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("DAILY PROGRESS SUMMARY", fontSize = 9.sp, color = CosmicTheme.WarnOrange, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (completionPercentage == 100) "Mission Complete, Mwanangu! 🏆" else "Unasoma nini leo? 📚",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CosmicTheme.TextLight
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color.White.copy(alpha = 0.06f), RoundedCornerShape(8.dp))
                                        .clickable { viewModel.forceSyncScraper() }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Sync, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(11.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("RE-SYNC", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.AccentTeal)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(56.dp)) {
                                CircularProgressIndicator(
                                    progress = { completionPercentage / 100f },
                                    modifier = Modifier.fillMaxSize(),
                                    color = CosmicTheme.NeonOrange,
                                    strokeWidth = 5.dp,
                                    trackColor = Color.White.copy(alpha = 0.08f)
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("$completionPercentage%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                                    Text("DONE", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextMuted)
                                }
                            }
                        }
                    }

                    // High-end, Modernized control card grouping filters with ample space
                    Surface(
                        color = CosmicTheme.MidnightGradStart.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Profile Sync Settings Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(13.dp))
                                    Text(
                                        text = "FILTER UTILITY & SYNC",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicTheme.AccentTeal,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("Sync with Profile", color = CosmicTheme.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    Switch(
                                        checked = syncWithProfile,
                                        onCheckedChange = { 
                                            syncWithProfile = it
                                            if (it) {
                                                viewModel.updateProfileSettings("UD089", selectedYearFilter)
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = CosmicTheme.AccentTeal,
                                            uncheckedThumbColor = CosmicTheme.TextMuted,
                                            uncheckedTrackColor = Color.Transparent
                                        ),
                                        modifier = Modifier.scale(0.7f)
                                    )
                                }
                            }

                            Text("Selected Programme", color = CosmicTheme.TextLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // BSc in Math & Stats - Preselected and active
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f))
                                        .border(1.5.dp, CosmicTheme.AccentTeal, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        "BSc in Math & Stats",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicTheme.AccentTeal
                                    )
                                }
                                
                                // CS More coming soon disabled chip
                                Box(
                                    modifier = Modifier
                                        .graphicsLayer { alpha = 0.5f }
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.03f))
                                        .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Lock, contentDescription = "Locked", tint = CosmicTheme.TextMuted, modifier = Modifier.size(9.dp))
                                        Text(
                                            "CS (More coming soon)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicTheme.TextMuted
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Academic Year Level", color = CosmicTheme.TextLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf("Year 1", "Year 2", "Year 3", "Year 4", "ALL").forEach { yr ->
                                    val isSel = selectedYearFilter == yr
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.Transparent)
                                            .border(1.dp, if (isSel) CosmicTheme.AccentTeal else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                            .clickable { 
                                                selectedYearFilter = yr
                                                if (syncWithProfile) {
                                                    viewModel.updateProfileSettings("UD089", yr)
                                                }
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = if (yr == "ALL") "All Years" else yr,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) CosmicTheme.AccentTeal else CosmicTheme.TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Academic Calendar Label
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACADEMIC TIMETABLE", 
                    fontSize = 9.sp, 
                    color = CosmicTheme.TextMuted, 
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                val currentMonthName = months[calendar.get(java.util.Calendar.MONTH)]
                val currentDayOfMonth = calendar.get(java.util.Calendar.DAY_OF_MONTH)
                Text(
                    text = "${currentDayName.substring(0,3)}, ${currentMonthName} ${currentDayOfMonth}",
                    fontSize = 11.sp,
                    color = CosmicTheme.AccentTeal,
                    fontWeight = FontWeight.Bold
                )
            }

            // Beautiful Horizontal Swipeable Date Strip Header
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(currentWeekDays) { item ->
                    val isSelected = activeDay == item.first
                    val bg = if (isSelected) CosmicTheme.AccentTeal.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.02f)
                    val borderCol = if (isSelected) CosmicTheme.AccentTeal else Color.White.copy(alpha = 0.06f)
                    val textColor = if (isSelected) CosmicTheme.AccentTeal else CosmicTheme.TextMuted
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(bg)
                            .border(1.2.dp, borderCol, RoundedCornerShape(14.dp))
                            .clickable { viewModel.selectTimetableDay(item.first) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.first.substring(0, 3).uppercase(),
                                color = textColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.second.toString(),
                                color = CosmicTheme.TextLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(1.dp))
                            Text(
                                text = item.third.uppercase(),
                                color = if (isSelected) CosmicTheme.AccentTeal.copy(alpha = 0.8f) else CosmicTheme.TextMuted.copy(alpha = 0.4f),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Pills filters ALL / UPCOMING / COMPLETED
            var selectedFilter by remember { mutableStateOf("ALL") }
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                listOf("ALL", "UPCOMING", "COMPLETED").forEach { f ->
                    val isSel = selectedFilter == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) Color.White.copy(alpha = 0.12f) else Color.Transparent)
                            .border(1.dp, if (isSel) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                            .clickable { selectedFilter = f }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = f,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) CosmicTheme.TextLight else CosmicTheme.TextMuted
                        )
                    }
                }
            }

            // Sleek active course filter alert banner
            val activeCourseFilter by viewModel.activeCourseFilter.collectAsStateWithLifecycle()
            if (activeCourseFilter != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CosmicTheme.AccentTeal.copy(alpha = 0.12f))
                        .border(1.dp, CosmicTheme.AccentTeal.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filtered",
                                tint = CosmicTheme.AccentTeal,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Filtered by course: $activeCourseFilter",
                                color = CosmicTheme.AccentTeal,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Clear",
                            color = CosmicTheme.TextLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier
                                .clickable { viewModel.setActiveCourseFilter(null) }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Routines evaluation (filtered by program, year, status, and course filter)
            val filteredRoutines = remember(routines, selectedFilter, selectedProgramFilter, selectedYearFilter, activeCourseFilter) {
                val currMinutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
                routines.filter { r ->
                    // 0. Course Filter from Chat deep links
                    val matchesCourse = activeCourseFilter == null || r.courseCode.equals(activeCourseFilter, ignoreCase = true)
                    if (!matchesCourse) return@filter false

                    // 1. Program Filter
                    val matchesProgram = selectedProgramFilter == "ALL" || r.program.equals(selectedProgramFilter, ignoreCase = true)
                    
                    // 2. Year Filter - using helper for robust course code intelligence
                    val matchesYear = if (selectedYearFilter == "ALL") true else {
                        val numericYear = when(selectedYearFilter) {
                            "Year 1" -> 1
                            "Year 2" -> 2
                            "Year 3" -> 3
                            "Year 4" -> 4
                            else -> 3
                        }
                        getYearFromCourseCode(r.courseCode) == numericYear
                    }

                    if (!matchesProgram || !matchesYear) {
                        false
                    } else {
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
            }

            if (filteredRoutines.isEmpty()) {
                val isComingSoon = selectedProgramFilter != "UD089" || selectedYearFilter == "Year 4"
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(CosmicTheme.MidnightGradStart.copy(alpha = 0.35f))
                            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        // Decorative Pulsing/Neon AI Scheduler status ring
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isComingSoon) CosmicTheme.WarnOrange.copy(alpha = 0.15f)
                                    else CosmicTheme.AccentTeal.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isComingSoon) Icons.Default.Schedule else Icons.Default.CheckCircle,
                                contentDescription = "Active Status",
                                tint = if (isComingSoon) CosmicTheme.WarnOrange else CosmicTheme.AccentTeal,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isComingSoon) "RATIBA COMING SOON!" else "HAKUNA VIPINDI LEO! 🎉",
                            color = CosmicTheme.TextLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val comingSoonMsg = if (isComingSoon) {
                            "Timetable for this category is coming soon. The intelligence layer is preparing these details."
                        } else {
                            "You have no classes today – enjoy your day! 🌟\n(Hakuna vipindi vilivyoratibiwa leo, msomi! Furahia siku yako! 🎉)"
                        }

                        Text(
                            text = comingSoonMsg,
                            color = CosmicTheme.TextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // AI Consultation trigger
                        TextButton(
                            onClick = {
                                viewModel.queryAiScheduler(selectedProgramFilter, selectedYearFilter)
                            },
                            colors = ButtonDefaults.textButtonColors(
                                containerColor = CosmicTheme.AccentTeal.copy(alpha = 0.1f),
                                contentColor = CosmicTheme.AccentTeal
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("ai_consult_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Context",
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Consult AI Scheduler", 
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Display active AI prompt / feedback bubble if query results exist
                        if (isAiLoading) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = CosmicTheme.AccentTeal
                                )
                                Text(
                                    text = "Analyzing Department hierarchies...",
                                    fontSize = 11.sp,
                                    color = CosmicTheme.TextMuted
                                )
                            }
                        }

                        aiResponse?.let { resp ->
                            Spacer(modifier = Modifier.height(14.dp))
                            val cleanMsg = if (resp.contains("\"message\"")) {
                                try {
                                    val startIdx = resp.indexOf("\"message\"")
                                    val valStart = resp.indexOf("\"", startIdx + 9)
                                    val valEnd = resp.indexOf("\"", valStart + 1)
                                    resp.substring(valStart + 1, valEnd)
                                } catch (e: Exception) {
                                    resp
                                }
                            } else {
                                resp
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.03f))
                                    .border(0.5.dp, CosmicTheme.AccentTeal.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = CosmicTheme.AccentTeal,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "INTELLIGENCE ENGINE RESPONDED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CosmicTheme.AccentTeal,
                                            letterSpacing = 1.sp
                                        )
                                    }
                                    Text(
                                        text = cleanMsg,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = CosmicTheme.TextLight
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Beautiful lessons scrollable list with 100% vertical timeline connected nodes
                val currMinutes = calendar.get(java.util.Calendar.HOUR_OF_DAY) * 60 + calendar.get(java.util.Calendar.MINUTE)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(filteredRoutines, key = { it.id }) { routine ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Left branch: Connected vertical timeline column node (with custom color-coding)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(22.dp)
                            ) {
                                val range = parseClassTimes(routine.times)
                                val status = if (range == null) "Upcoming" else {
                                    val (startMin, endMin) = range
                                    when {
                                        currMinutes in startMin until endMin -> "Live Now"
                                        currMinutes < startMin -> "Upcoming"
                                        else -> "Finished"
                                    }
                                }

                                val nodeColor = when (status) {
                                    "Live Now" -> Color(0xFF10B981) // Green for Live
                                    "Upcoming" -> CosmicTheme.AccentTeal // Blue for Upcoming
                                    else -> Color(0xFF64748B) // Gray for Finished
                                }

                                Box(
                                    modifier = Modifier
                                        .padding(top = 16.dp)
                                        .size(12.dp)
                                        .background(nodeColor, CircleShape)
                                        .then(
                                            if (status == "Live Now") {
                                                Modifier.border(2.dp, Color.White, CircleShape)
                                            } else Modifier
                                        )
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(100.dp)
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(nodeColor.copy(alpha = 0.4f), Color.Transparent)
                                            )
                                        )
                                )
                            }

                            // Right branch: Premium lesson details card with quick actions
                            PremiumRoutineCard(
                                routine = routine,
                                currMinutes = currMinutes,
                                onToggleAlarm = { valVal ->
                                    viewModel.toggleReminder(routine.id, valVal)
                                },
                                onDelete = {
                                    viewModel.deleteClass(routine.id)
                                },
                                onUpdateLocation = { newVenue ->
                                    viewModel.updateClassLocation(routine.id, newVenue)
                                }
                            )
                        }
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
                    text = "NEXT SESSION",
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
                                            text = "Ongoing • Ends in ${status.minutesLeft}m",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicTheme.NeonOrange
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = status.activeClass.courseName.uppercase(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CosmicTheme.TextLight,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Session Type Badge
                                        val typeColor = when (status.activeClass.classType.lowercase()) {
                                            "lab" -> CosmicTheme.EmeraldAccent
                                            "seminar" -> CosmicTheme.DevPurple
                                            else -> CosmicTheme.AccentTeal
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(typeColor.copy(alpha = 0.15f))
                                                .border(1.dp, typeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = status.activeClass.classType.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = typeColor
                                            )
                                        }
                                    }

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
                                                    Text("MUDA / TIME", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
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
                                                    Text("UKUMBI / VENUE", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
                                                    Text(status.activeClass.location, fontSize = 11.sp, color = CosmicTheme.TextLight, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }

                                    // slide up "Up Next" inside 45 mins
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
                                            text = "Starts in $hrUnit",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CosmicTheme.SunnyYellow
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = status.upcomingClass.courseName.uppercase(),
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = CosmicTheme.TextLight,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Session Type Badge
                                        val typeColor = when (status.upcomingClass.classType.lowercase()) {
                                            "lab" -> CosmicTheme.EmeraldAccent
                                            "seminar" -> CosmicTheme.DevPurple
                                            else -> CosmicTheme.AccentTeal
                                        }
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(typeColor.copy(alpha = 0.15f))
                                                .border(1.dp, typeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = status.upcomingClass.classType.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = typeColor
                                            )
                                        }
                                    }

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
                                                    Text("MUDA / TIME", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
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
                                                    Text("UKUMBI / VENUE", fontSize = 8.sp, color = CosmicTheme.TextMuted, fontWeight = FontWeight.Bold)
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
fun RoomFinderDialog(roomName: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = CosmicTheme.AccentTeal, fontWeight = FontWeight.Bold)
            }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = CosmicTheme.NeonOrange)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Room Finder", color = CosmicTheme.TextLight, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Room Name: $roomName",
                    color = CosmicTheme.TextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Building location & directions for your class room on campus:",
                    color = CosmicTheme.TextMuted,
                    fontSize = 12.sp
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.Explore, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(20.dp))
                        Text(
                            text = "Walk 150m from COAF Main Arch", 
                            fontSize = 11.sp, 
                            fontWeight = FontWeight.Bold, 
                            color = CosmicTheme.TextLight,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Take the left corridor on Level 2 near the Lab Wing.", 
                            fontSize = 10.sp, 
                            color = CosmicTheme.TextMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CosmicTheme.EmeraldAccent, modifier = Modifier.size(14.dp))
                    Text(
                        text = "GPS Coordinates loaded successfully", 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = CosmicTheme.EmeraldAccent
                    )
                }
            }
        },
        containerColor = CosmicTheme.DarkSlate,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun ReportVenueChangeDialog(
    currentRoom: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var textVal by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    if (textVal.isNotBlank()) {
                        onSubmit(textVal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal)
            ) {
                Text("Report", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CosmicTheme.TextMuted)
            }
        },
        title = {
            Text("Report Venue Change", color = CosmicTheme.TextLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Current: $currentRoom",
                    color = CosmicTheme.TextLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Did this class location change? Enter the updated venue below to help your campus groups stay synchronized.",
                    color = CosmicTheme.TextMuted,
                    fontSize = 12.sp
                )
                OutlinedTextField(
                    value = textVal,
                    onValueChange = { textVal = it },
                    placeholder = { Text("e.g. COAF LR8, Lab 3", color = CosmicTheme.TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = CosmicTheme.AccentTeal,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        containerColor = CosmicTheme.DarkSlate,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun PremiumRoutineCard(
    routine: RoutineEntity,
    currMinutes: Int,
    onToggleAlarm: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onUpdateLocation: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showMapFinderDialog by remember { mutableStateOf(false) }

    // Logic to determine status
    val range = parseClassTimes(routine.times)
    val status = if (range == null) "Upcoming" else {
        val (startMin, endMin) = range
        when {
            currMinutes in startMin until endMin -> "Live Now"
            currMinutes < startMin -> "Upcoming"
            else -> "Finished"
        }
    }

    val statusText = when (status) {
        "Live Now" -> "LIVE NOW"
        "Upcoming" -> "UPCOMING"
        else -> "COMPLETED"
    }

    val statusBgColor = when (status) {
        "Live Now" -> Color(0xFF10B981).copy(alpha = 0.12f)
        "Upcoming" -> CosmicTheme.AccentTeal.copy(alpha = 0.12f)
        else -> Color.White.copy(alpha = 0.05f)
    }

    val statusTextColor = when (status) {
        "Live Now" -> Color(0xFF10B981)
        "Upcoming" -> CosmicTheme.AccentTeal
        else -> CosmicTheme.TextMuted
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (status == "Live Now") CosmicTheme.MidnightGradStart.copy(alpha = 0.6f) else CosmicTheme.MidnightGradStart
        ),
        border = BorderStroke(
            1.dp, 
            if (status == "Live Now") Color(0xFF10B981).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("premium_routine_card_${routine.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // First row: Type indicators and status pill + 3-dot dropdown menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type & Status Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val classTypeColor = when (routine.classType.lowercase()) {
                        "lab" -> CosmicTheme.EmeraldAccent
                        "seminar" -> CosmicTheme.DevPurple
                        else -> CosmicTheme.AccentTeal
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(classTypeColor.copy(alpha = 0.18f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = routine.classType.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = classTypeColor
                        )
                    }

                    // Status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusBgColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusTextColor
                        )
                    }
                }

                // Three-dot action menu
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = CosmicTheme.TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        containerColor = CosmicTheme.DarkSlate
                    ) {
                        DropdownMenuItem(
                            text = { Text("Set Reminder", color = Color.White, fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onToggleAlarm(!routine.remindersEnabled)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (routine.remindersEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                    contentDescription = null,
                                    tint = CosmicTheme.AccentTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Copy Info", color = Color.White, fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                val clip = android.content.ClipData.newPlainText(
                                    "Class Details", 
                                    "${routine.courseName} (${routine.courseCode}) is scheduled at ${routine.times} in Room ${routine.location} taught by ${routine.lecturer}."
                                )
                                clipboard.setPrimaryClip(clip)
                                android.widget.Toast.makeText(context, "Lesson details copied!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = CosmicTheme.AccentTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Report Room Change", color = Color.White, fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                showReportDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = CosmicTheme.AccentTeal,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Lesson", color = Color.Red.copy(alpha = 0.8f), fontSize = 12.sp) },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red.copy(alpha = 0.8f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Details
            Text(
                text = routine.courseName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = CosmicTheme.TextLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${routine.courseCode} • Lect: ${routine.lecturer}",
                color = CosmicTheme.TextMuted,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
            Spacer(modifier = Modifier.height(10.dp))

            // Bottom metadata: Time & Location with Pin / Room Finder Tap!
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime, 
                        contentDescription = "Time", 
                        tint = CosmicTheme.AccentTeal, 
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = routine.times,
                        fontSize = 11.sp,
                        color = CosmicTheme.TextLight,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Room/Venue with Pin Finder Tap!
                Box(
                    modifier = Modifier
                        .scale(0.95f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .clickable { showMapFinderDialog = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place, 
                            contentDescription = "Venue map pin", 
                            tint = CosmicTheme.WarnOrange, 
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = routine.location,
                            fontSize = 11.sp,
                            color = CosmicTheme.TextLight,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Compass",
                            tint = CosmicTheme.AccentTeal,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal dialogs
    if (showReportDialog) {
        ReportVenueChangeDialog(
            currentRoom = routine.location,
            onDismiss = { showReportDialog = false },
            onSubmit = { newRoom ->
                showReportDialog = false
                onUpdateLocation(newRoom)
            }
        )
    }

    if (showMapFinderDialog) {
        RoomFinderDialog(
            roomName = routine.location,
            onDismiss = { showMapFinderDialog = false }
        )
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
@OptIn(ExperimentalMaterial3Api::class)
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
            val userProgSetting by viewModel.userProgram.collectAsStateWithLifecycle()
            val userProgDesc = if (userProgSetting == "UD089") "BSc Math/Stats" else "CS Resources"
            
            Text(
                text = "🔥 Trending Now (${userProgDesc})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.WarnOrange,
                modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
            )

            val trendingDeck = remember(materials, userProgSetting) {
                val targets = materials.filter {
                    if (userProgSetting == "UD089") {
                        it.programme == "UD089" || it.programme.contains("Math", ignoreCase = true) || it.courseCode.contains("MTH", ignoreCase = true)
                    } else {
                        it.programme == "Computer Science" || it.courseCode.contains("CS", ignoreCase = true)
                    }
                }
                if (targets.isEmpty()) {
                    if (userProgSetting == "UD089") {
                        listOf(
                            StudyMaterialEntity(
                                id = -101,
                                title = "MTH 302 Real Analysis Notes",
                                description = "Compiled premium study notes on topological properties of R, limit superior and limit inferior, Cauchy sequences, and Heine-Borel theorem.",
                                programme = "UD089",
                                year = "Year 3",
                                semester = "Semester 1",
                                courseCode = "MTH-302",
                                fileName = "Real_Analysis_MTH302_Master.pdf",
                                fileType = "pdf",
                                uploadedByName = "Japhet M.",
                                uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Japhet",
                                sizeText = "3.2 MB"
                            ),
                            StudyMaterialEntity(
                                id = -102,
                                title = "Probability Theory II Solved Papers",
                                description = "Complete step-by-step solutions for UE and test booklets covering joint distributions and moment generating functions.",
                                programme = "UD089",
                                year = "Year 2",
                                semester = "Semester 2",
                                courseCode = "MTH-212",
                                fileName = "Prob_Theory_SolvedPapers.pdf",
                                fileType = "pdf",
                                uploadedByName = "Lydia N.",
                                uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Lydia",
                                sizeText = "4.1 MB"
                            )
                        )
                    } else {
                        listOf(
                            StudyMaterialEntity(
                                id = -201,
                                title = "C++ Core Pointers Cheat Sheet",
                                description = "A condensed master guide to dynamic memory allocation containing stack vs heap representations, double pointers, and smart pointer syntax.",
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
                                id = -202,
                                title = "Database Normalization Exercises",
                                description = "Step-by-step resolution of database tables from 1NF to BCNF with dependencies, primary keys, and index configurations.",
                                programme = "Computer Science",
                                year = "Year 2",
                                semester = "Semester 1",
                                courseCode = "CS-204",
                                fileName = "DBMS_1NF_BCNF_Solved.pdf",
                                fileType = "pdf",
                                uploadedByName = "Sarah K.",
                                uploadedByPhoto = "https://api.dicebear.com/7.x/avataaars/svg?seed=Sarah",
                                sizeText = "1.8 MB"
                            )
                        )
                    }
                } else {
                    targets
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(trendingDeck.size) { idx ->
                    val item = trendingDeck[idx]
                    Card(
                        modifier = Modifier
                            .width(220.dp)
                            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                            .clickable { viewModel.openMaterialViewer(item) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart.copy(alpha = 0.45f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.courseCode,
                                        fontSize = 9.sp,
                                        color = CosmicTheme.AccentTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = CosmicTheme.WarnOrange,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Top #${idx + 1}",
                                        color = CosmicTheme.WarnOrange,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = item.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CosmicTheme.TextLight,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.description,
                                fontSize = 10.sp,
                                color = CosmicTheme.TextMuted,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 13.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        tint = CosmicTheme.AccentTeal,
                                        modifier = Modifier.size(11.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "214 downloads",
                                        fontSize = 9.sp,
                                        color = CosmicTheme.TextMuted,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color.White.copy(alpha = 0.05f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = item.fileType.uppercase(),
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicTheme.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Text(
                text = "Browse Departments:",
                fontWeight = FontWeight.Bold,
                color = CosmicTheme.TextLight,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val gridProgrammes = listOf(
                Triple("BSc Math/Stats", Pair("📊", "UD089 Mathematics & Statistics Syllabus"), true),
                Triple("Computer Science", Pair("💻", "Software, Database & Core Systems"), true),
                Triple("Engineering", Pair("⚙️", "Syllabus, Calculus & Linear Circuits"), false),
                Triple("Business Admin", Pair("💼", "Accountancy, Finance & Operations"), false),
                Triple("Medicine", Pair("🩺", "Anatomy, Pathology & Clinical Guides"), false),
                Triple("Law & Humanities", Pair("⚖️", "Constitutions, Case Files & Logic"), false)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(gridProgrammes.size) { index ->
                    val (title, info, isActive) = gridProgrammes[index]
                    val (emoji, tagline) = info
                    
                    val bg = if (isActive) CosmicTheme.MidnightGradStart else Color.White.copy(alpha = 0.02f)
                    val borderCol = if (isActive) CosmicTheme.AccentTeal.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.04f)
                    
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = bg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, borderCol, RoundedCornerShape(16.dp))
                            .clickable(enabled = isActive) { 
                                val actualName = if (title == "BSc Math/Stats") "UD089" else title
                                viewModel.selectProgramme(actualName) 
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth()
                                .graphicsLayer {
                                    if (!isActive) {
                                        alpha = 0.45f
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isActive) CosmicTheme.AccentTeal.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(emoji, fontSize = 16.sp)
                                }
                                
                                if (!isActive) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.08f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Icon(
                                                imageVector = Icons.Default.Lock,
                                                contentDescription = "Locked",
                                                tint = CosmicTheme.TextMuted,
                                                modifier = Modifier.size(8.dp)
                                            )
                                            Text(
                                                text = "SOON",
                                                color = CosmicTheme.TextMuted,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CosmicTheme.EmeraldAccent.copy(alpha = 0.12f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ACTIVE",
                                            color = CosmicTheme.EmeraldAccent,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(10.dp))
                            
                            Text(
                                text = title,
                                color = CosmicTheme.TextLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tagline,
                                color = CosmicTheme.TextMuted,
                                fontSize = 10.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 12.sp
                            )
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

    // Modern ModalBottomSheet uploader overlay with device file picker integration
    if (showUploadModal) {
        val context = LocalContext.current
        ModalBottomSheet(
            onDismissRequest = { showUploadModal = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CosmicTheme.MidnightGradStart,
            contentColor = CosmicTheme.TextLight,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().testTag("upload_bottom_sheet")
        ) {
            var title by remember { mutableStateOf("") }
            var desc by remember { mutableStateOf("") }
            var prog by remember { mutableStateOf(selectedProg ?: "UD089") }
            var yr by remember { mutableStateOf(selectedYr ?: "Year 3") }
            var sem by remember { mutableStateOf(selectedSem ?: "Semester 1") }
            var code by remember { mutableStateOf("") }
            var fName by remember { mutableStateOf("") }
            var fType by remember { mutableStateOf("pdf") }

            val filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: android.net.Uri? ->
                if (uri != null) {
                    var name = ""
                    try {
                        val cursor = context.contentResolver.query(uri, null, null, null, null)
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val displayNameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                                if (displayNameIndex != -1) {
                                    name = it.getString(displayNameIndex)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (name.isEmpty()) {
                        name = uri.lastPathSegment ?: "selected_document"
                    }
                    fName = name
                    val ext = name.substringAfterLast('.', "pdf").lowercase()
                    fType = if (ext in listOf("pdf", "docx", "pptx", "xlsx", "zip")) ext else "pdf"
                    if (title.isBlank()) {
                        title = name.substringBeforeLast('.')
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upload Study Resource 📤",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CosmicTheme.TextLight
                    )
                    IconButton(onClick = { showUploadModal = false }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close Sheet", tint = CosmicTheme.TextLight)
                    }
                }

                Text(
                    text = "Appwrite Community Cloud Vault Sync",
                    fontSize = 11.sp,
                    color = CosmicTheme.AccentTeal,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(86.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.03f))
                        .border(1.dp, CosmicTheme.AccentTeal.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .clickable { filePickerLauncher.launch("*/*") }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.CloudUpload,
                            contentDescription = null,
                            tint = CosmicTheme.AccentTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (fName.isBlank()) "Tafuta nakala (Tap to select file)" else fName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (fName.isBlank()) CosmicTheme.TextLight else CosmicTheme.AccentTeal,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (fName.isNotBlank()) {
                            Text(
                                text = "Auto-extracted from storage • Ext: ${fType.uppercase()}",
                                fontSize = 9.sp,
                                color = CosmicTheme.TextMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Document Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.TextLight,
                        unfocusedTextColor = CosmicTheme.TextLight,
                        focusedBorderColor = CosmicTheme.AccentTeal,
                        unfocusedBorderColor = CosmicTheme.Slate700
                    )
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Short Description") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.TextLight,
                        unfocusedTextColor = CosmicTheme.TextLight,
                        focusedBorderColor = CosmicTheme.AccentTeal,
                        unfocusedBorderColor = CosmicTheme.Slate700
                    )
                )

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Course Code (e.g. MTH-302, CS-204)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.TextLight,
                        unfocusedTextColor = CosmicTheme.TextLight,
                        focusedBorderColor = CosmicTheme.AccentTeal,
                        unfocusedBorderColor = CosmicTheme.Slate700
                    )
                )

                Text("File Format Accent:", color = CosmicTheme.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("pdf", "docx", "pptx").forEach { format ->
                        val isSel = format == fType
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal else Color.White.copy(alpha = 0.04f))
                                .border(1.dp, if (isSel) CosmicTheme.AccentTeal else Color.White.copy(alpha = 0.05f), RoundedCornerShape(8.dp))
                                .clickable { fType = format }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(format.uppercase(), color = if (isSel) Color.White else CosmicTheme.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        viewModel.uploadStudyMaterial(title, desc, prog, yr, sem, code, fName, fType, "1.4 MB")
                        showUploadModal = false
                    },
                    modifier = Modifier.fillMaxWidth().height(46.dp).testTag("confirm_upload_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                    shape = RoundedCornerShape(12.dp),
                    enabled = title.isNotBlank() && code.isNotBlank() && fName.isNotBlank()
                ) {
                    Text("Sync & Share with Campus", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
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
                val context = LocalContext.current
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(listings, key = { it.id }) { listing ->
                        MarketplaceItemCard(
                            listing = listing,
                            onClick = { viewModel.selectActiveListing(listing.id) },
                            onChatClick = {
                                viewModel.openChatWithUser(listing.sellerId, listing.sellerName, listing.sellerPhoto)
                                Toast.makeText(context, "Opening direct inbox thread with ${listing.sellerName}!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        } else {
            // Detailed Single Listing detail sheet containing Facebook Marketplace Q&A nesting Q&A accordion replies.
            ListingDetailsView(listing = activeListing, viewModel = viewModel, onClose = { viewModel.selectActiveListing(null) })
        }
    }

    if (showSellModal) {
        SellItemDialog(onDismiss = { showSellModal = false }) { title, desc, price, cat, cond, urls ->
            viewModel.publishListing(
                title = title,
                description = desc,
                price = price,
                category = cat,
                condition = cond,
                imageSeed = "item_seed_" + (1000..9999).random(),
                imageUrls = urls
            )
            showSellModal = false
        }
    }
}

fun getMarketplaceImageUrl(seedOrUrl: String, category: String, index: Int = 0): String {
    if (seedOrUrl.startsWith("http://") || seedOrUrl.startsWith("https://") || seedOrUrl.startsWith("content://") || seedOrUrl.startsWith("file://")) {
        return seedOrUrl
    }
    // High-quality category photos from Unsplash for realistic placeholder previews
    val keywords = when (category.lowercase()) {
        "electronics" -> listOf(
            "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=500&q=80", // Smart watch
            "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=500&q=80", // Laptop
            "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=500&q=80", // Casio fx
            "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500&q=80", // iPad
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80"  // Headphones
        )
        "books & furniture" -> listOf(
            "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=500&q=80", // Wooden Table
            "https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?w=500&q=80", // Library Book pile
            "https://images.unsplash.com/photo-1532012197267-da84d127e765?w=500&q=80", // Book open
            "https://images.unsplash.com/photo-1544816155-12df9643f363?w=500&q=80", // Backpack
            "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=500&q=80"  // Desk lamp
        )
        "services & tutoring" -> listOf(
            "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=500&q=80", // Mac coding
            "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=500&q=80", // Tutoring writing
            "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=500&q=80", // Team work
            "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=500&q=80"  // Digital learning
        )
        else -> listOf(
            "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&q=80", // Grocery/General
            "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?w=500&q=80", // Polaroid camera
            "https://images.unsplash.com/photo-1572635196237-14b3f281503f?w=500&q=80", // Sunglasses
            "https://images.unsplash.com/photo-1485955900006-10f4d324d411?w=500&q=80", // Plant pot
            "https://images.unsplash.com/photo-1509062522246-3755977927d7?w=500&q=80"  // School supplies
        )
    }

    // Specific seed lookups
    if (seedOrUrl.contains("Casiofx991", ignoreCase = true)) {
         return "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=500&q=80"
    }
    if (seedOrUrl.contains("LaptopDesk", ignoreCase = true)) {
         return "https://images.unsplash.com/photo-1518455027359-f3f8164ba6bd?w=500&q=80"
    }

    val activeIndex = index.coerceIn(0, keywords.lastIndex)
    return keywords[activeIndex]
}

data class SokoBadgeConfig(
    val bgColor: Color,
    val textColor: Color,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val config = when (status.lowercase()) {
        "available" -> SokoBadgeConfig(
            Color(0xFF065F46).copy(alpha = 0.15f), // Emeral Darkish
            Color(0xFF34D399), // Emerald Bright
            "Available",
            Icons.Default.CheckCircle
        )
        "negotiable" -> SokoBadgeConfig(
            Color(0xFF78350F).copy(alpha = 0.15f), // Warm Amber Darkish
            Color(0xFFFBBF24), // Amber Bright
            "Negotiable",
            Icons.Default.Info
        )
        else -> SokoBadgeConfig(
            Color(0xFF7F1D1D).copy(alpha = 0.15f), // Red Darkish
            Color(0xFFF87171), // Red Bright
            "Sold",
            Icons.Default.Cancel
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(config.bgColor)
            .border(0.5.dp, config.textColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = config.icon,
            contentDescription = null,
            tint = config.textColor,
            modifier = Modifier.size(10.dp)
        )
        Text(
            text = config.label.uppercase(),
            color = config.textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun MarketplaceItemCard(listing: MarketplaceListingEntity, onClick: () -> Unit, onChatClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
        modifier = Modifier
            .fillMaxWidth()
            .cosmicCard(CosmicTheme.isDark)
            .clickable { onClick() }
            .testTag("listing_card_${listing.id}")
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
    ) {
        Column {
            // Thumbnail container with high-quality prioritized first image or custom category illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFF0F172A))
            ) {
                // Low-res soft blur-hash preview instantly before main image loads (simulated blur-hash background)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawRect(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        CosmicTheme.AccentTeal.copy(alpha = 0.25f),
                                        Color(0xFF1E293B)
                                    )
                                )
                            )
                        }
                )

                // High-fidelity cover thumbnail passed to Coil AsyncImage
                val curImages = listing.getImageList()
                val imageModel = getMarketplaceImageUrl(curImages.firstOrNull() ?: listing.imageSeed, listing.category, 0)
                
                AsyncImage(
                    model = imageModel,
                    contentDescription = listing.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // High-Contrast Price Tag overlay top corner
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, CosmicTheme.EmeraldAccent, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$" + String.format("%.0f", listing.price),
                        color = CosmicTheme.EmeraldAccent,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                }

                // Vibrant Status Badge overlay bottom corner
                StatusBadge(
                    status = listing.status,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )

                // Condition Pill overlay top-left
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = listing.condition.uppercase(),
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Body text with plenty of elegant whitespace
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = listing.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = CosmicTheme.TextLight
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = listing.description,
                    color = CosmicTheme.TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = CosmicTheme.AccentTeal,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Chuo Kikuu", color = CosmicTheme.TextMuted, fontSize = 10.sp)
                    }

                    // Private Soko messenger trigger
                    IconButton(
                        onClick = { onChatClick() },
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f))
                            .border(1.dp, CosmicTheme.AccentTeal.copy(alpha = 0.30f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubble,
                            contentDescription = "Inbox Private chat",
                            tint = CosmicTheme.AccentTeal,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SwipeableImageCarousel(images: List<String>, category: String, modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableStateOf(0) }
    // If the list is empty, default to at least 1 image (using the seed as model)
    val finalImages = if (images.isEmpty()) listOf("") else images
    val totalImages = finalImages.size

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp))
    ) {
        val currentImage = finalImages[selectedIndex]
        val cleanUrl = getMarketplaceImageUrl(currentImage, category, selectedIndex)

        // Main high fidelity product image loaded with smooth scale logic
        AsyncImage(
            model = cleanUrl,
            contentDescription = "Muonekano wa Bidhaa $selectedIndex",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient soft shadow overlay to pop icons and bottom indicators
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.40f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.70f)
                        )
                    )
                )
        )

        // Navigation arrows layout
        if (totalImages > 1) {
            IconButton(
                onClick = { selectedIndex = (selectedIndex - 1 + totalImages) % totalImages },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Picha Iliyopita",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            IconButton(
                onClick = { selectedIndex = (selectedIndex + 1) % totalImages },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Picha Inayofuata",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Image Index Overlay indicator count
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${selectedIndex + 1} / $totalImages Picha",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Dynamic Dot Indicator controls at the bottom center
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalImages) { idx ->
                val isSelected = idx == selectedIndex
                Box(
                    modifier = Modifier
                        .size(if (isSelected) 8.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) CosmicTheme.AccentTeal else Color.White.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun ListingDetailsView(listing: MarketplaceListingEntity, viewModel: CampusViewModel, onClose: () -> Unit) {
    val comments by viewModel.activeListingComments.collectAsStateWithLifecycle()
    val commentsAccordionState by viewModel.commentAccordionState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var commentText by remember { mutableStateOf("") }
    var replyingToId by remember { mutableStateOf<Int?>(null) }
    var replyText by remember { mutableStateOf("") }

    val listingImages = listing.getImageList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("listing_details_container"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Close header
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to listings", tint = CosmicTheme.TextLight)
                }
                Text(
                    text = if (listing.category == "Discussions") "Discussion Thread" else "Campus Soko details",
                    fontWeight = FontWeight.Black,
                    color = CosmicTheme.TextLight,
                    fontSize = 16.sp
                )

                // Seller Actions or Delete listing
                if (currentUser?.id == listing.sellerId) {
                    TextButton(onClick = { viewModel.markListingAsSold(listing) }) {
                        Text("Marekebisho", color = CosmicTheme.WarnOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Box(modifier = Modifier.size(36.dp))
                }
            }
        }

        // Horizontal Swipeable Images Gallery Carousel as Requested
        item {
            SwipeableImageCarousel(
                images = listingImages,
                category = listing.category,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Redesigned Listing Hero Information with Soft UI/Elevation and Whitespace
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CosmicTheme.MidnightGradStart),
                modifier = Modifier
                    .cosmicCard(CosmicTheme.isDark)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = listing.title,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = CosmicTheme.TextLight,
                        letterSpacing = (-0.5).sp
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CosmicTheme.Slate700)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(listing.category.uppercase(), color = CosmicTheme.AccentTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("HALI: " + listing.condition.uppercase(), color = CosmicTheme.TextLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (listing.category != "Discussions") {
                        // Soft UI pricing dashboard card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.03f))
                                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("BEI YA OFA", color = CosmicTheme.TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                    Text(
                                        text = "$" + String.format("%.2f", listing.price),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 26.sp,
                                        color = CosmicTheme.EmeraldAccent
                                    )
                                }

                                // Vibrant color-coded badge instead of a text-heavy label
                                StatusBadge(status = listing.status)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "MAELEZO YA BIDHAA",
                        color = CosmicTheme.TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = listing.description,
                        fontSize = 14.sp,
                        color = CosmicTheme.TextLight.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Divider(color = CosmicTheme.Slate700.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Consolidated Communication Actions dashboard with separated distinct target layouts
                    Text(
                        text = "MAWASILIANO NA UNUNUZI",
                        color = CosmicTheme.AccentTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Side-by-side or stacked distinct call-to-action buttons designed to reduce cognitive load
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // PRIMARY ACTION: In-app direct real-time chat with the seller
                        if (currentUser?.id != listing.sellerId && listing.category != "Discussions") {
                            Button(
                                onClick = {
                                    viewModel.openChatWithUser(listing.sellerId, listing.sellerName, listing.sellerPhoto)
                                    Toast.makeText(context, "Imeanza soga ya moja kwa moja na ${listing.sellerName}!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("chat_with_seller_btn")
                                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChatBubble,
                                    contentDescription = "Sogoa sasa",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ujumbe Binafsi (Chat in Soko)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        // SECONDARY QUICK REACH: WhatsApp Messaging vs Dialing Call options Panel
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // WhatsApp
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0x1922C55E)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .border(1.dp, Color(0x3322C55E), RoundedCornerShape(12.dp))
                                    .clickable {
                                        Toast.makeText(context, "Tunakuunganisha na WhatsApp ya muuzaji...", Toast.LENGTH_SHORT).show()
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(Color(0xFF22C55E), CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4ADE80))
                                    }
                                }
                            }

                            // Dial Phone Call
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.04f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        Toast.makeText(context, "Simulating direct phone call to +255 765 249 018...", Toast.LENGTH_LONG).show()
                                    }
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Call, contentDescription = "Call", tint = CosmicTheme.TextLight, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Piga Simu", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                                    }
                                }
                            }
                        }

                        // Manage listing action (For original sellers)
                        if (currentUser?.id == listing.sellerId) {
                            Button(
                                onClick = {
                                    viewModel.deleteListingItem(listing.id)
                                    onClose()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.15f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ondoa Chapisho Hili", color = Color.Red, fontSize = 13.sp, fontWeight = FontWeight.Bold)
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
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.cosmicCard(CosmicTheme.isDark)
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
                    Text("Hakuna maswali ya wazi bado. Kuwa wa kwanza kuuliza!", color = CosmicTheme.TextMuted, fontSize = 12.sp)
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

// Dialog helper to sell item with multi-image gallery selection and cloud storage simulation
@Composable
fun SellItemDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Double, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Electronics") }
    var condition by remember { mutableStateOf("Like New") }

    // Multi-Image Upload states
    var selectedImages by remember { mutableStateOf<List<String>>(emptyList()) }
    var uploadProgress by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var currentUploadJob by remember { mutableStateOf<String?>(null) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Set of pre-configured clean asset URLs for realistic product placeholders
    val unspList = when (category.lowercase()) {
        "electronics" -> listOf(
            "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=400&q=80",
            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&q=80",
            "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&q=80",
            "https://images.unsplash.com/photo-1525547719571-a2d4ac8945e2?w=400&q=80",
            "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=400&q=80"
        )
        "books & furniture" -> listOf(
            "https://images.unsplash.com/photo-1544816155-12df9643f363?w=400&q=80",
            "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=400&q=80",
            "https://images.unsplash.com/photo-1524758631624-e2822e304c36?w=400&q=80",
            "https://images.unsplash.com/photo-1538688525198-9b88f6f53126?w=400&q=80",
            "https://images.unsplash.com/photo-1507504038482-7621c51b3255?w=400&q=80"
        )
        else -> listOf(
            "https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400&q=80",
            "https://images.unsplash.com/photo-1522201949034-507737b4e3d7?w=400&q=80",
            "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=400&q=80",
            "https://images.unsplash.com/photo-1488190211105-8b0e65b80b4e?w=400&q=80",
            "https://images.unsplash.com/photo-1501504905252-473c47e087f8?w=400&q=80"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Publish Soko Listing",
                    fontWeight = FontWeight.Black,
                    color = CosmicTheme.TextLight,
                    fontSize = 18.sp
                )
                Text(
                    text = "Fill in the details to publish your items safely",
                    color = CosmicTheme.TextMuted,
                    fontSize = 12.sp
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Kichwa cha Tangazo (e.g. Printer solid)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.TextLight,
                        unfocusedTextColor = CosmicTheme.TextLight,
                        focusedBorderColor = CosmicTheme.AccentTeal
                    )
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Maelezo ya Kina ya Bidhaa") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.TextLight,
                        unfocusedTextColor = CosmicTheme.TextLight,
                        focusedBorderColor = CosmicTheme.AccentTeal
                    )
                )

                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Bei ya Ofa (USD $)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = CosmicTheme.TextLight,
                        unfocusedTextColor = CosmicTheme.TextLight,
                        focusedBorderColor = CosmicTheme.AccentTeal
                    )
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text("Kundi la Bidhaa:", color = CosmicTheme.AccentTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Electronics", "Books & Furniture", "Services & Tutoring").forEach { cat ->
                        val isSel = cat == category
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate700)
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(cat, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text("Hali ya Bidhaa:", color = CosmicTheme.AccentTeal, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Like New", "Good", "Used").forEach { cond ->
                        val isSel = cond == condition
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal else CosmicTheme.Slate700)
                                .clickable { condition = cond }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(cond, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Divider(color = Color.White.copy(alpha = 0.05f))

                // SECTION: Custom Multi-Image Upload Button and Indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PAKIA PICHA (${selectedImages.size}/5)",
                        color = CosmicTheme.AccentTeal,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    if (selectedImages.size < 5) {
                        TextButton(
                            onClick = {
                                // Simulate sequentially picking and uploading up to 5 images from device gallery simultaneously
                                val nextIndex = selectedImages.size
                                if (nextIndex < unspList.size) {
                                    val newUrl = unspList[nextIndex]
                                    selectedImages = selectedImages + newUrl
                                    
                                    // Start a non-blocking mock firebase channel upload thread
                                    scope.launch {
                                        currentUploadJob = "listing_img_${nextIndex + 1}.jpg"
                                        uploadProgress = uploadProgress + (newUrl to 0)
                                        for (pct in 1..10) {
                                            kotlinx.coroutines.delay(120)
                                            uploadProgress = uploadProgress + (newUrl to (pct * 10))
                                        }
                                        currentUploadJob = null
                                    }
                                } else {
                                    Toast.makeText(context, "Mwisho wa picha ni tano chuo!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = CosmicTheme.EmeraldAccent)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Piga/Chagua", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Cloud Storage Real-time Upload Progress Indicator Info Box
                currentUploadJob?.let { jobName ->
                                        val testProgress = selectedImages.lastOrNull()?.let { uploadProgress[it] } ?: 0
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(CosmicTheme.EmeraldAccent.copy(alpha = 0.08f))
                            .border(0.5.dp, CosmicTheme.EmeraldAccent.copy(alpha = 0.20f), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(CosmicTheme.EmeraldAccent, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Inapakia '$jobName' Firebase storage...",
                                        color = CosmicTheme.TextLight,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text("$testProgress%", color = CosmicTheme.EmeraldAccent, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { testProgress / 100f },
                                modifier = Modifier.fillMaxWidth().height(3.dp).clip(CircleShape),
                                color = CosmicTheme.EmeraldAccent,
                                trackColor = Color.White.copy(alpha = 0.05f)
                            )
                        }
                    }
                }

                // Grid view of thumbnail previews with removal close triggers as requested
                if (selectedImages.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        selectedImages.forEachIndexed { idx, url ->
                            val progress = uploadProgress[url] ?: 100
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.03f))
                                    .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                            ) {
                                // Loaded Image Preview Thumbnails
                                AsyncImage(
                                    model = url,
                                    contentDescription = "Preview thumbnail",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Progress overlay scrim for unfinished uploads
                                if (progress < 100) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(Color.Black.copy(alpha = 0.6f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("$progress%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                // Interactive individual floating deletion Close Button
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(2.dp)
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color.Red.copy(alpha = 0.85f))
                                        .clickable {
                                            selectedImages = selectedImages.toMutableList().apply { removeAt(idx) }
                                            uploadProgress = uploadProgress.toMutableMap().apply { remove(url) }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Ondoa Picha",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.01f))
                            .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Bado hujachagua picha (Optional picha 1 up to 5)", color = CosmicTheme.TextMuted, fontSize = 10.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceStr.toDoubleOrNull() ?: 0.0
                    // Join array string into delimited format for Room entities listing
                    val stringifiedUrls = selectedImages.joinToString(",")
                    onSave(title, desc, p, category, condition, stringifiedUrls)
                },
                enabled = title.isNotBlank() && priceStr.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Publish Item", fontWeight = FontWeight.Bold, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CosmicTheme.TextMuted)
            }
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
                                        text = if (room.participantId == "japhet_moderator") "J" else room.participantName.substring(0, 1).uppercase(),
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
                                                text = if (room.participantId == "japhet_moderator") "Ask Me (Jay)" else room.participantName,
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
                                                    Text("AI Assistant", color = CosmicTheme.DevPurple, fontSize = 8.sp, fontWeight = FontWeight.Bold)
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
                                        val displayLast = if (room.participantId == "japhet_moderator" && (room.lastMessage.startsWith("Hey! Welcome to Smart Campus") || room.lastMessage.startsWith("Hey! Welcome to") || room.lastMessage.contains("Japhet"))) {
                                            val displayName = currentUser?.displayName ?: "Student"
                                            val firstName = displayName.split(" ").firstOrNull() ?: "there"
                                            val currentYr = viewModel.userYear.value
                                            "Hey $firstName! Ready to crush your $currentYr Math schedule today?"
                                        } else {
                                            room.lastMessage
                                        }
                                        Text(
                                            text = displayLast,
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

fun parseMessageTextAndLinks(text: String): Pair<String, List<String>> {
    val regex = "\\[([^\\]]+)\\]".toRegex()
    val matches = regex.findAll(text)
    val links = matches.map { it.groupValues[1] }.toList()
    val cleanText = text.replace(regex, "").trim()
    return Pair(cleanText, links)
}

@Composable
fun ActiveChatView(room: ChatRoomEntity, viewModel: CampusViewModel, onBack: () -> Unit) {
    val messages by viewModel.activeChatMessages.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isJayTyping by viewModel.isJayTyping.collectAsStateWithLifecycle()
    var messageText by remember { mutableStateOf("") }

    val displayNameOverride = if (room.participantId == "japhet_moderator") "Ask Me (Jay)" else room.participantName
    val displayLetterOverride = if (room.participantId == "japhet_moderator") "J" else room.participantName.substring(0, 1).uppercase()
    val subtitleOverride = if (room.participantId == "japhet_moderator") "Personal AI Assistant • Active" else "Campus Member • Active"

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
                Text(displayLetterOverride, color = CosmicTheme.AccentTeal, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(displayNameOverride, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CosmicTheme.TextLight)
                Text(
                    text = subtitleOverride,
                    fontSize = 11.sp,
                    color = if (room.participantId == "japhet_moderator") CosmicTheme.AccentTeal else CosmicTheme.TextMuted
                )
            }
        }

        // Messages list
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(CosmicTheme.DarkSlate)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages) { msg ->
                val isMe = msg.senderId == currentUser?.id
                val rawText = msg.text
                val displayName = currentUser?.displayName ?: "Student"
                val firstName = displayName.split(" ").firstOrNull() ?: "there"
                val currentYr = viewModel.userYear.collectAsStateWithLifecycle().value

                // Active Greeting routing intercept
                val displayText = if (msg.senderId == "japhet_moderator" && (rawText.startsWith("Hey! Welcome to Smart Campus") || rawText.startsWith("Hey! Welcome to") || rawText.contains("Japhet, let me know"))) {
                    "Hey $firstName! Ready to crush your $currentYr Math schedule today? I see you have Tutorial MT360 later. Need help finding the room or prepping notes? [View Timetable]"
                } else {
                    rawText
                }

                val parsed = parseMessageTextAndLinks(displayText)
                val cleanText = parsed.first
                val links = parsed.second

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = if (isMe) Alignment.CenterEnd else Alignment.CenterStart
                ) {
                    Column(
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
                        modifier = Modifier.widthIn(max = 295.dp).padding(vertical = 2.dp)
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
                            )
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)) {
                                Text(
                                    text = cleanText, 
                                    color = if (isMe) Color.White else CosmicTheme.TextLight, 
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp,
                                    style = LocalTextStyle.current.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isMe) "Wewe" else (if (room.participantId == "japhet_moderator") "Ask Me (Jay)" else room.participantName),
                                    color = if (isMe) Color.White.copy(alpha = 0.6f) else CosmicTheme.TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = if (isMe) TextAlign.End else TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        // Status indicators for user messages below bubble
                        if (isMe) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Delivered • Seen",
                                color = CosmicTheme.AccentTeal,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }

                        // Custom deep links configured as clickable iOS pill buttons
                        links.forEach { linkText ->
                            Button(
                                onClick = {
                                    // Parse any specific course code if mentioned to highlight it
                                    val courseMatch = "[A-Za-z]{2}[0-9]{3}".toRegex().find(displayText)
                                    if (courseMatch != null) {
                                        viewModel.setActiveCourseFilter(courseMatch.value.uppercase())
                                    }

                                    when {
                                        linkText.contains("Timetable", ignoreCase = true) || linkText.contains("Schedule", ignoreCase = true) || linkText.contains("Ratiba", ignoreCase = true) -> {
                                            viewModel.requestTabNavigation(AppTab.Timetable)
                                        }
                                        linkText.contains("Library", ignoreCase = true) || linkText.contains("Maktaba", ignoreCase = true) -> {
                                            viewModel.requestTabNavigation(AppTab.Maktaba)
                                        }
                                        linkText.contains("Market", ignoreCase = true) || linkText.contains("Soko", ignoreCase = true) -> {
                                            viewModel.requestTabNavigation(AppTab.Marketplace)
                                        }
                                        else -> {
                                            viewModel.requestTabNavigation(AppTab.Timetable)
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CosmicTheme.AccentTeal.copy(alpha = 0.15f),
                                    contentColor = CosmicTheme.AccentTeal
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Text(text = linkText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // Real-time animated typing indicator when Jay is active
            if (isJayTyping) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CosmicTheme.AccentTeal)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CosmicTheme.AccentTeal.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CosmicTheme.AccentTeal.copy(alpha = 0.3f))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Jay is typing...",
                            color = CosmicTheme.TextMuted,
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
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
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(viewModel: CampusViewModel, onBackToHome: (() -> Unit)? = null) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val programSetting by viewModel.userProgram.collectAsStateWithLifecycle()
    val yearSetting by viewModel.userYear.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    var showNotifBoard by remember { mutableStateOf(false) }
    var privacyModeEnabled by remember { mutableStateOf(true) }
    var showEditAcademicSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val programDisplay = if (programSetting == "UD089") "BSc Math/Stats" else "CS Core Curriculum"

    if (showNotifBoard) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
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
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile top navigation bar
            item {
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
                        text = "My Profile Hub",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
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
            }

            // 1. Visual Polish: Glassmorphic Hero Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = if (isDarkTheme) {
                                    listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                                } else {
                                    listOf(Color(0xFFE2E8F0), Color(0xFFF1F5F9))
                                }
                            )
                        )
                        .border(
                            1.dp,
                            if (isDarkTheme) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Centered premium circular avatar
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .drawBehind {
                                    drawCircle(
                                        color = CosmicTheme.AccentTeal.copy(alpha = 0.12f),
                                        radius = size.minDimension / 2f + 8f
                                    )
                                    drawCircle(
                                        color = CosmicTheme.AccentTeal.copy(alpha = 0.06f),
                                        radius = size.minDimension / 2f + 16f
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f))
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
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Verified Pro student text/icon
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentUser?.displayName ?: "Student Core",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = CosmicTheme.TextLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified Academic Student",
                                tint = CosmicTheme.AccentTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = currentUser?.university ?: "University of Science & Tech",
                            fontSize = 12.sp,
                            color = CosmicTheme.TextMuted,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Beautiful badge displaying major and level
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CosmicTheme.AccentTeal.copy(alpha = 0.1f))
                                .border(1.dp, CosmicTheme.AccentTeal.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${programDisplay} • ${yearSetting}",
                                fontSize = 11.sp,
                                color = CosmicTheme.AccentTeal,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // 2. Consolidate Stats: Side-by-Side Horizontal Tiles / Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val schedulesCount = viewModel.routines.collectAsStateWithLifecycle().value.count { it.remindersEnabled }
                    val activeListingsCount = viewModel.allListings.collectAsStateWithLifecycle().value.count { it.sellerId == currentUser?.id }

                    // Card for Schedules
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Today,
                                contentDescription = null,
                                tint = CosmicTheme.WarnOrange,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "$schedulesCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTheme.TextLight
                            )
                            Text(
                                text = "Courses active",
                                fontSize = 11.sp,
                                color = CosmicTheme.TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Card for Listings
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.6f) else Color(0xFFF1F5F9)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.dp,
                                if (isDarkTheme) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = CosmicTheme.EmeraldAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "$activeListingsCount",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = CosmicTheme.TextLight
                            )
                            Text(
                                text = "Items for sale",
                                fontSize = 11.sp,
                                color = CosmicTheme.TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 3. Modern Section Header: Study Stats
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Study Stats (Gamification) ⚡",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicTheme.TextLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Study hours tile
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFFF8FAFC)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (isDarkTheme) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.02f),
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CosmicTheme.AccentTeal.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QueryBuilder,
                                        contentDescription = null,
                                        tint = CosmicTheme.AccentTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "14.5 Hours",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicTheme.TextLight
                                    )
                                    Text(
                                        text = "Studied This Week",
                                        fontSize = 9.sp,
                                        color = CosmicTheme.TextMuted
                                    )
                                }
                            }
                        }

                        // Contributions tile
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.4f) else Color(0xFFF8FAFC)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (isDarkTheme) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.02f),
                                    RoundedCornerShape(16.dp)
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(CosmicTheme.EmeraldAccent.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderOpen,
                                        contentDescription = null,
                                        tint = CosmicTheme.EmeraldAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "5 PDFs Shared",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CosmicTheme.TextLight
                                    )
                                    Text(
                                        text = "Material Contributed",
                                        fontSize = 9.sp,
                                        color = CosmicTheme.TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. Student Privacy Shield: Card Refinement (Soft Blue/Teal tint, visually lightweight)
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) {
                            Color(0xFF0F172A).copy(alpha = 0.8f)
                        } else {
                            Color(0xFFF0F9FF)
                        }
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = if (privacyModeEnabled) {
                                CosmicTheme.AccentTeal.copy(alpha = 0.3f)
                            } else {
                                Color.Red.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (privacyModeEnabled) Icons.Default.Lock else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (privacyModeEnabled) CosmicTheme.AccentTeal else Color.Red,
                                    modifier = Modifier.size(22.dp)
                                )
                                Column {
                                    Text(
                                        text = "Student Privacy Shield",
                                        color = CosmicTheme.TextLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = if (privacyModeEnabled) "Active Protection (Namba Imefichwa)" else "Under-protected",
                                        color = if (privacyModeEnabled) CosmicTheme.AccentTeal else Color.Red,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }

                            Button(
                                onClick = { privacyModeEnabled = !privacyModeEnabled },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (privacyModeEnabled) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f),
                                    contentColor = if (privacyModeEnabled) CosmicTheme.AccentTeal else Color.Red
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (privacyModeEnabled) "Deactivate" else "Activate",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = if (privacyModeEnabled) {
                                "Your active listings only display an encrypted handle. Other students must initiate a chat via the secure Appwrite messaging system—your phone number remains hidden."
                            } else {
                                "WARNING: Deactivating the Shield renders your phone number public on physical item cards in the campus marketplace, exposing you to unvetted requests."
                            },
                            color = CosmicTheme.TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            // 5. 2x2 Quick Links Section
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Quick Settings & Links ⚙️",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = CosmicTheme.TextLight,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            // Link 1: Edit Profile (opens the Bottom Sheet)
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { showEditAcademicSheet = true }
                                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Edit Academic Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                                }
                            }

                            // Link 2: Under development toggle state / Theme setting
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.toggleTheme() }
                                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null, 
                                        tint = CosmicTheme.AccentTeal, 
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isDarkTheme) "Dark Interface" else "Light Interface", 
                                        fontSize = 11.sp, 
                                        fontWeight = FontWeight.Bold, 
                                        color = CosmicTheme.TextLight
                                    )
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            // Link 3: Security & Privacy (Highlights Privacy Shield toggle)
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { privacyModeEnabled = !privacyModeEnabled }
                                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Toggle Shield Lock", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                                }
                            }

                            // Link 4: Help/FAQ Note
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.3f) else Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        android.widget.Toast.makeText(context, "Msaada na support inapatikana kupitia dev@smartcampus.com", android.widget.Toast.LENGTH_LONG).show()
                                    }
                                    .border(1.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(12.dp))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = CosmicTheme.AccentTeal, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Help & Support", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = CosmicTheme.TextLight)
                                }
                            }
                        }
                    }
                }
            }

            // 6. Upcoming Deadline Preview: Non-intrusive card at bottom for "sticky" factor
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isDarkTheme) Color(0xFF1E293B).copy(alpha = 0.35f) else Color(0xFFFFFAF0)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (isDarkTheme) CosmicTheme.WarnOrange.copy(alpha = 0.2f) else Color(0xFFFDE8E8),
                            RoundedCornerShape(14.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(CosmicTheme.WarnOrange.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("⏳", fontSize = 14.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Next upcoming deadline:",
                                fontSize = 10.sp,
                                color = CosmicTheme.TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Assignment 3 Final Submission",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CosmicTheme.TextLight
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CosmicTheme.WarnOrange.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "2 days left",
                                fontSize = 9.sp,
                                color = CosmicTheme.WarnOrange,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            // System Version info footer
            item {
                Text(
                    text = "Smart Campus • Version 1.0.3",
                    fontSize = 10.sp,
                    color = CosmicTheme.TextMuted,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )
            }

            // 7. Logout Button: Elegant Outlined Button with Red Outline and Text
            item {
                OutlinedButton(
                    onClick = { viewModel.logout() },
                    border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Red
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .padding(bottom = 12.dp)
                        .testTag("logout_button")
                ) {
                    Text(
                        text = "Logout Account",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet Edit Academic Selection menu
    if (showEditAcademicSheet) {
        ModalBottomSheet(
            onDismissRequest = { showEditAcademicSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CosmicTheme.MidnightGradStart,
            contentColor = CosmicTheme.TextLight,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth().testTag("edit_academic_sheet"),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Edit Academic Affiliation 🎓",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = CosmicTheme.TextLight,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = "Select Academic Program:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.TextLight
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("UD089", "CS").forEach { p ->
                        val isSel = programSetting == p
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f))
                                .border(
                                    2.dp, 
                                    if (isSel) CosmicTheme.AccentTeal else Color.White.copy(alpha = 0.05f), 
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { viewModel.updateProfileSettings(p, yearSetting) }
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (p == "UD089") "BSc Math/Stats (UD089)" else "Computer Science (CS)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) CosmicTheme.AccentTeal else CosmicTheme.TextLight,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Select Year Level:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CosmicTheme.TextLight
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Year 1", "Year 2", "Year 3", "Year 4").forEach { y ->
                        val isSel = yearSetting == y
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSel) CosmicTheme.AccentTeal.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.03f))
                                .border(
                                    2.dp, 
                                    if (isSel) CosmicTheme.AccentTeal else Color.White.copy(alpha = 0.05f), 
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.updateProfileSettings(programSetting, y) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = y,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSel) CosmicTheme.AccentTeal else CosmicTheme.TextLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { showEditAcademicSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CosmicTheme.AccentTeal),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Save & Apply Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))
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
    val now = java.time.LocalTime.now()
    val currMinutes = now.hour * 60 + now.minute
    
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
            val endLocalTime = java.time.LocalTime.of(endTime / 60, endTime % 60)
            minLeft = java.time.Duration.between(now, endLocalTime).toMinutes().toInt()
            break
        } else if (startTime > currMinutes) {
            val startLocalTime = java.time.LocalTime.of(startTime / 60, startTime % 60)
            val diff = java.time.Duration.between(now, startLocalTime).toMinutes().toInt()
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
