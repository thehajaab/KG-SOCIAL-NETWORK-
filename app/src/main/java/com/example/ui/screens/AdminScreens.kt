package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.KgTopBar
import com.example.ui.components.OrderTimelineView
import com.example.ui.components.StatCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.KgViewModel
import com.example.ui.viewmodel.ScreenDestination
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminLoginScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    var adminUsername by remember { mutableStateOf("admin") }
    var adminPassword by remember { mutableStateOf("admin123") }
    var isAuthenticating by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Admin Authentication",
                subtitle = "Authorized Personnel Only",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavyDarker)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                shape = RoundedCornerShape(20.dp),
                color = DeepNavy,
                border = BorderStroke(1.dp, DeepNavyLight),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(AccentPurple.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = AccentPurple,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "KG Social Network",
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "SECURE ADMIN PORTAL",
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = adminUsername,
                        onValueChange = { adminUsername = it },
                        label = { Text("Admin Username / ID", color = SkyBlue) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SkyBlue) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_username"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = SkyBlue,
                            unfocusedBorderColor = DeepNavyLight
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = adminPassword,
                        onValueChange = { adminPassword = it },
                        label = { Text("Admin Password", color = SkyBlue) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SkyBlue) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_password"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            focusedBorderColor = SkyBlue,
                            unfocusedBorderColor = DeepNavyLight
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            isAuthenticating = true
                            viewModel.loginAdmin(adminUsername, adminPassword) {
                                isAuthenticating = false
                                viewModel.navigateTo(ScreenDestination.AdminDashboard)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("btn_admin_login_submit"),
                        enabled = !isAuthenticating
                    ) {
                        if (isAuthenticating) {
                            CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Authenticate & Enter Dashboard", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = DeepNavyDarker,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Default Credentials: admin / admin123\nSuper Admin: Jahangir Ahmed Kacher",
                            color = SubtitleText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDashboardScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val currentAdmin by viewModel.currentAdmin.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val unreadAdminNotifs by viewModel.unreadAdminNotifCount.collectAsState()
    val totalRevenue by viewModel.totalRevenue.collectAsState()
    val totalUsers by viewModel.totalUsersCount.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val inquiries by viewModel.contactRequests.collectAsState()

    val pendingCount = allOrders.count { it.orderStatus == "PENDING" }
    val inProgressCount = allOrders.count { it.orderStatus == "IN_PROGRESS" }
    val reviewCount = allOrders.count { it.orderStatus == "REVIEW" }
    val completedCount = allOrders.count { it.orderStatus == "COMPLETED" }

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Admin Console",
                subtitle = currentAdmin?.fullName ?: "Super Admin",
                showBackButton = false,
                unreadNotifCount = unreadAdminNotifs,
                onNotifClick = { viewModel.navigateTo(ScreenDestination.CustomerNotifications) },
                isAdmin = true,
                onAdminToggleClick = { viewModel.navigateTo(ScreenDestination.Home) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Admin Identity Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DeepNavy,
                    border = BorderStroke(1.dp, DeepNavyLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentAdmin?.fullName ?: "Jahangir Ahmed Kacher",
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = AccentPurple,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = currentAdmin?.role ?: "SUPER_ADMIN",
                                        color = PureWhite,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Live PostgreSQL & Room Connected",
                                color = StatusCompleted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        OutlinedButton(
                            onClick = { viewModel.logoutAdmin() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled),
                            border = BorderStroke(1.dp, StatusCancelled.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_admin_logout")
                        ) {
                            Text("Logout", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Real-Time New Orders Alert
            if (pendingCount > 0) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = StatusPending.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, StatusPending.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.navigateTo(ScreenDestination.AdminOrders) }
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = StatusPending)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "⚠️ $pendingCount Pending Orders Require Action",
                                    fontWeight = FontWeight.Bold,
                                    color = DarkText,
                                    fontSize = 13.sp
                                )
                                Text("Click to review, confirm, and assign tasks.", fontSize = 11.sp, color = SubtitleText)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SubtitleText)
                        }
                    }
                }
            }

            // 2x2 Grid of KPIs
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Total Orders",
                        value = "${allOrders.size}",
                        icon = Icons.Default.ShoppingBag,
                        iconColor = ElectricBlue,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "In Progress",
                        value = "$inProgressCount",
                        icon = Icons.Default.Sync,
                        iconColor = StatusInProgress,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Completed",
                        value = "$completedCount",
                        icon = Icons.Default.CheckCircle,
                        iconColor = StatusCompleted,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Customers",
                        value = "$totalUsers",
                        icon = Icons.Default.People,
                        iconColor = AccentPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Navigation Quick Links
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.navigateTo(ScreenDestination.AdminOrders) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("btn_nav_admin_orders")
                    ) {
                        Text("Manage Orders (${allOrders.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.navigateTo(ScreenDestination.Home) },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ElectricBlue),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("View Public App", fontSize = 12.sp, color = ElectricBlue)
                    }
                }
            }

            // Recent Orders Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Real Orders",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkText
                    )
                    TextButton(onClick = { viewModel.navigateTo(ScreenDestination.AdminOrders) }) {
                        Text("View All", color = ElectricBlue)
                    }
                }
            }

            items(allOrders.take(5)) { order ->
                AdminOrderListItem(
                    order = order,
                    onClick = {
                        viewModel.navigateTo(ScreenDestination.AdminOrderDetail(order.id))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsState()
    var selectedStatus by remember { mutableStateOf("ALL") }

    val statusTabs = listOf("ALL", "PENDING", "CONFIRMED", "IN_PROGRESS", "REVIEW", "COMPLETED", "CANCELLED")

    val filteredOrders = if (selectedStatus == "ALL") {
        allOrders
    } else {
        allOrders.filter { it.orderStatus.equals(selectedStatus, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Order Management",
                subtitle = "${filteredOrders.size} Orders",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.AdminDashboard) },
                isAdmin = true
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(innerPadding)
        ) {
            // Status Filter Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(statusTabs) { tab ->
                    val isSelected = selectedStatus == tab
                    val count = if (tab == "ALL") allOrders.size else allOrders.count { it.orderStatus.equals(tab, ignoreCase = true) }
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedStatus = tab },
                        label = { Text("$tab ($count)", fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DeepNavy,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
            }

            if (filteredOrders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No orders under status: $selectedStatus", color = SubtitleText, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredOrders) { order ->
                        AdminOrderListItem(
                            order = order,
                            onClick = {
                                viewModel.navigateTo(ScreenDestination.AdminOrderDetail(order.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderListItem(
    order: OrderEntity,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderGray),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("admin_order_item_${order.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    color = ElectricBlue
                )
                StatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = order.serviceTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = DarkText
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Customer: ${order.customerName}",
                    fontSize = 12.sp,
                    color = SubtitleText
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("• ₹${order.amount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = DarkText)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderDetailScreen(
    orderId: String,
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val order by viewModel.getOrderFlow(orderId).collectAsState(initial = null)
    val activities by viewModel.getOrderActivitiesFlow(orderId).collectAsState(initial = emptyList())
    val messages by viewModel.getOrderMessagesFlow(orderId).collectAsState(initial = emptyList())
    val currentAdmin by viewModel.currentAdmin.collectAsState()

    var showDeliverableDialog by remember { mutableStateOf(false) }
    var deliverableFilesText by remember { mutableStateOf("") }
    var deliverableNoteText by remember { mutableStateOf("") }

    var chatMessageText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(orderId) {
        viewModel.markOrderMessagesRead(orderId, isCustomer = false)
    }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = ElectricBlue)
        }
        return
    }

    val currentOrder = order!!

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Order: ${currentOrder.id}",
                subtitle = "Manage & Control Order",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.AdminOrders) },
                isAdmin = true
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SoftGray)
                .padding(innerPadding)
        ) {
            // Tabs
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhite,
                contentColor = ElectricBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Actions & Details", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        viewModel.markOrderMessagesRead(orderId, isCustomer = false)
                    },
                    text = { Text("Customer Chat (${messages.size})", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                )
            }

            if (selectedTab == 0) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Quick Action Control Bar
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DeepNavy,
                            border = BorderStroke(1.dp, DeepNavyLight)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Admin Status Actions", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = PureWhite)
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (currentOrder.orderStatus == "PENDING") {
                                        Button(
                                            onClick = {
                                                viewModel.updateOrderStatus(currentOrder.id, "CONFIRMED", "Order confirmed by Admin.")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusConfirmed),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_admin_confirm")
                                        ) {
                                            Text("Confirm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (currentOrder.orderStatus == "CONFIRMED" || currentOrder.orderStatus == "PENDING") {
                                        Button(
                                            onClick = {
                                                viewModel.updateOrderStatus(currentOrder.id, "IN_PROGRESS", "Team started production.")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusInProgress),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_admin_start_work")
                                        ) {
                                            Text("Start Work", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (currentOrder.orderStatus == "IN_PROGRESS") {
                                        Button(
                                            onClick = {
                                                viewModel.updateOrderStatus(currentOrder.id, "REVIEW", "Draft deliverables submitted for review.")
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusReview),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_admin_move_review")
                                        ) {
                                            Text("To Review", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (currentOrder.orderStatus != "COMPLETED") {
                                        Button(
                                            onClick = { showDeliverableDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_admin_complete")
                                        ) {
                                            Text("Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.assignStaff(currentOrder.id, "Jahangir Ahmed Kacher")
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyBlue),
                                        border = BorderStroke(1.dp, SkyBlue),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Assign Staff", fontSize = 11.sp)
                                    }

                                    if (currentOrder.orderStatus != "CANCELLED") {
                                        OutlinedButton(
                                            onClick = {
                                                viewModel.updateOrderStatus(currentOrder.id, "CANCELLED", "Order cancelled by Admin.")
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled),
                                            border = BorderStroke(1.dp, StatusCancelled),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Cancel Order", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Order Info Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Customer & Order Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text("Customer: ${currentOrder.customerName}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("Email: ${currentOrder.customerEmail}", fontSize = 12.sp, color = SubtitleText)
                                Text("Phone: ${currentOrder.customerPhone}", fontSize = 12.sp, color = SubtitleText)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Assigned To: ${currentOrder.assignedStaff}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                                Text("Amount: ₹${currentOrder.amount.toInt()} (${currentOrder.paymentStatus})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Client Requirements
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Client Submission Brief", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Requirements:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SubtitleText)
                                Text(currentOrder.requirements, fontSize = 13.sp, color = DarkText)
                                if (currentOrder.instructions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Instructions:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SubtitleText)
                                    Text(currentOrder.instructions, fontSize = 13.sp, color = DarkText)
                                }
                                if (currentOrder.attachedFiles.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Uploaded Assets:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SubtitleText)
                                    Text(currentOrder.attachedFiles, fontSize = 13.sp, color = ElectricBlue)
                                }
                            }
                        }
                    }

                    // Activity History
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Real-Time Audit Log", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                                Spacer(modifier = Modifier.height(10.dp))
                                activities.forEach { act ->
                                    Row(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = ElectricBlue,
                                            modifier = Modifier.size(12.dp).padding(top = 2.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(act.message, fontSize = 12.sp, color = DarkText)
                                            Text("By ${act.actorName} (${act.actorType})", fontSize = 10.sp, color = SubtitleText)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Admin Chat Tab
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(messages) { msg ->
                            val isMe = msg.senderType == "ADMIN"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isMe) DeepNavy else PureWhite,
                                    border = if (isMe) null else BorderStroke(1.dp, BorderGray),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = msg.senderName,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isMe) SkyBlue else ElectricBlue
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = msg.message,
                                            fontSize = 13.sp,
                                            color = if (isMe) PureWhite else DarkText
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Admin input field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatMessageText,
                            onValueChange = { chatMessageText = it },
                            placeholder = { Text("Reply as KG Support...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_admin_chat_message"),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureWhite,
                                unfocusedContainerColor = PureWhite
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (chatMessageText.isNotBlank()) {
                                    viewModel.sendMessage(
                                        orderId = currentOrder.id,
                                        text = chatMessageText,
                                        isSenderAdmin = true,
                                        receiverId = currentOrder.customerId
                                    )
                                    chatMessageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(DeepNavy)
                                .testTag("btn_send_admin_message")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = SkyBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Deliverables Dialog
        if (showDeliverableDialog) {
            AlertDialog(
                onDismissRequest = { showDeliverableDialog = false },
                title = { Text("Complete Order & Upload Deliverables") },
                text = {
                    Column {
                        Text("Provide download filename or storage link for the client:", fontSize = 12.sp, color = SubtitleText)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deliverableFilesText,
                            onValueChange = { deliverableFilesText = it },
                            placeholder = { Text("e.g. KG_Deliverables_Final.zip") },
                            modifier = Modifier.fillMaxWidth().testTag("input_deliverable_files"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = deliverableNoteText,
                            onValueChange = { deliverableNoteText = it },
                            placeholder = { Text("Completion note to customer...") },
                            modifier = Modifier.fillMaxWidth().testTag("input_deliverable_note"),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val files = if (deliverableFilesText.isBlank()) "KG_Final_Production_Files.zip" else deliverableFilesText
                            viewModel.completeOrderWithDeliverables(currentOrder.id, files, deliverableNoteText)
                            showDeliverableDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted)
                    ) {
                        Text("Submit & Complete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeliverableDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
