package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.KgRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Splash : ScreenDestination()
    object Onboarding : ScreenDestination()
    object Home : ScreenDestination()
    object Services : ScreenDestination()
    data class ServiceDetail(val serviceId: Long) : ScreenDestination()
    data class BookService(val serviceId: Long, val packageId: Long? = null) : ScreenDestination()
    object CustomerOrders : ScreenDestination()
    data class CustomerOrderDetail(val orderId: String) : ScreenDestination()
    object CustomerLogin : ScreenDestination()
    object CustomerRegister : ScreenDestination()
    object CustomerProfile : ScreenDestination()
    object CustomerNotifications : ScreenDestination()
    object Courses : ScreenDestination()
    object DigitalProducts : ScreenDestination()
    object PortfolioBlog : ScreenDestination()
    object ContactSupport : ScreenDestination()

    // Admin Specific Destinations (Protected)
    object AdminLogin : ScreenDestination()
    object AdminDashboard : ScreenDestination()
    object AdminOrders : ScreenDestination()
    data class AdminOrderDetail(val orderId: String) : ScreenDestination()
    object AdminServices : ScreenDestination()
    object AdminInquiries : ScreenDestination()
    object AdminSettings : ScreenDestination()
}

@OptIn(ExperimentalCoroutinesApi::class)
class KgViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    val repository = KgRepository(db)

    // Current Navigation Destination
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Auth States
    private val _currentCustomer = MutableStateFlow<UserEntity?>(null)
    val currentCustomer: StateFlow<UserEntity?> = _currentCustomer.asStateFlow()

    private val _currentAdmin = MutableStateFlow<AdminEntity?>(null)
    val currentAdmin: StateFlow<AdminEntity?> = _currentAdmin.asStateFlow()

    // Toast/Snackbar Message
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun showMessage(msg: String) {
        _userMessage.value = msg
    }

    // Navigation Helper
    fun navigateTo(screen: ScreenDestination) {
        // Enforce Admin Security: Block admin screens if not authenticated as Admin!
        if (screen is ScreenDestination.AdminDashboard ||
            screen is ScreenDestination.AdminOrders ||
            screen is ScreenDestination.AdminOrderDetail ||
            screen is ScreenDestination.AdminServices ||
            screen is ScreenDestination.AdminInquiries ||
            screen is ScreenDestination.AdminSettings
        ) {
            if (_currentAdmin.value == null) {
                _userMessage.value = "Admin authentication required. Redirected to Admin Login."
                _currentScreen.value = ScreenDestination.AdminLogin
                return
            }
        }
        _currentScreen.value = screen
    }

    // Reactive Data Sources
    val publishedServices = repository.serviceDao.getPublishedServicesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allServices = repository.serviceDao.getAllServicesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders = repository.orderDao.getAllOrdersFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customerOrders = _currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.orderDao.getOrdersByCustomerFlow(customer.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminNotifications = repository.notificationDao.getAdminNotificationsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadAdminNotifCount = repository.notificationDao.getUnreadAdminNotificationsCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val customerNotifications = _currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.notificationDao.getCustomerNotificationsFlow(customer.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCustomerNotifCount = _currentCustomer.flatMapLatest { customer ->
        if (customer != null) repository.notificationDao.getUnreadCustomerNotificationsCountFlow(customer.id)
        else flowOf(0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val courses = repository.courseDao.getPublishedCoursesFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products = repository.productDao.getPublishedProductsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portfolio = repository.contentDao.getPortfolioFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blogs = repository.contentDao.getBlogsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testimonials = repository.contentDao.getTestimonialsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val contactRequests = repository.contentDao.getAllContactRequestsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platformSettings = repository.contentDao.getSettingsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Admin Dashboard KPI metrics
    val totalRevenue = repository.orderDao.getTotalRevenueFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalUsersCount = repository.userDao.getUserCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Auto-login pre-seeded customer for smooth preview, or user can register/login
    init {
        viewModelScope.launch {
            // Check default customer
            val defCustomer = repository.userDao.getUserByEmailOrPhone("customer@kgsocialnetwork.com")
            if (defCustomer != null && _currentCustomer.value == null) {
                _currentCustomer.value = defCustomer
            }
        }
    }

    // --- Customer Authentication ---
    fun registerCustomer(
        fullName: String,
        email: String,
        phone: String,
        passwordPlain: String,
        address: String = "",
        city: String = "Srinagar",
        state: String = "Jammu & Kashmir",
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.registerCustomer(fullName, email, phone, passwordPlain, address, city, state)
            res.onSuccess { user ->
                _currentCustomer.value = user
                _userMessage.value = "Welcome, ${user.fullName}! Account created successfully."
                onSuccess()
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Registration failed."
            }
        }
    }

    fun loginCustomer(identifier: String, passwordPlain: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.loginCustomer(identifier, passwordPlain)
            res.onSuccess { user ->
                _currentCustomer.value = user
                _userMessage.value = "Welcome back, ${user.fullName}!"
                onSuccess()
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Login failed."
            }
        }
    }

    fun logoutCustomer() {
        _currentCustomer.value = null
        _userMessage.value = "Logged out from customer account."
        _currentScreen.value = ScreenDestination.Home
    }

    // --- Admin Authentication ---
    fun loginAdmin(username: String, passwordPlain: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.loginAdmin(username, passwordPlain)
            res.onSuccess { admin ->
                _currentAdmin.value = admin
                _userMessage.value = "Admin authenticated: ${admin.fullName} (${admin.role})"
                onSuccess()
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Admin login failed."
            }
        }
    }

    fun logoutAdmin() {
        _currentAdmin.value = null
        _userMessage.value = "Logged out from Admin Panel."
        _currentScreen.value = ScreenDestination.Home
    }

    // --- Service Details & Packages ---
    fun getPackagesForService(serviceId: Long): Flow<List<ServicePackageEntity>> {
        return repository.serviceDao.getPackagesForServiceFlow(serviceId)
    }

    // --- Order Placement ---
    fun placeOrder(
        service: ServiceEntity,
        pkg: ServicePackageEntity,
        requirements: String,
        instructions: String,
        attachedFiles: String,
        onSuccess: (OrderEntity) -> Unit
    ) {
        val customer = _currentCustomer.value
        if (customer == null) {
            _userMessage.value = "Please login or register to place an order."
            _currentScreen.value = ScreenDestination.CustomerLogin
            return
        }

        viewModelScope.launch {
            val res = repository.placeOrder(
                customer = customer,
                service = service,
                pkg = pkg,
                requirements = requirements,
                instructions = instructions,
                attachedFiles = attachedFiles
            )
            res.onSuccess { order ->
                _userMessage.value = "Order placed successfully! Order ID: ${order.id}"
                onSuccess(order)
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Failed to place order."
            }
        }
    }

    // --- Order Details Queries ---
    fun getOrderFlow(orderId: String): Flow<OrderEntity?> {
        return repository.orderDao.getOrderByIdFlow(orderId)
    }

    fun getOrderActivitiesFlow(orderId: String): Flow<List<OrderActivityEntity>> {
        return repository.orderActivityDao.getActivitiesForOrderFlow(orderId)
    }

    fun getOrderMessagesFlow(orderId: String): Flow<List<MessageEntity>> {
        return repository.messageDao.getMessagesForOrderFlow(orderId)
    }

    // --- Admin Actions on Order ---
    fun updateOrderStatus(orderId: String, newStatus: String, note: String = "") {
        val admin = _currentAdmin.value ?: return
        viewModelScope.launch {
            val res = repository.updateOrderStatus(orderId, newStatus, admin, note)
            res.onSuccess {
                _userMessage.value = "Order $orderId status updated to $newStatus."
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Failed to update status."
            }
        }
    }

    fun completeOrderWithDeliverables(orderId: String, files: String, note: String) {
        val admin = _currentAdmin.value ?: return
        viewModelScope.launch {
            val res = repository.completeOrderWithDeliverables(orderId, files, admin, note)
            res.onSuccess {
                _userMessage.value = "Order marked as COMPLETED. Deliverables uploaded!"
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Failed to complete order."
            }
        }
    }

    fun assignStaff(orderId: String, staffName: String) {
        val admin = _currentAdmin.value ?: return
        viewModelScope.launch {
            val res = repository.assignOrderStaff(orderId, staffName, admin)
            res.onSuccess {
                _userMessage.value = "Assigned to $staffName."
            }
        }
    }

    fun recordPayment(orderId: String, txnId: String, amount: Double) {
        viewModelScope.launch {
            val res = repository.recordOrderPayment(orderId, txnId, amount)
            res.onSuccess {
                _userMessage.value = "Payment recorded successfully for $orderId."
            }
        }
    }

    // --- Real-time Chat Messaging ---
    fun sendMessage(orderId: String, text: String, isSenderAdmin: Boolean, receiverId: Long) {
        viewModelScope.launch {
            val senderId: Long
            val senderName: String
            val senderType: String

            if (isSenderAdmin) {
                val admin = _currentAdmin.value ?: return@launch
                senderId = admin.id
                senderName = admin.fullName + " (KG Support)"
                senderType = "ADMIN"
            } else {
                val customer = _currentCustomer.value ?: return@launch
                senderId = customer.id
                senderName = customer.fullName
                senderType = "CUSTOMER"
            }

            val res = repository.sendMessage(
                orderId = orderId,
                senderId = senderId,
                senderName = senderName,
                senderType = senderType,
                receiverId = receiverId,
                text = text
            )
            res.onFailure { err ->
                _userMessage.value = err.message ?: "Could not send message."
            }
        }
    }

    fun markOrderMessagesRead(orderId: String, isCustomer: Boolean) {
        viewModelScope.launch {
            val userType = if (isCustomer) "CUSTOMER" else "ADMIN"
            repository.messageDao.markMessagesAsRead(orderId, userType)
        }
    }

    // --- Notifications ---
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.notificationDao.markAsRead(id)
        }
    }

    fun markAllAdminNotificationsRead() {
        viewModelScope.launch {
            repository.notificationDao.markAllAdminAsRead()
            _userMessage.value = "All admin notifications marked as read."
        }
    }

    // --- Contact / Inquiry ---
    fun submitContactInquiry(
        fullName: String,
        email: String,
        phone: String,
        whatsapp: String,
        service: String,
        budget: String,
        message: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.submitContactRequest(fullName, email, phone, whatsapp, service, budget, message)
            res.onSuccess {
                _userMessage.value = "Inquiry received! Jahangir Ahmed Kacher and the KG team will reach out promptly."
                onSuccess()
            }.onFailure { err ->
                _userMessage.value = err.message ?: "Failed to send inquiry."
            }
        }
    }

    // --- Course Enrollment ---
    fun enrollInCourse(course: CourseEntity) {
        val customer = _currentCustomer.value
        if (customer == null) {
            _userMessage.value = "Please log in to enroll in this course."
            _currentScreen.value = ScreenDestination.CustomerLogin
            return
        }
        viewModelScope.launch {
            repository.courseDao.enrollStudent(
                CourseEnrollmentEntity(
                    courseId = course.id,
                    customerId = customer.id,
                    courseTitle = course.title
                )
            )
            _userMessage.value = "Successfully enrolled in ${course.title}!"
        }
    }

    // --- Digital Product Download ---
    fun purchaseProduct(product: ProductEntity) {
        val customer = _currentCustomer.value
        if (customer == null) {
            _userMessage.value = "Please log in to purchase digital products."
            _currentScreen.value = ScreenDestination.CustomerLogin
            return
        }
        viewModelScope.launch {
            repository.productDao.purchaseProduct(
                ProductPurchaseEntity(
                    productId = product.id,
                    customerId = customer.id,
                    productTitle = product.title,
                    amount = product.price,
                    downloadToken = "KG-DL-${System.currentTimeMillis()}"
                )
            )
            _userMessage.value = "Purchased ${product.title}! Ready for instant download."
        }
    }
}
