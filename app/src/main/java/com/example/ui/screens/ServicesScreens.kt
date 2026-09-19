package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServiceEntity
import com.example.data.model.ServicePackageEntity
import com.example.ui.components.KgTopBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.KgViewModel
import com.example.ui.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val services by viewModel.publishedServices.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val categories = listOf(
        "All",
        "Social Media Marketing",
        "Digital Marketing",
        "SEO Services",
        "Website Development",
        "Graphic Design",
        "Video Editing",
        "Content Creation",
        "Blogger Services",
        "WordPress Services",
        "YouTube Services",
        "Freelancing Services",
        "Business Digital Solutions"
    )

    val filteredServices = services.filter { service ->
        val matchesCategory = selectedCategory == "All" || service.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                service.title.contains(searchQuery, ignoreCase = true) ||
                service.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Digital Services",
                subtitle = "${filteredServices.size} Available Services",
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Home) },
                onNotifClick = { viewModel.navigateTo(ScreenDestination.CustomerNotifications) }
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
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("input_service_search"),
                placeholder = { Text("Search services, e.g. SEO, Web, Video...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SubtitleText) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PureWhite,
                    unfocusedContainerColor = PureWhite
                )
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = PureWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (filteredServices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = null, tint = SubtitleText, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No services found", fontWeight = FontWeight.Bold, color = DarkText)
                        Text("Try searching with different keywords or reset category", fontSize = 12.sp, color = SubtitleText)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredServices) { service ->
                        ServiceFullCard(
                            service = service,
                            onClick = { viewModel.navigateTo(ScreenDestination.ServiceDetail(service.id)) },
                            onBookNow = { viewModel.navigateTo(ScreenDestination.BookService(service.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceFullCard(
    service: ServiceEntity,
    onClick: () -> Unit,
    onBookNow: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderGray),
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = ElectricBlue.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = service.category,
                        color = ElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "⏱ ${service.deliveryTime}",
                    fontSize = 11.sp,
                    color = SubtitleText,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = service.title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = service.description,
                fontSize = 13.sp,
                color = SubtitleText,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Features checklist preview
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                service.features.lines().take(3).forEach { feature ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusCompleted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = feature,
                            fontSize = 12.sp,
                            color = DarkText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = BorderGray)
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Starting from", fontSize = 11.sp, color = SubtitleText)
                    Text(
                        text = "₹${service.basePrice.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onClick,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("Details", fontSize = 12.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onBookNow,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_book_direct_${service.id}")
                    ) {
                        Text("Book Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceDetailScreen(
    serviceId: Long,
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val services by viewModel.publishedServices.collectAsState()
    val service = services.find { it.id == serviceId }
    val packages by viewModel.getPackagesForService(serviceId).collectAsState(initial = emptyList())
    var selectedPackage by remember { mutableStateOf<ServicePackageEntity?>(null) }

    LaunchedEffect(packages) {
        if (packages.isNotEmpty() && selectedPackage == null) {
            selectedPackage = packages.firstOrNull()
        }
    }

    if (service == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Service not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            KgTopBar(
                title = service.title,
                subtitle = service.category,
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.Services) }
            )
        },
        bottomBar = {
            Surface(
                color = PureWhite,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, BorderGray),
                modifier = Modifier.navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total Price", fontSize = 11.sp, color = SubtitleText)
                        Text(
                            text = "₹${(selectedPackage?.price ?: service.basePrice).toInt()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = ElectricBlue
                        )
                        Text(
                            text = selectedPackage?.name ?: "Basic",
                            fontSize = 11.sp,
                            color = SubtitleText
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.navigateTo(
                                ScreenDestination.BookService(
                                    serviceId = service.id,
                                    packageId = selectedPackage?.id
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("btn_proceed_to_book")
                    ) {
                        Text("Proceed to Book", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
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
            // Service Info Header Card
            item {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = service.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = service.description,
                            fontSize = 14.sp,
                            color = DarkText.copy(alpha = 0.85f),
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            color = SoftGray,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📋 Client Requirements Needed:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DarkText
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = service.requirementsPrompt,
                                    fontSize = 12.sp,
                                    color = SubtitleText
                                )
                            }
                        }
                    }
                }
            }

            // Package Selector
            item {
                Text(
                    text = "Select Service Tier Package",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkText
                )
            }

            items(packages) { pkg ->
                val isSelected = selectedPackage?.id == pkg.id
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) Color(0xFFF0F6FF) else PureWhite,
                    border = BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) ElectricBlue else BorderGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPackage = pkg }
                        .testTag("package_card_${pkg.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPackage = pkg },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = pkg.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = DarkText
                                )
                            }
                            Text(
                                text = "₹${pkg.price.toInt()}",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = ElectricBlue
                            )
                        }

                        Text(
                            text = pkg.description,
                            fontSize = 13.sp,
                            color = SubtitleText,
                            modifier = Modifier.padding(start = 32.dp, bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier.padding(start = 32.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("⏱ ${pkg.deliveryDays} Days Delivery", fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.SemiBold)
                            Text("🔄 ${pkg.revisions}", fontSize = 12.sp, color = DarkText, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            modifier = Modifier.padding(start = 32.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            pkg.features.lines().forEach { f ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = StatusCompleted, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = f, fontSize = 12.sp, color = DarkText)
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
fun BookServiceScreen(
    serviceId: Long,
    packageId: Long?,
    viewModel: KgViewModel,
    modifier: Modifier = Modifier
) {
    val services by viewModel.publishedServices.collectAsState()
    val service = services.find { it.id == serviceId }
    val packages by viewModel.getPackagesForService(serviceId).collectAsState(initial = emptyList())
    val currentCustomer by viewModel.currentCustomer.collectAsState()

    var selectedPkg by remember { mutableStateOf<ServicePackageEntity?>(null) }
    var requirementsText by remember { mutableStateOf("") }
    var instructionsText by remember { mutableStateOf("") }
    var attachedFilesText by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("UPI / QR Code") }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(packages, packageId) {
        if (packages.isNotEmpty()) {
            selectedPkg = if (packageId != null) {
                packages.find { it.id == packageId } ?: packages.first()
            } else {
                packages.first()
            }
        }
    }

    if (service == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Service not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            KgTopBar(
                title = "Order Service",
                subtitle = service.title,
                showBackButton = true,
                onBackClick = { viewModel.navigateTo(ScreenDestination.ServiceDetail(serviceId)) }
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
            // Customer status notice
            if (currentCustomer == null) {
                item {
                    Surface(
                        color = StatusPending.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, StatusPending.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = StatusPending)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Guest Customer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("You will be prompted to login/register upon ordering.", fontSize = 11.sp, color = SubtitleText)
                            }
                            Button(
                                onClick = { viewModel.navigateTo(ScreenDestination.CustomerLogin) },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("Login", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Order Summary Card
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Service:", color = SubtitleText, fontSize = 13.sp)
                            Text(service.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DarkText)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Selected Tier:", color = SubtitleText, fontSize = 13.sp)
                            Text(selectedPkg?.name ?: "Basic", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ElectricBlue)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Estimate:", color = SubtitleText, fontSize = 13.sp)
                            Text("${selectedPkg?.deliveryDays ?: 3} Business Days", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = BorderGray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Payable:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("₹${(selectedPkg?.price ?: service.basePrice).toInt()}", fontWeight = FontWeight.Black, fontSize = 20.sp, color = ElectricBlue)
                        }
                    }
                }
            }

            // Requirements Field
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("1. Requirements & Project Brief *", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                        Text(service.requirementsPrompt, fontSize = 12.sp, color = SubtitleText)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = requirementsText,
                            onValueChange = { requirementsText = it },
                            placeholder = { Text("Enter your detailed requirements, links, and goals here...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("input_order_requirements"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SoftGray,
                                unfocusedContainerColor = SoftGray
                            )
                        )
                    }
                }
            }

            // Additional Instructions & File Upload Simulation
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("2. Additional Instructions & Files", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = instructionsText,
                            onValueChange = { instructionsText = it },
                            placeholder = { Text("Special notes, color preferences, drive links...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .testTag("input_order_instructions"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = SoftGray,
                                unfocusedContainerColor = SoftGray
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Attached Files / Assets Reference:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = attachedFilesText,
                            onValueChange = { attachedFilesText = it },
                            placeholder = { Text("e.g. project_assets.zip, brand_logo.png") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_order_files"),
                            trailingIcon = {
                                TextButton(
                                    onClick = {
                                        attachedFilesText = if (attachedFilesText.isEmpty()) "project_brief.pdf (2.4MB)" else "$attachedFilesText, brand_assets.zip (14.2MB)"
                                    }
                                ) {
                                    Text("Add File", fontSize = 11.sp, color = ElectricBlue)
                                }
                            }
                        )
                    }
                }
            }

            // Payment Option
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = PureWhite,
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("3. Payment Method", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = DarkText)
                        Spacer(modifier = Modifier.height(8.dp))
                        val methods = listOf("UPI / QR Code (9622102381@upi)", "Razorpay Online", "Pay on Confirmation")
                        methods.forEach { method ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { paymentMethod = method }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = paymentMethod == method,
                                    onClick = { paymentMethod = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(method, fontSize = 13.sp, color = DarkText)
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        val pkg = selectedPkg ?: return@Button
                        if (requirementsText.trim().isEmpty()) {
                            viewModel.showMessage("Please provide your project requirements.")
                            return@Button
                        }
                        isSubmitting = true
                        viewModel.placeOrder(
                            service = service,
                            pkg = pkg,
                            requirements = requirementsText,
                            instructions = instructionsText,
                            attachedFiles = attachedFilesText
                        ) { createdOrder ->
                            isSubmitting = false
                            viewModel.navigateTo(ScreenDestination.CustomerOrderDetail(createdOrder.id))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_confirm_place_order"),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = PureWhite, modifier = Modifier.size(22.dp))
                    } else {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Confirm & Place Order (₹${(selectedPkg?.price ?: service.basePrice).toInt()})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
