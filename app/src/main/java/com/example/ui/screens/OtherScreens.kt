package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseEntity
import com.example.data.model.ProductEntity
import com.example.ui.components.KgTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.KgViewModel
import com.example.ui.viewmodel.ScreenDestination

@Composable
fun CoursesScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val courses by viewModel.courses.collectAsState()

    Scaffold(
        topBar = {
            KgTopBar(
                title = "KG Academy Courses",
                subtitle = "Learn High-Income Digital Skills",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) }
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
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DeepNavy,
                    border = BorderStroke(1.dp, DeepNavyLight)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Practical Industry Curriculum",
                            color = SkyBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Master Real-World Freelancing & Digital Marketing",
                            color = PureWhite,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Curated step-by-step masterclasses instructed by Jahangir Ahmed Kacher and senior industry practitioners.",
                            color = PureWhite.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            items(courses) { course ->
                CourseCard(
                    course = course,
                    onEnroll = { viewModel.enrollInCourse(course) }
                )
            }
        }
    }
}

@Composable
fun CourseCard(
    course: CourseEntity,
    onEnroll: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderGray),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().testTag("course_card_${course.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ElectricBlue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = course.level,
                        color = ElectricBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${course.lessonsCount} Lessons • ${course.duration}",
                    fontSize = 11.sp,
                    color = SubtitleText
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = course.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = course.description,
                fontSize = 12.sp,
                color = SubtitleText,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Instructor: ${course.instructor}", fontSize = 11.sp, color = DarkText, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Course Fee", fontSize = 10.sp, color = SubtitleText)
                    Text(
                        text = if (course.price == 0.0) "FREE" else "₹${course.price.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue
                    )
                }

                Button(
                    onClick = onEnroll,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    modifier = Modifier.testTag("btn_enroll_${course.id}")
                ) {
                    Text("Enroll Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DigitalProductsScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.products.collectAsState()

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Digital Products & Assets",
                subtitle = "Instant Download Resources",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) }
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
            items(products) { prod ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray),
                    modifier = Modifier.fillMaxWidth().testTag("product_card_${prod.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = AccentPurple.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = prod.category,
                                    color = AccentPurple,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(prod.fileName, fontSize = 11.sp, color = SubtitleText, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(prod.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(prod.description, fontSize = 12.sp, color = SubtitleText)

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BorderGray)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("₹${prod.price.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = ElectricBlue)

                            Button(
                                onClick = { viewModel.purchaseProduct(prod) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                modifier = Modifier.testTag("btn_buy_${prod.id}")
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Purchase & Download", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactSupportScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf("Digital Marketing Solutions") }
    var estimatedBudget by remember { mutableStateOf("₹10,000 - ₹25,000") }
    var message by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Contact KG Team",
                subtitle = "Let's Build Something Great",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) }
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Direct Contact Action Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = DeepNavy,
                    border = BorderStroke(1.dp, DeepNavyLight)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Direct Connect", color = SkyBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Jahangir Ahmed Kacher", color = PureWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Founder & Owner • KG Social Network", color = PureWhite.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("Jammu & Kashmir, India", color = SkyBlue, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/919622102381?text=Hello%20KG%20Social%20Network%20Team"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("btn_contact_whatsapp")
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:KACHERGROUPC@GMAIL.COM"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("btn_contact_email")
                            ) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Email Us", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Inquiry Form
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text("Send a Project Inquiry", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DarkText)
                        Text("Fill this form and our team will get back to you within 24 hours.", fontSize = 12.sp, color = SubtitleText)

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Your Name *") },
                            modifier = Modifier.fillMaxWidth().testTag("input_inquiry_name"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address *") },
                            modifier = Modifier.fillMaxWidth().testTag("input_inquiry_email"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number / WhatsApp") },
                            modifier = Modifier.fillMaxWidth().testTag("input_inquiry_phone"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = selectedService,
                            onValueChange = { selectedService = it },
                            label = { Text("Service Interested In") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text("Project Details & Message *") },
                            modifier = Modifier.fillMaxWidth().height(110.dp).testTag("input_inquiry_message"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (fullName.isBlank() || email.isBlank() || message.isBlank()) {
                                    viewModel.showMessage("Please provide your name, email, and message.")
                                    return@Button
                                }
                                isSubmitting = true
                                viewModel.submitContactInquiry(
                                    fullName = fullName,
                                    email = email,
                                    phone = phone,
                                    whatsapp = whatsapp,
                                    service = selectedService,
                                    budget = estimatedBudget,
                                    message = message
                                ) {
                                    isSubmitting = false
                                    fullName = ""
                                    email = ""
                                    phone = ""
                                    message = ""
                                    viewModel.navigateTo(ScreenDestination.Home)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_submit_inquiry"),
                            enabled = !isSubmitting
                        ) {
                            Text("Submit Inquiry", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val customerNotifs by viewModel.customerNotifications.collectAsState()
    val adminNotifs by viewModel.adminNotifications.collectAsState()
    val currentAdmin by viewModel.currentAdmin.collectAsState()

    var activeTab by remember { mutableStateOf(if (currentAdmin != null) 1 else 0) }

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Notifications",
                subtitle = "Real-time updates & alerts",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) }
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
            PrimaryTabRow(
                selectedTabIndex = activeTab,
                containerColor = PureWhite,
                contentColor = ElectricBlue
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("My Updates (${customerNotifs.size})", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Admin Alerts (${adminNotifs.size})", fontWeight = FontWeight.Bold) }
                )
            }

            val displayList = if (activeTab == 0) customerNotifs else adminNotifs

            if (displayList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No notifications here.", color = SubtitleText)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayList) { notif ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (notif.isRead) PureWhite else Color(0xFFF0F7FF),
                            border = BorderStroke(1.dp, if (notif.isRead) BorderGray else ElectricBlue.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.markNotificationRead(notif.id)
                                    if (notif.relatedOrderId.isNotEmpty()) {
                                        if (activeTab == 1) {
                                            viewModel.navigateTo(ScreenDestination.AdminOrderDetail(notif.relatedOrderId))
                                        } else {
                                            viewModel.navigateTo(ScreenDestination.CustomerOrderDetail(notif.relatedOrderId))
                                        }
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(ElectricBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = ElectricBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(notif.message, fontSize = 12.sp, color = SubtitleText)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val currentCustomer by viewModel.currentCustomer.collectAsState()
    val orders by viewModel.customerOrders.collectAsState()

    Scaffold(
        topBar = {
            KgTopBar(
                title = "My Profile",
                subtitle = currentCustomer?.fullName ?: "Customer Account",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) }
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
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentCustomer?.fullName?.take(1) ?: "U",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = PureWhite
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentCustomer?.fullName ?: "Guest User",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = DarkText
                        )
                        Text(
                            text = currentCustomer?.email ?: "Not logged in",
                            fontSize = 12.sp,
                            color = SubtitleText
                        )
                        Text(
                            text = "${currentCustomer?.city ?: "Srinagar"}, ${currentCustomer?.state ?: "Jammu & Kashmir"}",
                            fontSize = 11.sp,
                            color = ElectricBlue,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = BorderGray)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${orders.size}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText)
                                Text("Orders", fontSize = 11.sp, color = SubtitleText)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${orders.count { it.orderStatus == "COMPLETED" }}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = StatusCompleted)
                                Text("Completed", fontSize = 11.sp, color = SubtitleText)
                            }
                        }
                    }
                }
            }

            // Quick Actions
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column {
                        ListItem(
                            headlineContent = { Text("My Service Orders") },
                            leadingContent = { Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = ElectricBlue) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { viewModel.navigateTo(ScreenDestination.CustomerOrders) }
                        )
                        HorizontalDivider(color = BorderGray)
                        ListItem(
                            headlineContent = { Text("Enrolled Courses") },
                            leadingContent = { Icon(Icons.Default.School, contentDescription = null, tint = ElectricBlue) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { viewModel.navigateTo(ScreenDestination.Courses) }
                        )
                        HorizontalDivider(color = BorderGray)
                        ListItem(
                            headlineContent = { Text("Admin Console") },
                            leadingContent = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = AccentPurple) },
                            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
                            modifier = Modifier.clickable { viewModel.navigateTo(ScreenDestination.AdminDashboard) }
                        )
                    }
                }
            }

            // Logout Button
            item {
                Button(
                    onClick = { viewModel.logoutCustomer() },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusCancelled.copy(alpha = 0.1f), contentColor = StatusCancelled),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("btn_customer_logout")
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log Out", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
