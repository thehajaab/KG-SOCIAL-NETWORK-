package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderActivityEntity
import com.example.data.model.OrderEntity
import com.example.ui.components.KgTopBar
import com.example.ui.components.OrderTimelineView
import com.example.ui.components.StatusBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.KgViewModel
import com.example.ui.viewmodel.ScreenDestination
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerOrdersScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.customerOrders.collectAsState()
    val currentCustomer by viewModel.currentCustomer.collectAsState()

    Scaffold(
        topBar = {
            KgTopBar(
                title = "My Orders",
                subtitle = if (currentCustomer != null) "${orders.size} Active Orders" else "Please login",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) },
                onNotifClick = { viewModel.navigateTo(ScreenDestination.CustomerNotifications) }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (currentCustomer == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = SubtitleText, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Customer Login Required", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Log in to view and track your real service orders", fontSize = 12.sp, color = SubtitleText)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(ScreenDestination.CustomerLogin) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Log In Now")
                    }
                }
            }
        } else if (orders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = SubtitleText, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No Orders Yet", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
                    Text("You haven't placed any digital service orders yet.", fontSize = 12.sp, color = SubtitleText)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(ScreenDestination.Services) },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Explore Services")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(SoftGray)
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    CustomerOrderCard(
                        order = order,
                        onClick = {
                            viewModel.navigateTo(ScreenDestination.CustomerOrderDetail(order.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerOrderCard(
    order: OrderEntity,
    onClick: () -> Unit
) {
    val dateStr = remember(order.createdAt) {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(order.createdAt))
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderGray),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = order.serviceTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkText
            )

            Text(
                text = "Package: ${order.packageName}",
                fontSize = 12.sp,
                color = SubtitleText
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Amount", fontSize = 10.sp, color = SubtitleText)
                    Text("₹${order.amount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(dateStr, fontSize = 11.sp, color = SubtitleText)
                    Text(
                        text = if (order.paymentStatus == "PAID") "✓ Paid" else "Payment: ${order.paymentStatus}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (order.paymentStatus == "PAID") StatusCompleted else StatusPending
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerOrderDetailScreen(
    orderId: String,
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val order by viewModel.getOrderFlow(orderId).collectAsState(initial = null)
    val activities by viewModel.getOrderActivitiesFlow(orderId).collectAsState(initial = emptyList())
    val messages by viewModel.getOrderMessagesFlow(orderId).collectAsState(initial = emptyList())
    val currentCustomer by viewModel.currentCustomer.collectAsState()

    var chatMessageText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) } // 0 = Tracking & Info, 1 = Live Chat

    LaunchedEffect(orderId) {
        viewModel.markOrderMessagesRead(orderId, isCustomer = true)
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
                title = currentOrder.id,
                subtitle = currentOrder.serviceTitle,
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.CustomerOrders) }
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
            // Tab Selector: Tracking vs Live Chat
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = PureWhite,
                contentColor = ElectricBlue
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Overview & Progress", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                    icon = { Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        viewModel.markOrderMessagesRead(orderId, isCustomer = true)
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Support Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            if (messages.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = ElectricBlue,
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "${messages.size}",
                                        color = PureWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            if (selectedTab == 0) {
                // Tracking & Overview Tab
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Card
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentOrder.serviceTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = DarkText
                                    )
                                    StatusBadge(status = currentOrder.orderStatus)
                                }
                                Text("Tier: ${currentOrder.packageName}", fontSize = 13.sp, color = SubtitleText)
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Payable Amount", fontSize = 11.sp, color = SubtitleText)
                                        Text("₹${currentOrder.amount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = ElectricBlue)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Payment Status", fontSize = 11.sp, color = SubtitleText)
                                        Text(
                                            text = currentOrder.paymentStatus,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (currentOrder.paymentStatus == "PAID") StatusCompleted else StatusPending
                                        )
                                    }
                                }

                                if (currentOrder.paymentStatus != "PAID") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = {
                                            val upiUri = Uri.parse("upi://pay?pa=9622102381@upi&pn=KG%20Social%20Network&am=${currentOrder.amount}&cu=INR&tn=Order%20${currentOrder.id}")
                                            val intent = Intent(Intent.ACTION_VIEW, upiUri)
                                            try {
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                // Fallback: simulate payment confirmation
                                                viewModel.recordPayment(currentOrder.id, "UPI-REF-${System.currentTimeMillis() % 100000}", currentOrder.amount)
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().testTag("btn_pay_upi")
                                    ) {
                                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Pay ₹${currentOrder.amount.toInt()} via UPI (9622102381@upi)")
                                    }
                                }
                            }
                        }
                    }

                    // Completed Deliverables Box
                    if (currentOrder.orderStatus == "COMPLETED") {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = StatusCompleted.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, StatusCompleted.copy(alpha = 0.4f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = StatusCompleted)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Deliverables Ready for Download",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = StatusCompleted
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (currentOrder.completedFiles.isNotEmpty()) currentOrder.completedFiles else "KG-Final-Deliverables-Package.zip",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkText
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = {
                                            viewModel.showMessage("Downloading deliverables package...")
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusCompleted),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("btn_download_deliverables")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Download Completed Files", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Visual Timeline
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Order Progress Pipeline", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                                Spacer(modifier = Modifier.height(14.dp))
                                OrderTimelineView(currentStatus = currentOrder.orderStatus)
                            }
                        }
                    }

                    // Requirements & Instructions
                    item {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = PureWhite,
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Project Details", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Requirements:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SubtitleText)
                                Text(currentOrder.requirements, fontSize = 13.sp, color = DarkText)
                                if (currentOrder.instructions.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Additional Instructions:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SubtitleText)
                                    Text(currentOrder.instructions, fontSize = 13.sp, color = DarkText)
                                }
                                if (currentOrder.attachedFiles.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Client Files:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SubtitleText)
                                    Text(currentOrder.attachedFiles, fontSize = 13.sp, color = ElectricBlue)
                                }
                            }
                        }
                    }

                    // Activity Audit History
                    if (activities.isNotEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = PureWhite,
                                border = BorderStroke(1.dp, BorderGray)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Order Activity Log", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    activities.forEach { act ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Circle,
                                                contentDescription = null,
                                                tint = ElectricBlue,
                                                modifier = Modifier.size(8.dp).padding(top = 4.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
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
                }
            } else {
                // Real-Time Chat Tab
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
                        if (messages.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No messages yet. Send a message to chat directly with KG Social Network support regarding this order.",
                                        fontSize = 12.sp,
                                        color = SubtitleText,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            items(messages) { msg ->
                                val isMe = msg.senderType == "CUSTOMER"
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isMe) 14.dp else 2.dp,
                                            bottomEnd = if (isMe) 2.dp else 14.dp
                                        ),
                                        color = if (isMe) ElectricBlue else PureWhite,
                                        border = if (isMe) null else BorderStroke(1.dp, BorderGray),
                                        modifier = Modifier.widthIn(max = 280.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Text(
                                                text = msg.senderName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isMe) PureWhite.copy(alpha = 0.8f) else ElectricBlue
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
                    }

                    // Message input row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = chatMessageText,
                            onValueChange = { chatMessageText = it },
                            placeholder = { Text("Type message to KG team...") },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_customer_chat_message"),
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
                                        isSenderAdmin = false,
                                        receiverId = 0
                                    )
                                    chatMessageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue)
                                .testTag("btn_send_customer_message")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
