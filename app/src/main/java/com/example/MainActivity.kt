package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.KgViewModel
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
    private val viewModel: KgViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: KgViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(
                message = msg,
                duration = SnackbarDuration.Short
            )
            viewModel.clearUserMessage()
        }
    }

    // Determine if bottom navigation should be visible
    val isCustomerScreen = when (currentScreen) {
        is ScreenDestination.Home,
        is ScreenDestination.Services,
        is ScreenDestination.CustomerOrders,
        is ScreenDestination.Courses,
        is ScreenDestination.CustomerProfile -> true
        else -> false
    }

    // System Back Press handling
    BackHandler(enabled = currentScreen !is ScreenDestination.Home) {
        when (currentScreen) {
            is ScreenDestination.ServiceDetail -> viewModel.navigateTo(ScreenDestination.Services)
            is ScreenDestination.BookService -> {
                val serviceId = (currentScreen as ScreenDestination.BookService).serviceId
                viewModel.navigateTo(ScreenDestination.ServiceDetail(serviceId))
            }
            is ScreenDestination.CustomerOrderDetail -> viewModel.navigateTo(ScreenDestination.CustomerOrders)
            is ScreenDestination.AdminOrderDetail -> viewModel.navigateTo(ScreenDestination.AdminOrders)
            is ScreenDestination.AdminOrders -> viewModel.navigateTo(ScreenDestination.AdminDashboard)
            is ScreenDestination.AdminDashboard -> viewModel.navigateTo(ScreenDestination.Home)
            is ScreenDestination.AdminLogin -> viewModel.navigateTo(ScreenDestination.Home)
            else -> viewModel.navigateTo(ScreenDestination.Home)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (isCustomerScreen) {
                KgBottomNavigationBar(
                    currentScreen = currentScreen,
                    onNavigate = { destination -> viewModel.navigateTo(destination) }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (isCustomerScreen) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    is ScreenDestination.Home -> HomeScreen(viewModel = viewModel)
                    is ScreenDestination.Services -> ServicesScreen(viewModel = viewModel)
                    is ScreenDestination.ServiceDetail -> ServiceDetailScreen(
                        serviceId = screen.serviceId,
                        viewModel = viewModel
                    )
                    is ScreenDestination.BookService -> BookServiceScreen(
                        serviceId = screen.serviceId,
                        packageId = screen.packageId,
                        viewModel = viewModel
                    )
                    is ScreenDestination.CustomerOrders -> CustomerOrdersScreen(viewModel = viewModel)
                    is ScreenDestination.CustomerOrderDetail -> CustomerOrderDetailScreen(
                        orderId = screen.orderId,
                        viewModel = viewModel
                    )
                    is ScreenDestination.CustomerLogin -> CustomerAuthScreen(
                        isRegisterInitial = false,
                        viewModel = viewModel
                    )
                    is ScreenDestination.CustomerRegister -> CustomerAuthScreen(
                        isRegisterInitial = true,
                        viewModel = viewModel
                    )
                    is ScreenDestination.CustomerProfile -> ProfileScreen(viewModel = viewModel)
                    is ScreenDestination.CustomerNotifications -> NotificationsScreen(viewModel = viewModel)
                    is ScreenDestination.Courses -> CoursesScreen(viewModel = viewModel)
                    is ScreenDestination.DigitalProducts -> DigitalProductsScreen(viewModel = viewModel)
                    is ScreenDestination.ContactSupport -> ContactSupportScreen(viewModel = viewModel)

                    // Admin Protected Screens
                    is ScreenDestination.AdminLogin -> AdminLoginScreen(viewModel = viewModel)
                    is ScreenDestination.AdminDashboard -> AdminDashboardScreen(viewModel = viewModel)
                    is ScreenDestination.AdminOrders -> AdminOrdersScreen(viewModel = viewModel)
                    is ScreenDestination.AdminOrderDetail -> AdminOrderDetailScreen(
                        orderId = screen.orderId,
                        viewModel = viewModel
                    )
                    else -> HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun KgBottomNavigationBar(
    currentScreen: ScreenDestination,
    onNavigate: (ScreenDestination) -> Unit
) {
    NavigationBar(
        containerColor = PureWhite,
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            BottomNavItem("Home", Icons.Default.Home, ScreenDestination.Home, "nav_item_home"),
            BottomNavItem("Services", Icons.Default.Work, ScreenDestination.Services, "nav_item_services"),
            BottomNavItem("Orders", Icons.Default.ShoppingBag, ScreenDestination.CustomerOrders, "nav_item_orders"),
            BottomNavItem("Courses", Icons.Default.School, ScreenDestination.Courses, "nav_item_courses"),
            BottomNavItem("Profile", Icons.Default.Person, ScreenDestination.CustomerProfile, "nav_item_profile")
        )

        navItems.forEach { item ->
            val isSelected = currentScreen::class == item.destination::class
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.destination) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ElectricBlue,
                    selectedTextColor = ElectricBlue,
                    unselectedIconColor = SubtitleText,
                    unselectedTextColor = SubtitleText,
                    indicatorColor = ElectricBlue.copy(alpha = 0.12f)
                ),
                modifier = Modifier.testTag(item.testTag)
            )
        }
    }
}

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val destination: ScreenDestination,
    val testTag: String
)

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
